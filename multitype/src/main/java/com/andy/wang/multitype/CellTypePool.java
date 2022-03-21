package com.andy.wang.multitype;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;

public class CellTypePool<M extends BaseViewHolder> {
    public ICellTypeMap iCellTypeMap;
    public SparseArray<Class> cellTypeSparseArray = new SparseArray();
    public SparseArray<Constructor<M>> constructorCache = new SparseArray();

    private CellTypePool() {

    }

    private static volatile CellTypePool instance;

    public static CellTypePool getInstance() {
        if (instance == null) {
            synchronized (CellTypePool.class) {
                if (instance == null) {
                    instance = new CellTypePool();
                }
            }
        }
        return instance;
    }

    public M get(ViewGroup parentView, int cellType) {
        Constructor<M> viewHolderConstructor = constructorCache.get(cellType);
        if (viewHolderConstructor != null) {
            try {
                return viewHolderConstructor.newInstance(parentView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (iCellTypeMap == null) {
            try {
                iCellTypeMap = (ICellTypeMap) Class.forName("com.andy.wang.CellTypeMap").newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            iCellTypeMap.loadCellTypeMap(cellTypeSparseArray);
        }
        if (iCellTypeMap != null && cellTypeSparseArray.size() > 0) {
            Class holderClass = cellTypeSparseArray.get(cellType);
            try {
                Constructor<M> constructor = holderClass.getConstructor(ViewGroup.class);
                constructorCache.put(cellType, constructor);
                return constructor.newInstance(parentView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
