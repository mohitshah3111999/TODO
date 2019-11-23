package com.company.mohitshah3111999.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Calendar;

import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.arrayList;
import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.customAdapter;
import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.finalList;

public class AddDataActivity extends AppCompatActivity {

    int toMinute, toHour, fromHour, fromMinute;
    SQLiteDatabase sqLiteDatabase;

    public static TextView time, title, description;

    public void clicked(View view){
        arrayList.add(new DataHolder(fromHour+":"+fromMinute, toHour+":"+toMinute, title.getText().toString(), description.getText().toString()));
        finalList.add(new DataHolder(fromHour+":"+fromMinute, toHour+":"+toMinute, title.getText().toString(), description.getText().toString()));
        customAdapter.notifyDataSetChanged();
        sqLiteDatabase.execSQL("insert into data values(?, ?, ?, ? , ?)", new String[]{MainActivity.selectedDay,
                fromHour+":"+fromMinute, toHour+":"+toMinute,
                title.getText().toString(), description.getText().toString()});
        finish();
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        sqLiteDatabase = this.openOrCreateDatabase("dates" ,MODE_PRIVATE, null);

        time = findViewById(R.id.timeReceived);
        title = findViewById(R.id.editText2);
        description = findViewById(R.id.editText3);

        Intent intent = getIntent();
        fromHour = intent.getIntExtra("fromHour", 0);
        fromMinute = intent.getIntExtra("fromMinute", 0);
        toHour = intent.getIntExtra("toHour", 0);
        toMinute= intent.getIntExtra("toMinute", 0);
        time.setText(" " + fromHour + ":" + fromMinute + "      \t\t\t\t" + toHour + ":" +toMinute);
    }
}
