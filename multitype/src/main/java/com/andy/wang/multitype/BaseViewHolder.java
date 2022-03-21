package com.andy.wang.multitype;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull ViewGroup parentView, @LayoutRes int layout) {
        super(LayoutInflater.from(parentView.getContext())
                .inflate(layout, parentView, false));
    }
}
