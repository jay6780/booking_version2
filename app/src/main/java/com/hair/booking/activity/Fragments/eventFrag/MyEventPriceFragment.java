package com.hair.booking.activity.Fragments.eventFrag;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.AddService;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.events.AddEvent;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.activity.tools.Model.Service_percent;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.Utils;
import com.hair.booking.activity.tools.adapter.EventsListAdapter;
import com.hair.booking.activity.tools.adapter.ImageAdapter2;
import com.hair.booking.activity.tools.adapter.ImageAdapter3;
import com.hair.booking.activity.tools.adapter.ServiceAdapter;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyEventPriceFragment extends Fragment {
    private RecyclerView myserviceRecycler;
    private EventsListAdapter eventsListAdapter;
    private DatabaseReference serviceRef;
    private ImageView add,clear;
    private DatabaseReference adminRef,countRef;
    private Banner banner;
    private int bookcount,review;
    private ArrayList<Service>services;
    private SearchView searchservicename;
    private LinearLayout home_layout,root_view;
    private SkeletonScreen skeletonScreen;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        myserviceRecycler = view.findViewById(R.id.my_serviceRecycler);
        add = view.findViewById(R.id.add);
        banner = view.findViewById(R.id.banner);
        searchservicename = view.findViewById(R.id.search);
        clear = view.findViewById(R.id.clear);
        root_view = view.findViewById(R.id.root_view);
        home_layout = view.findViewById(R.id.home_layout);
        myserviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        services = new ArrayList<>();
        if (user != null) {
            serviceRef = FirebaseDatabase.getInstance().getReference("EventOrg").child(user.getUid());
            fetchServices();
        }
        add.setOnClickListener(v -> openAddServiceActivity());
        clear.setOnClickListener(view1 -> clearDiscount());
        intiBanner();
        setupSearchView();
        initSkeleton();
        return view;
    }


    private void initSkeleton() {
        home_layout.setVisibility(View.VISIBLE);
        skeletonScreen = Skeleton.bind(root_view)
                .load(R.layout.skeletonlayout_2)
                .duration(1000)
                .color(R.color.colorFontGreyDark)
                .angle(20)
                .show();
        new Handler().postDelayed(() -> {
            skeletonScreen.hide();
            root_view.setVisibility(View.GONE);
        }, 1000);
    }


    private void clearDiscount() {
        Dialog clearDiscount = new Dialog();
        clearDiscount.clearDiscount(getActivity());
    }

    private void intiBanner() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            adminRef = FirebaseDatabase.getInstance().getReference("reviews").child(userId);
            countRef = FirebaseDatabase.getInstance().getReference("BookCount").child(userId);
            countRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer count = dataSnapshot.child("count").getValue(Integer.class);
                    bookcount = count != null ? count : 0;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
                }
            });
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalRating = 0;
                    int reviewCount = 0;
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        Integer rating = reviewSnapshot.child("rating").getValue(Integer.class);
                        if (rating != null) {
                            totalRating += rating;
                            reviewCount++;
                        }
                    }
                    if (reviewCount > 0) {
                        float averageRating = (float) totalRating / reviewCount;
                        review = (int) averageRating;
                    } else {
                        review = 0;
                    }
                    final List<Service_percent> services = new ArrayList<>();
                    services.add(new Service_percent("Total Booking", bookcount));
                    services.add(new Service_percent("Customer Satisfaction", review));
                    double sd = 2.8;
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    if (getActivity() != null) {
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int screenWidth = displayMetrics.widthPixels - Utils.dp2px(getActivity(), 20);
                        int height = (int) (screenWidth / sd) + Utils.dp2px(getActivity(), 20);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(screenWidth, height);
                        lp.leftMargin = Utils.dp2px(getActivity(), 10);
                        lp.rightMargin = Utils.dp2px(getActivity(), 10);
                        lp.topMargin = Utils.dp2px(getActivity(), 5);
                        banner.setLayoutParams(lp);
                        banner.setAdapter(new ImageAdapter3(services,getContext()))
                                .setIndicator(new CircleIndicator(getActivity()))
                                .setOnBannerListener((data, position) -> {
                                })
                                .start();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
                }
            });
        }
    }

    private void setupSearchView() {
        searchservicename.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; 
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterServices(newText);
                return true;
            }
        });
    }

    private void filterServices(String query) {
        List<Service> filteredList = new ArrayList<>();
        for (Service service : services) {
            if (service.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(service);
            }
        }
        eventsListAdapter = new EventsListAdapter(filteredList, getContext());
        myserviceRecycler.setAdapter(eventsListAdapter);
    }

    private void openAddServiceActivity() {
        Intent intent = new Intent(getContext(), AddEvent.class);
        startActivity(intent);
    }
    private void fetchServices() {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("EventOrg").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        serviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                services.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        services.add(service);
                    }
                }
                eventsListAdapter = new EventsListAdapter(services, getContext());
                myserviceRecycler.setAdapter(eventsListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyServicePriceFragment", "Error fetching services: " + databaseError.getMessage());
            }
        });
    }
}
