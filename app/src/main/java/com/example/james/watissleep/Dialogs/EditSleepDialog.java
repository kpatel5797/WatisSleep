package com.example.james.watissleep.Dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.james.watissleep.R;
import com.example.james.watissleep.Adapters.SleepAdapter;
import com.example.james.watissleep.Database_Tables.SleepEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by James on 16-06-21.
 */
public class EditSleepDialog extends AppCompatDialogFragment {
    EditText sleep_time_edit;
    EditText sleep_date_edit;
    EditText wake_time_edit;
    EditText wake_date_edit;
    SleepEntry currentSleepEntry;
    SleepAdapter sleepAdapter;
    View itemView;

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflator.inflate(R.layout.edit_sleepentry_dialog,null);
        getDialog().setTitle("Edit The Sleep Entry");
        // user can click outside the window to dismiss
        setCancelable(true);

        // time and date string formats
        final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");

        // editText fields
        sleep_time_edit = (EditText) view.findViewById(R.id.sleep_time_edit);
        sleep_date_edit = (EditText) view.findViewById(R.id.sleep_date_edit);
        wake_time_edit = (EditText) view.findViewById(R.id.wake_time_edit);
        wake_date_edit = (EditText) view.findViewById(R.id.wake_date_edit);

        // editText fields
        sleep_date_edit.setText(dateFormat.format(currentSleepEntry.getSleepTime()));
        sleep_time_edit.setText(timeFormat.format(currentSleepEntry.getSleepTime()));
        wake_time_edit.setText(timeFormat.format(currentSleepEntry.getWakeTime()));
        wake_date_edit.setText(dateFormat.format(currentSleepEntry.getWakeTime()));

        // get the calendars for new sleep and wake times (to be set by user)
        final Calendar new_sleep_date_and_time = Calendar.getInstance();
        new_sleep_date_and_time.setTimeInMillis(currentSleepEntry.getSleepTime());
        final Calendar new_wake_date_and_time = Calendar.getInstance();
        new_wake_date_and_time.setTimeInMillis(currentSleepEntry.getWakeTime());

        // keep a record of the old sleep and wake time in case user wants to undo change
        final Calendar original_sleep = Calendar.getInstance();
        original_sleep.setTimeInMillis(currentSleepEntry.getSleepTime());
        final  Calendar original_wake = Calendar.getInstance();
        original_wake.setTimeInMillis(currentSleepEntry.getWakeTime());

        // set the sleep_date
        sleep_date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTimeInMillis(currentSleepEntry.getSleepTime());
                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        newCalendar.set(year,monthOfYear,dayOfMonth);
                        new_sleep_date_and_time.set(Calendar.YEAR,year);
                        new_sleep_date_and_time.set(Calendar.MONTH,monthOfYear);
                        new_sleep_date_and_time.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        sleep_date_edit.setText(dateFormat.format(newCalendar.getTime()));
                    }

                }, newCalendar.get(newCalendar.YEAR), newCalendar.get(newCalendar.MONTH), newCalendar.get(newCalendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });

        // set the sleep_time
        sleep_time_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(currentSleepEntry.getSleepTime());

                TimePickerDialog dialog =  new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar newCalendar = Calendar.getInstance();
                                newCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                newCalendar.set(Calendar.MINUTE,minute);
                                // set the minute and hour for the alarm to ring
                                new_sleep_date_and_time.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                new_sleep_date_and_time.set(Calendar.MINUTE,minute);
                                sleep_time_edit.setText(timeFormat.format(newCalendar.getTime()));

                            }
                        }, c.get(c.HOUR_OF_DAY), c.get(c.MINUTE), false);
                dialog.show();
            }
        });

        // set the wake_date
        wake_date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTimeInMillis(currentSleepEntry.getWakeTime());
                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        newCalendar.set(year,monthOfYear,dayOfMonth);
                        new_wake_date_and_time.set(Calendar.YEAR,year);
                        new_wake_date_and_time.set(Calendar.MONTH,monthOfYear);
                        new_wake_date_and_time.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        wake_date_edit.setText(dateFormat.format(newCalendar.getTime()));
                    }

                }, newCalendar.get(newCalendar.YEAR), newCalendar.get(newCalendar.MONTH), newCalendar.get(newCalendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });

        // set the wake_time
        wake_time_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(currentSleepEntry.getWakeTime());

                TimePickerDialog dialog =  new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar newCalendar = Calendar.getInstance();
                                newCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                newCalendar.set(Calendar.MINUTE,minute);
                                // set the minute and hour for the alarm to ring
                                new_wake_date_and_time.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                new_wake_date_and_time.set(Calendar.MINUTE,minute);
                                wake_time_edit.setText(timeFormat.format(newCalendar.getTime()));

                            }
                        }, c.get(c.HOUR_OF_DAY), c.get(c.MINUTE), false);
                dialog.show();

            }
        });

        // handle the user clicking the set button (entry is changed in DB)
        FancyButton btn_set = (FancyButton) view.findViewById(R.id.btn_set_sleep_entry_edit);
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // set the sleeptime and wake time to the one's that were set by the user
                        currentSleepEntry.setSleepTime(new_sleep_date_and_time.getTimeInMillis());
                        currentSleepEntry.setWakeTime(new_wake_date_and_time.getTimeInMillis());
                    }
                });
                // dismiss the dialog
                dismiss();
                // show a snackBar that tells the user that the sleep entry was updated
                // also give the user the option to revert the entry back to what it was originally
                sleepAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar
                        .make(itemView,"Sleep Entry Updated!",Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        currentSleepEntry.setSleepTime(original_sleep.getTimeInMillis());
                                        currentSleepEntry.setWakeTime(original_wake.getTimeInMillis());
                                        sleepAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                snackbar.show();
            }
        });

        // handle the user clicking the cancel button
        // nothing is changed in the DB and the dialog is dismissed
        FancyButton btn_cancel = (FancyButton) view.findViewById(R.id.btn_cancel_sleep_entry_edit);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    public void setSleepEntry(SleepEntry sleepEntry) {
        this.currentSleepEntry = sleepEntry;
    }

    public void setSleepAdapter (SleepAdapter sleepAdapter) {
        this.sleepAdapter = sleepAdapter;
    }

    public void setItemView (View itemView) {
        this.itemView = itemView;
    }
}
