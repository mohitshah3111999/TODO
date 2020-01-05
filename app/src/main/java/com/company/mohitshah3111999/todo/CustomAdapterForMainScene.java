package com.company.mohitshah3111999.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CustomAdapterForMainScene extends BaseAdapter {
    Context context;
    ArrayList<String> list;

    public CustomAdapterForMainScene(Context context, ArrayList<String> arrayList){
        this.list = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.DateMain);
        textView.setText(list.get(position));
        return convertView;
    }
}
