package com.example.mkany.time2do;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.example.mkany.time2do.DataBase.TaskContract;
import com.example.mkany.time2do.DataBase.TaskDBHelper;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class MainActivity extends AppCompatActivity{

    public static TaskDBHelper taskDBHelper ;
    private EditText text;
    private RadioGroup radioGroup;
    private RadioButton normal, high;
    private RecyclerView recyclerView;
    public static RecyclerAdaptorTasks adaptorTasks = new RecyclerAdaptorTasks();
    public static CheckedAdaptor checkedAdaptor = new CheckedAdaptor();
    private int priority = 3, count = 0;
    private Context context;
    private RecyclerView doneRecyclerView;
    public static LinearLayout checkedTasks, line;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();
        taskDBHelper = new TaskDBHelper(this);
        recyclerView = (RecyclerView) findViewById(R.id.tasks_recycler_view);
        doneRecyclerView = (RecyclerView) findViewById(R.id.done_recycler_view);
        checkedTasks = (LinearLayout) findViewById(R.id.checked_tasks);
        imageButton = (ImageButton) findViewById(R.id.upArrow);
        line = (LinearLayout) findViewById(R.id.line);

        adaptorTasks = new RecyclerAdaptorTasks();
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adaptorTasks);

        SlideInRightAnimator slideInUpAnimator = new SlideInRightAnimator(new OvershootInterpolator(1f));
        recyclerView.setItemAnimator(slideInUpAnimator);
        slideInUpAnimator.setAddDuration(1000);
//        runLayoutAnimation(recyclerView);

        checkedAdaptor = new CheckedAdaptor();
        doneRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        doneRecyclerView.setAdapter(checkedAdaptor);

        SlideInLeftAnimator slideInLeftAnimator = new SlideInLeftAnimator(new OvershootInterpolator(1f));
        doneRecyclerView.setItemAnimator(slideInLeftAnimator);
        slideInLeftAnimator.setAddDuration(1000);
//        runLayoutAnimation(doneRecyclerView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        updateCheckedList();
        updateUI();

        doneRecyclerView.setVisibility(View.VISIBLE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0)
                {
                    imageButton.setImageResource(R.drawable.ic_up_arrow);
                    doneRecyclerView.setVisibility(View.INVISIBLE);
                    count++;
                }
                else if(count>0)
                {
                    imageButton.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    doneRecyclerView.setVisibility(View.VISIBLE);
                    count = 0;
                }
            }
        });

        checkedTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0)
                {
                    imageButton.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    doneRecyclerView.setVisibility(View.VISIBLE);
                    count++;
                }
                else if(count>0)
                {
                    imageButton.setImageResource(R.drawable.ic_up_arrow);
                    doneRecyclerView.setVisibility(View.INVISIBLE);
                    count = 0;
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder alartDialog = new AlertDialog.Builder(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.activity_note, null);

                text = (EditText) view.findViewById(R.id.noteDescription);
                radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
                normal = (RadioButton) view.findViewById(R.id.radio_button2);
                high = (RadioButton) view.findViewById(R.id.radio_button1);
                priority = 3;
                alartDialog.setTitle("New Task")
                        .setView(view)
                        .setIcon(R.drawable.note4);
                radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup rg, int checkedID)
                    {
                        if(checkedID == normal.getId())
                        {
                            priority = 2;
                        }
                        else if(checkedID == high.getId())
                        {
                            priority = 1;
                        }
                        else
                            priority = 3;
                    }
                });
                alartDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(text.getText());
                        NoteData data = new NoteData();
                        SQLiteDatabase database = taskDBHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        if(task.isEmpty())
                        {
                            values.put(TaskContract.TaskEntry.COL_TASK_TITLE, "No title");
                            data.setTitle("No title");
                        }
                        else{
                            values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                            data.setTitle(task);
                        }
                        data.setPriority(priority);
                        data.setDone(0);
                        values.put(TaskContract.TaskEntry.COL_TASK_Priority, priority);
                        values.put(TaskContract.TaskEntry.COL_TASK_isDone, 0);
                        database.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null,
                                values, SQLiteDatabase.CONFLICT_REPLACE);
                        database.close();
                        adaptorTasks.add(context, data);
                    }
                })
                        .setNegativeButton("Cancel", null)
                        .create();
                alartDialog.show();

            }
        });
    }

    private void updateCheckedList() {
        doneRecyclerView.setVisibility(View.INVISIBLE);
        SQLiteDatabase database = taskDBHelper.getReadableDatabase();
        final String[] coloumns = {TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE,
                TaskContract.TaskEntry.COL_TASK_Priority, TaskContract.TaskEntry.COL_TASK_isDone};

        Cursor cursor = database.query(TaskContract.TaskEntry.TABLE, coloumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            NoteData task = new NoteData();
            task.setTitle(cursor.getString(1));
            task.setPriority(cursor.getInt(2));
            task.setDone(cursor.getInt(3));
            if (task.getDone() == 1) {
                checkedAdaptor.add(context, task);
            }

            while (cursor.moveToNext()) {
                NoteData note = new NoteData();
                note.setTitle(cursor.getString(1));
                note.setPriority(cursor.getInt(2));
                note.setDone(cursor.getInt(3));
                if (note.getDone() == 1) {
                    checkedAdaptor.add(context, note);
                }
            }
            cursor.close();
            database.close();
        }
    }

    private void updateUI() {
        SQLiteDatabase database = taskDBHelper.getReadableDatabase();
        final String [] coloumns = {TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE,
                TaskContract.TaskEntry.COL_TASK_Priority, TaskContract.TaskEntry.COL_TASK_isDone};

        Cursor cursor = database.query(TaskContract.TaskEntry.TABLE, coloumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst())
        {
            NoteData task = new NoteData();
            task.setTitle(cursor.getString(1));
            task.setPriority(cursor.getInt(2));
            task.setDone(cursor.getInt(3));
            if(task.getDone() == 0)
            {
                adaptorTasks.add(context,task);
            }

            while (cursor.moveToNext()) {
                NoteData note = new NoteData();
                note.setTitle(cursor.getString(1));
                note.setPriority(cursor.getInt(2));
                note.setDone(cursor.getInt(3));
                if(note.getDone() == 0)
                {
                    adaptorTasks.add(context,note);
                }
            }
            cursor.close();
            database.close();
}

        else if (adaptorTasks.getItemCount() == 0)
        {
            Toast.makeText(context, "No tasks", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
