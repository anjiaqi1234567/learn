package com.andy.blink;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class MetricTest {

    public static void main(String[] args) throws Exception {
        // get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        MemoryStateBackend memoryStateBackend = new MemoryStateBackend();
        env.setStateBackend(memoryStateBackend);
        SourceFunction<String> out = new OutSource();
        DataStreamSource<String> text = env.addSource(out);
        // parse the data
        DataStream<WordWithCount> windowCounts = text
                .flatMap(new RichFlatMapFunction<String, WordWithCount>() {
                    private Counter count;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        count = getRuntimeContext()
                                .getMetricGroup()
                                .counter("myCounter");
                    }

                    public void flatMap(String s, Collector<WordWithCount> collector) throws Exception {
                            for (String word : s.split(" ")) {
                                collector.collect(new WordWithCount(word, 1L));
                                count.inc();
                                System.out.println(count.getCount() + " thread:" + Thread.currentThread().getId());
                            }
                    }
                }).setParallelism(4);

        DataStream<WordWithCount> resultStream = windowCounts.keyBy(new MyKeySelector())
                .window(TumblingProcessingTimeWindows.of(Time.seconds(4)))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount wordWithCount, WordWithCount t1) throws Exception {
                        WordWithCount result = new WordWithCount
                                (wordWithCount.word, wordWithCount.count + t1.count);
                        return result;
                    }
                });
        resultStream.addSink(new SinkFunction<WordWithCount>() {
            public void invoke(WordWithCount wordWithCount) throws Exception {
                System.out.println(wordWithCount.toString());
            }
        });
        System.out.println(env.getExecutionPlan());
        System.out.println("!!!!!!!!!");
        env.execute("MetricTest WordCount");
    }

    public static class MyKeySelector implements KeySelector<WordWithCount, String> {
        public String getKey (WordWithCount wordWithCount) throws Exception {
            return wordWithCount.word;
        }
    }
    public static class OutSource implements SourceFunction<String> {

        private String[] str = {
                "aa bb","cc dd"
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