package kr.ac.jbnu.se.taskalarm.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Subject {
    @DocumentId
    public String documentId;

    public String name;

    @ServerTimestamp
    public Date timestamp = new Date();

    @Exclude
    public List<Task> tasks = new ArrayList<>();

    public Subject() {
    }

    public Subject(String documentId, String name) {
        this.documentId = documentId;
        this.name = name;
    }

    public Subject copy() {
        Subject newSubject = new Subject();
        newSubject.documentId = this.documentId;
        newSubject.name = this.name;
        newSubject.timestamp = this.timestamp;
        return newSubject;
    }
}
