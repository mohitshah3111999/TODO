package com.company.mohitshah3111999.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

public class CalenderActivity extends AppCompatActivity {

    CalendarView calendarView;
    String date;

    public void dateSelector(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("date", date);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                if(i2<10){
                    date = "0" + i2 + "/" + (i1+1) + "/" + i;
                }else{
                    date = i2 + "/" + (i1+1) + "/" + i;
                }
                Toast.makeText(CalenderActivity.this, i2+"/"+(i1+1)+"/"+i, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
