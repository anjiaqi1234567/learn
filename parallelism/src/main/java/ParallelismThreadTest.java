import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class ParallelismThreadTest {

    public static void main(String[] args) throws Exception {
        // get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        MemoryStateBackend memoryStateBackend = new MemoryStateBackend();
        env.setStateBackend(memoryStateBackend);
        SourceFunction<String> out1 = new OutSource();
        DataStreamSource<String> text1 = env.addSource(out1);
        // parse the data
        DataStream<WordWithCount> joinStream = text1
                .flatMap(new FlatMapFunction<String, WordWithCount>() {
                    public void flatMap(String s, Collector<WordWithCount> collector) throws Exception {
                            for (String word : s.split(" ")) {
                                System.out.println(word + " flatmap thread:" + Thread.currentThread().getId());
                                collector.collect(new WordWithCount(word, 1L));
                            }
                    }
                }).setParallelism(4);

        joinStream.keyBy(new MyKeySelector())
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount wordWithCount, WordWithCount t1) throws Exception {
                        WordWithCount result = new WordWithCount
                                (wordWithCount.word, wordWithCount.count + t1.count);
                        System.out.println(result.toString()+ " keyby thread" + Thread.currentThread().getId());
                        return result;
                    }
                });
        System.out.println(env.getExecutionPlan());
        System.out.println("!!!!!!!!!");
        env.execute("Window WordCount");
    }

    public static class MyKeySelector implements KeySelector<WordWithCount, String> {
        public String getKey (WordWithCount wordWithCount) throws Exception {
            return wordWithCount.word;
        }
    }
    public static class OutSource implements SourceFunction<String> {

        private String[] str = {
                "aa bb","aa bb"
        };
        public void run(SourceContext<String> sourceContext) throws Exception {
            int index =0;
            while (true) {
                if(index == str.length)
                    index = 0;
                sourceContext.collect(str[index]);
                index++;
                Thread.sleep(1000);
            }
        }
        public void cancel() {
        }

    }
    // Data type for words with count and timestamp

}