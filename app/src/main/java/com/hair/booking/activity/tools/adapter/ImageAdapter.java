package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.FullScreenImageActivity;
import com.hair.booking.activity.MainPageActivity.discount.discount_activity;
import com.hair.booking.activity.tools.Model.Discounts;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageAdapter extends BannerAdapter<Discounts, ImageAdapter.ServiceViewHolder> {
    private Context context;
    public ImageAdapter(List<Discounts> services, Context context) {
        super(services);
        this.context = context;
    }

    @Override
    public ServiceViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindView(ServiceViewHolder holder, Discounts data, int position, int size) {
        if (data.getServiceName().contains(context.getString(R.string.nodiscount))) {
            holder.serviceName.setText(context.getString(R.string.nodiscount));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.serviceName.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.serviceName.setLayoutParams(params);
            holder.serviceName.setTextSize(12);
            holder.explorebtn.setVisibility(View.GONE);
            holder.hair.setVisibility(View.GONE);
            holder.girl.setVisibility(View.GONE);
            holder.percent.setVisibility(View.GONE);
        } else {
            holder.serviceName.setText(data.getServiceName());
            holder.percent.setText(data.getDiscount()+" %");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.serviceName.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_IN_PARENT);
            holder.serviceName.setLayoutParams(params);
            holder.explorebtn.setVisibility(View.VISIBLE);
            holder.hair.setVisibility(View.VISIBLE);
            holder.girl.setVisibility(View.VISIBLE);
            holder.percent.setVisibility(View.VISIBLE);
        }

        holder.explorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("30%".equals(data.getDiscount())) {
                    goto30percent(data.getUserId(),data.getDiscount(), context);
                } else {
                    goto50percent(data.getUserId(),data.getDiscount(), context);
                }
            }
        });
    }

    private void goto50percent(String userId,String discount,Context context) {
        Intent intent = new Intent(context, discount_activity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("discount", discount);
        context.startActivity(intent);
    }

    private void goto30percent(String userId,String discount,Context context) {
        Intent intent = new Intent(context, discount_activity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("discount", discount);
        context.startActivity(intent);
    }


    class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, percent;
        AppCompatButton explorebtn;
        ImageView girl,hair;
        public ServiceViewHolder(View view) {
            super(view);
            explorebtn = view.findViewById(R.id.explorebtn);
            hair = view.findViewById(R.id.hair);
            girl = view.findViewById(R.id.girl);
            serviceName = view.findViewById(R.id.service_name);
            percent = view.findViewById(R.id.percent);
        }
    }
}
