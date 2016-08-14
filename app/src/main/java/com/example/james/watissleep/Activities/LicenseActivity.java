package com.example.james.watissleep.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.james.watissleep.Adapters.ExpListAdapter;
import com.example.james.watissleep.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LicenseActivity extends AppCompatActivity {

    List<String> libs;
    Map<String, List<String>> description;


    ExpandableListView mListView;
    ExpandableListAdapter eListAdapter;
    //private ArrayAdapter mArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_list);

        mListView = (ExpandableListView) findViewById(R.id.list_license);
        data();
        eListAdapter = new ExpListAdapter(this, libs, description);
        mListView.setAdapter(eListAdapter);

        /*
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, myListLicense);
        if (mListView != null) {
            mListView.setAdapter(mArrayAdapter);
        }
        */

        setupActionBar();
    }

    public void data()
    {
        libs = new ArrayList<>();
        description = new HashMap<>();

        libs.add("Fancybuttons");
        libs.add("MPAndroidChart");
        libs.add("MaterialSeekBarPreference");
        libs.add("Glide");
        libs.add("RecyclerView Animators");
        libs.add("RecyclerViewHeader");
        libs.add("WilliamChart");

        List<String> FB = new ArrayList<>();
        List<String> MP = new ArrayList<>();
        List<String> Bar = new ArrayList<>();
        List<String> Gl = new ArrayList<>();
        List<String> RV = new ArrayList<>();
        List<String> RVH = new ArrayList<>();
        List<String> WC = new ArrayList<>();

        FB.add("Licensed under MIT License");
        MP.add("Copyright 2016 Philipp Jahoda\n" +
                "\n" +
                "Licensed under the Apache License v2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at\n" +
                "\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.");
        Bar.add("Licensed under Apache License v2.0");
        Gl.add("Licensed under BSD License, MIT License and Apache License v2.0");
        RV.add("Copyright 2015 Wasabeef\n" +
                "\n" +
                "Licensed under the Apache License v2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        RVH.add("Copyright 2015 Bartosz Lipiński\n" +
                "\n" +
                "Licensed under the Apache License v2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        WC.add("Copyright 2015 Bartosz Lipiński\n" +
                "\n" +
                "Licensed under the Apache License v2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");

        description.put(libs.get(0), FB);
        description.put(libs.get(1), MP);
        description.put(libs.get(2), Bar);
        description.put(libs.get(3), Gl);
        description.put(libs.get(4), RV);
        description.put(libs.get(5), RVH);
        description.put(libs.get(6), WC);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
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
