package com.hair.booking.activity.tools.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.AddService_view;
import com.hair.booking.activity.events.AddService_view_event;
import com.hair.booking.activity.events.delete_eventinfo;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.DialogUtils.UserProviderDialog;
import com.hair.booking.activity.tools.Model.Service;
import com.lee.avengergone.DisappearView;

import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private Context context;
    public EventsListAdapter(List<Service> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.price.setText("Price: " + service.getPrice());

        String imageUrl = service.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageFromUrl(holder.imageView, imageUrl);
        } else {
            loadDrawable(holder.imageView, service.getName());
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("EventOrg").child(userId);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ServiceInfo");
        DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("Discounts");

        holder.dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select an option")
                        .setItems(new String[]{"Delete", "Update Event", "Discount", "Add package"}, (dialog, which) -> {
                            if (which == 0) {
                                // Delete option
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("Confirm Deletion")
                                        .setMessage("Are you sure you want to delete this service?")
                                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                                            databaseReference4.child(service.getKey()).removeValue();
                                            databaseReference2.child(userId).removeValue();
                                            if (context instanceof Activity) {
                                                Activity activity = (Activity) context;
                                                DisappearView disappearView = DisappearView.attach(activity);
                                                disappearView.execute(holder.itemView, 1500, new AccelerateInterpolator(0.5f), true);

                                                String serviceNameToDelete = service.getName();
                                                databaseReference.orderByChild("name").equalTo(serviceNameToDelete).get()
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                                                    snapshot.getRef().removeValue()
                                                                            .addOnSuccessListener(aVoid -> {
                                                                            });
                                                                }
                                                            } else {
                                                                Toast.makeText(view.getContext(), "Failed to query EventOrg: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            } else if (which == 1) {
                                Intent intent = new Intent(context, AddService_view_event.class);
                                intent.putExtra("gender", service.getGender());
                                intent.putExtra("imageUrl", service.getImageUrl());
                                intent.putExtra("key", service.getKey());
                                intent.putExtra("serviceName", service.getName());
                                intent.putExtra("price", String.valueOf(service.getPrice()));
                                context.startActivity(intent);
                            } else if (which == 2) {
                                Dialog discountDialog = new Dialog();
                                int drawableResId = 0;
                                String imageUrl = service.getImageUrl();

                                // Determine drawableResId if imageUrl is not available
                                if (imageUrl == null || imageUrl.isEmpty()) {
                                    if (service.getName().contains("Light Makeup")) {
                                        drawableResId = R.mipmap.imgesample1;
                                    } else if (service.getName().contains("Smokey-eye Makeup")) {
                                        drawableResId = R.mipmap.imagesample3;
                                    } else if (service.getName().contains("Wedding makeup")) {
                                        drawableResId = R.mipmap.imagesample4;
                                    } else if (service.getName().contains("Graduation light makeup look")) {
                                        drawableResId = R.mipmap.imagesample7;
                                    } else if (service.getName().contains("Service and events")) {
                                        drawableResId = R.mipmap.makeup;
                                    }
                                }

                                discountDialog.discountdialog(view.getContext(), userId, service.getName(), drawableResId, imageUrl, service.getGender(),service.getKey());
                            } else if (which == 3) {
                                Intent intent = new Intent(context, delete_eventinfo.class);
                                intent.putExtra("imageUrl", service.getImageUrl());
                                intent.putExtra("name", service.getName());
                                context.startActivity(intent);
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
