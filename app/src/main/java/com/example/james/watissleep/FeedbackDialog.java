package com.example.james.watissleep;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Calendar;

import io.realm.Realm;

/**
 * Created by James on 16-06-01.
 */
public class FeedbackDialog extends AppCompatDialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        View view = inflator.inflate(R.layout.wake_feedback_fragment,null);
        getDialog().setTitle("How was your sleep?");
        setCancelable(false);
        final Calendar c = Calendar.getInstance();

        ImageView bad_img = (ImageView) view.findViewById(R.id.bad_img);
        ImageView ok_img = (ImageView) view.findViewById(R.id.ok_img);
        ImageView good_img = (ImageView) view.findViewById(R.id.good_img);
        final Realm realm = Realm.getDefaultInstance();

        bad_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do some db transaction
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        FeedbackEntry fe = realm.createObject(FeedbackEntry.class);
                        fe.setFeedbackString("bad");
                        fe.setFeedbackDate(c.getTimeInMillis());
                    }
                });
                dismiss();
            }
        });

        ok_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do some db transaction
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        FeedbackEntry fe = realm.createObject(FeedbackEntry.class);
                        fe.setFeedbackString("ok");
                        fe.setFeedbackDate(c.getTimeInMillis());
                    }
                });
                dismiss();
            }
        });

        good_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do some db transaction
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        FeedbackEntry fe = realm.createObject(FeedbackEntry.class);
                        fe.setFeedbackString("good");
                        fe.setFeedbackDate(c.getTimeInMillis());
                    }
                });
                dismiss();
            }
        });
        return view;

    }
}
