package timestampsAndWatermarks;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

/**
 * Created by Andy on 2017/7/4.
 */
public class BoundedOutOfOrdernessGenerator implements AssignerWithPeriodicWatermarks<WordWithCount> {

    private final long maxOutOfOrderness = 3500; // 3.5 seconds

    private long currentMaxTimestamp;

    public long extractTimestamp(WordWithCount element, long previousElementTimestamp) {
        long timestamp = element.getTimeStamp();
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
        return timestamp;
    }

    public Watermark getCurrentWatermark() {
        // return the watermark as current highest timestamp minus the out-of-orderness bound
        Watermark watermark = new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        System.out.println("generate watermark:" + watermark.toString());
        return watermark;
    }
}
