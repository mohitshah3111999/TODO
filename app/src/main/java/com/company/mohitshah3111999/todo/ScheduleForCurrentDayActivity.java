package com.company.mohitshah3111999.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class ScheduleForCurrentDayActivity extends AppCompatActivity{

    int toHour, toMinute, fromHour, fromMinute;
    static ArrayList<DataHolder> arrayList;
    static CustomAdapter customAdapter;
    SQLiteDatabase sqLiteDatabase;
    String day;
    static TextView newtextView;

    public void gotoNext(View view){
        Calendar calendar = Calendar.getInstance();
        toHour = calendar.get(Calendar.HOUR_OF_DAY);
        toMinute = calendar.get(Calendar.MINUTE);
        fromHour = calendar.get(Calendar.HOUR_OF_DAY);
        fromMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                fromHour = i;
                fromMinute = i1;
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleForCurrentDayActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(day + " " + fromHour + ":" + fromMinute);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(checkTime(date)) {
                            Intent intent = new Intent(getApplicationContext(), AddDataActivity.class);
                            intent.putExtra("currentDay", day);
                            intent.putExtra("fromHour", fromHour);
                            intent.putExtra("fromMinute", fromMinute);
                            intent.putExtra("toHour", i);
                            intent.putExtra("toMinute", i1);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ScheduleForCurrentDayActivity.this, "Enter Right time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, toHour, 0, DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();
                Calendar calendar1= Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, fromHour);
                calendar1.set(Calendar.MINUTE, fromMinute);
                calendar1.set(Calendar.SECOND, 0);
                startAlarm(calendar1);
            }
        }, fromHour, 0, DateFormat.is24HourFormat(getApplicationContext()));
        timePickerDialog1.show();
    }

    private boolean checkEligibleOrNot(int fromHour, int fromMinute) {
//        TODO we will create a method in which we will compare the times and will see that
//         the upcoming time is not colliding with any of the before.
        int count = 0;
        for(DataHolder item: arrayList){
            String endTime = item.toTime;
            String enteredTime = fromHour + ":" + fromMinute;
            Date d1 = null, d2 = null;
            try {
                d1 = new SimpleDateFormat("HH:mm").parse(endTime);
                d2 = new SimpleDateFormat("HH:mm").parse(enteredTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(d1.before(d2)){
                count = 1;
            }
        }
        if(count == 1){
            return false;
        }else {
            return true;
        }
    }

    private void makeInOrder() {
        if(arrayList.size() != 0){
            ArrayList<Date> fromTimeList = new ArrayList<>();
            for(DataHolder time: arrayList){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                try {
                    Date date = simpleDateFormat.parse(time.fromTime);
                    fromTimeList.add(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(fromTimeList);
            arrayList.clear();
            for(Date item: fromTimeList){
                String fromTimeString = new SimpleDateFormat("HH:mm").format(item);
                Cursor cursor = sqLiteDatabase.rawQuery("select * from data where day = '" + day + "'" + " and fromTime = '" + fromTimeString + "'",  null);
                int toTime = cursor.getColumnIndex("toTime");
                int titleCol = cursor.getColumnIndex("title");
                int descriptionCol = cursor.getColumnIndex("description");
                cursor.moveToFirst();
                try{
                    while (true){
                        String toTimeText = cursor.getString(toTime);
                        String title = cursor.getString(titleCol);
                        String description = cursor.getString(descriptionCol);
                        arrayList.add(new DataHolder(fromTimeString, toTimeText, title, description));
                        cursor.moveToNext();
                    }
                }catch (Exception e) {
                    Log.i("Error", e.toString());
                }
                customAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_for_current_day);

        Toolbar toolbar = findViewById(R.id.my_toolbar2);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newtextView = findViewById(R.id.textView5);

        sqLiteDatabase = this.openOrCreateDatabase("dates", MODE_PRIVATE, null);

        ListView listView = findViewById(R.id.schedule);
        arrayList = new ArrayList<>();
        customAdapter = new CustomAdapter(this, arrayList);


        day = getIntent().getStringExtra("currentDay");
        listView.setAdapter(customAdapter);

        addData();

        makeInOrder();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(ScheduleForCurrentDayActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i1) {
                                sqLiteDatabase.execSQL("delete from data where title = ? and fromTime = ? and toTime = ? and description = ? and day = ?",
                                        new String[]{arrayList.get(i).taskTitle, arrayList.get(i).fromTime, arrayList.get(i).toTime, arrayList.get(i).taskDescription, day});
                                arrayList.remove(i);
                                if(arrayList.size() != 0){
                                    newtextView.setVisibility(View.INVISIBLE);
                                }else{
                                    newtextView.setVisibility(View.VISIBLE);
                                }
                                customAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

//        TODO add onclick later on.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                intent.putExtra("currentItem", position);
                startActivity(intent);
            }
        });
    }

    private void addData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Cursor cursor = sqLiteDatabase.rawQuery("select * from data where day = '" + day + "'",  null);
        int fromTime = cursor.getColumnIndex("fromTime");
        int toTime = cursor.getColumnIndex("toTime");
        int titleCol = cursor.getColumnIndex("title");
        int descriptionCol = cursor.getColumnIndex("description");
        cursor.moveToFirst();
        try{
            while (true){
                String fromTimeText = cursor.getString(fromTime);
                String toTimeText = cursor.getString(toTime);
                String title = cursor.getString(titleCol);
                String description = cursor.getString(descriptionCol);
                Date otherDate = simpleDateFormat.parse(day + " " +fromTimeText);
                if(checkTime(otherDate)){
                    arrayList.add(new DataHolder(fromTimeText, toTimeText, title, description));
                }
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.i("Error", e.toString());
        }
        if(arrayList.size() != 0){
            newtextView.setVisibility(View.INVISIBLE);
        }else{
            newtextView.setVisibility(View.VISIBLE);
        }
        customAdapter.notifyDataSetChanged();
    }

    private boolean checkTime(Date otherDate) {
        /** format of other date will be like "dd/MM/yyyy HH:mm".
         */
        Date date = new Date();
        return otherDate.after(date);
    }


    private void startAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
