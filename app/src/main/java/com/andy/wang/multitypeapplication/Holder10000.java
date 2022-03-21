package com.andy.wang.multitypeapplication;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.andy.wang.multitype.BaseViewHolder;
import com.andy.wang.multitype_annotations.CellType;

@CellType(10000)
public class Holder10000 extends BaseViewHolder {
    public Holder10000(@NonNull ViewGroup parentView) {
        super(parentView, R.layout.item10000);
    }
}
