package kr.ac.jbnu.se.taskalarm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.task_alarm.R;
import com.example.task_alarm.databinding.ActivityMainBinding;

import kr.ac.jbnu.se.taskalarm.adapter.SubjectAdapter;

public class MainActivity extends AppCompatActivity {// AppCompatActivity를 상속받음

    private ActivityMainBinding binding; //뷰를 쉽게 제어할수있는 바인딩 ex) recycler_item.xml -> RecyclerItemBinding
    private MainViewModel viewModel;
    private SubjectAdapter adapter = new SubjectAdapter(); //Adapter는 뷰와 데이터를 연결하는 브릿지 역할 / 클래스 객체 변수 =new 클래스()
    private DrawerLayout drawerLayout;
    private View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //onCreate는 콜백 메소드, Activity생성 단계에 한번 실행되는 메소드이다.
        super.onCreate(savedInstanceState); //savedInstance=변경된 값을 유지시켜줌, super=부모클래스의 필드값이나 메소드를 직접 부를 때 사용

        binding = ActivityMainBinding.inflate(getLayoutInflater()); //액티비티 메인 바인딩에서 레이아웃 인플레이터를 가져옴, 인플레이터=view를 만드는 가장 기본적인 방법, 리사이클러 뷰를 만들때 주로사용
        viewModel = new ViewModelProvider(this).get(MainViewModel.class); //ViewModelProvider는 ViewModel을 생성할 때 어떤 객체를 인자로 전달해줘야할지 모르니까, 아래와 같은 Factory 클래스를 정의해서 어떤 인자를 생성자에 전달할 지 정의해줘야 한다

        setContentView(binding.getRoot()); //괄호 안에있는 레이아웃을 보여주기 위해 setContentView사용 , getRoot()를 사용해 레이아웃 내 최상위 뷰의 인스턴스를 얻는다.

        adapter.setOnCheckedChangeListener(task -> viewModel.setDone(task, !task.done)); //setOnCheckedChangeListener는 사용자가 체크박스의 상태를 변경했을때 반응하는 이벤트 처리,
        binding.recyclerView.setAdapter(adapter); //어댑터 설정

        viewModel.subjects.observe(this, subjects -> { //observe를 이용해 데이터의 변경을 관찰
            adapter.submitList(subjects);
        });

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_View); //여기서부터는 드로어레이아웃
        drawerView=(View)findViewById(R.id.drawerView);
        drawerLayout.setDrawerListener(listener);

        View.OnClickListener listener=new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(drawerView);
            }
        };
        ImageButton drawbtn= (ImageButton) findViewById(R.id.imageButton);
        drawbtn.setOnClickListener(listener);
    }
    DrawerLayout.DrawerListener listener=new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
}






