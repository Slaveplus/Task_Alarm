package com.example.task_alarm;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.jbnu.se.taskalarm.MainViewModel;
import kr.ac.jbnu.se.taskalarm.model.Task;

public class Calendar extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MainViewModel viewModel;
    private final ListenerRegistration tasksRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        viewModel=new ViewModelProvider(this).get(MainViewModel.class);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        TextView task = (TextView) findViewById(R.id.task_view);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                task.setVisibility(View.VISIBLE);
                task.setText(String.format("%d/%d/%d",year,month+1,dayOfMonth));
            }
        });
        Date now = new Date(); //현재 날짜를 생성
        tasksRegistration = db.collectionGroup("tasks") //collectionGroup=파이어베이스의 아이디가 같은 컬렉션을 모아놓은 것, 여기서는 id가 tasks이다
                .whereGreaterThan("to", now) //이 코드를 활성화 하면 지금 날짜 기준으로 전의 과제들은 표현 X
                .orderBy("to")
                .orderBy("from")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }
                    ArrayList<kr.ac.jbnu.se.taskalarm.model.Task> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        tasks.add(document.toObject(Task.class));
                    }
                    _tasks.setValue(tasks); //_tasks에 tasks로 값 설정
                });
    }
}


//        db.collection("subjects2")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot document:task.getResult()){
//                                Log.d(TAG,document.getId()+"=>");
//
//                            }
//                        }
//                    }
//                });
