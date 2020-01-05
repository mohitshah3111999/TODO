package com.company.mohitshah3111999.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.arrayList;
import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.customAdapter;
import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.newtextView;

public class AddDataActivity extends AppCompatActivity {

    int toMinute, toHour, fromHour, fromMinute;
    SQLiteDatabase sqLiteDatabase;

    public static TextView time, title, description;
    String fromHourString, fromMinuteString, toHourString, toMinuteString;

    public void clicked(View view){
        String fromTime = fromHourString + ":" + fromMinuteString;
        String toTime = toHourString + ":" + toMinuteString;
        String titleInString = title.getText().toString();
        String descriptionInString = description.getText().toString();
        String day = getIntent().getStringExtra("currentDay");
        arrayList.add(new DataHolder(fromTime, toTime, title.getText().toString(), description.getText().toString()));
        newtextView.setVisibility(View.INVISIBLE);
        customAdapter.notifyDataSetChanged();
        sqLiteDatabase.execSQL("insert into data values ('" + day + "', '"
        + fromTime + "', '" + toTime + "', '" + titleInString +
                "', '" + descriptionInString + "')");
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        Toolbar toolbar = findViewById(R.id.my_toolbar3);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sqLiteDatabase = this.openOrCreateDatabase("dates" ,MODE_PRIVATE, null);

        time = findViewById(R.id.timeReceived);
        title = findViewById(R.id.editText2);
        description = findViewById(R.id.editText3);

        Intent intent = getIntent();
        fromHour = intent.getIntExtra("fromHour", 0);
        fromMinute = intent.getIntExtra("fromMinute", 0);
        toHour = intent.getIntExtra("toHour", 0);
        toMinute= intent.getIntExtra("toMinute", 0);

        fromHourString = String.valueOf(fromHour);
        fromMinuteString = String.valueOf(fromMinute);
        toHourString = String.valueOf(toHour);
        toMinuteString = String.valueOf(toMinute);
        if(fromHour < 10){
            fromHourString = "0" + fromHour;
        }
        if(fromMinute < 10){
            fromMinuteString = "0" + fromMinute;
        }
        if(toHour < 10){
            toHourString = "0" + toHour;
        }

        if(toMinute < 10){
            toMinuteString = "0" + toMinute;
        }

//        TODO make 2 dates: fromDate and toDate and then compare them and
//         if the difference between them is less than 60 minutes, show it to user to not to add such activity.
        Date fromDate = null, toDate = null;

        try {
            fromDate = new SimpleDateFormat("HH:mm").parse(fromHour + ":" + fromMinute);
            toDate = new SimpleDateFormat("HH:mm").parse(toHour + ":" + toMinute);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffMins = Math.abs((fromDate.getTime() - toDate.getTime())/60000);

        if(fromDate.after(toDate)) {
            Toast.makeText(this, "Please Create Proper Activity", Toast.LENGTH_SHORT).show();
            finish();
        }else if(diffMins < 60){
            Toast.makeText(this, "We recommend You to create at least of 1 hour activity", Toast.LENGTH_SHORT).show();
            finish();
        }
        time.setText(" " + fromHourString + ":" + fromMinuteString + "      \t\t\t\t" + toHourString + ":" +toMinuteString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
