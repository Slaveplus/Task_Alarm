package com.example.task_alarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class drawerview extends AppCompatActivity {
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerview);

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),"로그아웃 합니다",Toast.LENGTH_SHORT);
                Intent intent=new Intent(drawerview.this,Login.class);
                startActivity(intent);
            }
        });
    }

}
