package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.FullScreenImageActivity;
import com.hair.booking.activity.tools.Model.Review;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.idlestar.ratingstar.RatingStarView;
import com.lee.avengergone.DisappearView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    private Context context;
    private String key = SPUtils.getInstance().getString(AppConstans.providers);
    public ReviewAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.username.setText(review.getUsername());
        holder.content.setText(review.getContent());
        holder.ratingBar.setRating(review.getRating());
        holder.ratingBar.setEnabled(false);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(review.getUserId())) {
            holder.dots.setVisibility(View.VISIBLE);
        } else {
            holder.dots.setVisibility(View.GONE);
        }
        holder.dots.setOnClickListener(view -> {
            deleteReview(review.getReviewId(),holder.itemView);
        });

        holder.imageContent.setOnClickListener(view -> {
            String imageUrl = review.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                context.startActivity(intent);
            }
        });

        if (review.getImage().isEmpty()){
            holder.imageContent.setVisibility(View.GONE);
        }else{
            holder.imageContent.setVisibility(View.VISIBLE);

        }

        Glide.with(holder.itemView.getContext())
                .load(review.getUserImage())
                .apply(RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.baseline_person_24))
                .into(holder.userImage);



        Glide.with(holder.itemView.getContext())
                .load(review.getImage())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.imageContent);
    }
    private void deleteReview(String reviewId, View view) {
        Activity activity = (Activity) context;
        new AlertDialog.Builder(activity)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete your review?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DisappearView disappearView = DisappearView.attach(activity);
                    disappearView.execute(view, 1500, new AccelerateInterpolator(0.5f), true);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                                .getReference("reviews")
                                .child(key)
                                .child(reviewId);
                        reviewRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Review deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete review", Toast.LENGTH_SHORT).show();
                                });
                    }, 1500);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView username, content;
        RatingStarView ratingBar;
        ImageView userImage, imageContent, dots;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            dots = itemView.findViewById(R.id.dots);
            userImage = itemView.findViewById(R.id.userImage);
            imageContent = itemView.findViewById(R.id.imageContent);
            username = itemView.findViewById(R.id.username);
            content = itemView.findViewById(R.id.content);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
