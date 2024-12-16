package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.R;

public class BookemptyAdapter extends RecyclerView.Adapter<BookemptyAdapter.ProviderViewHolder> {
    private Context context;

    public BookemptyAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emptyadapter2, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }
    public class ProviderViewHolder extends RecyclerView.ViewHolder {
        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
