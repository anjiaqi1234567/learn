package windowFunction;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class BlinkWindowFunctionTest {
    public static void main(String[] args) throws Exception {
        // get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        MemoryStateBackend memoryStateBackend = new MemoryStateBackend();
        env.setStateBackend(memoryStateBackend);
        // get input data by connecting to the socket
        SourceFunction<String> out = new OutSource();
        DataStream<String> text = env.addSource(out);
        // parse the data
        DataStream<WordWithCount> windowCounts = text
                .flatMap(new FlatMapFunction<String, WordWithCount>() {
                    public void flatMap(String s, Collector<WordWithCount> collector) throws Exception {
                            for (String word : s.split(" ")) {
                                collector.collect(new WordWithCount(word, 1L));
                            }
                    }
                });
        windowCounts.keyBy(new MyKeySelector())
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount wordWithCount, WordWithCount t1) throws Exception {
                        WordWithCount result = new WordWithCount
                                (wordWithCount.word, wordWithCount.count + t1.count);
                        System.out.println(result.toString());
                        return result;
                    }
                });

        /*windowCounts.keyBy(new MyKeySelector())
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .fold(0L, new FoldFunction<timestampsAndWatermarks.WordWithCount, Long>() {
                    public Long fold(Long s, timestampsAndWatermarks.WordWithCount o) throws Exception {
                        System.out.println(s + o.count + "  thread:" + Thread.currentThread().getId());
                        return s + o.count;
                    }
                });*/


        env.execute("Window WordCount");
    }

    public static class MyKeySelector implements KeySelector<WordWithCount, String> {
        public String getKey (WordWithCount wordWithCount) throws Exception {
            return wordWithCount.word;
        }
    }
    public static class OutSource implements SourceFunction<String> {

        private String[] str = {
                "aa bb","bb cc","cc aa"
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

    public static class WordWithCount {

        public String word;
        public long count;

        public WordWithCount() {}


        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return word + " : " + count + " : ";
        }
    }

}