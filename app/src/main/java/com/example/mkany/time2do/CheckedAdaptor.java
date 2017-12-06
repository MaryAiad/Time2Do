package com.example.mkany.time2do;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.mkany.time2do.DataBase.TaskContract;

import java.util.ArrayList;

/**
 * Created by Mery on 10/17/2017.
 */

public class CheckedAdaptor extends RecyclerView.Adapter<CheckedAdaptor.ViewHolder>{

    private ArrayList<NoteData> doneList = new ArrayList<>();
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public CheckBox checkBox;
        public ImageButton imageButton;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.task_title);
            imageButton = (ImageButton) itemView.findViewById(R.id.checked_delete);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.checkBox);
        }
    }

    public CheckedAdaptor() {
    }

    public void add(Context c ,NoteData done)
    {
        MainActivity.checkedTasks.setVisibility(View.VISIBLE);
        MainActivity.line.setVisibility(View.VISIBLE);
        this.context = c;
        this.doneList.add(done);
        notifyItemInserted(doneList.size()-1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_checked_tasks, parent, false);
        v.setMinimumWidth(parent.getMeasuredWidth());
        CheckedAdaptor.ViewHolder viewHolder = new CheckedAdaptor.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MainActivity.checkedTasks.setVisibility(View.VISIBLE);
        MainActivity.line.setVisibility(View.VISIBLE);

        final NoteData noteData = doneList.get(position);
        holder.checkBox.setText(noteData.gettitle());
        holder.checkBox.setPaintFlags(holder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.checkBox.setChecked(true);
        holder.imageButton.setImageResource(R.drawable.ic_deleteblack);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = MainActivity.taskDBHelper.getWritableDatabase();
                    database.execSQL("DELETE FROM " + TaskContract.TaskEntry.TABLE+ " WHERE "+
                            TaskContract.TaskEntry.COL_TASK_TITLE + "='" + doneList.get(holder.getAdapterPosition()).gettitle() + "'");
                database.close();

                TranslateAnimation animation = new TranslateAnimation(holder.itemView.getWidth(), 0, 0, 0);
                animation.setDuration(400);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        doneList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), doneList.size());
                        if(doneList.size() == 0)
                        {
                            MainActivity.checkedTasks.setVisibility(View.INVISIBLE);
                            MainActivity.line.setVisibility(View.INVISIBLE);
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                holder.itemView.startAnimation(animation);
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.adaptorTasks.add(context, noteData);
                SQLiteDatabase database = MainActivity.taskDBHelper.getWritableDatabase();
                database.execSQL("update "+ TaskContract.TaskEntry.TABLE + " set "+ TaskContract.TaskEntry.COL_TASK_isDone+
                        " = 0 where "+TaskContract.TaskEntry.COL_TASK_TITLE + "= '"+noteData.gettitle()+"' ;");
                database.close();
                holder.checkBox.setChecked(false);
                holder.checkBox.setPaintFlags(0);
                doneList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), doneList.size());
                if(doneList.size() == 0)
                {
                    MainActivity.checkedTasks.setVisibility(View.INVISIBLE);
                    MainActivity.line.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(doneList == null)
        {
            return 0;
        }
        else
            return doneList.size();
    }
}