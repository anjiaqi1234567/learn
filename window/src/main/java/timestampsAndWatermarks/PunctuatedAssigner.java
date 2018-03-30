package timestampsAndWatermarks;

import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * Created by Andy on 2017/6/29.
 */
public class PunctuatedAssigner implements AssignerWithPunctuatedWatermarks<WordWithCount> {

    @Nullable
    public Watermark checkAndGetNextWatermark(WordWithCount wordWithCount, long l) {
        return null;
    }

    public long extractTimestamp(WordWithCount wordWithCount, long l) {
        return 0;
    }
}