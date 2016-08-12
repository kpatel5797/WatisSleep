package com.example.james.watissleep.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.james.watissleep.Adapters.SleepAdapter;
import com.example.james.watissleep.Database_Tables.SleepEntry;
import com.example.james.watissleep.R;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class SleepListActivity extends AppCompatActivity {

    private SleepAdapter adapter;
    // total amount scrolled in the page
    int totalScrolled = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_list);
        Realm realm = Realm.getDefaultInstance();

        // get the sleep entries
        RealmResults<SleepEntry> sleep_data = realm.where(SleepEntry.class).findAllSorted("wakeTime",Sort.DESCENDING);

        // init the recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new SlideInRightAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        SpannableString spannableString = new SpannableString("Sleep Entries");
        spannableString.setSpan(new TypefaceSpan("RobotoTTF/RobotoCondensed-Bold.ttf"), 0, spannableString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        // init the header
        RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader) findViewById(R.id.header);
        recyclerViewHeader.attachTo(recyclerView);
        ImageView header_view = (ImageView) findViewById(R.id.header_image);
        Glide.with(this)
                .load("http://i.imgur.com/KZZyESg.jpg")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(header_view);

        // init the adapter
        adapter = new SleepAdapter(getApplicationContext(),sleep_data,this);
        recyclerView.setAdapter(adapter);

        // colour of the actionBar
        final ColorDrawable actionBarColor = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        actionBarColor.setAlpha(255);

        // swipe to dismiss
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.delete(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // init the actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(actionBarColor);
        actionBar.setTitle(spannableString);
        actionBar.show();

        // empty_view imageView and textView
        ImageView emptyViewImage = (ImageView) findViewById(R.id.empty_view_image);
        TextView emptyViewText = (TextView) findViewById(R.id.empty_view_text);
        Typeface roboto_regular = Typeface.createFromAsset(getAssets(),"fonts/RobotoTTF/Roboto-Regular.ttf");
        emptyViewText.setTypeface(roboto_regular);

        // handle when the data is empty (show the emptyView)
        if (sleep_data.isEmpty()) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
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
