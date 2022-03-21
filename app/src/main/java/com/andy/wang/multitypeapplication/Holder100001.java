package com.andy.wang.multitypeapplication;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.andy.wang.multitype.BaseViewHolder;
import com.andy.wang.multitype_annotations.CellType;

@CellType(100001)
public class Holder100001 extends BaseViewHolder {
    public Holder100001(@NonNull ViewGroup parentView) {
        super(parentView, R.layout.item100001);
    }
}
