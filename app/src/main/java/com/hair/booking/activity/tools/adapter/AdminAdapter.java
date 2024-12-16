package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.List;
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {
    private Context context;
    private List<Usermodel> adminList;
    private OnAdminClickListener listener;

    public AdminAdapter(Context context, List<Usermodel> adminList, OnAdminClickListener listener) {
        this.context = context;
        this.adminList = adminList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Usermodel admin = adminList.get(position);
        holder.username.setText(admin.getUsername());
        Glide.with(context)
                .load(admin.getImage())
                .transform(new CircleCrop())
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdminClick(admin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView image;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.admin_username);
            image = itemView.findViewById(R.id.admin_image);
        }
    }
    public interface OnAdminClickListener {
        void onAdminClick(Usermodel admin);
    }

}
