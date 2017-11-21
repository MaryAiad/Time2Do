package com.example.mkany.time2do;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;

/**
 * Created by Mery on 10/8/2017.
 */

public class DiffCallback extends DiffUtil.Callback {

    ArrayList<NoteData> oldList;
    ArrayList<NoteData> newList;

    public DiffCallback(ArrayList<NoteData> old, ArrayList<NoteData> newL){
        this.oldList = old;
        this.newList = newL;
    }
    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).gettitle().equals(newList.get(newItemPosition).gettitle()) ;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        NoteData task1 = oldList.get(oldItemPosition);
        NoteData task2 = newList.get(newItemPosition);
        return task1.gettitle().equals(task2.gettitle());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
