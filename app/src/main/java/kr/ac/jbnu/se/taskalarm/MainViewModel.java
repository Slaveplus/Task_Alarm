package kr.ac.jbnu.se.taskalarm;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import kr.ac.jbnu.se.taskalarm.model.Subject;
import kr.ac.jbnu.se.taskalarm.model.Task;

public class MainViewModel extends ViewModel { //ViewModel로 부터 상속받음

    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); //final은 지역변수를 상수화 시켜줌, 지역변수는 사용되면 사라짐 그걸 방지
    private final MutableLiveData<List<Subject>> _subjects = new MutableLiveData<>(); //MutableLiveData는 메인의 observe와 연결됨
    private final MutableLiveData<List<Task>> _tasks = new MutableLiveData<>();
    MediatorLiveData<List<Subject>> subjects = new MediatorLiveData<>(); //mediatorlivedata는 여러개의 라이브 데이터를 다시 매핑할수있다

    private final ListenerRegistration subjectListenerRegistration; //파이어베이스 리스너 등록
    private final ListenerRegistration tasksRegistration;

    public MainViewModel() {
        initData();

        subjectListenerRegistration = db.collection("subjects2") //subjects트리의 데이터를 수집
                .orderBy("name") //이름순으로 정렬
                .addSnapshotListener((value, error) -> { //이 코드가 작동될때의 데이터베이스 상태를 캡쳐하는 리스너등록, 값 and 에러
                    if (error != null) { //에러가 널값이 아니라면
                        error.printStackTrace(); // 에러를 띄워줌 그리고 리턴
                        return;
                    }

                    ArrayList<Subject> subjects = new ArrayList<>(); //ArrayList는 자바에서 사용하는 동적리스트임, 크기가 변함, 리스트를 상속받아서 사용
                    for (DocumentSnapshot document : value.getDocuments()) {
                        subjects.add(document.toObject(Subject.class)); //subject클래스를 가져옴
                    }
                    _subjects.setValue(subjects); //_subjects의 리스트에 subjects를 추가함
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
                    ArrayList<Task> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        tasks.add(document.toObject(Task.class));
                    }
                    _tasks.setValue(tasks); //_tasks에 tasks로 값 설정
                });

        subjects.addSource(_subjects, subjects -> {
            LinkedHashMap<String, Subject> mappedSubjects = new LinkedHashMap<>(); //LinkedHashmap의 key와 value의 타입을 지정한다. 여기서는 key=string / value type=subject
            for (Subject subject : subjects) { //과목의 끝까지 for문
                mappedSubjects.put(subject.documentId, subject); //파이어베이스의 트리 id를 지정 / value=subject
            }

            List<Task> tasks = _tasks.getValue();
            if (tasks == null) return; //얻어온 tasks가 null이면 아무것도 리턴X / 반환타입이 객체라 써줌

            for (Task task : tasks) {
                Objects.requireNonNull(mappedSubjects.get(task.parentId)).tasks.add(task); //명시적으로 nonnull이여야 함을 표현할때 Objects.requirenonnull을 씀 중요X / 결국 mappedSubjects에 값을 추가하는 것
            }

            this.subjects.setValue(new ArrayList<>(mappedSubjects.values())); //subjects 리스트에 값 추가
        });

        subjects.addSource(_tasks, tasks -> { //subjects 리스트에 소스 추가
            List<Subject> subjects = _subjects.getValue(); //_subjects의 값을 가져와서 리스트 subjects에 넣기
            if (subjects == null) return;

            LinkedHashMap<String, Subject> mappedSubjects = new LinkedHashMap<>();
            for (Subject subject : subjects) {
                mappedSubjects.put(subject.documentId, subject.copy());
            }

            for (Task task : tasks) {
                Objects.requireNonNull(mappedSubjects.get(task.parentId)).tasks.add(task);
            }

            this.subjects.setValue(new ArrayList<>(mappedSubjects.values()));
        });
    }

    @Override
    protected void onCleared() {
        subjectListenerRegistration.remove();
        tasksRegistration.remove();

        super.onCleared();
    }

    public void setDone(Task task, boolean checked) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("done", checked);

        db.collection("subjects2")
                .document(task.parentId)
                .collection("tasks")
                .document(task.documentId)
                .update(data);
    }

    @SuppressWarnings("SameParameterValue")
    private Date createDate(int year, int month, int day, int hourOfDay, int minute) {
        assert (month >= 1 && month <= 12);
        assert (day >= 1 && day <= 31);
        assert (hourOfDay >= 0 && hourOfDay <= 23);
        assert (minute >= 0 && minute <= 59);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hourOfDay, minute);
        return calendar.getTime();
    }

    //여기서 밑으로 부터는 만약 초기 데이터 없다면 보여줄게 없어서 넣어놓은 더미데이터들
    private void initData() {
        db.collection("subjects2")
                .count()
                .get(AggregateSource.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                        return;
                    }

                    AggregateQuerySnapshot snapshot = task.getResult();
                    long count = snapshot.getCount();
                    if (count != 0) return;

                    HashMap<Subject, List<Task>> items = new HashMap<>();

                    String id = db.collection("subjects2").document().getId();
                    items.put(new Subject(id, "리눅스시스템"), Arrays.asList(
                                    new Task(id, "VI 실습화면 캡쳐",
                                            createDate(2022, 11, 7, 17, 19),
                                            createDate(2022, 11, 14, 16, 0))
                            )
                    );

                    id = db.collection("subjects2").document().getId();
                    items.put(new Subject(id, "C++ 프로그래밍"), Arrays.asList(
                                    new Task(id, "과제8",
                                            createDate(2022, 11, 10, 19, 38),
                                            createDate(2022, 11, 14, 0, 0)),
                                    new Task(id, "과제7",
                                            createDate(2022, 10, 20, 11, 7),
                                            createDate(2022, 10, 24, 0, 0)),
                                    new Task(id, "과제6",
                                            createDate(2022, 10, 12, 18, 13),
                                            createDate(2022, 10, 17, 23, 59)),
                                    new Task(id, "과제5",
                                            createDate(2022, 10, 6, 10, 56),
                                            createDate(2022, 10, 10, 23, 59)),
                                    new Task(id, "과제4",
                                            createDate(2022, 9, 28, 10, 11),
                                            createDate(2022, 10, 3, 23, 59)),
                                    new Task(id, "과제3",
                                            createDate(2022, 9, 21, 11, 11),
                                            createDate(2022, 9, 26, 23, 59)),
                                    new Task(id, "과제2",
                                            createDate(2022, 9, 14, 9, 6),
                                            createDate(2022, 9, 19, 23, 59)),
                                    new Task(id, "과제1",
                                            createDate(2022, 9, 6, 10, 49),
                                            createDate(2022, 9, 12, 23, 59))
                            )
                    );

                    id = db.collection("subjects2").document().getId();
                    items.put(new Subject(id, "창의적공학설계"), Arrays.asList(
                                    new Task(id, "(제7차 과제) (팀 과제) 추가 수행한 내용 발표 파일(.ppt) 업로드하기",
                                            createDate(2022, 11, 15, 14, 0),
                                            createDate(2022, 11, 20, 0, 0)),
                                    new Task(id, "(제6차 과제) (팀 과제) 추가 수행한 내용 발표 파일(.ppt) 업로드하기",
                                            createDate(2022, 10, 30, 22, 0),
                                            createDate(2022, 11, 7, 0, 0)),
                                    new Task(id, "(제5차 과제) (팀 과제) 추가 수행한 내용 발표 파일(.ppt) 업로드하기",
                                            createDate(2022, 10, 11, 10, 0),
                                            createDate(2022, 10, 16, 0, 0)),
                                    new Task(id, "(제4차 과제) (팀 과제) 문제정의문 작성 과정에 대한 발표자료 업로드하기",
                                            createDate(2022, 9, 25, 20, 0),
                                            createDate(2022, 10, 5, 12, 0)),
                                    new Task(id, "(제3차 과제) (팀 과제) 브레인스토밍한 결과 업로드 및 발표하기",
                                            createDate(2022, 9, 25, 20, 0),
                                            createDate(2022, 9, 28, 12, 0)),
                                    new Task(id, "(제2차 과제) (개인 과제) (예습쪽지 제출) 3장 기초 창의적 발상 도구",
                                            createDate(2022, 9, 13, 23, 30),
                                            createDate(2022, 9, 20, 0, 0)),
                                    new Task(id, "(제1차 과제) (개인 과제) 자기소개 동영상 제작하여 업로드하기",
                                            createDate(2022, 9, 1, 12, 0),
                                            createDate(2022, 9, 19, 12, 0))
                            )
                    );

                    id = db.collection("subjects2").document().getId();
                    items.put(new Subject(id, "수학2"), Arrays.asList(
                                    new Task(id, "10주차 과제",
                                            createDate(2022, 11, 12, 11, 32),
                                            createDate(2022, 11, 18, 0, 0)),
                                    new Task(id, "7주차 과제",
                                            createDate(2022, 10, 19, 21, 56),
                                            createDate(2022, 10, 26, 12, 0)),
                                    new Task(id, "6주차 2&3차시 과제",
                                            createDate(2022, 10, 14, 21, 56),
                                            createDate(2022, 10, 21, 11, 0)),
                                    new Task(id, "5주차 2&3차시 과제",
                                            createDate(2022, 10, 7, 20, 53),
                                            createDate(2022, 10, 14, 11, 0)),
                                    new Task(id, "4주차 2&3차시 과제",
                                            createDate(2022, 9, 30, 18, 50),
                                            createDate(2022, 10, 7, 11, 0)),
                                    new Task(id, "3주차 2&3차시 과제",
                                            createDate(2022, 9, 24, 0, 29),
                                            createDate(2022, 9, 30, 11, 0)),
                                    new Task(id, "2주차 2&3차시 과제",
                                            createDate(2022, 9, 16, 23, 39),
                                            createDate(2022, 9, 23, 11, 0)),
                                    new Task(id, "1주차시 과제",
                                            createDate(2022, 9, 14, 14, 19),
                                            createDate(2022, 9, 16, 11, 0))
                            )
                    );

                    WriteBatch batch = db.batch();
                    for (Subject subject : items.keySet()) {
                        batch.set(db.collection("subjects2")
                                        .document(subject.documentId),
                                subject);
                    }
                    for (List<Task> tasks : items.values()) {
                        for (Task t : tasks) {
                            batch.set(db.collection("subjects2")
                                            .document(t.parentId)
                                            .collection("tasks")
                                            .document(),
                                    t);
                        }
                    }

                    batch.commit();
                });
    }
}
