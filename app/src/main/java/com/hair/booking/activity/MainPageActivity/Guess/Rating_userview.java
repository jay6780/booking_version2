package com.hair.booking.activity.MainPageActivity.Guess;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.addreview.addReviewActivity;
import com.hair.booking.activity.tools.Model.Review;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ReviewAdapter;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rating_userview extends AppCompatActivity {
    String key = SPUtils.getInstance().getString(AppConstans.key);
    private TextView rateTextView;
    private RatingReviews ratingReviews;
    private String username;
    private String image;
    private ImageView userImage,back;
    private TextView usernameTxt;
    private RecyclerView commentRecycler;
    private DatabaseReference reviewRef;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private String TAG = "Rating_adminview";
    private ImageView addreview;
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
        addreview = findViewById(R.id.addreview);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList, Rating_userview.this);
        commentRecycler.setLayoutManager(new LinearLayoutManager(Rating_userview.this));
        commentRecycler.setAdapter(reviewAdapter);
        initViewRate();
        fetchdata();
        back.setOnClickListener(view -> onBackPressed());
        initloadProviderdata();
        addreview.setVisibility(View.VISIBLE);
        addreview.setOnClickListener(view -> addreviews());
    }

    private void addreviews() {
        Intent intent = new Intent(Rating_userview.this, addReviewActivity.class);
        startActivity(intent);
    }


    private void initloadProviderdata() {
        if (key != null && !key.isEmpty()) {
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(key);
            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Events").child(key);

            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            username = user.getUsername();
                            image = user.getImage();
                            updateUserDetails(username, image);
                        }
                    } else {
                        loadFromEvents(eventsRef);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data from ADMIN: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e(TAG, "Invalid key provided.");
        }
    }

    private void loadFromEvents(DatabaseReference eventsRef) {
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usermodel user = dataSnapshot.getValue(Usermodel.class);
                    if (user != null) {
                        username = user.getUsername();
                        image = user.getImage();
                        updateUserDetails(username, image);
                    }
                } else {
                    Log.e(TAG, "No data found for the provided key in both ADMIN and Events.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data from Events: " + databaseError.getMessage());
            }
        });
    }

    private void updateUserDetails(String username, String image) {
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
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(key);
        reviewRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    reviewList.add(review);
                    Collections.sort(reviewList, (r1, r2) -> Long.compare(r2.getTimemilli(), r1.getTimemilli()));

                    reviewAdapter.notifyDataSetChanged();
                    commentRecycler.scrollToPosition(0);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    for (int i = 0; i < reviewList.size(); i++) {
                        if (reviewList.get(i).getReviewId().equals(review.getReviewId())) {
                            reviewList.set(i, review);
                            Collections.sort(reviewList, (r1, r2) -> Long.compare(r2.getTimemilli(), r1.getTimemilli()));
                            reviewAdapter.notifyDataSetChanged();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read reviews: " + databaseError.getMessage());
            }
        });
    }

    private void initViewRate() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(key);
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