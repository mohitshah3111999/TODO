package com.company.mohitshah3111999.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ScheduleForCurrentDayActivity extends AppCompatActivity{

    int toHour, toMinute, fromHour, fromMinute;
    static ArrayList<DataHolder> arrayList, finalList;
    static CustomAdapter customAdapter;
    SQLiteDatabase sqLiteDatabase;

    public void gotoNext(View view){
        Calendar calendar = Calendar.getInstance();
        toHour = calendar.get(Calendar.HOUR_OF_DAY);
        toMinute = calendar.get(Calendar.MINUTE);
        fromHour = calendar.get(Calendar.HOUR_OF_DAY);
        fromMinute = calendar.get(Calendar.MINUTE);

//        TODO add 2 picker to add time from and time to.
        TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                fromHour = i;
                fromMinute = i1;
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleForCurrentDayActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Intent intent = new Intent(getApplicationContext(), AddDataActivity.class);
                        intent.putExtra("fromHour", fromHour);
                        intent.putExtra("fromMinute", fromMinute);
                        intent.putExtra("toHour", i);
                        intent.putExtra("toMinute", i1);
                        startActivity(intent);
                    }
                }, toHour, toMinute, false);
                timePickerDialog.show();
                Calendar calendar1= Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, fromHour);
                calendar1.set(Calendar.MINUTE, fromMinute);
                calendar1.set(Calendar.SECOND, 0);
                startAlarm(calendar1);
            }
        }, fromHour, fromMinute, false);
        timePickerDialog1.show();

        Toast.makeText(this, toHour + " : " + toMinute, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_for_current_day);

        sqLiteDatabase = this.openOrCreateDatabase("dates", MODE_PRIVATE, null);

        ListView listView = findViewById(R.id.schedule);
        arrayList = new ArrayList<>();


        try{
            Cursor cursor = sqLiteDatabase.rawQuery("select * from data where day = ?", new String[]{MainActivity.selectedDay},  null);
            int fromTime = cursor.getColumnIndex("fromTime");
            int toTime = cursor.getColumnIndex("toTime");
            int titleCol = cursor.getColumnIndex("title");
            int descriptionCol = cursor.getColumnIndex("description");
            cursor.moveToFirst();
            while (true){
                String fromTimeText = cursor.getString(fromTime);
                String toTimeText = cursor.getString(toTime);
                String title = cursor.getString(titleCol);
                String description = cursor.getString(descriptionCol);
                arrayList.add(new DataHolder(fromTimeText, toTimeText, title, description));
                cursor.moveToNext();
            }
        }catch (Exception e) {
            Log.i("Error", e.toString());
        }

        finalList = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        for(DataHolder item: arrayList){
            String itemTimeInString = item.fromTime;
            try {
                Date itemTime = dateFormat.parse(itemTimeInString);
                Date currentTime = new Date();
                if(dateFormat.format(itemTime).compareTo(dateFormat.format(currentTime)) < 0){
                    sqLiteDatabase.execSQL("delete from data where fromTime = ? and toTime = ? and title = ? and description = ?", new String[]{item.fromTime, item.toTime, item.taskTitle, item.taskDescription});
                }else{
                    finalList.add(item);
                }
            }catch (ParseException e){
                Log.i("ExceptionIs", e.toString());
            }
        }

        customAdapter = new CustomAdapter(this, finalList);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                intent.putExtra("currentItem", i);
                startActivity(intent);
            }
        });

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
                                sqLiteDatabase.execSQL("delete from data where title = ? and fromTime = ? and toTime = ?", new String[]{arrayList.get(i).taskTitle, arrayList.get(i).fromTime, arrayList.get(i).toTime});
                                arrayList.remove(i);
                                customAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
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
}
