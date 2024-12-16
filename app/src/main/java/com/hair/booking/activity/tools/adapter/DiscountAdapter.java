package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.profile.profileDiscount;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile2;
import com.hair.booking.activity.tools.Model.Discounts;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {

    private Context context;
    private List<Discounts> discountList;
    private DatabaseReference adminRef,eventRef;

    public DiscountAdapter(Context context, List<Discounts> discountList) {
        this.context = context;
        this.discountList = discountList;
        this.adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        this.eventRef = FirebaseDatabase.getInstance().getReference("Events");
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        Discounts discount = discountList.get(position);
        holder.serviceName.setText(discount.getServiceName());
        holder.discount.setText(discount.getDiscount()+" %");
        Glide.with(context)
                .load(discount.getImageUrl())
                .circleCrop()
                .into(holder.image);
        holder.appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfile(discount.getServiceName(),discount.getDiscount(),discount.getUserId(), context);
            }
        });

    }

    private void gotoProfile(String servicename, String discount, String userId, Context context) {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(userId);
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(userId);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot adminSnapshot) {
                if (adminSnapshot.exists()) {
                    String address = adminSnapshot.child("address").getValue(String.class);
                    String age = adminSnapshot.child("age").getValue(String.class);
                    String email = adminSnapshot.child("email").getValue(String.class);
                    String image = adminSnapshot.child("image").getValue(String.class);
                    String lengthOfService = adminSnapshot.child("lengthOfService").getValue(String.class);
                    String name = adminSnapshot.child("name").getValue(String.class);
                    boolean online = adminSnapshot.child("online").getValue(Boolean.class);
                    String username = adminSnapshot.child("username").getValue(String.class);
                    openProfile(email, username, image,
                            address, age,lengthOfService, userId,online,servicename,discount);
                    Log.d("ADMIN_DATA", "Name: " + name + ", Email: " + email);
                } else {
                    Log.w("ADMIN_DATA", "No ADMIN data found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ADMIN_DATA", "Failed to read ADMIN data", error.toException());
            }
        });
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot eventSnapshot) {
                if (eventSnapshot.exists()) {
                    String address = eventSnapshot.child("address").getValue(String.class);
                    String age = eventSnapshot.child("age").getValue(String.class);
                    String email = eventSnapshot.child("email").getValue(String.class);
                    String image = eventSnapshot.child("image").getValue(String.class);
                    String lengthOfService = eventSnapshot.child("lengthOfService").getValue(String.class);
                    String name = eventSnapshot.child("name").getValue(String.class);
                    boolean online = eventSnapshot.child("online").getValue(Boolean.class);
                    String username = eventSnapshot.child("username").getValue(String.class);
                    openProfile(email, username, image,
                            address, age,lengthOfService, userId,online,servicename,discount);
                    Log.d("EVENT_DATA", "Name: " + name + ", Email: " + email);
                } else {
                    Log.w("EVENT_DATA", "No Events data found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EVENT_DATA", "Failed to read Events data", error.toException());
            }
        });
    }

    private void openProfile(String email, String username, String image, String address, String age, String lengthOfservice,
                             String key,boolean isOnline,String servicename,String discount) {
        Intent intent = new Intent(context, profileDiscount.class);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("image", image);
        intent.putExtra("address", address);
        intent.putExtra("age", age);
        intent.putExtra("lengthOfservice", lengthOfservice);
        intent.putExtra("key", key);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("servicename", servicename);
        intent.putExtra("discount", discount);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    static class DiscountViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, discount;
        ImageView image;
        AppCompatButton appointment;

        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.service_name);
            appointment = itemView.findViewById(R.id.appointment);
            discount = itemView.findViewById(R.id.discount_value);
            image = itemView.findViewById(R.id.service_image);
        }
    }
}
