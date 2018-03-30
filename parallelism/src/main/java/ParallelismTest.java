import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class ParallelismTest {

    public static void main(String[] args) throws Exception {
        // get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setParallelism(2);
        MemoryStateBackend memoryStateBackend = new MemoryStateBackend();
        env.setStateBackend(memoryStateBackend);
        SourceFunction<String> out1 = new OutSource();
        DataStreamSource<String> text1 = env.addSource(out1);
        // parse the data
        SingleOutputStreamOperator<WordWithCount> singleOutputStreamOperator1 = text1
                .flatMap(new FlatMapFunction<String, WordWithCount>() {
                    public void flatMap(String s, Collector<WordWithCount> collector) throws Exception {
                            for (String word : s.split(" ")) {
                                collector.collect(new WordWithCount(word, 1L));
                            }
                    }
                }).setParallelism(3);
        DataStream<WordWithCount> windowCounts1 = singleOutputStreamOperator1;


        SourceFunction<String> out2 = new OutSource();
        DataStreamSource<String> text2 = env.addSource(out2);
        // parse the data
        SingleOutputStreamOperator<WordWithCount> singleOutputStreamOperator2 = text2
                .flatMap(new FlatMapFunction<String, WordWithCount>() {
                    public void flatMap(String s, Collector<WordWithCount> collector) throws Exception {
                        for (String word : s.split(" ")) {
                            collector.collect(new WordWithCount(word, 1L));
                        }
                    }
                }).setParallelism(1);
        DataStream<WordWithCount> windowCounts2 = singleOutputStreamOperator2;

        DataStream<WordWithCount> joinStream = windowCounts1.join(windowCounts2)
                .where(new MyKeySelector())
                .equalTo(new MyKeySelector())
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .apply(new JoinFunction<WordWithCount, WordWithCount, WordWithCount>() {
                    int count = 0;
                    public WordWithCount join(WordWithCount wordWithCount, WordWithCount wordWithCount2) throws Exception {
                        System.out.println("count : " + (++count));
                        return new WordWithCount(wordWithCount.word, 2);
                    }
                }).flatMap(new FlatMapFunction<WordWithCount, WordWithCount>() {
                    public void flatMap(WordWithCount wordWithCount, Collector<WordWithCount> collector) throws Exception {
                        collector.collect(wordWithCount);
                    }
                }).setParallelism(5);

        joinStream.keyBy(new MyKeySelector())
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount wordWithCount, WordWithCount t1) throws Exception {
                        WordWithCount result = new WordWithCount
                                (wordWithCount.word, wordWithCount.count + t1.count);
                        System.out.println(result.toString());
                        return result;
                    }
                }).setParallelism(6);
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
                    continue;
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