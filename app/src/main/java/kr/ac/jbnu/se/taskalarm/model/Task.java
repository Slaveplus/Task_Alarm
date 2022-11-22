package kr.ac.jbnu.se.taskalarm.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Task {
    @DocumentId
    public String documentId;

    public String parentId;
    public String name;
    public Date from;
    public Date to;
    public boolean done = false;

    @ServerTimestamp
    public Date timestamp = new Date(); //원래는 timestamp

    public Task() {
    }

    public Task(String parentId, String name, Date from, Date to) {
        this.parentId = parentId;
        this.name = name;
        this.from = from;
        this.to = to;
    }
}
