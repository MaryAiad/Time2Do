package com.example.mkany.time2do;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mery on 7/24/2017.
 */

public class NoteData extends AppCompatActivity {

    String title;
    int isDone;
    int priority;

    public void setTitle(String description) {
        this.title = description;
    }

    public void setDone(int done) {
        isDone = done;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String gettitle() {
        return title;
    }

    public int getDone() {
        return isDone;
    }

    public int getPriority() {
        return priority;
    }
}
