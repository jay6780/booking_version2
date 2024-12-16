package com.hair.booking.activity.MainPageActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.Ageandservice;
import com.hair.booking.activity.MainPageActivity.Admin.Createadmin;
import com.hair.booking.activity.MainPageActivity.Admin.MyservicePrice;
import com.hair.booking.activity.MainPageActivity.Admin.UserChat;
import com.hair.booking.activity.MainPageActivity.Guess.Createacc;
import com.hair.booking.activity.MainPageActivity.bookingUi.bookingInterface;
import com.hair.booking.activity.MainPageActivity.calendar.CalendarAdmin;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.maps.MapSelectActivity;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.DialogUtils.UserProviderDialog;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    private static  String TAG = "Hompage";
    private ImageView drawerToggle,logoImage,messageImg,messageImg2,drawerToggle2;
    private DrawerLayout drawerLayout;
    private RelativeLayout rl1;
    private LinearLayout profile, createacc, login, logout,userMenus,pleaseLog,adminMenu;
    private LinearLayout createadmin, updateAdmin;
    private FirebaseAuth mAuth;
    private RelativeLayout menu1,menu2;
    private DatabaseReference guessRef, adminRef,serviceRef;
    private ImageView guessImage;
    private TextView userEmail,usernameText,phone,fullname,addressUser,ageAdmin;
    private static final String FIRST_RUN_KEY = "firstRun";
    private String email ="s.realamiler@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesspage);
        changeStatusBarColor(getResources().getColor(R.color.bgColor));
        userEmail = findViewById(R.id.email);
        addressUser = findViewById(R.id.address);
        adminMenu = findViewById(R.id.adminMenu);
        messageImg = findViewById(R.id.messageImg);
        messageImg2 = findViewById(R.id.messageImg2);
        menu1 = findViewById(R.id.menu1);
        menu2 = findViewById(R.id.menu2);
        pleaseLog = findViewById(R.id.pleaselog);
        rl1 = findViewById(R.id.rl);
        userMenus = findViewById(R.id.userMenus);
        usernameText = findViewById(R.id.username);
        createadmin = findViewById(R.id.addAdmin);
        updateAdmin = findViewById(R.id.adminprofile);
        profile = findViewById(R.id.profile);
        drawerToggle2 = findViewById(R.id.toggle2);
        drawerToggle = findViewById(R.id.toggle);
        phone = findViewById(R.id.phone);
        fullname = findViewById(R.id.fullname);
        createacc = findViewById(R.id.register);
        login = findViewById(R.id.login);
        logout = findViewById(R.id.logout);
        guessImage = findViewById(R.id.avatar);
        drawerLayout = findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();
        guessRef = FirebaseDatabase.getInstance().getReference("Guess");
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        serviceRef = FirebaseDatabase.getInstance().getReference("Service");
        loadUserDetails();
        if (isFirstRun()) {
            openNotificationSettings();
        }
        initLogo();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.home) {
                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.register) {
                    Intent intent = new Intent(getApplicationContext(), Createacc.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else if (v.getId() == R.id.login) {
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else if (v.getId() == R.id.profile) {
                    Dialog dialog = new Dialog();
//                    dialog.updateProfile(HomePage.this,guessImage,userEmail,usernameText,phone,fullname,addressUser);

                } else if (v.getId() == R.id.adminprofile) {
                    Dialog dialog = new Dialog();
                    dialog.updateAdminProfile(HomePage.this,guessImage,userEmail,usernameText,phone,fullname,addressUser,ageAdmin,"");

                 } else if (v.getId() == R.id.logout) {
                    Dialog dialog = new Dialog();
                    dialog.logout(HomePage.this);
                } else if (v.getId() == R.id.addAdmin) {
                    Intent intent = new Intent(getApplicationContext(), Createadmin.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.pleaselog) {
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.messageImg) {
                    Intent intent = new Intent(getApplicationContext(), User_list.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.messageImg2) {
                    Intent intent = new Intent(getApplicationContext(), UserChat.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();



                } else if (v.getId() == R.id.service_manage) {
                Intent intent = new Intent(getApplicationContext(), MyservicePrice.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

                } else if (v.getId() == R.id.book) {
                    Intent intent = new Intent(getApplicationContext(), bookingInterface.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.admin_Calendar) {
                    Intent intent = new Intent(getApplicationContext(), CalendarAdmin.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                 }
            }
        };
        idlisterners(clickListener);
}
    private void initLogo() {
        RequestOptions requestOptions = new RequestOptions().circleCrop();
        Glide.with(getApplicationContext())
                .load(R.mipmap.applogo)
                .apply(requestOptions)
                .into(logoImage);
    }

    private boolean isFirstRun() {
        SPUtils spUtils = SPUtils.getInstance();
        boolean isFirstRun = spUtils.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            spUtils.put(FIRST_RUN_KEY, false);
        }

        return isFirstRun;
    }

    private void loadUserDetails() {
        ProgressDialog progressDialog = ProgressDialog.show(this, "", "Loading...", true);
        progressDialog.setCancelable(false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();  // Get current user's email
            adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(userId);
            guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
            serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(userId);

            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null && email.equals(userEmail)) {
                            createadmin.setVisibility(View.VISIBLE);
                        }

                        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                                if (!serviceSnapshot.exists()) {
                                    UserProviderDialog serviceDialog = new UserProviderDialog();
                                    serviceDialog.serviceDialog(HomePage.this);
                                }

                                // Check address and age and proceed accordingly
                                boolean hasAddress = dataSnapshot.hasChild("address");
                                boolean hasAge = dataSnapshot.hasChild("age");

                                if (hasAddress && hasAge) {
                                    String name = user.getName();
                                    storeUserInDatabase(name);
                                    updateUserUI(user, true);  // Update UI for ADMIN user
                                } else {
                                    if (!hasAge) {
                                        Intent startAgeService = new Intent(getApplicationContext(), Ageandservice.class);
                                        startActivity(startAgeService);
                                        finish();
                                        SavedUserType("ADMIN");
                                    } else if (!hasAddress) {
                                        Intent startMapSelect = new Intent(getApplicationContext(), MapSelectActivity.class);
                                        startActivity(startMapSelect);
                                        finish();
                                        SavedUserType("ADMIN");
                                    }
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        // Check Guess reference if admin reference doesn't exist
                        guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Usermodel user = dataSnapshot.getValue(Usermodel.class);
                                    if (user != null) {
                                        String name = user.getName();
                                        storeUserInDatabase(name);
                                        updateUserUI(user, false);  // Update UI for Guess user
                                        SavedUserType("Guess");
                                    }
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        } else {
            loadDefault();  // Handle case where user is null
            progressDialog.dismiss();
        }

        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerToggle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }
    private void SavedUserType(String userType) {
        SPUtils.getInstance().put(AppConstans.USERTYPE, userType);

    }


    private void storeUserInDatabase(String name) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userRef.setValue(userData).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "User data stored successfully");
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to store user data: " + e.getMessage());
        });
    }
    private void updateUserUI(Usermodel user, boolean isAdmin) {
        usernameText.setVisibility(View.VISIBLE);
        userEmail.setVisibility(View.VISIBLE);
        pleaseLog.setVisibility(View.GONE);
        logout.setVisibility(View.VISIBLE);
        if (isAdmin) {
            menu2.setVisibility(View.GONE);
            drawerToggle2.setVisibility(View.VISIBLE);
            drawerToggle.setVisibility(View.GONE);
            menu1.setVisibility(View.VISIBLE);
            adminMenu.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            updateAdmin.setVisibility(View.VISIBLE);
            profile.setVisibility(View.GONE);
            createacc.setVisibility(View.GONE);
            userMenus.setVisibility(View.GONE);
        } else {
            drawerToggle2.setVisibility(View.GONE);
            drawerToggle.setVisibility(View.VISIBLE);
            menu2.setVisibility(View.VISIBLE);
            menu1.setVisibility(View.GONE);
            userMenus.setVisibility(View.VISIBLE);
            profile.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            adminMenu.setVisibility(View.GONE);
            createacc.setVisibility(View.GONE);
            createadmin.setVisibility(View.GONE);
            updateAdmin.setVisibility(View.GONE);
        }

        String image = user.getImage();
        guessImage.setTag(image);
        userEmail.setText(user.getEmail());
        addressUser.setText(user.getAddress());
        fullname.setText(user.getName());
        usernameText.setText(user.getUsername());
        phone.setText(user.getPhone());

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(getApplicationContext())
                    .load(image)
                    .apply(requestOptions)
                    .into(guessImage);
        } else {
            guessImage.setImageResource(R.drawable.baseline_person_24);
        }

        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }
    private void idlisterners(View.OnClickListener clickListener) {
        findViewById(R.id.book).setOnClickListener(clickListener);
        findViewById(R.id.service_manage).setOnClickListener(clickListener);
        findViewById(R.id.messageImg2).setOnClickListener(clickListener);
        findViewById(R.id.home).setOnClickListener(clickListener);
        findViewById(R.id.messageImg).setOnClickListener(clickListener);
        findViewById(R.id.pleaselog).setOnClickListener(clickListener);
        findViewById(R.id.adminprofile).setOnClickListener(clickListener);
        findViewById(R.id.addAdmin).setOnClickListener(clickListener);
        findViewById(R.id.register).setOnClickListener(clickListener);
        findViewById(R.id.login).setOnClickListener(clickListener);
        findViewById(R.id.profile).setOnClickListener(clickListener);
        findViewById(R.id.logout).setOnClickListener(clickListener);
        findViewById(R.id.admin_Calendar).setOnClickListener(clickListener);
    }

        private void loadDefault() {
        menu1.setVisibility(View.GONE);
        menu2.setVisibility(View.VISIBLE);
        usernameText.setVisibility(View.GONE);
        userEmail.setVisibility(View.GONE);
        adminMenu.setVisibility(View.GONE);
        profile.setVisibility(View.GONE);
        logout.setVisibility(View.GONE);
        logoImage.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);
        createadmin.setVisibility(View.GONE);
        createacc.setVisibility(View.VISIBLE);
        userMenus.setVisibility(View.GONE);
        pleaseLog.setVisibility(View.VISIBLE);
        messageImg.setVisibility(View.GONE);
        messageImg2.setVisibility(View.GONE);
    }

    private void openNotificationSettings() {
        Dialog notification = new Dialog();
        notification.notiffDialog(this);
    }
    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
}
