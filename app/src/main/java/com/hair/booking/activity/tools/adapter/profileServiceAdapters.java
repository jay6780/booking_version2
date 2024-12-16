package com.hair.booking.activity.tools.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.List;

public class profileServiceAdapters extends RecyclerView.Adapter<profileServiceAdapters.ServiceViewHolder> {
    private List<Service> serviceList;
    private OnServiceCheckedListener listener;
    private int selectedPosition = -1;
    private Context context;
    int currentNumber;
    public profileServiceAdapters(List<Service> serviceList, OnServiceCheckedListener listener, Context context) {
        this.serviceList = serviceList;
        this.listener = listener;
        this.context = context;  // Initialize context
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service3, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        int servicePrice = service.getPrice();
        String discountValueStr = SPUtils.getInstance().getString(AppConstans.discount, "0"); // Default to "0" if null or not set
        Log.d("Discount", "discountValue: " + discountValueStr);
        //discount calculation
        double discountValue = 0;
        if (discountValueStr != null && !discountValueStr.isEmpty()) {
            discountValue = Double.parseDouble(discountValueStr);
        }
        double discountedPrice = servicePrice;
        if (discountValue > 0) {
            discountedPrice = servicePrice - (servicePrice * (discountValue / 100.0));
        }
        Log.d("Discount", "Original Price: " + servicePrice + ", Discount: " + discountValue + "%, Discounted Price: " + discountedPrice);
        String displayDiscount = discountValueStr.isEmpty() ? "0%" : discountValueStr + "%";
        if (discountValueStr != null && !discountValueStr.isEmpty()) {
            holder.price.setText(String.format("PRICE: %.2f php (Discount: %s)", discountedPrice, displayDiscount));
            holder.price.setTextSize(12);
        }else{
            holder.price.setText(String.format("PRICE: %.2f php", discountedPrice));
        }





        holder.checkBox.setChecked(service.isChecked());
        double finalDiscountedPrice = discountedPrice;
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && selectedPosition != -1 && selectedPosition != position) {
                holder.checkBox.setChecked(false);
                Toast.makeText(context, "Only one service per session is allowed", Toast.LENGTH_SHORT).show();
            } else {
                if (isChecked) {
                    String servicename = service.getName();
                    String imageService = service.getImageUrl();
                    SPUtils.getInstance().put(AppConstans.imageService, imageService);
                    SPUtils.getInstance().put(AppConstans.servicename, servicename);
                    Log.d("Service name","Servicename: "+servicename);
                    selectedPosition = position;
                    service.setChecked(true);
                    currentNumber = 0;
                } else {
                    selectedPosition = -1;
                    service.setChecked(false);
                    currentNumber = 0;
                }
                listener.onServiceChecked(isChecked, finalDiscountedPrice);
            }
        });

        String imageUrl = service.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageFromUrl(holder.imageView, imageUrl);
        } else {
            loadDrawable(holder.imageView, service.getName());
        }
    }

    private void loadImageFromUrl(ImageView imageView, String imageUrl) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .circleCrop()
                .into(imageView);
    }
    private void loadDrawable(ImageView imageView, String serviceName) {
        int drawableResId = 0;
        if (serviceName.contains("Light Makeup")) {
            drawableResId = R.mipmap.imgesample1;
        } else if (serviceName.contains("Smokey-eye Makeup")) {
            drawableResId = R.mipmap.imagesample3;
        } else if (serviceName.contains("Wedding makeup")) {
            drawableResId = R.mipmap.imagesample4;
        } else if (serviceName.contains("Graduation light makeup look")) {
            drawableResId = R.mipmap.imagesample7;
        } else if (serviceName.contains("Service and events")) {
            drawableResId = R.mipmap.makeup;
        }
        Glide.with(imageView.getContext())
                .load(drawableResId)
                .circleCrop()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, price,number;
        ImageView imageView;
        CheckBox checkBox;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            checkBox = itemView.findViewById(R.id.checkbox);
            imageView = itemView.findViewById(R.id.serviceImg);
            price = itemView.findViewById(R.id.price);
            serviceName = itemView.findViewById(R.id.service_name);
        }
    }
    public interface OnServiceCheckedListener {
        void onServiceChecked(boolean isChecked,double serviceprice);
    }
}
