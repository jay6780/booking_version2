package com.hair.booking.activity.tools.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Service;

import java.util.List;

public class portfolio_eventAdapter extends RecyclerView.Adapter<portfolio_eventAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private Context context;
    public portfolio_eventAdapter(List<Service> serviceList,Context context) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_serviceitem, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Service service = serviceList.get(position);
        String imageUrl = service.getImageUrl();
        int drawableResId = 0; // Variable to hold drawable resource ID

        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageFromUrl(holder.imageView, imageUrl);
        } else {
            drawableResId = loadDrawable(holder.imageView, service.getName());
        }
        int finalDrawableResId = drawableResId;

    }
    private void loadImageFromUrl(ImageView imageView, String imageUrl) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }
    private int loadDrawable(ImageView imageView, String serviceName) {
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
                .into(imageView);

        return drawableResId; // Return the drawable resource ID
    }



    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.serviceImg);
        }
    }
}
