package com.hair.booking.activity.tools.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.Rating_adminview;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.tools.Model.Service_percent;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageAdapter2 extends BannerAdapter<Service_percent, ImageAdapter2.ServiceViewHolder> {
    private Context context;
    public ImageAdapter2(List<Service_percent> services,Context context) {
        super(services);
        this.context = context;
    }

    @Override
    public ServiceViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindView(ServiceViewHolder holder, Service_percent data, int position, int size) {

        holder.serviceName.setText(data.getName());
        String name = data.getName();
        Log.d("Imageadaptername","name: "+ name);
        int totalRating = 0;
        int reviewCount = 0;

        totalRating += data.getPercent();
        reviewCount++;
        float averageRating = (float) totalRating / reviewCount;
        if (reviewCount > 0) {
            if (data.getName().startsWith("Total Booking")) {
                holder.rating.setText(String.valueOf(data.getPercent()));
                holder.image.setImageResource(R.mipmap.event_yellow);
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFE3B7"));
            } else {
                holder.rating.setText(String.format("%.1f", averageRating));
                holder.image.setImageResource(R.mipmap.girlred);
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFB7B7"));
            }
        } else {
            holder.rating.setText("0");
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.contains("Customer Satisfaction")) {
                    openRating(context);
                }
            }
        });

    }

    private void openRating(Context context) {
        Intent intent = new Intent(context, Rating_adminview.class);
        context.startActivity(intent);
    }


    class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, rating;
        ImageView image;
        RelativeLayout relativeLayout;
        public ServiceViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            relativeLayout =view.findViewById(R.id.relative);
            serviceName = view.findViewById(R.id.textValue);
            rating = view.findViewById(R.id.rating);
        }
    }
}
