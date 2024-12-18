package com.hair.booking.activity.MainPageActivity.chat;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.Event_userchat;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ProviderAdapter;
import com.hair.booking.activity.tools.adapter.emptyAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User_list extends AppCompatActivity {
    RecyclerView providerRecycler;
    DatabaseReference databaseReference, chatRooms,events;
    ArrayList<Usermodel> providerList;
    ProviderAdapter providerAdapter;
    private emptyAdapter emptyAdapter;
    private ImageView backBtn;
    private String currentUserEmail; // Current user's email
    private SearchView userSearch;
    private SkeletonScreen skeletonScreen;
    private LinearLayout ll_skeleton;
    private boolean isSkeletonShown = false;
    private String imageUrl;
    private ImageView deleteBtn,bell;
    private TextView delete_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_provider);
        providerRecycler = findViewById(R.id.provider);
        ll_skeleton = findViewById(R.id.ll_skeleton);
        deleteBtn = findViewById(R.id.delete);
        bell = findViewById(R.id.bell);
        backBtn = findViewById(R.id.back);
        userSearch = findViewById(R.id.search);
        delete_num = findViewById(R.id.delete_num);
        providerRecycler.setLayoutManager(new LinearLayoutManager(this));
        currentUserEmail = SPUtils.getInstance().getString(AppConstans.userEmail);
        chatRooms = FirebaseDatabase.getInstance().getReference("chatRooms");
        databaseReference = FirebaseDatabase.getInstance().getReference("ADMIN");
        events = FirebaseDatabase.getInstance().getReference("Events");
        imageUrl = getIntent().getStringExtra("imageUrl");
        SPUtils.getInstance().put(AppConstans.ImageUrl, imageUrl);
        providerList = new ArrayList<>();
        providerAdapter = new ProviderAdapter(providerList, this);
        emptyAdapter = new emptyAdapter(this);
        providerRecycler.setAdapter(providerAdapter);
        Intent intent = new Intent(this, MessageNotificationService.class);
        startService(intent);
        initSkeleton();
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setupSearchView();
        initShowbook();
        providerAdapter.setOnSelectionChangeListener(new ProviderAdapter.OnSelectionChangeListener() {
            @Override
            public void onSelectionChanged(int selectedCount) {
                if (selectedCount > 0) {
                    delete_num.setText(String.valueOf(selectedCount));
                    delete_num.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    delete_num.setText("0");
                    delete_num.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                }
            }
        });

        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), history_book.class);
                startActivity(intent);
            }
        });


        backBtn.setOnClickListener(view -> onBackPressed());
        deleteBtn.setOnClickListener(view -> showDeleteConfirmation());
        checkIfUserInChatRoom();
    }

    private void initShowbook() {
        startService(new Intent(this, MessageNotificationService.class));
        TextView badgeCount = findViewById(R.id.badge_count);
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknum);
        if(badgenum == null) {
            badgeCount.setText("0");
            return;
        }

        badgeCount.setVisibility(View.VISIBLE);
        badgeCount.setText(badgenum);

    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this conversation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    providerAdapter.deleteSelectedItems(this);
                    updateDeleteUI();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    providerAdapter.cancelSelection();
                    dialog.dismiss();
                    updateDeleteUI();
                })
                .show();
    }

    private void updateDeleteUI() {
        deleteBtn.setVisibility(View.GONE);
        delete_num.setVisibility(View.GONE);
        delete_num.setText("0");
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
        chatRooms.addValueEventListener(new ValueEventListener() {
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
            fetchProviderForEmail(email, "ADMIN");
            fetchProviderForEmail(email, "Events");
        }
    }
    private void fetchProviderForEmail(String targetEmail, String node) {
        Log.d("email", "Searching for provider with email: " + targetEmail + " in " + node);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(node);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean providerFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String emailInDatabase = snapshot.child("email").getValue(String.class);
                    if (emailInDatabase != null && emailInDatabase.equals(targetEmail)) {
                        Usermodel usermodel = snapshot.getValue(Usermodel.class);
                        if (usermodel != null) {
                            // Check for duplication based on email or key
                            boolean alreadyExists = false;
                            for (Usermodel existingUser : providerList) {
                                if (existingUser.getEmail().equals(targetEmail)) {
                                    alreadyExists = true;
                                    break;
                                }
                            }

                            if (!alreadyExists) {
                                usermodel.setKey(snapshot.getKey());
                                String name = usermodel.getName();
                                boolean isOnline = usermodel.isOnline();
                                SPUtils.getInstance().put(AppConstans.isOnline,isOnline);
                                String time = usermodel.getTimestamp();
                                String log = name +" "+ time+ " "+isOnline;
                                Log.d("timelogs", "UserData: " + log);
                                providerList.add(usermodel);
                                providerAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("User_list", "Provider already exists: " + targetEmail);
                            }
                        }
                        providerFound = true;
                        break;
                    }
                }

                if (!providerFound) {
                    Log.d("User_list", "No provider data found for email: " + targetEmail + " in " + node);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User_list", node + " DatabaseError: " + databaseError.getMessage());
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
        userChat.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(userChat);
        finish();
        super.onBackPressed();
    }
}
