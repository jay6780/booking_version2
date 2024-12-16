package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.activity.tools.Model.Makeup;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Provider.hmua;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public class MakeupAdapter extends RecyclerView.Adapter<MakeupAdapter.MakeupViewHolder> {
    private List<Makeup> makeupList;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    public MakeupAdapter(List<Makeup> makeupList) {
        this.makeupList = makeupList;
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("myfaceswap");
        storageReference = FirebaseStorage.getInstance().getReference("target_image");
    }

    @NonNull
    @Override
    public MakeupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_makeup, parent, false);
        return new MakeupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MakeupViewHolder holder, int position) {
        Makeup makeup = makeupList.get(position);
        holder.bind(makeup);

        Glide.with(holder.makeupImage.getContext())
                .asBitmap()
                .load(makeup.getImageResId())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.makeupImage.setImageBitmap(resource);
                        uploadMakeupImage(makeup, resource);
                    }
                });

        holder.booking.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), hmua.class);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return makeupList.size();
    }

    public void clear() {
        makeupList.clear();
        notifyDataSetChanged();
    }

    static class MakeupViewHolder extends RecyclerView.ViewHolder {
        private ImageView makeupImage;
        private TextView makeupName;
        private AppCompatButton booking;

        public MakeupViewHolder(@NonNull View itemView) {
            super(itemView);
            booking = itemView.findViewById(R.id.book);
            makeupImage = itemView.findViewById(R.id.makeupImage);
            makeupName = itemView.findViewById(R.id.makeupName);
        }

        public void bind(Makeup makeup) {
            Glide.with(makeupImage.getContext())
                    .load(makeup.getImageResId())
                    .circleCrop()
                    .into(makeupImage);
            makeupName.setText(makeup.getName());
        }
    }

    private void uploadMakeupImage(Makeup makeup, Bitmap bitmap) {
        if (bitmap == null) {
            // Handle null bitmap case
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(fileName);

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    DatabaseReference userRef = databaseReference.child(user.getUid()).child("target_url");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                userRef.setValue(downloadUrl)
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                            } else {
                                userRef.setValue(downloadUrl)
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }).addOnFailureListener(e -> {
                });
            }).addOnFailureListener(e -> {
            });
        } else {
        }

    }
}