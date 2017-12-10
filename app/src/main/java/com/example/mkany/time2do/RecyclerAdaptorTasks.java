package com.example.mkany.time2do;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mkany.time2do.DataBase.TaskContract;
import java.util.ArrayList;

/**
 * Created by Mery on 7/26/2017.
 */

public class RecyclerAdaptorTasks extends RecyclerView.Adapter<RecyclerAdaptorTasks.ViewHolder>  {

    private ArrayList<NoteData> tasks= new ArrayList<>();
    private ArrayList<NoteData> done = new ArrayList<>();
    private Context context;
    private ViewGroup group;
    private EditText text;
    private RadioGroup radioGroup;
    private RadioButton normal, high, low;
    private int priority;


    class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox check;
        public CardView cardView ;
        public LinearLayout linearLayout;
        public ImageButton delete, edit;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            check = (CheckBox) itemView.findViewById(R.id.task_title);
            cardView = (CardView) itemView.findViewById(R.id.task_view);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.coloredPart);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            textView = (TextView) itemView.findViewById(R.id.task_name);
        }
    }

    public RecyclerAdaptorTasks(Context c, ArrayList<NoteData> n) {
        this.context = c;
        this.tasks = n;
    }

    public void add(NoteData n)
    {
        tasks.add(n);
        notifyItemInserted(tasks.size()-1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_task_view, viewGroup, false);

        this.group = viewGroup;

        v.setMinimumWidth(viewGroup.getMeasuredWidth());
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final NoteData data = tasks.get(position);
        viewHolder.delete.setImageResource(R.drawable.ic_deleteblack);
        viewHolder.edit.setImageResource(R.drawable.ic_edit);
        viewHolder.check.setChecked(false);
        viewHolder.textView.setTextColor(Color.BLACK);
        viewHolder.textView.setText(data.gettitle());

        colorPart(viewHolder, data);

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SQLiteDatabase database = null;
                if (context instanceof MainActivity) {
                    System.err.println("imageButton click instance ");
                    database = ((MainActivity) context).taskDBHelper.getWritableDatabase();
                }
                System.err.println("imageButton click not instance ");

                database.execSQL("DELETE FROM " + TaskContract.TaskEntry.TABLE+ " WHERE "+
                        TaskContract.TaskEntry.COL_TASK_TITLE + "='" + tasks.get(viewHolder.getAdapterPosition()).gettitle() + "'");
                database.close();

                TranslateAnimation animation = new TranslateAnimation(0, viewHolder.itemView.getWidth(), 0, 0);
                animation.setDuration(500);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tasks.remove(viewHolder.getAdapterPosition());
                        notifyItemRemoved(viewHolder.getAdapterPosition());
                        notifyItemRangeChanged(viewHolder.getAdapterPosition(), tasks.size());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                viewHolder.itemView.startAnimation(animation);
            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v2 = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.activity_note, group, false);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(viewHolder.itemView.getContext());
                text = (EditText) v2.findViewById(R.id.noteDescription);
                radioGroup = (RadioGroup) v2.findViewById(R.id.radio_group);
                normal = (RadioButton) v2.findViewById(R.id.radio_button2);
                high = (RadioButton) v2.findViewById(R.id.radio_button1);
                low = (RadioButton) v2.findViewById(R.id.radio_button3);

                text.setText(data.gettitle());
                priority = data.getPriority();

                if(priority == 1){
                    high.setChecked(true);
                }
                else if(priority == 2){
                    normal.setChecked(true);
                }
                else if(priority == 3){
//                    radioGroup.check(low.getId());
                    low.setChecked(true);
                }

                alertDialog.setView(v2)
                        .setTitle("Edit task");

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
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
                final String oldTitle = data.gettitle();

                alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(text.getText());

                        SQLiteDatabase database = null;
                        if (context instanceof MainActivity) {
                            database = ((MainActivity) context).taskDBHelper.getWritableDatabase();
                        }

                        String query;
                        if(task.isEmpty())
                        {
                            database.execSQL("update "+ TaskContract.TaskEntry.TABLE + " set "+TaskContract.TaskEntry.COL_TASK_TITLE +" = 'No title' ,"+
                                    TaskContract.TaskEntry.COL_TASK_Priority + " = " +priority + " where " + TaskContract.TaskEntry.COL_TASK_TITLE
                                    + " = '" + oldTitle + "' ;");
                            data.setTitle("No title");
                        }
                        else{
                            System.err.println("title "+oldTitle);
                            query = "update "+TaskContract.TaskEntry.TABLE + " set "+ TaskContract.TaskEntry.COL_TASK_TITLE +" = '"+
                                    text.getText() +"' , "+ TaskContract.TaskEntry.COL_TASK_Priority + " = " +priority + " where " + TaskContract.TaskEntry.COL_TASK_TITLE
                                    + " = '" + oldTitle + "' ;";
                            database.execSQL(query);
                            data.setTitle(task);
                            System.err.println(data.gettitle());
                        }
                        database.close();
                        data.setPriority(priority);
                        data.setDone(0);

                        colorPart(viewHolder, data);

                        viewHolder.textView.setText(data.gettitle());
                        tasks.add(viewHolder.getAdapterPosition(), data);
                        notifyItemInserted(viewHolder.getAdapterPosition());
                        tasks.remove(viewHolder.getAdapterPosition());
                        notifyItemRemoved(viewHolder.getAdapterPosition());
                        Toast.makeText(context, data.gettitle() + " Edited", Toast.LENGTH_SHORT).show();

                    }
                })
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.show();
            }
        });

        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    buttonView.setChecked(true);
                    buttonView.setPaintFlags(buttonView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

                    TranslateAnimation animation1 = new TranslateAnimation(0, viewHolder.itemView.getWidth(), 0, 0);
                    animation1.setDuration(500);
                    animation1.setFillAfter(true);
                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (context instanceof MainActivity) {
                                SQLiteDatabase database = ((MainActivity) context).taskDBHelper.getWritableDatabase();
                                database.execSQL("update " + TaskContract.TaskEntry.TABLE + " set " + TaskContract.TaskEntry.COL_TASK_isDone +
                                        " = 1 where " + TaskContract.TaskEntry.COL_TASK_TITLE + "= '" + data.gettitle() + "' ;");
                                database.close();
                            } else {
                                Toast.makeText(context, "Not instance ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            done.add(data);
                            tasks.remove(viewHolder.getAdapterPosition());
                            notifyItemRemoved(viewHolder.getAdapterPosition());
                            notifyItemRangeChanged(viewHolder.getAdapterPosition(), tasks.size());
                            if(tasks.size()== 0)
                            {
                                Toast.makeText(context, "Excelent Finished All Tasks", Toast.LENGTH_SHORT).show();
                            }
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).checkedAdaptor.add(data);
                                ((MainActivity) context).checkedTasks.setVisibility(View.VISIBLE);
                                ((MainActivity) context).line.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    viewHolder.itemView.startAnimation(animation1);
                }
                else
                {
                    buttonView.setChecked(false);
                    buttonView.setPaintFlags(0);
                    viewHolder.textView.setTextColor(Color.BLACK);
                }
            }
        });
    }

    private void colorPart(ViewHolder viewHolder, NoteData data) {
        if (data.getPriority() == 1)
        {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#F44336"));
        }
        else if (data.getPriority() == 2)
        {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFF59D"));
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#FFEB3B"));
        }
        else if (data.getPriority() == 3)
        {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#9E9E9E"));
        }
        viewHolder.check.setHighlightColor(Color.parseColor("#f44336"));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
