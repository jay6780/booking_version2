package com.hair.booking.activity.tools.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class MapResultAdapter extends RecyclerView.Adapter<MapResultAdapter.ViewHolder> {

    private List<String> searchResults;
    private OnItemClickListener listener;

    public MapResultAdapter(List<String> searchResults, OnItemClickListener listener) {
        this.searchResults = searchResults;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String address = searchResults.get(position);
        holder.addressText.setText(address);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView addressText;

        public ViewHolder(View itemView) {
            super(itemView);
            addressText = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String address);
    }

}
