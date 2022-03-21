package com.andy.wang.multitypeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;

import com.andy.wang.multitype.BaseViewHolder;
import com.andy.wang.multitype.CellTypePool;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new TestAdapter());
    }

    private class TestAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            BaseViewHolder baseViewHolder = CellTypePool.getInstance().get(parent, viewType);
            return baseViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 10000;
            } else {
                return 100001;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}