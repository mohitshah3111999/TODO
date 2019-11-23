package com.company.mohitshah3111999.todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<DataHolder> arrayList;
    TextView fromTime, toTime, taskTitle;

    CustomAdapter(Context context, ArrayList<DataHolder> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.activity_holder, viewGroup, false);
        fromTime = view.findViewById(R.id.fromTime);
        toTime = view.findViewById(R.id.toTime);
        taskTitle = view.findViewById(R.id.taskTitle);
        fromTime.setText(arrayList.get(i).fromTime);
        toTime.setText(arrayList.get(i).toTime);
        taskTitle.setText(arrayList.get(i).taskTitle);

        return view;
    }
}
