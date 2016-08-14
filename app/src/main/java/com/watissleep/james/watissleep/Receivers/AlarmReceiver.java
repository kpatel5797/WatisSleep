package com.watissleep.james.watissleep.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.watissleep.james.watissleep.Activities.MainActivity;
import com.watissleep.james.watissleep.R;

import java.io.IOException;

/**
 * Created by James on 16-05-17.
 */


public class AlarmReceiver extends BroadcastReceiver {
    // declare as static so it can be used by the MainActivity class
    // use this instance if you want to access any of the classes methods
    static boolean broadcast_received = false;
    static MediaPlayer mMediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // this is to handle the buttons ui
        editor.putBoolean("broadcast_received",true);
        editor.commit();
        // init the vibrator and play the vibration pattern
        Vibrator testVibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // 0 represents start immediately
        // 1000 (milliseconds) is the amount of time to vibrate
        // 1000 (milliseconds) is the amount of time to rest
        long[] pattern = {0, 1000, 1000};
        if (sharedPreferences.getBoolean("vib",true) == true) {
            testVibrate.vibrate(pattern,0);
        }

        // get the shared preferences file and read from it alarm_preference
        SharedPreferences getAlarms = PreferenceManager.getDefaultSharedPreferences(context);
        String alarms = getAlarms.getString("alarm_preference", "default ringtone");
        // parse the Uri of the alarm path into a readable string
        Uri uri = Uri.parse(alarms);
        // call the play method
        playSound(context, uri);

        // mMediaPlayer.stop() is called in the onWakeAction method
        // of the MainActivity class

        // display the notification
        if (sharedPreferences.getBoolean("send_notification",true) == true) {
            displayNotification(context);
        }

        // wake the device
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "alarm");
        wl.acquire(3000);
        wl.release();

    }

    // playSound(context, alert) takes the current context and a Uri
    // and loads a sound into a MediaPlayer object
    // Nothing is returned by this method
    private void playSound(Context context, Uri alert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        int alarm_volume = sharedPreferences.getInt("alarm_volume",0);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            // set the alarm volume to what it is in settings
            //audioManager.setStreamVolume(AudioManager.STREAM_ALARM,SettingsActivity.alarm_volume,0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM,alarm_volume,0);
            // start the alarm if the volume is not 0
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void displayNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_app_icon);
        builder.setContentTitle("It's time to wake up");
        builder.setContentText("You should probably wake up now. It would not be wise to snooze");
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);


        // make the notification heads-up
        builder.setVibrate(new long[0]);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
}

