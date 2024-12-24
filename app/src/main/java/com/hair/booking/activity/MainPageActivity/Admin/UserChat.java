package com.hair.booking.activity.MainPageActivity.Admin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.adapter.UserchatAdapter;
import com.hair.booking.activity.tools.adapter.emptyAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UserChat extends AppCompatActivity {
    RecyclerView providerRecycler;
    DatabaseReference databaseReference, chatRooms;
    ArrayList<Usermodel> providerList;
    UserchatAdapter providerAdapter;
    private emptyAdapter emptyAdapter;
    private ImageView backBtn;
    private String currentUserEmail;
    private SearchView userSearch;
    private SkeletonScreen skeletonScreen;
    private LinearLayout ll_skeleton;
    private boolean isSkeletonShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_userlayout);
        providerRecycler = findViewById(R.id.provider);
        ll_skeleton = findViewById(R.id.ll_skeleton);
        backBtn = findViewById(R.id.back);
        userSearch = findViewById(R.id.search);
        providerRecycler.setLayoutManager(new LinearLayoutManager(this));
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        chatRooms = FirebaseDatabase.getInstance().getReference("chatRooms");
        databaseReference = FirebaseDatabase.getInstance().getReference("Guess");
        providerList = new ArrayList<>();
        providerAdapter = new UserchatAdapter(providerList, this);
        emptyAdapter = new emptyAdapter(this);
        providerRecycler.setAdapter(providerAdapter);
        ll_skeleton.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MessageNotificationService.class);
        startService(intent);
        initSkeleton();
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        backBtn.setOnClickListener(view -> onBackPressed());
        checkIfUserInChatRoom();
        setupSearchView();
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

    private void checkIfUserInChatRoom() {
        chatRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isUserInChatRoom = false;
                Set<String> connectedUsers = new HashSet<>();
                for (DataSnapshot chatRoom : dataSnapshot.getChildren()) {
                    ArrayList<String> users = (ArrayList<String>) chatRoom.child("users").getValue();
                    if (users != null && users.contains(currentUserEmail)) {
                        isUserInChatRoom = true;
                        for (String email : users) {
                            if (!email.equals(currentUserEmail)) {
                                connectedUsers.add(email);
                            }
                        }
                    }
                }
                if (isUserInChatRoom) {
                    fetchProvidersForConnectedUsers(new ArrayList<>(connectedUsers));
                } else {
                    providerList.clear();
                    providerAdapter.notifyDataSetChanged();
                    providerRecycler.setAdapter(emptyAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User_list", "ChatRoomsError: " + databaseError.getMessage());
            }
        });
    }

    private void fetchProvidersForConnectedUsers(ArrayList<String> connectedUsers) {
        for (String email : connectedUsers) {
            fetchProviderForEmail(email);
        }
    }

    private void fetchProviderForEmail(String targetEmail) {
        Log.d("email", "Searching for provider with email: " + targetEmail);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean providerFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String emailInDatabase = snapshot.child("email").getValue(String.class);
                    if (emailInDatabase != null && emailInDatabase.equals(targetEmail)) {
                        boolean isDuplicate = false;
                        for (Usermodel existingProvider : providerList) {
                            if (existingProvider.getEmail().equals(emailInDatabase)) {
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (!isDuplicate) {
                            Usermodel usermodel = snapshot.getValue(Usermodel.class);
                            if (usermodel != null) {
                                usermodel.setKey(snapshot.getKey());
                                providerList.add(usermodel);
                                providerAdapter.notifyDataSetChanged();
                            }
                        }

                        providerFound = true;
                        break;
                    }
                }

                if (providerFound) {
                    providerAdapter.notifyDataSetChanged();
                } else {
                    Log.d("User_list", "No provider data found for email: " + targetEmail);;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserChat.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                Log.e("User_list", "DatabaseError: " + databaseError.getMessage());
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
        Intent userChat = new Intent(getApplicationContext(), newHome.class);
        startActivity(userChat);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
}
