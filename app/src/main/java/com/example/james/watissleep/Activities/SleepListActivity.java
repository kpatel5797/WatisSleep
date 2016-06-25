package com.example.james.watissleep.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.james.watissleep.Adapters.SleepAdapter;
import com.example.james.watissleep.Database_Tables.SleepEntry;
import com.example.james.watissleep.R;

import java.util.Calendar;

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

        // init the header
        RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader) findViewById(R.id.header);
        recyclerViewHeader.attachTo(recyclerView);
        ImageView header_view = (ImageView) findViewById(R.id.header_image);
        Glide.with(this)
                .load("http://images.all-free-download.com/images/wallpapers_large/interesting_3d_shapes_wallpaper_abstract_3d_wallpaper_151.jpg")
                .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(header_view);

        // init the adapter
        adapter = new SleepAdapter(getApplicationContext(),sleep_data,this);
        recyclerView.setAdapter(adapter);

        // init the actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        // empty_view imageView and textView
        ImageView emptyViewImage = (ImageView) findViewById(R.id.empty_view_image);
        TextView emptyViewText = (TextView) findViewById(R.id.empty_view_text);
        Typeface roboto_regular = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Regular.ttf");
        emptyViewText.setTypeface(roboto_regular);

        // handle when the data is empty (show the emptyView)
        if (sleep_data.isEmpty()) {
            header_view.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyViewImage.setVisibility(View.VISIBLE);
            emptyViewImage.setAlpha(0.3f);
            emptyViewText.setVisibility(View.VISIBLE);
            emptyViewText.setAlpha(0.3f);
        } else {
            emptyViewImage.setVisibility(View.GONE);
            emptyViewText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // check if the hour is less than 6:00pm and more than 9:00am
        Calendar time = Calendar.getInstance();
        // change from night theme to day theme at 9:00am and from day to night at 6:00pm
        if (time.get(Calendar.HOUR_OF_DAY) < 9 || time.get(Calendar.HOUR_OF_DAY) >= 18) {
            // change the background colour
            recyclerView.setBackgroundColor(Color.parseColor("#424242"));
        }
    }
}
