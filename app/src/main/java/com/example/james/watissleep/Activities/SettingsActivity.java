package com.example.james.watissleep.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.james.watissleep.AppCompatPreferenceActivity;
import com.example.james.watissleep.R;
import com.pavelsikun.seekbarpreference.SeekBarPreference;

import java.util.Calendar;

/**
 * Created by James on 16-05-11.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    // alarm tone volume
    static SeekBarPreference volume_seek;
    private Calendar time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // handle changing the settings theme (light/dark theme)
        time = Calendar.getInstance();
        if (time.get(Calendar.HOUR_OF_DAY) < 9 || time.get(Calendar.HOUR_OF_DAY) >= 18) {
            setTheme(R.style.PreferenceTheme);
        }

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        setupActionBar();


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
