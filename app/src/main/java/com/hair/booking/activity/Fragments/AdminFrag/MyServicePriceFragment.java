package com.hair.booking.activity.Fragments.AdminFrag;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.AddService;
import com.hair.booking.activity.MainPageActivity.Admin.Ageandservice;
import com.hair.booking.activity.MainPageActivity.maps.MapSelectActivity;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.DialogUtils.UserProviderDialog;
import com.hair.booking.activity.tools.Model.Review;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.activity.tools.Model.Service_percent;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.Utils.Utils;
import com.hair.booking.activity.tools.adapter.EventsListAdapter;
import com.hair.booking.activity.tools.adapter.ImageAdapter;
import com.hair.booking.activity.tools.adapter.ImageAdapter2;
import com.hair.booking.activity.tools.adapter.ServiceAdapter;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyServicePriceFragment extends Fragment {
    private RecyclerView myserviceRecycler;
    private ServiceAdapter serviceAdapter;
    private DatabaseReference serviceRef;
    private ImageView add,clear;
    private Banner banner;
    private DatabaseReference adminRef,countRef;
    private int bookcount,review;
    private ArrayList<Service>services;
    private SearchView searchservicename;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        myserviceRecycler = view.findViewById(R.id.my_serviceRecycler);
        banner = view.findViewById(R.id.banner);
        add = view.findViewById(R.id.add);
        clear = view.findViewById(R.id.clear);
        searchservicename = view.findViewById(R.id.search);
        services = new ArrayList<>();
        myserviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(user.getUid());
            fetchServices();
        }
        add.setOnClickListener(v -> openAddServiceActivity());
        clear.setOnClickListener(view1 -> clearDiscount());
        intiBanner();
        setupSearchView();
        return view;
    }

    private void clearDiscount() {
        Dialog clearDiscount = new Dialog();
        clearDiscount.clearDiscount(getActivity());
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
        serviceAdapter = new ServiceAdapter(filteredList, getContext());
        myserviceRecycler.setAdapter(serviceAdapter);
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
                    String countofbook = String.valueOf(count);
                    SPUtils.getInstance().put(AppConstans.booknumAdmin,countofbook);
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
                        banner.setAdapter(new ImageAdapter2(services,getContext()))
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


    private void openAddServiceActivity() {
        Intent intent = new Intent(getContext(), AddService.class);
        startActivity(intent);
    }
    private void fetchServices() {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                serviceAdapter = new ServiceAdapter(services, getContext());
                myserviceRecycler.setAdapter(serviceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyServicePriceFragment", "Error fetching services: " + databaseError.getMessage());
            }
        });
    }
}
