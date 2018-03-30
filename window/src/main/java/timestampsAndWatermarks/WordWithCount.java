package timestampsAndWatermarks;

import com.alibaba.blink.streaming.connectors.tt.TimeTunnel4Source;

import java.text.SimpleDateFormat;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class WordWithCount {

    public WordKey word;
    public long count;
    public long timestamp;

    public WordWithCount() {}

    public long getTimeStamp() {
        return timestamp;
    }

    public WordWithCount(String word, long count, long timestamp) {
        this.word = new WordKey();
        this.word.setKey(word);
        this.word.setBack(word);
        this.count = count;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        java.util.Date dt = new java.util.Date(timestamp);
        String sDateTime = sdf.format(dt);
        return word.getKey() + " : " + count + " : " + sDateTime;
        TimeTunnel4Source
    }

    public static void main(String[] args) {
        char a = ';';
        System.out.println((int)a);
    }
}