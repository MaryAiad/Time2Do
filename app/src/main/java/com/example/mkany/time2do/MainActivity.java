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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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
import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;
import com.podcopic.animationlib.library.sliders.SlideInLeft;
import com.podcopic.animationlib.library.sliders.SlideInRight;
import com.podcopic.animationlib.library.sliders.SlideInUp;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static TaskDBHelper taskDBHelper ;
    private EditText text;
    private RadioGroup radioGroup;
    private RadioButton normal, high;
    public RecyclerView recyclerView;
    public static RecyclerAdaptorTasks adaptorTasks = new RecyclerAdaptorTasks();
    public static CheckedAdaptor checkedAdaptor = new CheckedAdaptor();
    public int priority = 3, count=0;
    public static Context context ;
    public static RecyclerView doneRecyclerView;
    public static LinearLayout checkedTasks, line;
    public ImageButton imageButton;

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
//        final ImageView imageView3 = new ImageView(this);
//        imageView3.setImageResource(R.drawable.rednote);
//
//        ImageView imageView2 = new ImageView(this);
//        imageView2.setImageResource(R.drawable.cam2);
//
//        ImageView imageView1 = new ImageView(this);
//        imageView1.setImageResource(R.drawable.record2);

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
//                layout.setBackgroundColor(Color.BLACK);

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
//                                updateUI();
                                adaptorTasks.add(context, data);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                alartDialog.show();

            }
        });

//        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
//
//        itemBuilder.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
//
//        SubActionButton noteButton = itemBuilder.setContentView(imageView3).build();
//        SubActionButton imageButton = itemBuilder.setContentView(imageView2).build();
//        SubActionButton recordButton = itemBuilder.setContentView(imageView1).build();
//
//        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
//                .addSubActionView(recordButton)
//                .addSubActionView(imageButton)
//                .addSubActionView(noteButton)
//                .attachTo(fab).build();
//

//        noteButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//                actionMenu.close(true);
//                Toast.makeText(getApplicationContext(), "Option1", Toast.LENGTH_SHORT).show();
//
////                Intent intent = new Intent(getApplicationContext(), AddNote.class);
////                startActivityForResult(intent, 100);
//                final AlertDialog.Builder alartDialog = new AlertDialog.Builder(MainActivity.this);
//                View view = getLayoutInflater().inflate(R.layout.activity_note, null);
//                text = (EditText) view.findViewById(R.id.noteDescription);
//                alartDialog.setTitle("Add ToDo")
//                        .setView(view)
//                        .setIcon(R.drawable.note4)
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String task = String.valueOf(text.getText());
//                                SQLiteDatabase database = taskDBHelper.getWritableDatabase();
//                                ContentValues values = new ContentValues();
//                                if(task.isEmpty())
//                                {
//                                    values.put(TaskContract.TaskEntry.COL_TASK_TITLE, "No title");
//                                }
//                                else{
//                                    values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
//                                }
//                                database.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null,
//                                        values, SQLiteDatabase.CONFLICT_REPLACE);
//                                database.close();
//                                updateUI();
//                            }
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .create();
//                alartDialog.show();
//
//            }
//        });
//
//        recordButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//                actionMenu.close(true);
//                Toast.makeText(getApplicationContext(), "Option3", Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(MainActivity.this, AddNote.class);
////                startActivity(intent);
//            }
//        });
//
//        imageButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//                actionMenu.close(true);
//                Toast.makeText(getApplicationContext(), "Option2", Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(getApplication(), AddNote.class);
////                startActivity(intent);
//            }
//        });
//

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

//    public static void delete(NoteData data, Context context, int position)
//    {
//        Toast.makeText(context, "position "+position, Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, "name of position "+ list.get(position-1).gettitle(), Toast.LENGTH_SHORT).show();
//        SQLiteDatabase database = taskDBHelper.getWritableDatabase();
//        Toast.makeText(context,data.gettitle()+ " Finished position:"+ position, Toast.LENGTH_SHORT).show();
//        database.execSQL("DELETE FROM " + TaskContract.TaskEntry.TABLE+ " WHERE "+TaskContract.TaskEntry.COL_TASK_TITLE+"='"+data.gettitle()+"'");
//        Toast.makeText(context,"Deleted ", Toast.LENGTH_SHORT).show();
//        list.remove(position);
//        if(list.size()==0)
//        {
//            Toast.makeText(context, "excelent finished All tasks", Toast.LENGTH_SHORT).show();
//        }
//        database.close();
////        done.add(data);
////        boolean b = database.delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry.COL_TASK_TITLE
////                +" = " + title , null) > 0;
//    }

    public void updateCheckedList() {
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
    public void updateUI() {
//        list = new ArrayList<>();
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

//            if(adaptorTasks.getItemCount() == 0)
//            {
//                System.err.println("adaptor = 0");
//                adaptorTasks = new RecyclerAdaptorTasks(context, list);
//                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//                recyclerView.setAdapter(adaptorTasks);
//                runLayoutAnimation(recyclerView);
//            }
        }

        else if (adaptorTasks.getItemCount() == 0)
        {
            Toast.makeText(context, "No tasks", Toast.LENGTH_SHORT).show();
        }

//            recyclerView.getItemAnimator().setChangeDuration(4000);
//
//            ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
//                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//                @Override
//                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                          RecyclerView.ViewHolder target) {
//                    return true;
//                }
//
//                @Override
//                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
//                {
//                    Toast.makeText(context, "ay 7agaa", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onChildDraw(Canvas c, RecyclerView recyclerView,
//                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
//                                        int actionState, boolean isCurrentlyActive) {
//                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                        // Get RecyclerView item from the ViewHolder
//                        View itemView = viewHolder.itemView;
//
//                        Paint p = new Paint();
//                        if (dX > 0) {
//
//            /* Set your color for positive displacement */
//                            p.setARGB(255, 255, 0, 0);
//                            // Draw Rect with varying right side, equal to displacement dX
//                            c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
//                                    (float) itemView.getBottom(), p);
//                        } else {
//            /* Set your color for negative displacement */
//                            p.setARGB(255, 0, 255, 0);
//
//                            // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
//                            c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
//                                    (float) itemView.getRight(), (float) itemView.getBottom(), p);
//                        }
//
//                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                    }
//                }
//            });
//            swipeToDismissTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
