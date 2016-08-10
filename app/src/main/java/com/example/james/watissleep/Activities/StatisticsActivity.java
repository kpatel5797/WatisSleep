package com.example.james.watissleep.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.example.james.watissleep.Database_Tables.FeedbackEntry;
import com.example.james.watissleep.Database_Tables.SleepEntry;
import com.example.james.watissleep.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_main);
        final ColorDrawable actionBarColor = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(actionBarColor);
        actionBar.show();

        showFeedbackPie();
        //showHoursSleepGraph();
        setTextViews();
    }

    public void setTextViews() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<FeedbackEntry> good_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","good").findAll();
        final RealmResults<FeedbackEntry> ok_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","ok").findAll();
        final RealmResults<FeedbackEntry> bad_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","bad").findAll();
        final RealmResults<SleepEntry> all_sleep_entries = realm.where(SleepEntry.class).findAll();
        // store the total hours of sleep from each sleep entry
        double total_hours_sleep = 0;
        // store the sleep times in HH:mm format
        ArrayList<String> bed_times = new ArrayList<>();
        ArrayList<String> wake_times = new ArrayList<>();
        int len_sleep_entries = all_sleep_entries.size();
        for (int j = 0; j < len_sleep_entries; j++) {
            total_hours_sleep += all_sleep_entries.get(j).getAmount_of_sleep();
            SimpleDateFormat bedFormat = new SimpleDateFormat("HH:mm");
            bed_times.add(bedFormat.format(new Date(all_sleep_entries.get(j).getSleepTime())));
            wake_times.add(bedFormat.format(new Date(all_sleep_entries.get(j).getWakeTime())));

        }
        // seconds from milliseconds
        double seconds = total_hours_sleep / 1000.0;
        // minutes from seconds
        double minutes = seconds / 60.0;
        // hours from minutes
        double hours_of_sleep = minutes / 60.0;
        double average_amount_of_sleep = (all_sleep_entries.size() > 0) ? hours_of_sleep / all_sleep_entries.size() : hours_of_sleep / (all_sleep_entries.size() + 1);

        String average_bed_time = calculateAverageOfTime(bed_times);
        String average_wake_time = calculateAverageOfTime(wake_times);

        Typeface roboto_regular = Typeface.createFromAsset(getAssets(), "fonts/RobotoTTF/Roboto-Regular.ttf");
        TextView average_sleep_text = (TextView) findViewById(R.id.average_hours_sleep_text);
        TextView average_sleep_number = (TextView) findViewById(R.id.average_hours_sleep_number);
        average_sleep_text.setTypeface(roboto_regular);
        average_sleep_number.setTypeface(roboto_regular);
        average_sleep_number.setText(String.format("%1$,.2f", average_amount_of_sleep) + " hrs");

        TextView average_bed_text = (TextView) findViewById(R.id.average_sleep_time_text);
        TextView average_bed_number = (TextView) findViewById(R.id.average_sleep_time_number);
        average_bed_text.setTypeface(roboto_regular);
        average_bed_number.setTypeface(roboto_regular);
        average_bed_number.setText(average_bed_time);

        TextView average_wake_time_text = (TextView) findViewById(R.id.average_wake_time_text);
        TextView average_wake_time_number = (TextView) findViewById(R.id.average_wake_time_number);
        average_wake_time_text.setTypeface(roboto_regular);
        average_wake_time_number.setTypeface(roboto_regular);
        average_wake_time_number.setText(average_wake_time);

        TextView good_sleeps_text = (TextView) findViewById(R.id.good_sleeps_text);
        TextView ok_sleeps_text = (TextView) findViewById(R.id.ok_sleeps_text);
        TextView bad_sleeps_text = (TextView) findViewById(R.id.bad_sleeps_text);
        TextView good_sleeps_number = (TextView) findViewById(R.id.number_good_sleeps);
        TextView ok_sleeps_number = (TextView) findViewById(R.id.number_ok_sleeps);
        TextView bad_sleeps_number = (TextView) findViewById(R.id.number_bad_sleeps);
        good_sleeps_text.setTypeface(roboto_regular);
        ok_sleeps_text.setTypeface(roboto_regular);
        bad_sleeps_text.setTypeface(roboto_regular);
        good_sleeps_number.setTypeface(roboto_regular);
        ok_sleeps_number.setTypeface(roboto_regular);
        bad_sleeps_number.setTypeface(roboto_regular);
        good_sleeps_number.setText(Integer.toString(good_entries.size()));
        ok_sleeps_number.setText(Integer.toString(ok_entries.size()));
        bad_sleeps_number.setText(Integer.toString(bad_entries.size()));

    }

    public void showFeedbackPie() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<FeedbackEntry> good_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","good").findAll();
        final RealmResults<FeedbackEntry> ok_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","ok").findAll();
        final RealmResults<FeedbackEntry> bad_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","bad").findAll();

        int good = good_entries.size();
        int ok = ok_entries.size();
        int bad = bad_entries.size();

        PieChart pieChart = (PieChart) findViewById(R.id.feedbackChart);
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(good,0));
        entries.add(new Entry(ok,1));
        entries.add(new Entry(bad,2));

        SpannableString s = new SpannableString("Total Sleeps:\n" + Integer.toString(good_entries.size() + ok_entries.size() + bad_entries.size()));
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(),0);

        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/RobotoTTF/Roboto-Regular.ttf"));
        pieChart.setCenterText(s);
        pieChart.setCenterTextSize(20f);
        pieChart.setTransparentCircleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(65f);
        pieChart.setHoleColor(getResources().getColor(R.color.colorPrimaryDark));
        pieChart.setTransparentCircleRadius(65f);
        pieChart.setDrawCenterText(true);

        pieChart.setUsePercentValues(true);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSelectionShift(5f);

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            labels.add("");
        }

        PieData data = new PieData(labels, dataSet);
        pieChart.setData(data);
        data.setDrawValues(false);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        pieChart.setDescription("");
        int[] pie_colors = {Color.parseColor("#AED581"), Color.parseColor("#EDB858"), Color.parseColor("#ED5885")};
        dataSet.setColors(pie_colors);
    }

    public static String calculateAverageOfTime(ArrayList<String> timeInHHmm) {
        long seconds = 0;
        for (String timestr : timeInHHmm) {
            String[] hhmmss = timestr.split(":");
            seconds += Integer.valueOf(hhmmss[0]) * 60 * 60;
            seconds += Integer.valueOf(hhmmss[1]) * 60;
        }

        seconds /= (timeInHHmm.size() > 0) ? timeInHHmm.size() : (timeInHHmm.size() + 1);

        long hh = seconds / 60 / 60;
        long mm = (seconds / 60) % 60;
        if (hh >= 12) {
            if (hh == 12) {
                return String.format("%02d:%02d PM", hh, mm);
            } else {
                return String.format("%02d:%02d PM", hh-12, mm);
            }
        } else {
            if (hh == 0) {
                return String.format("%02d:%02d AM", 12, mm);
            } else {
                return String.format("%02d:%02d AM", hh, mm);
            }
        }
    }
}
