package com.example.james.watissleep.Activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.example.james.watissleep.AlarmReceiver;
import com.example.james.watissleep.Database_Tables.SleepEntry;
import com.example.james.watissleep.Dialogs.FeedbackDialog;
import com.example.james.watissleep.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import mehdi.sakout.fancybuttons.FancyButton;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Declare the alarm intent, the alarm manager, the calendar,
    // the pending intent and the minute and hour of the alarm
    Intent intentAlarm;
    // alarmManager for the alarm
    AlarmManager alarmManager;
    Calendar c, time;
    // hour and minute to set the alarm to; these are set in the timePicker dialog (set in onAlarmClick method)
    int hourToSet, minuteToSet;
    // pending intent for the AlarmReceiver.class
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // get the shared preferences file
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // don't show a label on the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // transparent toolbar
        toolbar.getBackground().setAlpha(0);

        // set the statusBar color to primaryDark if the sdk is  > lollipop
        // otherwise it will be set to appcompat.dayNight theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        // BUILD THE FEEDBACK DB
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        // set the typeface for all the textView's
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Light.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Regular.ttf");
        Typeface roboto_thin = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Thin.ttf");
        Typeface roboto_light = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Light.ttf");

        TextView dateText = (TextView) findViewById(R.id.dateText);
        dateText.setTypeface(roboto_thin);
        long date = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
        String dateString = dateFormat.format(date);
        dateText.setText(dateString);

        TextView timeText = (TextView) findViewById(R.id.timeText);
        timeText.setTypeface(roboto_light);

        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        alarmSet.setText(sharedPreferences.getString("alarmTime","NO ALARM SET"));
        alarmSet.setTypeface(roboto_light);

        //############################# SHARED PREFERENCES #########################################
        // show the buttons based on the shared prefs
        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        LinearLayout snooze_wake_layout = (LinearLayout) findViewById(R.id.snooze_wake_layout);
        LinearLayout three_btn_layout = (LinearLayout) findViewById(R.id.three_button_layout);
        if (sharedPreferences.getBoolean("set_alarm_btn_visible",true) == true) {
            set_alarm_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.INVISIBLE);
            reset_btn.setVisibility(View.INVISIBLE);
            confirm_btn.setVisibility(View.INVISIBLE);
            three_btn_layout.setVisibility(View.GONE);
        } else if (sharedPreferences.getBoolean("alarm_confirmed",false) == true) {
            set_alarm_btn.setVisibility(View.GONE);
            three_btn_layout.setVisibility(View.VISIBLE);
            // TODO(JAMES):set this back to INVISIBLE, this is for testing purposes
            cancel_btn.setVisibility(View.VISIBLE);
            reset_btn.setVisibility(View.INVISIBLE);
            confirm_btn.setVisibility(View.INVISIBLE);
        } else {
            set_alarm_btn.setVisibility(View.GONE);
            three_btn_layout.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);
            reset_btn.setVisibility(View.VISIBLE);
            confirm_btn.setVisibility(View.VISIBLE);
        }

        if (sharedPreferences.getBoolean("broadcast_received",false) == true) {
            snooze_wake_layout.setVisibility(View.VISIBLE);
            reset_btn.setVisibility(View.INVISIBLE);
            cancel_btn.setVisibility(View.INVISIBLE);
            confirm_btn.setVisibility(View.INVISIBLE);
            three_btn_layout.setVisibility(View.GONE);
            // If the alarm is playing then discard all notifications that are still active
            NotificationManager notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
        } else {
            snooze_wake_layout.setVisibility(View.GONE);
        }

        if (AlarmReceiver.getMediaPlayer() != null) {
            TextView setText = (TextView) findViewById(R.id.alarmSet);
            setText.setText("Wake up");
        }

        //##########################################################################################

        // Load the header image
        ImageView header_image = (ImageView) findViewById(R.id.header_image);
        Glide.with(this)
                .load("http://www.androidguys.com/wp-content/uploads/2015/12/Poly-Lakeside.jpg")
                .centerCrop()
                .into(header_image);
        Realm realm = Realm.getDefaultInstance();

        // Line chart for the home screen
        LineChartView sleep_hours_chart = (LineChartView) findViewById(R.id.hours_sleep_chart);
        // realm results
        RealmResults<SleepEntry> hours_sleep_list = realm.where(SleepEntry.class).findAll().sort("wakeTime", Sort.DESCENDING);
        if (hours_sleep_list.size() >= 5) {
            int size_hours_sleep_list = hours_sleep_list.size();
            // array that will store the most recent 5 amounts of sleep (values) and sleep times (labels)
            ArrayList<Float> recent_five_hours_sleep_arraylist = new ArrayList<Float>();
            ArrayList<String> recent_five_sleep_time_arraylist = new ArrayList<String>();
            // iterate through the realm results
            for (int i = 4; i >= 0; --i) {
                // get the number of hours of sleep
                Long current_amount_sleep = (long) TimeUnit.MILLISECONDS.toHours(hours_sleep_list.get(i).getAmount_of_sleep());
                Long current_sleep_time = hours_sleep_list.get(i).getSleepTime();
                // date formatter for label
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM, dd");


                recent_five_hours_sleep_arraylist.add(current_amount_sleep.floatValue());
                recent_five_sleep_time_arraylist.add(sdf.format(current_sleep_time));
            }

            float[] recent_five_hours_sleep = new float[recent_five_hours_sleep_arraylist.size()];
            int x = 0;
            for (Float f : recent_five_hours_sleep_arraylist) {
                recent_five_hours_sleep[x++] = (f != null ? f : Float.NaN);
            }
            String[] recent_five_sleep_time =
                    recent_five_sleep_time_arraylist.toArray(new String[recent_five_sleep_time_arraylist.size()]);

            LineSet dataset = new LineSet(recent_five_sleep_time, recent_five_hours_sleep);
            dataset.setColor(Color.parseColor("#0290c3"))
                    .setThickness(Tools.fromDpToPx(3))
                    .setSmooth(true);
            for (int i = 0; i < recent_five_sleep_time.length; i++) {
                Point point = (Point) dataset.getEntry(i);
                point.setRadius(20.0f);
                point.setStrokeThickness(10.0f);
                point.setColor(Color.parseColor("#ffffff"));
                point.setStrokeColor(Color.parseColor("#0290c3"));
            }

            sleep_hours_chart.addData(dataset);
            Paint thresPaint = new Paint();
            thresPaint.setColor(Color.parseColor("#FF3D00"));
            thresPaint.setStyle(Paint.Style.STROKE);
            thresPaint.setAntiAlias(true);
            thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
            thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

            Paint gridPaint = new Paint();
            gridPaint.setColor(Color.parseColor("#EEEEEE"));
            gridPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setAntiAlias(true);
            gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

            sleep_hours_chart.setBorderSpacing(Tools.fromDpToPx(30))
                    .setXLabels(AxisController.LabelPosition.OUTSIDE)
                    .setLabelsColor(Color.parseColor("#000000"))
                    .setYLabels(AxisController.LabelPosition.OUTSIDE)
                    .setXAxis(true)
                    .setAxisBorderValues(0, 12, 2)
                    .setGrid(ChartView.GridType.HORIZONTAL,12,1,gridPaint)
                    .setYAxis(true);
            sleep_hours_chart.setAxisColor(Color.parseColor("#000000"));
            sleep_hours_chart.show();
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // function to handle the fade out of the sleep button and the
    // fade in of the wake and snooze buttons
    // called when sleepButton is clicked
    public void onSleepAction(View view) {

        // set the alarm
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,hourToSet);
        c.set(Calendar.MINUTE,minuteToSet);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        // check if the current time is more than the alarm set
        // (ie. check if the alarm was set in the past)
        if (System.currentTimeMillis() > c.getTimeInMillis()) {
            // set the alarm for tomorrow (ie. the next day)
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
            Toast.makeText(MainActivity.this, "You cant set the alarm for a past time!", Toast.LENGTH_SHORT).show();
        }
        // create an intent/pending intent to set the alarm
        intentAlarm = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    public void onSnooze(View view) {
        // turn off the vibrator, turn off the alarm tone
        // and cancel the pending intent
        Vibrator currentVibrate = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        currentVibrate.cancel();
        if (AlarmReceiver.getMediaPlayer() != null) {
            AlarmReceiver.getMediaPlayer().stop();
        }
        // cancel the current pending intent
        intentAlarm = new Intent(this, AlarmReceiver.class);
        PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT).cancel();

        // set a calendar for 1 minute in future
        // TODO(JAMES): get the snooze time from sharedPreferences (user should be able to set via preference activity)
        Calendar snoozeTime = Calendar.getInstance();
        snoozeTime.add(Calendar.MINUTE,1);

        // set a new pendingIntent to broadcast to the AlarmReceiver in 1 min
        intentAlarm = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime.getTimeInMillis(), pendingIntent);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("set_alarm_btn_visible",false);
        editor.putBoolean("reset_alarm_btn_visible",false);
        editor.putBoolean("confirm_alarm_btn_visible",false);
        editor.putBoolean("cancel_alarm_btn_visible",false);
        editor.putBoolean("alarm_confirmed",true);
        editor.putBoolean("broadcast_received",false);

        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        alarmSet.setText("Alarm snoozed");
        editor.putString("alarmTime","Alarm snoozed");

        // set all the buttons to invisible
        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        LinearLayout snooze_wake_layout = (LinearLayout) findViewById(R.id.snooze_wake_layout);
        LinearLayout three_btn_layout = (LinearLayout) findViewById(R.id.three_button_layout);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        set_alarm_btn.setVisibility(View.GONE);
        reset_btn.setVisibility(View.INVISIBLE);
        // TODO(JAMES):set this back to INVISIVBLE, this is only for testing purposes
        cancel_btn.setVisibility(View.VISIBLE);
        confirm_btn.setVisibility(View.INVISIBLE);
        snooze_wake_layout.setVisibility(View.GONE);
        three_btn_layout.setVisibility(View.VISIBLE);

        editor.commit();

    }

    // method called when the cancel alarm button is clicked
    public void onCancelAlarm(View v) {
        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        set_alarm_btn.setVisibility(View.VISIBLE);
        // set the visibility of the buttons
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        LinearLayout three_btn_layout = (LinearLayout) findViewById(R.id.three_button_layout);
        three_btn_layout.setVisibility(View.GONE);
        reset_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);
        confirm_btn.setVisibility(View.INVISIBLE);

        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        alarmSet.setText("NO ALARM SET");

        // cancel the current pending intent
        intentAlarm = new Intent(this, AlarmReceiver.class);
        PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT).cancel();

        // save the state of the buttons (this code might be deprecated, check)
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("set_alarm_btn_visible",true);
        editor.putBoolean("reset_alarm_btn_visible",false);
        editor.putBoolean("cancel_alarm_btn_visible",false);
        editor.putBoolean("confirm_alarm_btn_visible",false);
        editor.putBoolean("alarm_confirmed",false);
        editor.putString("alarmTime","NO ALARM SET");
        editor.commit();
    }

    public void onWakeAction(View view) {
        // turn off the vibrator, turn off the alarm tone
        // and cancel the pending intent
        Vibrator currentVibrate = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        currentVibrate.cancel();
        if (AlarmReceiver.getMediaPlayer() != null) {
            AlarmReceiver.getMediaPlayer().stop();
        }

        // cancel the current pending intent
        intentAlarm = new Intent(this, AlarmReceiver.class);
        PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT).cancel();

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("set_alarm_btn_visible",true);
        editor.putBoolean("reset_alarm_btn_visible",false);
        editor.putBoolean("confirm_alarm_btn_visible",false);
        editor.putBoolean("cancel_alarm_btn_visible",false);
        editor.putBoolean("broadcast_received",false);
        editor.putString("alarmTime","NO ALARM SET");
        editor.putBoolean("alarm_confirmed",false);

        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        alarmSet.setText("NO ALARM SET");

        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        LinearLayout snooze_wake_layout = (LinearLayout) findViewById(R.id.snooze_wake_layout);
        LinearLayout three_btn_layout = (LinearLayout) findViewById(R.id.three_button_layout);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        set_alarm_btn.setVisibility(View.VISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);
        three_btn_layout.setVisibility(View.GONE);
        snooze_wake_layout.setVisibility(View.GONE);
        confirm_btn.setVisibility(View.INVISIBLE);

        // handle (and show) the dialog
        FeedbackDialog feedbackDialog = new FeedbackDialog();
        feedbackDialog.show(getSupportFragmentManager(), "hello");

        // get the sleep time from shared prefs
        final long sleep_date_time_milliseconds = sharedPreferences.getLong("sleepTime",0);
        Calendar c = Calendar.getInstance();
        // get the current time (wake time)
        final long current_date_time_milliseconds = c.getTimeInMillis();

        // the amount of sleep is the diff b/w sleep and wake time
        final long amount_of_sleep = current_date_time_milliseconds - sleep_date_time_milliseconds;

        // array or images to choose as header for entry card TODO(JAMES): add more images (possibly db?)
        final String[] images = {"http://wallpapercave.com/wp/j7c1FtS.jpg",
                "http://eskipaper.com/images/nice-wallpaper-5.jpg",
                "http://eskipaper.com/images/nice-wallpapers-12.jpg",
                "http://hdwallnpics.com/wp-content/gallery/nice-wallpapers-desktop/Nice-Desktop-Wallpapers-2.jpg",
                "http://i.imgur.com/jcgo2Sr.jpg",
                "http://4.bp.blogspot.com/-w3I5XQ1vWbg/Up6hKWsBRrI/AAAAAAAABss/I7_nbS_7wQ0/s1600/Nice+Wallpapers+(12).jpg",
                "http://eskipaper.com/images/nice-backgrounds-8.jpg",
                "http://g01.a.alicdn.com/kf/HTB1f1YeKXXXXXbzXVXXq6xXFXXXC/Home-Decor-Custom-Canvas-Prints-font-b-Nice-b-font-font-b-Wallpapers-b-font-font.jpg",
                "https://s-media-cache-ak0.pinimg.com/736x/12/06/ea/1206ea4d881c8e540c819ae1b6b67671.jpg",
                "http://eskipaper.com/images/nice-wallpaper-4.jpg",
                "http://2.bp.blogspot.com/-pqiJBMYxA5E/UvmzMgtHQ9I/AAAAAAAAB3A/6YTiPUoRYqk/s1600/Nice-wallpaper+desktop+(1).jpg",
                "http://www.whitegadget.com/attachments/pc-wallpapers/16374d1223299205-abstract-wallpapers-images-photos-picture-gallery-beautiful-abstract-wallpaper.jpg"};

        // make sure that the same image is not used twice
        int random_index = new Random().nextInt(images.length);
        while (random_index == sharedPreferences.getInt("previous_image_index",0)) {
            random_index = new Random().nextInt(images.length);
        }
        editor.putInt("previous_image_index",random_index);

        final int image_index = random_index;

        // execute the transaction to create the entry in the table
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SleepEntry s = realm.createObject(SleepEntry.class);
                s.setSleepTime(sleep_date_time_milliseconds);
                s.setWakeTime(current_date_time_milliseconds);
                s.setAmount_of_sleep(amount_of_sleep);
                s.setHeader_image(images[image_index]);
            }
        });

        // set the sleepTime back to zero and commit to the editor
        editor.putLong("sleepTime",0);
        editor.commit();
    }

    public void onConfirmAlarm(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("set_alarm_btn_visible",false);
        editor.putBoolean("reset_alarm_btn_visible",false);
        editor.putBoolean("confirm_alarm_btn_visible",false);
        editor.putBoolean("cancel_alarm_btn_visible",false);
        editor.putBoolean("alarm_confirmed",true);

        // set all the buttons to invisible
        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        LinearLayout snooze_wake_layout = (LinearLayout) findViewById(R.id.snooze_wake_layout);
        LinearLayout three_btn_layout = (LinearLayout) findViewById(R.id.three_button_layout);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        set_alarm_btn.setVisibility(View.GONE);
        reset_btn.setVisibility(View.INVISIBLE);
        // TODO(JAMES):set this back to INVISIVBLE, this is only for testing purposes
        cancel_btn.setVisibility(View.VISIBLE);
        three_btn_layout.setVisibility(View.VISIBLE);
        snooze_wake_layout.setVisibility(View.GONE);
        confirm_btn.setVisibility(View.INVISIBLE);

        // create a new entry into the sleepData table
        Calendar c = Calendar.getInstance();
        long current_date_time_milliseconds = c.getTimeInMillis();
        editor.putLong("sleepTime",current_date_time_milliseconds);
        // commit to the editor
        editor.commit();


    }

    // function to handle the timePicker dialog when the user selects
    // and alarm time. Called when alarmText is clicked
    public void onAlarmClick(View view) {

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMin = c.get(Calendar.MINUTE);

        TimePickerDialog dialog =  new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // set the minute and hour for the alarm to ring
                        hourToSet = hourOfDay;
                        minuteToSet = minute;

                        // set the visibility of the buttons
                        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
                        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
                        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
                        LinearLayout three_btn_layout = (LinearLayout) findViewById(R.id.three_button_layout);
                        reset_btn.setVisibility(View.VISIBLE);
                        cancel_btn.setVisibility(View.VISIBLE);
                        confirm_btn.setVisibility(View.VISIBLE);
                        three_btn_layout.setVisibility(View.VISIBLE);

                        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
                        set_alarm_btn.setVisibility(View.GONE);

                        // call the onSleep method
                        onSleepAction(view);

                        String AM_PM;
                        if (hourOfDay < 12) {
                            if (hourOfDay == 0) hourOfDay = 12;
                            AM_PM = "AM";
                        } else {
                            hourOfDay -= 12;
                            if (hourOfDay == 0) hourOfDay = 12;
                            AM_PM = "PM";
                        }

                        String time = String.format("%d:%02d %s", hourOfDay, minute, AM_PM);

                        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
                        alarmSet.setText("Alarm set for " + time);

                        // save the alarm time
                        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("alarmTime","Alarm set for " +time);
                        editor.putBoolean("set_alarm_btn_visible",false);
                        editor.putBoolean("reset_alarm_btn_visible",true);
                        editor.putBoolean("cancel_alarm_btn_visible",true);
                        editor.putBoolean("confirm_alarm_btn_visible",true);
                        editor.putBoolean("alarm_confirmed",false);
                        editor.commit();

                    }
                }, mHour, mMin, false);
        dialog.show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // RUN THE INTENT AFTER A DELAY OF 250 MILLISECONDS
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            intent.putExtra("FROM_ACTIVITY","MainActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.right_in,R.anim.left_out);

        } else if (id == R.id.nav_stats) {
            //CharSequence statsMessage = "This is supposed to start the Statistics activity!";
            //Toast.makeText(MainActivity.this, statsMessage, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),PieActivity.class);
            intent.putExtra("FROM_ACTIVITY","MainActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.right_in,R.anim.left_out);
        } else if (id == R.id.nav_edit) {
            Intent intent = new Intent(getApplicationContext(),SleepListActivity.class);
            intent.putExtra("FROM_ACTIVITY","MainActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.right_in,R.anim.left_out);
        } else if (id == R.id.nav_info) {
            CharSequence infoMessage = "This is supposed to start the About activity!";
            Toast.makeText(MainActivity.this, infoMessage, Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
