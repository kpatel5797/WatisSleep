package com.example.james.watissleep.Activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.james.watissleep.AlarmReceiver;
import com.example.james.watissleep.Database_Tables.SleepEntry;
import com.example.james.watissleep.Dialogs.FeedbackDialog;
import com.example.james.watissleep.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        // hide the toolbar but keep the functionality
        //toolbar.setVisibility(View.GONE);

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
        TextView clock = (TextView) findViewById(R.id.alarmTime);
        //TextView hey = (TextView) findViewById(R.id.hey);
        //TextView bed = (TextView) findViewById(R.id.bedText);
        TextView alarmText = (TextView) findViewById(R.id.alarmText);
        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        // get the alarmTime string from shared prefs
        alarmSet.setText(sharedPreferences.getString("alarmTime",""));
        alarmSet.setTypeface(roboto_thin);
        alarmText.setTypeface(typeface);
        //bed.setTypeface(typeface);
        clock.setTypeface(roboto_regular);
        //hey.setTypeface(typeface);

        ImageView centre_image = (ImageView) findViewById(R.id.centre_image);
        ImageView time_image_background = (ImageView) findViewById(R.id.time_image_background);

        Glide.with(this).load("http://www.localwom.com/i/cool-pattern-free-wallpapers.jpg").centerCrop().into(centre_image);
        if (Build.VERSION.SDK_INT >= 20) {
            Glide.with(this)
                    .load("https://4kwallpapers.co/wp-content/uploads/2015/09/blue-material-design-ultra-hd-wallpapers.png")
                    .centerCrop()
                    .crossFade()
                    .into(time_image_background);
        } else {
            Glide.with(this)
                    .load("https://cdn-images.xda-developers.com/direct/3/3/9/3/1/1/8/Design.jpg")
                    .centerCrop()
                    .crossFade()
                    .into(time_image_background);
        }

        TextView dateText = (TextView) findViewById(R.id.dateText);
        dateText.setTypeface(typeface);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd");
        String dateString = sdf.format(date);
        dateText.setText(dateString);

        // check if the hour is less than 6:00pm and more than 9:00am
        time = Calendar.getInstance();
        //TextView bedText = (TextView) findViewById(R.id.bedText);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        // change from night theme to day theme at 9:00am and from day to night at 6:00pm
        if (time.get(Calendar.HOUR_OF_DAY) >= 9 && time.get(Calendar.HOUR_OF_DAY) < 18) {
            // change the header image, the background colour and
            // the welcome text
            mainLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));
            //bedText.setText("Enjoy Your Day!");
        }

        // set the alarmSet text when the alarm is ringing
        // TODO(James): This needs to be implemented as a listener so it can dynamically set the text
        if (AlarmReceiver.getMediaPlayer() != null && AlarmReceiver.getMediaPlayer().isPlaying()) {
            TextView setText = (TextView) findViewById(R.id.alarmSet);
            setText.setText("WAKE UP!");
        }

        //############################# SHARED PREFERENCES #########################################
        // show the buttons based on the shared prefs
        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        FancyButton snooze_btn = (FancyButton) findViewById(R.id.btn_snooze);
        if (sharedPreferences.getBoolean("set_alarm_btn_visible",true) == true) {
            set_alarm_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.INVISIBLE);
            reset_btn.setVisibility(View.INVISIBLE);
            confirm_btn.setVisibility(View.INVISIBLE);
        } else if (sharedPreferences.getBoolean("alarm_confirmed",false) == true) {
            set_alarm_btn.setVisibility(View.INVISIBLE);
            // TODO(JAMES):set this back to INVISIBLE, this is for testing purposes
            cancel_btn.setVisibility(View.VISIBLE);
            reset_btn.setVisibility(View.INVISIBLE);
            confirm_btn.setVisibility(View.INVISIBLE);
        } else {
            set_alarm_btn.setVisibility(View.INVISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);
            reset_btn.setVisibility(View.VISIBLE);
            confirm_btn.setVisibility(View.VISIBLE);
        }

        FancyButton wake_btn = (FancyButton) findViewById(R.id.btn_wake);
        if (sharedPreferences.getBoolean("broadcast_received",false) == true) {
            wake_btn.setVisibility(View.VISIBLE);
            snooze_btn.setVisibility(View.VISIBLE);
            reset_btn.setVisibility(View.INVISIBLE);
            cancel_btn.setVisibility(View.INVISIBLE);
            confirm_btn.setVisibility(View.INVISIBLE);
            // If the alarm is playing then discard all notifications that are still active
            NotificationManager notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
        } else {
            wake_btn.setVisibility(View.INVISIBLE);
            snooze_btn.setVisibility(View.INVISIBLE);
        }

        //##########################################################################################




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
        FancyButton wake_btn = (FancyButton) findViewById(R.id.btn_wake);
        FancyButton snooze_btn = (FancyButton) findViewById(R.id.btn_snooze);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        set_alarm_btn.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        // TODO(JAMES):set this back to INVISIVBLE, this is only for testing purposes
        cancel_btn.setVisibility(View.VISIBLE);
        wake_btn.setVisibility(View.INVISIBLE);
        snooze_btn.setVisibility(View.INVISIBLE);
        confirm_btn.setVisibility(View.INVISIBLE);

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
        reset_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);
        confirm_btn.setVisibility(View.INVISIBLE);


        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        alarmSet.setText("");

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
        editor.putString("alarmTime","");
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

        // make the alarmSet textView blank
        TextView alarmSet = (TextView) findViewById(R.id.alarmSet);
        alarmSet.setText("");

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
        editor.putString("alarmTime","");
        editor.putBoolean("alarm_confirmed",false);

        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
        FancyButton reset_btn = (FancyButton) findViewById(R.id.btn_reset_alarm);
        FancyButton cancel_btn = (FancyButton) findViewById(R.id.btn_cancel);
        FancyButton wake_btn = (FancyButton) findViewById(R.id.btn_wake);
        FancyButton snooze_btn = (FancyButton) findViewById(R.id.btn_snooze);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        set_alarm_btn.setVisibility(View.VISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);
        wake_btn.setVisibility(View.INVISIBLE);
        snooze_btn.setVisibility(View.INVISIBLE);
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
        FancyButton wake_btn = (FancyButton) findViewById(R.id.btn_wake);
        FancyButton snooze_btn = (FancyButton) findViewById(R.id.btn_snooze);
        FancyButton confirm_btn = (FancyButton) findViewById(R.id.btn_confirm_alarm);
        set_alarm_btn.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        // TODO(JAMES):set this back to INVISIVBLE, this is only for testing purposes
        cancel_btn.setVisibility(View.VISIBLE);
        wake_btn.setVisibility(View.INVISIBLE);
        snooze_btn.setVisibility(View.INVISIBLE);
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
                        reset_btn.setVisibility(View.VISIBLE);
                        cancel_btn.setVisibility(View.VISIBLE);
                        confirm_btn.setVisibility(View.VISIBLE);

                        FancyButton set_alarm_btn = (FancyButton) findViewById(R.id.btn_alarm);
                        set_alarm_btn.setVisibility(View.INVISIBLE);

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
                        alarmSet.setText(time);
                        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Thin.ttf");
                        alarmSet.setTypeface(typeface);
                        alarmSet.setTextSize(35);

                        // save the alarm time
                        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("alarmTime",time.toString());
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    intent.putExtra("FROM_ACTIVITY","MainActivity");
                    startActivity(intent);
                }
            }, 250);
        } else if (id == R.id.nav_stats) {
            //CharSequence statsMessage = "This is supposed to start the Statistics activity!";
            //Toast.makeText(MainActivity.this, statsMessage, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(),PieActivity.class);
                    intent.putExtra("FROM_ACTIVITY","MainActivity");
                    startActivity(intent);
                }
            }, 250);
        } else if (id == R.id.nav_edit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(),SleepListActivity.class);
                    intent.putExtra("FROM_ACTIVITY","MainActivity");
                    startActivity(intent);
                }
            }, 250);
        } else if (id == R.id.nav_info) {
            CharSequence infoMessage = "This is supposed to start the About activity!";
            Toast.makeText(MainActivity.this, infoMessage, Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
