package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile3;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile5;
import com.hair.booking.activity.tools.Model.Usermodel;

import java.util.ArrayList;

public class Event_providerAdapter extends RecyclerView.Adapter<Event_providerAdapter.ProviderViewHolder> {
    private ArrayList<Usermodel> providerList;
    private Context context;
    private DatabaseReference databaseReference;

    public Event_providerAdapter(ArrayList<Usermodel> providerList, Context context) {
        this.providerList = providerList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("ADMIN");
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Usermodel provider = providerList.get(position);
        holder.nameTextView.setText("Name: "+provider.getUsername());

        if(provider.getAddress() == null){
            holder.address.setText("N/A");
        }else{
            holder.address.setText("Address: "+provider.getAddress());
        }

        holder.address.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(provider.getImage())
                .placeholder(R.drawable.baseline_person_24)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView);

        DatabaseReference providerStatusRef = databaseReference.child(provider.getKey()).child("online");
        providerStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isOnline = dataSnapshot.getValue(Boolean.class);
                if (isOnline != null) {
                    holder.imageOnline.setVisibility(isOnline ? View.VISIBLE : View.GONE);
                } else {
                    holder.imageOnline.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        holder.itemView.setOnClickListener(view -> {
            openProfile(provider.getEmail(), provider.getUsername(), provider.getImage(),
                    provider.getAddress(), provider.getAge(), provider.getLengthOfService(),
                    provider.getKey(),provider.isOnline());
        });
    }


    public void updateList(ArrayList<Usermodel> newList) {
        providerList = newList;
        notifyDataSetChanged();
    }

    private void openProfile(String email, String username, String image, String address, String age, String lengthOfservice,
                             String key,boolean isOnline) {
        Intent intent = new Intent(context, providerProfile5.class);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("image", image);
        intent.putExtra("address", address);
        intent.putExtra("age", age);
        intent.putExtra("lengthOfservice", lengthOfservice);
        intent.putExtra("key", key);
        intent.putExtra("isOnline", isOnline);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView,address;
        ImageView imageView, imageOnline;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            imageOnline = itemView.findViewById(R.id.imgOnline);
            nameTextView = itemView.findViewById(R.id.provider_name);
            imageView = itemView.findViewById(R.id.provider_image);
        }
    }
}
