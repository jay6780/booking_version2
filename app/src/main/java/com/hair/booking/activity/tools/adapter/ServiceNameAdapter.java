package com.hair.booking.activity.tools.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.R;

import java.util.List;

public class ServiceNameAdapter extends RecyclerView.Adapter<ServiceNameAdapter.ViewHolder> {

    private List<String> serviceNamesList;

    public ServiceNameAdapter(List<String> serviceNamesList) {
        this.serviceNamesList = serviceNamesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String serviceWithPrice = serviceNamesList.get(position);
        String[] parts = serviceWithPrice.split(" - ");
        String serviceName = parts[0];
        String price = parts.length > 1 ? parts[1] : "N/A";
        holder.serviceNameTextView.setText(serviceName + " - " +"Price: "+price);
    }

    @Override
    public int getItemCount() {
        return serviceNamesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView serviceNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.service_name_text);
        }
    }
}
