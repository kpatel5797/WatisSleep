package com.example.james.watissleep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import mehdi.sakout.fancybuttons.FancyButton;

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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // get the current sleep entry
        SleepEntry current = data.get(position);

        // get the sleepTime and wakeTime
        Date sleep_date = new Date(current.getSleepTime());
        Date wake_date = new Date(current.getWakeTime());

        // date/time format
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd       hh:mm aa");


        String sleep_display = format.format(sleep_date);
        String wake_display = format.format(wake_date);

        // set the text on the card
        holder.sleepTime.setText(sleep_display);
        holder.wakeTime.setText(wake_display);

        // set the header image
        if (data.get(position).getHeader_image() == null) {
            holder.headerImage.setVisibility(View.GONE);
        } else {
            Glide.with(holder.headerImage.getContext()).load(current.getHeader_image()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.headerImage);
        }


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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                data.deleteFromRealm(position); // Delete and remove object directly
            }
        });
        // notify the adapter that an item was deleted
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sleepTime;
        TextView wakeTime;
        FancyButton deleteButton;
        FancyButton editButton;
        ImageView headerImage;

        public MyViewHolder(final View itemView) {
            super(itemView);
            deleteButton = (FancyButton) itemView.findViewById(R.id.btn_delete_sleep_entry);
            editButton = (FancyButton) itemView.findViewById(R.id.btn_edit_sleep_entry);

            // delete the entry when the delete button on the card is clicked
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the current sleep entry
                    SleepEntry current = data.get(getAdapterPosition());

                    // show the edit dialog
                    // set the sleepAdapter to this class
                    // set the current sleepEntry to current
                    EditSleepDialog editSleepDialog = new EditSleepDialog();

                    // set all the params for the dialog
                    editSleepDialog.setSleepEntry(current);
                    editSleepDialog.setSleepAdapter(thisSleepAdapter);
                    editSleepDialog.setItemView(itemView);

                    // show the dialog
                    editSleepDialog.show(sleepListActivity.getSupportFragmentManager(),"hello");
                }
            });

            headerImage = (ImageView) itemView.findViewById(R.id.recycler_header);
            sleepTime = (TextView) itemView.findViewById(R.id.sleep_Time);
            wakeTime = (TextView) itemView.findViewById(R.id.wake_Time);

        }
    }
}
