package com.watissleep.james.watissleep.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.watissleep.james.watissleep.Activities.SleepListActivity;
import com.watissleep.james.watissleep.Database_Tables.FeedbackEntry;
import com.watissleep.james.watissleep.Database_Tables.SleepEntry;
import com.watissleep.james.watissleep.Dialogs.EditSleepDialog;
import com.watissleep.james.watissleep.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by James on 16-06-21.
 */
public class SleepAdapter extends RecyclerView.Adapter<SleepAdapter.MyViewHolder> {
    // the layout inflater
    private LayoutInflater inflater;
    // the data set (set of sleep entries)
    RealmResults<SleepEntry> data;
    // an instance of the sleepListActivity class (Activity)
    SleepListActivity sleepListActivity;
    // an instance of this class (SleepAdapter)
    SleepAdapter thisSleepAdapter = this;

    public SleepAdapter(Context context, RealmResults<SleepEntry> data, SleepListActivity sleepListActivity) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.sleepListActivity = sleepListActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // get the current sleep entry
        SleepEntry current = data.get(position);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<FeedbackEntry> feedbacks = realm.where(FeedbackEntry.class).findAll().sort("feedbackDate", Sort.DESCENDING);
        FeedbackEntry current_face = feedbacks.get(position);

        if (current_face.getFeedbackString().equals("good")) {
            holder.faceImage.setImageResource(R.drawable.happy_face);
        } else if (current_face.getFeedbackString().equals("ok")) {
            holder.faceImage.setImageResource(R.drawable.neutral_face);
        } else if (current_face.getFeedbackString().equals("bad")) {
            holder.faceImage.setImageResource(R.drawable.sad_face);
        }

        // get the sleepTime and wakeTime
        Date sleep_date = new Date(current.getSleepTime());
        Date wake_date = new Date(current.getWakeTime());

        // date/time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
        SimpleDateFormat timesFormat = new SimpleDateFormat("hh:mm aa");


        String sleep_date_display = dateFormat.format(sleep_date);
        String sleep_times_display = timesFormat.format(sleep_date) + " - " + timesFormat.format(wake_date);

        // set the text on the card
        holder.sleepTimes.setText(sleep_times_display);
        holder.sleepDate.setText(sleep_date_display);

        Typeface roboto_condensed_light = Typeface.createFromAsset(sleepListActivity.getAssets(),"fonts/RobotoTTF/Roboto-Medium.ttf");
        Typeface roboto_condensed_bold = Typeface.createFromAsset(sleepListActivity.getAssets(),"fonts/RobotoTTF/RobotoCondensed-Bold.ttf");
        holder.sleepTimes.setTypeface(roboto_condensed_bold);
        holder.sleepDate.setTypeface(roboto_condensed_light);

        // set the header image
        holder.headerColor.setBackgroundColor(Color.parseColor(current.getHeader_color()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    SleepEntry current = data.get(position);

                    // show the edit dialog
                    // set the sleepAdapter to this class
                    // set the current sleepEntry to current
                    EditSleepDialog editSleepDialog = new EditSleepDialog();

                    // set all the params for the dialog
                    editSleepDialog.setSleepEntry(current);
                    editSleepDialog.setSleepAdapter(thisSleepAdapter);
                    editSleepDialog.setItemView(holder.itemView);
                    editSleepDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyCustomTheme);

                    // show the dialog
                    editSleepDialog.show(sleepListActivity.getSupportFragmentManager(), "hello");
                    return true;
                } catch (NullPointerException e) {
                    Log.e("Error: ","Could not load the edit dialog, null pointer");
                    return false;
                }
            }
        });


    }

    public int getPosition(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void delete(final int position) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<FeedbackEntry> feedbacks = realm.where(FeedbackEntry.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                data.deleteFromRealm(position); // Delete and remove object directly
                feedbacks.deleteFromRealm(position);
            }
        });
        // notify the adapter that an item was deleted
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sleepTimes;
        TextView sleepDate;
        ImageView faceImage;
        View headerColor;

        public MyViewHolder(final View itemView) {
            super(itemView);

            faceImage = (ImageView) itemView.findViewById(R.id.face_image);
            headerColor = (View) itemView.findViewById(R.id.color_label);
            sleepTimes = (TextView) itemView.findViewById(R.id.sleep_times);
            sleepDate = (TextView) itemView.findViewById(R.id.sleep_date);

        }
    }
}
