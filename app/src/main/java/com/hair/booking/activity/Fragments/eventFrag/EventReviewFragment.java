package com.hair.booking.activity.Fragments.eventFrag;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Guess.Rating_userview;
import com.hair.booking.activity.MainPageActivity.Guess.addReviewActivity2;
import com.hair.booking.activity.tools.Model.Review;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ReviewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventReviewFragment extends Fragment {
    String key = SPUtils.getInstance().getString(AppConstans.providers);
    private DatabaseReference reviewRef;
    private RecyclerView reviewrecycler;
    private String TAG = "ReviewFragment";
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_fragment, container, false);
        initClick(view);
        initView(view);
        fetchdata();
        Log.d(TAG,"key: "+key);
        return view;
    }

    private void fetchdata() {
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(key);
        reviewRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    reviewList.add(review);

                    // Sort the list based on timemilli in descending order
                    Collections.sort(reviewList, (r1, r2) -> Long.compare(r2.getTimemilli(), r1.getTimemilli()));

                    reviewAdapter.notifyDataSetChanged();
                    reviewrecycler.scrollToPosition(0); // Scroll to the top for the latest review
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Review review = dataSnapshot.getValue(Review.class);
                if (review != null) {
                    for (int i = 0; i < reviewList.size(); i++) {
                        if (reviewList.get(i).getReviewId().equals(review.getReviewId())) {
                            reviewList.set(i, review);

                            // Re-sort the list after update
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
                // Handle child moved if necessary, depending on your use case.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read reviews: " + databaseError.getMessage());
            }
        });
    }


    private void initView(View view) {
        reviewrecycler = view.findViewById(R.id.reviewrecycler);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList,getContext());
        reviewrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewrecycler.setAdapter(reviewAdapter);
    }

    private void initClick(View view) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) return;

                switch (v.getId()) {
                    case R.id.ViewAll:
                        Intent intent = new Intent(getActivity(), Rating_userview.class);
                        startActivity(intent);
                        break;
                }
            }
        };
        idListeners(view, clickListener);
    }

    private void idListeners(View view, View.OnClickListener clickListener) {
        view.findViewById(R.id.ViewAll).setOnClickListener(clickListener);
    }
}
