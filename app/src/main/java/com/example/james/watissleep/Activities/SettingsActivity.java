package com.example.james.watissleep.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;

import com.example.james.watissleep.AppCompatPreferenceActivity;
import com.example.james.watissleep.Database_Tables.FeedbackEntry;
import com.example.james.watissleep.Database_Tables.SleepEntry;
import com.example.james.watissleep.R;
import com.pavelsikun.seekbarpreference.SeekBarPreference;

import java.util.Calendar;

import io.realm.Realm;

/**
 * Created by James on 16-05-11.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    // alarm tone volume
    static SeekBarPreference volume_seek;
    private Calendar time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        setupActionBar();

        // set the color of the volume_seek setting
        volume_seek = (SeekBarPreference) findPreference("volume_seek");
        SpannableString spannableString = new SpannableString("Alarm Volume");
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cardview_dark_background)), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        volume_seek.setTitle(spannableString);



        // handle the vibrate toggle & write vib to shared preferences
        SwitchPreference vibrate = (SwitchPreference) findPreference("vibrate_setting");
        vibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean current = (Boolean) newValue;
                if (current == true) {
                    editor.putBoolean("vib",true);
                } else {
                    editor.putBoolean("vib",false);
                }
                editor.commit();
                return true;
            }
        });

        SwitchPreference notifications = (SwitchPreference) findPreference("notification_preference");
        notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean current = (Boolean) newValue;
                if (current == true) {
                    editor.putBoolean("send_notification",true);
                } else {
                    editor.putBoolean("send_notification",false);
                }
                editor.commit();
                return true;
            }
        });

        ListPreference snoozePreference = (ListPreference) findPreference("snooze_preference");
        snoozePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String current = (String) newValue;
                int value = Integer.parseInt(current);
                editor.putInt("snooze_amount_minutes",value);
                editor.commit();
                return true;
            }
        });

        Preference delete_database_preference = findPreference("delete_database");
        delete_database_preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setMessage("No... Don't do it! You worked too hard to delete your precious sleep data!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.delete(FeedbackEntry.class);
                                realm.delete(SleepEntry.class);
                                realm.commitTransaction();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);


    }

    // when the activity is finished
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // handle the seekBar
        volume_seek = (SeekBarPreference) findPreference("volume_seek");
        editor.putInt("alarm_volume",volume_seek.getCurrentValue());
        SwitchPreference vibrate = (SwitchPreference) findPreference("vibrate_setting");
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        // write vib to shared preferences
        if(vibrate.isChecked() == true) {
            editor.putBoolean("vib",true);
        } else {
            editor.putBoolean("vib",false);
        }
        editor.commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            SpannableString spannableString = new SpannableString("Settings");
            spannableString.setSpan(new TypefaceSpan("RobotoTTF/RobotoCondensed-Bold.ttf"), 0, spannableString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(spannableString);
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
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
