package timestampsAndWatermarks;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Created by Andy on 2017/6/29.
 */
public class MyTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<WordWithCount> {
    public MyTimestampExtractor(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    public long extractTimestamp(WordWithCount wordWithCount) {
        return wordWithCount.getTimeStamp();
    }
}
