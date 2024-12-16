package com.hair.booking.activity.MainPageActivity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Review;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ReviewAdapter;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rating_adminview extends AppCompatActivity {
    private TextView rateTextView;
    private RatingReviews ratingReviews;
    private String username = SPUtils.getInstance().getString(AppConstans.AdminUsername);
    private String image = SPUtils.getInstance().getString(AppConstans.AdminImage);
    private ImageView userImage,back;
    private TextView usernameTxt;
    private RecyclerView commentRecycler;
    private DatabaseReference reviewRef;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private String TAG = "Rating_adminview";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_adminview);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        rateTextView = findViewById(R.id.rateTextView);
        userImage = findViewById(R.id.userImage);
        ratingReviews = findViewById(R.id.rating_reviews);
        back = findViewById(R.id.back);
        commentRecycler = findViewById(R.id.commentRecycler);
        usernameTxt = findViewById(R.id.username);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList,Rating_adminview.this);
        commentRecycler.setLayoutManager(new LinearLayoutManager(Rating_adminview.this));
        commentRecycler.setAdapter(reviewAdapter);
        initViewRate();
        initLoaduserDetail();
        fetchdata();
        back.setOnClickListener(view -> onBackPressed());
    }

    private void initLoaduserDetail() {
        usernameTxt.setText(username);
        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(userImage);
        } else {
            userImage.setImageResource(R.mipmap.man);
        }
    }

    private void fetchdata() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(uid);
        reviewRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    reviewList.add(review);
                    reviewAdapter.notifyItemInserted(reviewList.size() - 1);
                    commentRecycler.scrollToPosition(reviewList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    for (int i = 0; i < reviewList.size(); i++) {
                        if (reviewList.get(i).getReviewId().equals(review.getReviewId())) {
                            reviewList.set(i, review);
                            reviewAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    for (int i = 0; i < reviewList.size(); i++) {
                        if (reviewList.get(i).getReviewId().equals(review.getReviewId())) {
                            reviewList.remove(i);
                            reviewAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child moved if necessary, depending on your use case.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read reviews: " + databaseError.getMessage());
            }
        });
    }


    private void initViewRate() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalRating = 0;
                    int reviewCount = 0;
                    int[] ratingCounts = new int[5]; // For ratings 5, 4, 3, 2, 1
                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                        Integer rating = reviewSnapshot.child("rating").getValue(Integer.class);
                        if (rating != null && rating >= 1 && rating <= 5) {
                            totalRating += rating;
                            reviewCount++;
                            ratingCounts[5 - rating]++; // Map 5 -> 0, 4 -> 1, ..., 1 -> 4
                        }
                    }
                    if (reviewCount > 0) {
                        float averageRating = (float) totalRating / reviewCount;
                        rateTextView.setText(String.format("%.1f", averageRating));
                    }
                    int colors[] = new int[]{
                            Color.parseColor("#0e9d58"),
                            Color.parseColor("#bfd047"),
                            Color.parseColor("#ffc105"),
                            Color.parseColor("#ef7e14"),
                            Color.parseColor("#d36259")
                    };
                    ratingReviews.createRatingBars(100, BarLabels.STYPE2, colors, ratingCounts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchFilteredPackages", "Failed to fetch data: " + error.getMessage());
            }
        });
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}