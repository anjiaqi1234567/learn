package com.andy.blink;

import java.text.SimpleDateFormat;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class WordWithCount {

    public String word;
    public long count;
    public long timestamp;

    public WordWithCount() {}

    public long getTimeStamp() {
        return timestamp;
    }

    public WordWithCount(String word, long count, long timestamp) {
        this.word = word;
        this.count = count;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        java.util.Date dt = new java.util.Date(timestamp);
        String sDateTime = sdf.format(dt);
        return word + " : " + count + " : " + sDateTime;
    }
}