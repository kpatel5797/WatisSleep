package com.watissleep.james.watissleep.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Toast;

import com.watissleep.james.watissleep.Dialogs.FeedbackDialog;
import com.watissleep.james.watissleep.Database_Tables.FeedbackEntry;
import com.watissleep.james.watissleep.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class PieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_screen);

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

        SpannableString s = new SpannableString("Distribution of\nSleep Feedback");
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, s.length(),0);

        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/RobotoTTF/Roboto-MediumItalic.ttf"));
        pieChart.setCenterText(s);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawCenterText(true);

        pieChart.setUsePercentValues(true);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Good");
        labels.add("Ok");
        labels.add("Bad");

        PieData data = new PieData(labels, dataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(15);
        pieChart.setData(data);
        data.setValueTypeface(Typeface.createFromAsset(getAssets(), "fonts/RobotoTTF/Roboto-Light.ttf"));
        data.setValueFormatter(new PercentFormatter());
        Legend l = pieChart.getLegend();

        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        pieChart.setDescription("");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        setupActionBar();
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void showGoodEntries(View view) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<FeedbackEntry> good_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","good").findAll();
        Toast.makeText(this, String.format("number of GOOD entries: %d",good_entries.size()), Toast.LENGTH_SHORT).show();
    }

    public void showOkEntries(View view) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<FeedbackEntry> good_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","ok").findAll();
        Toast.makeText(this, String.format("number of OK entries: %d",good_entries.size()), Toast.LENGTH_SHORT).show();
    }

    public void showBadEntries(View view) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<FeedbackEntry> good_entries = realm.where(FeedbackEntry.class).equalTo("feedbackString","bad").findAll();
        Toast.makeText(this, String.format("number of BAD entries: %d",good_entries.size()), Toast.LENGTH_SHORT).show();
    }

    public void showTotalEntriesInInterval(View view) {
        Realm realm = Realm.getDefaultInstance();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR,-7);
        long one_week_ago = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_YEAR,7);
        long today = c.getTimeInMillis();

        final RealmResults<FeedbackEntry> test = realm.where(FeedbackEntry.class).between("feedbackDate",one_week_ago,today).findAll();
        Toast.makeText(this, String.format("%d",test.size()), Toast.LENGTH_SHORT).show();
    }

    public void clearDatabase(View view) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FeedbackEntry.class);
        realm.commitTransaction();
    }

    public void showFeedbackDialog(View view) {
        FeedbackDialog feedbackDialog = new FeedbackDialog();
        feedbackDialog.show(getSupportFragmentManager(), "hello");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

}

