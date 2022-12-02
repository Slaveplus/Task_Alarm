package com.example.task_alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import kr.ac.jbnu.se.taskalarm.MainActivity;

public class Login extends AppCompatActivity {
    EditText Id,pwd;
    Button log;
    public static Context context_main;
    public String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context_main=this;
        Id=findViewById(R.id.Id);
        pwd=findViewById(R.id.password);
        log=findViewById(R.id.login);
        
        log.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               name = Id.getText().toString();
               String passwd = pwd.getText().toString();
               if (name.equals("") || passwd.equals("")) {
                   Toast.makeText(getApplicationContext(), "학번과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
               }
               else {
                    String[] field1 = new String[2];
                    String[] data1 = new String[2];
                    field1[0] = "name";
                    field1[1] = "passwd";
                    data1[0]=name;
                    data1[1]=passwd;
                    PutData putdata = new PutData("http://ubuntu-hanwn.p-e.kr/jimin/crawl.php", "POST", field1, data1);
                    if(putdata.startPut()) {
                        Toast.makeText(getApplicationContext(), "로그인 중", Toast.LENGTH_LONG).show();
                        if (putdata.onComplete()) {
                            String result = putdata.getResult();
                            if (result.equals(""))
                            {
                                Toast.makeText(getApplicationContext(), "로그인에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            }
        });
    }
}
