package com.andy.blink;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;
import org.slf4j.LoggerFactory;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class SourceStreamTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SourceStreamTest.class);


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
                                System.out.println(count.getCount());
                            }
                    }
                });

        System.out.println(env.getExecutionPlan());
        System.out.println("!!!!!!!!!");

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

}