package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.ServiceInfo;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.List;

public class Package_adapter extends RecyclerView.Adapter<Package_adapter.ServiceInfoViewHolder> {

    private Context context;
    private List<ServiceInfo> serviceInfoList;
    private OnPriceChangeListener priceChangeListener;
    private int totalPrice = 0;
    private OnSelectionChangeListener selectionChangeListener;
    public interface OnPriceChangeListener {
        void onPriceChanged(int updatedPrice, String serviceInfo);
    }

    public interface OnSelectionChangeListener {
        void onSelectionChanged(boolean isAnyItemSelected);
    }
    public void setSelectionChangeListener(OnSelectionChangeListener selectionChangeListener) {
        this.selectionChangeListener = selectionChangeListener;
    }

    public Package_adapter(Context context, List<ServiceInfo> serviceInfoList, OnPriceChangeListener listener) {
        this.context = context;
        this.serviceInfoList = serviceInfoList;
        this.priceChangeListener = listener;
    }

    @NonNull
    @Override
    public ServiceInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false);
        return new ServiceInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceInfoViewHolder holder, int position) {
        ServiceInfo serviceInfo = serviceInfoList.get(position);

        // Set service details
        holder.serviceName.setText(serviceInfo.getInformation());
        holder.price.setText(String.format("Price: %s", serviceInfo.getPrice()));

        // Load service image
        Glide.with(context)
                .load(serviceInfo.getImgInfoUrl())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .circleCrop()
                .into(holder.serviceImg);

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(serviceInfo.isChecked());
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            serviceInfo.setChecked(isChecked);

            try {
                int servicePrice = Integer.parseInt(serviceInfo.getPrice());
                if (isChecked) {
                    totalPrice += servicePrice;
                } else {
                    totalPrice -= servicePrice;
                }
                if (priceChangeListener != null) {
                    priceChangeListener.onPriceChanged(isChecked ? servicePrice : -servicePrice, serviceInfo.getInformation());
                }

                // Notify selection state change
                if (selectionChangeListener != null) {
                    boolean isAnyItemSelected = false;
                    for (ServiceInfo info : serviceInfoList) {
                        if (info.isChecked()) {
                            isAnyItemSelected = true;
                            break;
                        }
                    }
                    selectionChangeListener.onSelectionChanged(isAnyItemSelected);
                }

            } catch (NumberFormatException e) {
                Log.e("Package_adapter", "Error parsing price: " + e.getMessage());
            }
        });
    }
        @Override
    public int getItemCount() {
        return serviceInfoList.size();
    }

    static class ServiceInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImg;
        TextView serviceName, price;
        CheckBox checkbox;

        public ServiceInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImg = itemView.findViewById(R.id.serviceImg);
            serviceName = itemView.findViewById(R.id.service_name);
            price = itemView.findViewById(R.id.price);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
