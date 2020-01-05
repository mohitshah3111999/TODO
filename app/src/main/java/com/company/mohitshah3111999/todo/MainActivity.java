package com.company.mohitshah3111999.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    ArrayList<String> dates;
    SQLiteDatabase sqLiteDatabase;
    CustomAdapterForMainScene customAdapterForMainScene;
    TextView textView;
//    TODO date format will be "dd/MM/yyyy".

    public void showCalender(View view){
        final Calendar calendar = Calendar.getInstance();
        final int[] myear = {calendar.get(Calendar.YEAR)};
        final int[] mmonth = {calendar.get(Calendar.MONTH)};
        final int[] mday = {calendar.get(Calendar.DAY_OF_MONTH)};

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String currentDateString = mday[0] + "/" + (mmonth[0] + 1) + "/" + myear[0];
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                Date dateInCurrent = null;
                Date dateInSelected = null;
                try {
                    dateInCurrent = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateString);
                    dateInSelected = new SimpleDateFormat("dd/MM/yyyy").parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dateInSelected.compareTo(dateInCurrent) >= 0){
                    Date passedDate = null;
                    try {
                        passedDate = new SimpleDateFormat("dd/MM/yyyy").parse(dayOfMonth + "/" + (month + 1) + "/" + year);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(checkIfDateAlreadyPresentOrNot(passedDate)) {
                        sqLiteDatabase.execSQL("insert into newdates values("
                                + dayOfMonth
                                + ", " + (month + 1) +
                                ", " + year + ")");
                        dates.add(dayOfMonth + "/" + (month + 1) + "/" + year);
                        try {
                            makeInOrder();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        textView.setVisibility(View.INVISIBLE);
                        customAdapterForMainScene.notifyDataSetChanged();
                    }
                }
            }
        }, myear[0], mmonth[0], mday[0]);
        datePickerDialog.show();
    }

    private boolean checkIfDateAlreadyPresentOrNot(Date s) {
        String ndate = new SimpleDateFormat("dd/MM/yyyy").format(s);
        if(dates.contains(ndate)) {
            Toast.makeText(this, "Date Is Already present", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        textView = findViewById(R.id.textView4);

        sqLiteDatabase = this.openOrCreateDatabase("dates", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists newdates(day integer, month integer, year integer)");
        sqLiteDatabase.execSQL("create table if not exists data(day text, fromTime text, toTime text, title text, description text)");


        ListView listView = findViewById(R.id.listView);
        dates = new ArrayList<>();
        customAdapterForMainScene = new CustomAdapterForMainScene(getApplicationContext(), dates);
        listView.setAdapter(customAdapterForMainScene);

        addData();

        try {
            makeInOrder();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ScheduleForCurrentDayActivity.class);
                intent.putExtra("currentDay", dates.get(i));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i1) {
                                String date = dates.get(i);
                                String day = date.split("/")[0];
                                String month = date.split("/")[1];
                                String year = date.split("/")[2];
                                sqLiteDatabase.execSQL("delete from newdates where day = " + day +
                                        " and month = " + month + " and year = " + year);
                                sqLiteDatabase.execSQL("delete from data where day = '" + date + "'");
                                dates.remove(i);
                                if(dates.size() == 0) {
                                    textView.setVisibility(View.VISIBLE);
                                }
                                customAdapterForMainScene.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

    }


    private void makeInOrder() throws ParseException {
//        Method to make all items in order.
        ArrayList<Date> newDates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for(String date: dates){
            newDates.add(simpleDateFormat.parse(date));
        }
        Collections.sort(newDates);
        dates.clear();
        for(Date getter: newDates){
            String upcomingDate = simpleDateFormat.format(getter);
            dates.add(upcomingDate);
        }
        customAdapterForMainScene.notifyDataSetChanged();
    }

    private void addData() {
//        Method to add data in arrayList.
        Cursor cursor = sqLiteDatabase.rawQuery("select * from newdates", null);
        int day = cursor.getColumnIndex("day");
        int month = cursor.getColumnIndex("month");
        int year = cursor.getColumnIndex("year");
        cursor.moveToFirst();

        try{
            while (true){
                int obtainDay = cursor.getInt(day);
                int obtainMonth = cursor.getInt(month);
                int obtainYear = cursor.getInt(year);
                String newday = obtainDay + "/" + obtainMonth + "/" + obtainYear;
                if(dateChecker(obtainDay, obtainMonth, obtainYear)) {
                    dates.add(newday);
                }else{
                    sqLiteDatabase.execSQL("delete from newdates where day = " + obtainDay +
                    " and month = " + obtainMonth + " and year = " + obtainYear);
                    sqLiteDatabase.execSQL("delete from data where day = '" + newday + "'");
                }
                cursor.moveToNext();
            }
        }catch (Exception e){
            Log.i("ErrorIs", e.toString());
        }
        customAdapterForMainScene.notifyDataSetChanged();
        if(dates.size() != 0){
            textView.setVisibility(View.INVISIBLE);
        }
    }


    private boolean dateChecker(int obtainDay, int obtainMonth, int obtainYear) {
//        Method to check dates are valid or not.
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        String obtainDateString = obtainDay + "/" + obtainMonth + "/" + obtainYear;
        String currentDateInString = currentDay + "/" + currentMonth + "/" + currentYear;
        Date currentDate = null;
        Date obtainDate = null;
        try {
            obtainDate = new SimpleDateFormat("dd/MM/yyyy").parse(obtainDateString);
            currentDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentDateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(obtainDate.compareTo(currentDate) >= 0){
            return true;
        }
        return false;
    }
}
