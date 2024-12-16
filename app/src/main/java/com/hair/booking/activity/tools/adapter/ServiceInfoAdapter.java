package com.hair.booking.activity.tools.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hair.booking.R;
import com.hair.booking.activity.events.serviceInfo_update;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.ServiceInfo;

import java.util.List;

public class ServiceInfoAdapter extends RecyclerView.Adapter<ServiceInfoAdapter.ServiceInfoViewHolder> {
    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private Context context;
    private List<ServiceInfo> serviceInfoList;

    public ServiceInfoAdapter(Context context, List<ServiceInfo> serviceInfoList) {
        this.context = context;
        this.serviceInfoList = serviceInfoList;
    }

    @NonNull
    @Override
    public ServiceInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_info, parent, false);
        return new ServiceInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceInfoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ServiceInfo serviceInfo = serviceInfoList.get(position);
        holder.informationText.setText(serviceInfo.getInformation());
        holder.priceText.setText("price: " + serviceInfo.getPrice() + " php");
        String imgUrl = serviceInfo.getImgInfoUrl();
        if (imgUrl != null) {
            Glide.with(context)
                    .load(imgUrl)
                    .error(R.mipmap.man)
                    .circleCrop()
                    .into(holder.imgInfo);
        }

        holder.itemView.setOnLongClickListener(view -> {
            if (position >= 0 && position < serviceInfoList.size()) {
                Dialog deleteinfo = new Dialog();
                deleteinfo.deleteinfo(context, serviceInfo.getInformation(), position, ServiceInfoAdapter.this, serviceInfoList);
            }
            return true;
        });


        holder.imgInfo.setOnClickListener(view -> {
            Intent intent = new Intent(context, serviceInfo_update.class);
            intent.putExtra("key", serviceInfo.getKey());
            intent.putExtra("imgInfo", serviceInfo.getImgInfoUrl());
            intent.putExtra("information", serviceInfo.getInformation());
            intent.putExtra("price", serviceInfo.getPrice());
            intent.putExtra("servicename", serviceInfo.getServicename());
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return serviceInfoList.size();
    }

    public static class ServiceInfoViewHolder extends RecyclerView.ViewHolder {
        TextView informationText, priceText;
        ImageView imgInfo;

        public ServiceInfoViewHolder(View itemView) {
            super(itemView);
            informationText = itemView.findViewById(R.id.informationText);
            priceText = itemView.findViewById(R.id.priceText);
            imgInfo = itemView.findViewById(R.id.imgInfo);
        }
    }
}
