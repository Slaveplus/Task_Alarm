package com.example.task_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;

public class manualADD extends AppCompatActivity {
    TextView dateText;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add);

        dateText=findViewById(R.id.deadline);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyy.MM.dd");
        Button datePickerBtn = findViewById(R.id.date_picker);
        dateText.setText(format1.format(c.getTime()));

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int pYear = calendar.get(Calendar. YEAR);
                int pMonth = calendar.get(Calendar. MONTH);
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(manualADD.this,
                        new DatePickerDialog.OnDateSetListener(){
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                                month = month+1;
                                String date= year + "." + month + "." + day;
                                dateText.setText(date);
                            }
                        }, pYear, pMonth, pDay);
                datePickerDialog.show();
            }
        });
    }
}