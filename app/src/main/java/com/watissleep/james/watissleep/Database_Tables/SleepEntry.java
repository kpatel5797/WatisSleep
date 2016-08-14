package com.watissleep.james.watissleep.Database_Tables;

import io.realm.RealmObject;

/**
 * Created by James on 16-06-21.
 */
public class SleepEntry extends RealmObject {
    long sleepTime;
    long wakeTime;
    long amount_of_sleep;

    public String getHeader_color() {
        return header_color;
    }

    public void setHeader_color(String header_color) {
        this.header_color = header_color;
    }

    String header_color;

    public SleepEntry() {

    }

    public SleepEntry(long sleepTime, long wakeTime, long amount_of_sleep, String header_image) {
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.amount_of_sleep = amount_of_sleep;
        this.header_color = header_image;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public long getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(long wakeTime) {
        this.wakeTime = wakeTime;
    }

    public long getAmount_of_sleep() {
        return amount_of_sleep;
    }

    public void setAmount_of_sleep(long amount_of_sleep) {
        this.amount_of_sleep = amount_of_sleep;
    }
}
