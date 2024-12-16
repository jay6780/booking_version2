package com.hair.booking.activity.MainPageActivity.profile;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Provider.Event_provider;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.MainPageActivity.maps.MapsActivity2;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.Event_holderAdapter;

import java.util.Arrays;

public class providerProfile5 extends AppCompatActivity {
    private TextView ages,userAddress,userLenghtexp,name,profiletxt;
    private ImageView profileimage,backBtn;
    AppCompatButton messagebtn;
    private String chatRoomId;
    private String image;
    private String email;
    private String providerName;
    private String address,key;
    private String age,lengthOfservice;
    private String TAG = "providerProfile3";
    private boolean isOnline;
    private DatabaseReference databaseReference;
    private TextView servicesTab, portfolioTab, reviewsTab;
    private ViewPager viewPager;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_profile3);
        changeStatusBarColor(getResources().getColor(R.color.pink));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        viewPager = findViewById(R.id.viewPager);
        Event_holderAdapter adapter = new Event_holderAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        servicesTab = findViewById(R.id.services);
        portfolioTab = findViewById(R.id.rate);
        reviewsTab = findViewById(R.id.reviews);
        profileimage = findViewById(R.id.avatar);
        ages = findViewById(R.id.age);
        name = findViewById(R.id.name);
        backBtn = findViewById(R.id.back);
        userAddress = findViewById(R.id.address);
        userLenghtexp = findViewById(R.id.lenghtofservice);
        profiletxt = findViewById(R.id.profiletxt);
        messagebtn = findViewById(R.id.message);
        profiletxt.setText(R.string.artistsdetails);
        key = getIntent().getStringExtra("key");
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        Log.d(TAG, "Is Online: " + isOnline);
        SPUtils.getInstance().put(AppConstans.KEY, key);
        Log.d("SavedKey", "userId: " + key);
        if (key == null) {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            Intent redirect = new Intent(getApplicationContext(), User_list.class);
            startActivity(redirect);
            overridePendingTransition(0,0);
            finish(); // or redirect
            return;
        }
        String location = getString(R.string.address);
        String names = getString(R.string.name);
        String taon = getString(R.string.yearss);
        image = getIntent().getStringExtra("image");
        providerName = getIntent().getStringExtra("username");
        address = getIntent().getStringExtra("address");
        email = getIntent().getStringExtra("email");
        age = getIntent().getStringExtra("age");
        lengthOfservice = getIntent().getStringExtra("lengthOfservice");
        ages.setText("Age: " + (age != null ? age : "N/A"));
        userLenghtexp.setText((lengthOfservice != null ? lengthOfservice : "N/A")+" "+taon);
        Log.d(TAG, "Storing Provider Name: " + providerName);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        Log.d(TAG, "Storing Email: " + email);
        SPUtils.getInstance().put(AppConstans.providers, key);
        SPUtils.getInstance().put(AppConstans.email, email);
        name.setText(names+": " + (providerName != null ? providerName : "N/A"));
        userAddress.setText(location+": " + (address != null ? address : "N/A"));
        initTabs();
        Glide.with(this)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(profileimage);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
                intent.putExtra("username", providerName);
                intent.putExtra("address",address);
                intent.putExtra("age", age);
                intent.putExtra("lengthOfservice",lengthOfservice);
                intent.putExtra("image",image);
                intent.putExtra("email",email);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        messagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndCreateChatRoom(email,providerName,image);
            }
        });

    }

    private void initTabs() {
        selectTab(servicesTab);
        initialtab(portfolioTab);
        initialtab(reviewsTab);
        servicesTab.setOnClickListener(v -> viewPager.setCurrentItem(0));
        portfolioTab.setOnClickListener(v -> viewPager.setCurrentItem(1));
        reviewsTab.setOnClickListener(v -> viewPager.setCurrentItem(2));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        selectTab(servicesTab);
                        break;
                    case 1:
                        selectTab(portfolioTab);
                        break;
                    case 2:
                        selectTab(reviewsTab);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void selectTab(TextView selectedTab) {
        // Apply maroon color and underline to the selected tab
        resetTabStyle(servicesTab);
        resetTabStyle(portfolioTab);
        resetTabStyle(reviewsTab);
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maroon)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }

    private void initialtab(TextView selectedTab) {
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }

    private void resetTabStyle(TextView tab) {
        SpannableString content = new SpannableString(tab.getText());
        content.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab.setText(content);
    }



    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void checkAndCreateChatRoom(String providerEmail, String providerName,String image) {
        String currentUserEmail = SPUtils.getInstance().getString(AppConstans.userEmail);
        String chatRoomId = createChatRoomId(currentUserEmail, providerEmail);
        databaseReference.child("chatRooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUsers(Arrays.asList(currentUserEmail, providerEmail));
                    databaseReference.child("chatRooms").child(chatRoomId).setValue(chatRoom)
                            .addOnSuccessListener(aVoid -> {
                                Intent intent = new Intent(getApplicationContext(), chatActivity.class);
                                intent.putExtra("chatRoomId", chatRoomId);
                                intent.putExtra("providerName", providerName);
                                intent.putExtra("image",image);
                                intent.putExtra("providerEmail",providerEmail);
                                intent.putExtra("address",address);
                                intent.putExtra("key",key);
                                intent.putExtra("isOnline",isOnline);
                                SavedAta(chatRoomId,providerName,image,providerEmail,address,key,isOnline);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Failed to create chat room", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Intent intent = new Intent(getApplicationContext(), chatActivity.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("providerName", providerName);
                    intent.putExtra("image",image);
                    intent.putExtra("providerEmail",providerEmail);
                    intent.putExtra("address",address);
                    intent.putExtra("key",key);
                    intent.putExtra("isOnline",isOnline);
                    SavedAta(chatRoomId,providerName,image,providerEmail,address,key,isOnline);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to check chat room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SavedAta(String chatRoomId, String providerName, String image, String providerEmail, String address, String key, boolean isOnline) {
        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        SPUtils.getInstance().put(AppConstans.image, image);
        SPUtils.getInstance().put(AppConstans.providerEmail, providerEmail);
        SPUtils.getInstance().put(AppConstans.address, address);
        SPUtils.getInstance().put(AppConstans.key, key);
        SPUtils.getInstance().put(AppConstans.isOnline, isOnline);
    }

    private String createChatRoomId(String email1, String email2) {
        chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
    }
    public void onBackPressed(){
        Intent back = new Intent(getApplicationContext(), Event_provider.class);
        startActivity(back);
        overridePendingTransition(0,0);
        finish();
    }
}