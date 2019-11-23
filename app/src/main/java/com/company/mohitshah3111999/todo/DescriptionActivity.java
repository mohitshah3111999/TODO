package com.company.mohitshah3111999.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.arrayList;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent intent = getIntent();
        int position = intent.getIntExtra("currentItem", 0);

        TextView textView = findViewById(R.id.editText);
        textView.setText(arrayList.get(position).taskDescription);
        if(textView.getText().toString().length() == 0){
            Toast.makeText(this, "There is no description for the task", Toast.LENGTH_SHORT).show();
        }
    }
}
