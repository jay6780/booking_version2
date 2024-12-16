package com.hair.booking.activity.tools.adapter;

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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile2;
import com.hair.booking.activity.tools.Model.Usermodel;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ProviderViewHolder> {
    private ArrayList<Usermodel> providerList;
    private Context context;
    private DatabaseReference userFavoritesRef;
    private FirebaseAuth mAuth;

    String currentUserId;
    public ArtistAdapter(ArrayList<Usermodel> providerList, Context context) {
        this.providerList = providerList;
        this.context = context;
        this.userFavoritesRef = FirebaseDatabase.getInstance().getReference("favorites");
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_layout, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Usermodel provider = providerList.get(position);
        String name = context.getString(R.string.name);
        String address = context.getString(R.string.address);
        String years = context.getString(R.string.years);
        holder.nameTextView.setText(name+": "+provider.getUsername());

        if(provider.getLengthOfService() == null){
            holder.experience.setText("N/A");
        }else{
            holder.experience.setText(years+": "+provider.getLengthOfService());
        }
        String userId = provider.getKey();
        Log.d("userId", "UserId: " + userId);

        if (userId != null) {
            DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(userId);
            reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int totalRating = 0;
                    int reviewCount = 0;
                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                        Integer rating = reviewSnapshot.child("rating").getValue(Integer.class);
                        if (rating != null) {
                            totalRating += rating;
                            reviewCount++;
                        }
                    }
                    if (reviewCount > 0) {
                        float averageRating = (float) totalRating / reviewCount;
                        holder.ratevalue.setText(String.format("%.1f", averageRating));
                    } else {
                        holder.ratevalue.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProviderAdapter", "Error fetching reviews: " + error.getMessage());
                    holder.ratevalue.setText("0");
                }
            });
        }

        if(provider.getAddress() == null){
            holder.address.setText("N/A");
        }else{
            holder.address.setText(address+": "+provider.getAddress());
        }

        currentUserId = mAuth.getCurrentUser().getUid();
        userFavoritesRef.child(currentUserId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.heart.setImageResource(R.mipmap.heart_fullred);
                } else {
                    holder.heart.setImageResource(R.mipmap.heart_nobg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ArtistAdapter", "Error checking favorite status: " + error.getMessage());
            }
        });

        // Set heart icon click listener to toggle favorite status
        holder.heart.setOnClickListener(view -> {
            currentUserId = mAuth.getCurrentUser().getUid();
            userFavoritesRef.child(currentUserId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Remove from favorites
                        userFavoritesRef.child(currentUserId).child(userId).removeValue();
                        holder.heart.setImageResource(R.mipmap.heart_nobg);
                    } else {
                        // Add to favorites
                        userFavoritesRef.child(currentUserId).child(userId).setValue(true);
                        holder.heart.setImageResource(R.mipmap.heart_fullred);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ArtistAdapter", "Error updating favorite status: " + error.getMessage());
                }
            });
        });


        holder.address.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(provider.getImage())
                .placeholder(R.mipmap.man)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView);
        holder.appointment.setOnClickListener(view -> {
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
        Intent intent = new Intent(context, providerProfile2.class);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("image", image);
        intent.putExtra("address", address);
        intent.putExtra("age", age);
        intent.putExtra("lengthOfservice", lengthOfservice);
        intent.putExtra("key", key);
        intent.putExtra("isOnline", isOnline);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView,address,experience,ratevalue;
        ImageView imageView,heart;
        AppCompatButton appointment;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            ratevalue = itemView.findViewById(R.id.ratevalue);
            heart = itemView.findViewById(R.id.heart);
            experience = itemView.findViewById(R.id.experience);
            address = itemView.findViewById(R.id.address);
            appointment = itemView.findViewById(R.id.appointment);
            nameTextView = itemView.findViewById(R.id.provider_name);
            imageView = itemView.findViewById(R.id.provider_image);
        }
    }
}
