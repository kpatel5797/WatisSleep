package com.watissleep.james.watissleep.Database_Tables;

import java.util.Calendar;

import io.realm.RealmObject;

/**
 * Created by James on 16-06-05.
 */

public class FeedbackEntry extends RealmObject {
    String feedbackString;

    public long getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(long feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    long feedbackDate;

    public FeedbackEntry() {
    }

    public FeedbackEntry(String feedbackString, long feedbackDate) {
        final Calendar c = Calendar.getInstance();

        this.feedbackString = feedbackString;
        this.feedbackDate = feedbackDate;
    }


    public String getFeedbackString() {
        return feedbackString;
    }

    public void setFeedbackString(String feedbackString) {
        this.feedbackString = feedbackString;
    }
}
