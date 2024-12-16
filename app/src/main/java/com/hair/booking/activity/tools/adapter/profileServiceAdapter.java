package com.hair.booking.activity.tools.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.activity.tools.DialogUtils.UserProviderDialog;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.R;

import java.util.List;

public class profileServiceAdapter extends RecyclerView.Adapter<profileServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;

    public profileServiceAdapter(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service2, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.price.setText("PRICE: " + service.getPrice() +" "+"php");

        // Load drawable based on service name
        String imageUrl = service.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageFromUrl(holder.imageView, imageUrl);
        } else {
            loadDrawable(holder.imageView, service.getName());
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Service").child(userId);
        holder.dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select an option")
                        .setItems(new String[]{"Delete", "Update Price"}, (dialog, which) -> {
                            if (which == 0) {
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("Confirm Deletion")
                                        .setMessage("Are you sure you want to delete this service?")
                                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                                            String serviceNameToDelete = service.getName();
                                            databaseReference.orderByChild("name").equalTo(serviceNameToDelete).get()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                                                snapshot.getRef().removeValue()
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            serviceList.remove(position);
                                                                            notifyItemRemoved(position);
                                                                            Toast.makeText(view.getContext(), "Service deleted: " + serviceNameToDelete, Toast.LENGTH_SHORT).show();
                                                                        });
                                                            }
                                                        }
                                                    });
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            } else if (which == 1) {
                                UserProviderDialog userProviderDialog = new UserProviderDialog();
                                userProviderDialog.savedData(view.getContext(), service.getName(),service.getPrice());
                            }
                        })
                        .show();
            }
        });
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

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, price;
        ImageView imageView, dots;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            dots = itemView.findViewById(R.id.dots);
            imageView = itemView.findViewById(R.id.serviceImg);
            price = itemView.findViewById(R.id.price);
            serviceName = itemView.findViewById(R.id.service_name);
        }
    }
}
