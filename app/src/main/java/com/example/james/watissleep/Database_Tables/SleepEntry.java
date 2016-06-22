package com.example.james.watissleep.Database_Tables;

import io.realm.RealmObject;

/**
 * Created by James on 16-06-21.
 */
public class SleepEntry extends RealmObject {
    long sleepTime;
    long wakeTime;
    long amount_of_sleep;

    public String getHeader_image() {
        return header_image;
    }

    public void setHeader_image(String header_image) {
        this.header_image = header_image;
    }

    String header_image;

    public SleepEntry() {

    }

    public SleepEntry(long sleepTime, long wakeTime, long amount_of_sleep, String header_image) {
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.amount_of_sleep = amount_of_sleep;
        this.header_image = header_image;
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
