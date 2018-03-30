package com.andy.blink.joinstream;

import com.andy.blink.BoundedOutOfOrdernessGenerator;
import com.andy.blink.WordWithCount;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class JoinStreamTest implements Runnable {
    public static volatile long timestamp = 1496301598000L;
    public static void main(String[] args) throws Exception {
        new Thread(new JoinStreamTest()).start();
        // get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        MemoryStateBackend memoryStateBackend = new MemoryStateBackend();
        env.setStateBackend(memoryStateBackend);
        env.getConfig().setAutoWatermarkInterval(1000);
        SourceFunction<WordWithCount> out1 = new OutSource1();
        DataStream<WordWithCount> windowCounts1 = env.addSource(out1)
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator());
        SourceFunction<WordWithCount> out2 = new OutSource2();
        DataStream<WordWithCount> windowCounts2 = env.addSource(out2)
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator());

        DataStream<WordWithCount> joinStream = windowCounts1.join(windowCounts2)
                .where(new MyKeySelector())
                .equalTo(new MyKeySelector())
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .apply(new JoinFunction<WordWithCount, WordWithCount, WordWithCount>() {
                    private static final long serialVersionUID = -7438614447391425367L;
                    public WordWithCount join(WordWithCount wordWithCount, WordWithCount wordWithCount2) throws Exception {
                        return new WordWithCount(wordWithCount.word, wordWithCount.count + 1, timestamp);
                    }
                });
        joinStream.print();
//        DataStream<WordWithCount> data = joinStream.keyBy(new MyKeySelector())
//                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
//                .reduce(new ReduceFunction<WordWithCount>() {
//                    private static final long serialVersionUID = -4659927181103907862L;
//
//                    public WordWithCount reduce(WordWithCount wordWithCount, WordWithCount t1) throws Exception {
//                        WordWithCount result = new WordWithCount
//                                (wordWithCount.word, wordWithCount.count + t1.count);
//                        System.out.println(result.toString());
//                        return result;
//                    }
//                });
        System.out.println(env.getExecutionPlan());
        System.out.println("!!!!!!!!!");
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

    public static class MyKeySelector implements KeySelector<WordWithCount, String> {
        private static final long serialVersionUID = -4997337793205496707L;

        public String getKey (WordWithCount wordWithCount) throws Exception {
            return wordWithCount.word;
        }
    }
    public static class OutSource1 implements SourceFunction<WordWithCount> {

        private static final long serialVersionUID = 2500877900768241421L;
        private String[] str = {
                "aa","bb"
        };
        public void run(SourceContext<WordWithCount> sourceContext) throws Exception {
            int index =0;
            while (true) {
                if(index == str.length)
                    index = 0;
                sourceContext.collect(new WordWithCount(str[index], 1, timestamp));
                index++;
                Thread.sleep(10);
            }
        }
        public void cancel() {
        }
    }

    public static class OutSource2 implements SourceFunction<WordWithCount> {

        private static final long serialVersionUID = 2500877900768241421L;
        private String[] str = {
                "ee","dd"
        };
        public void run(SourceContext<WordWithCount> sourceContext) throws Exception {
            int index =0;
            while (true) {
                if(index == str.length)
                    index = 0;
                sourceContext.collect(new WordWithCount(str[index], 1, timestamp));
                index++;
                Thread.sleep(10);
            }
        }
        public void cancel() {
        }
    }
    // Data type for words with count and timestamp

}