package com.company.mohitshah3111999.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ArrayList<String> dates;
    static String selectedDay;
    static ArrayList<String> finalData;

    public void showCalender(View view){
        Intent intent = new Intent(getApplicationContext(), CalenderActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer);

        final SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("dates", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists newdates(date text primary key)");
        sqLiteDatabase.execSQL("create table if not exists data(day text, fromTime text, toTime text, title text, description text)");

        NavigationView navigationView = findViewById(R.id.navigator);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.listView);
        dates = new ArrayList<>();

//      TODO create SQL query to select a particular item from databse, we will receive the date from the calender.
        String s;
        if(getIntent()!= null){
            Intent intent = getIntent();
            if(intent.getStringExtra("date") != null) {
                s = intent.getStringExtra("date");
                try {
                    sqLiteDatabase.execSQL("insert into newdates values(?)", new String[]{s});
                }catch (Exception e){
                    Log.i("Hello", "Here");
                }
                dates.add(s);
            }
        }


        Cursor cursor = sqLiteDatabase.rawQuery("select * from newdates", null);
        finalData = new ArrayList<>();
        int definer = cursor.getColumnIndex("date");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        cursor.moveToFirst();
        try {
            while (true) {
                String answer = cursor.getString(definer);
                Date date = new Date();
                String nDate = dateFormat.format(date);
                Date date1 = dateFormat.parse(answer);
                Date date2 = dateFormat.parse(nDate);
                assert date1 != null;
                assert date2 != null;
                Log.i("comparison", String.valueOf(date2.compareTo(date1)));
                Log.i("Date1", date1.toString());
                Log.i("Date2", date2.toString());
                if(date2.compareTo(date1) <= 0) {
                    finalData.add(answer);
                }
                cursor.moveToNext();
            }
        }catch (Exception e){
            Log.i("errorIs", e.toString());
        }

//        TODO Rearrange the list items, so that particular date remains at particular place.
        Collections.sort(finalData);

        Date today = new Date();
        try {
            if (finalData.get(0).equals(dateFormat.format(today))) {

                Log.i("Yes", "It's matching");
            }
        }catch (Exception e){
            Log.i("ExceptionIs", e.toString());
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalData);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ScheduleForCurrentDayActivity.class);
                intent.putExtra("date", finalData.get(i));
                selectedDay = finalData.get(i);
                startActivity(intent);
                Toast.makeText(MainActivity.this, finalData.get(i), Toast.LENGTH_SHORT).show();
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
                                sqLiteDatabase.execSQL("delete from newdates where date = ?", new String[]{finalData.get(i)});
                                sqLiteDatabase.execSQL("delete from data where day = ?", new String[]{finalData.get(i)});
                                finalData.remove(i);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case (R.id.changeName):
                return true;
        }
        return false;
    }
}
