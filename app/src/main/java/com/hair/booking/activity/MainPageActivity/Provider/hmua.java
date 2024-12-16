package com.hair.booking.activity.MainPageActivity.Provider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ArtistAdapter;
import com.hair.booking.activity.tools.adapter.humaAdapter;

import java.util.ArrayList;

public class hmua extends AppCompatActivity {
    RecyclerView providerRecycler;
    DatabaseReference databaseReference;
    ArrayList<Usermodel> providerList;
    ArtistAdapter providerAdapter;
    private TextView profiletxt;
    private ImageView gunting,messageImg,bell;
    private SearchView userSearch;
    private SkeletonScreen skeletonScreen;
    private LinearLayout  ll_skeleton;
    private boolean isSkeletonShown = false;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hmua_layout);
        providerRecycler = findViewById(R.id.provider);
        userSearch = findViewById(R.id.search);
        gunting = findViewById(R.id.gunting);
        bell = findViewById(R.id.bell);
        profiletxt = findViewById(R.id.profiletxt);
        messageImg = findViewById(R.id.messageImg);
        ll_skeleton = findViewById(R.id.ll_skeleton);
        providerRecycler.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("ADMIN");
        ll_skeleton.setVisibility(View.VISIBLE);
        providerList = new ArrayList<>();
        providerAdapter = new ArtistAdapter(providerList, this);
        providerRecycler.setAdapter(providerAdapter);
        profiletxt.setText(getString(R.string.hmua));
        initSkeleton();
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setupSearchView();
        initbadgeCount();
        gunting.setOnClickListener(view -> onBackPressed());
        messageImg.setOnClickListener(view -> intenttochat());
        bell.setOnClickListener(view -> intentToHistory());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                providerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usermodel usermodel = snapshot.getValue(Usermodel.class);
                    usermodel.setKey(snapshot.getKey());
                    providerList.add(usermodel);
                }
                providerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(hmua.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                Log.e("User_list", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    private void initbadgeCount() {
        TextView badgeCount = findViewById(R.id.badge_count);
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknum);
        if(badgenum == null){
            badgeCount.setText("0");
            SPUtils.getInstance().put(AppConstans.booknum, "0");
        }else{
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(badgenum);

        }
    }

    private void intentToHistory() {
        Intent intent = new Intent(getApplicationContext(), history_book.class);
        intent.putExtra("bookprovideremail", bookprovideremail);
        startActivity(intent);
    }

    private void intenttochat() {
        Intent chat = new Intent(getApplicationContext(), User_list.class);{
            startActivity(chat);
        }
    }

    private void initSkeleton() {
        skeletonScreen = Skeleton.bind(ll_skeleton)
                .load(R.layout.skeletonlayout_2)
                .duration(1000)
                .color(R.color.colorFontGreyDark)
                .angle(20)
                .show();
        new android.os.Handler().postDelayed(() -> {
            skeletonScreen.hide();
            providerRecycler.setVisibility(View.VISIBLE);
            ll_skeleton.setVisibility(View.GONE);
        }, 1000);
    }


    private void setupSearchView() {
        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showSkeleton();
                filterProviderList(newText);
                return true;
            }
        });
    }

    private void showSkeleton() {
        if (!isSkeletonShown) {
            skeletonScreen = Skeleton.bind(ll_skeleton)
                    .load(R.layout.skeletonlayout_2)
                    .duration(1000)
                    .color(R.color.colorFontGreyDark)
                    .angle(20)
                    .show();
            isSkeletonShown = true;
        }
    }
    private void hideSkeleton() {
        if (skeletonScreen != null && isSkeletonShown) {
            skeletonScreen.hide();
            isSkeletonShown = false;
        }
    }


    private void filterProviderList(String query) {
        ArrayList<Usermodel> filteredList = new ArrayList<>();
        for (Usermodel user : providerList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        providerAdapter.updateList(filteredList);
        if (filteredList.isEmpty()) {
            hideSkeleton();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideSkeleton();
                }
            }, 1000);
        }
    }
    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }
}
