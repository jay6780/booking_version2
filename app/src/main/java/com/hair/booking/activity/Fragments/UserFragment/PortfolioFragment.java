package com.hair.booking.activity.Fragments.UserFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.portfolioAdapter;

import java.util.ArrayList;
import java.util.List;

;

public class PortfolioFragment extends Fragment {

    private DatabaseReference serviceRef,guessRef;
    private portfolioAdapter serviceAdapter;
    private ViewSkeletonScreen skeletonScreen;
    private LinearLayout ll_skeleton;
    private RecyclerView myserviceRecycler;
    String key = SPUtils.getInstance().getString(AppConstans.providers);
    private String TAG = "ServiceProvidersFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_providers, container, false);
        ll_skeleton = view.findViewById(R.id.ll_skeleton);
        ll_skeleton.setVisibility(View.VISIBLE);
        myserviceRecycler = view.findViewById(R.id.priceList);
        int numberOfColumns = 2;
        myserviceRecycler.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        initSkeleton();
        initGuessData();
        return view;
    }


    private void initGuessData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
            guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            String gender = user.getGender();
                            Log.d(TAG, "Gender: " + gender);
                            fetchServices(gender);
                        }
                    }
                }
                @SuppressLint("LongLogTag")
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
                }
            });
        }
    }

    private void fetchServices(String gender) {
        serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(key);
        serviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Service> services = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        if ("Female".equals(gender) && "Female".equals(service.getGender())) {
                            services.add(service);
                        } else if ("Male".equals(gender) && "Male".equals(service.getGender())) {
                            services.add(service);
                        }
                    }
                }
                serviceAdapter = new portfolioAdapter(services, getContext());
                myserviceRecycler.setAdapter(serviceAdapter);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching services: " + databaseError.getMessage());
            }
        });
    }

    private void initSkeleton() {
        skeletonScreen = Skeleton.bind(ll_skeleton)
                .load(R.layout.skeletonlayout_2)
                .duration(1000)
                .color(R.color.colorFontGreyDark)
                .angle(20)
                .show();
        new Handler().postDelayed(() -> {
            skeletonScreen.hide();
            myserviceRecycler.setVisibility(View.VISIBLE);
            ll_skeleton.setVisibility(View.GONE);
        }, 1000);
    }
}