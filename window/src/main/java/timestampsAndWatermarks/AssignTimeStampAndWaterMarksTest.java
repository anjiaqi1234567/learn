package timestampsAndWatermarks;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class AssignTimeStampAndWaterMarksTest implements Runnable {
    public static volatile long timestamp = 1496301598000L;
    public static StreamExecutionEnvironment env;
    public static void main(String[] args) throws Exception {
        new Thread(new AssignTimeStampAndWaterMarksTest()).start();
        // get the execution environment
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);//默认值200
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
                                collector.collect(new WordWithCount(word, 1L, timestamp));
                            }
                    }
                });
        //assign timestamp
        //windowCounts = windowCounts.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator());
        windowCounts = windowCounts.assignTimestampsAndWatermarks(new MyTimestampExtractor(Time.seconds(0)));
        DataStream sinkStream = windowCounts.keyBy(new MyKeySelector())
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount wordWithCount, WordWithCount t1) throws Exception {
                        wordWithCount.count++;
                        wordWithCount.timestamp = timestamp;
                        return wordWithCount;
                    }
                });
        sinkStream.print();

        env.execute("Window WordCount");
    }

    public void run() {
        while(true) {
            timestamp++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyKeySelector implements KeySelector<WordWithCount, WordKey> {
        public WordKey getKey (WordWithCount wordWithCount) throws Exception {
            return wordWithCount.word;
        }
    }

    public static class OutSource implements SourceFunction<String> {

        private String[] str = {
                "aa bb","cc dd "
        };
        public void run(SourceContext<String> sourceContext) throws Exception {
            int index =0;
            while (true) {
                if(index == str.length)
                    index = 0;
                sourceContext.collect(str[index]);
                index++;
                Thread.sleep(10);
            }
        }
        public void cancel() {
        }

    }
    // Data type for words with count and timestamp

}