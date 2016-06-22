package com.example.james.watissleep;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;

public class SleepListActivity extends AppCompatActivity {

    private SleepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Realm realm = Realm.getDefaultInstance();

        // get the sleep entries
        RealmResults<SleepEntry> sleep_data = realm.where(SleepEntry.class).findAll().sort("wakeTime", Sort.DESCENDING);

        // init the recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new ScaleInTopAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // init the adapter
        adapter = new SleepAdapter(getApplicationContext(),sleep_data,this);
        recyclerView.setAdapter(adapter);

        // init the actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        // set the header image
        ImageView header = (ImageView) findViewById(R.id.screen_header);
        Glide.with(this).load("http://science-all.com/images/wallpapers/nice-wallpaper/nice-wallpaper-17.jpg").centerCrop().into(header);
    }
}
