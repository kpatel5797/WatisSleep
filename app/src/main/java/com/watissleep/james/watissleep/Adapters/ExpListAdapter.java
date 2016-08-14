package com.watissleep.james.watissleep.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.watissleep.james.watissleep.R;

import java.util.List;
import java.util.Map;

/**
 * Created by James on 2016-08-13.
 */
public class ExpListAdapter extends BaseExpandableListAdapter {

    Context context;
    List<String> libs;
    Map<String, List<String>> description;

    public ExpListAdapter(Context context, List<String> libs, Map<String, List<String>> description) {
        this.context = context;
        this.libs = libs;
        this.description = description;
    }

    @Override
    public int getGroupCount() {
        return libs.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return description.get(libs.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return libs.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return description.get(libs.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String libName = (String) getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.libs_txt_parent,null);
        }

        TextView txt_parent = (TextView) convertView.findViewById(R.id.txt_parent);
        txt_parent.setText(libName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String descpName = (String) getChild(groupPosition, childPosition);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.libs_txt_child,null);
        }

        TextView txt_child = (TextView) convertView.findViewById(R.id.txt_child);
        txt_child.setText(descpName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
