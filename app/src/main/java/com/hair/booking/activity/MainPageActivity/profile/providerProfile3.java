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
import androidx.cardview.widget.CardView;
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
import com.hair.booking.activity.MainPageActivity.Provider.hmua;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.MainPageActivity.maps.MapsActivity2;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.Event_holderAdapter;
import com.hair.booking.activity.tools.adapter.ServiceProviderAdapter;

import java.util.Arrays;

public class providerProfile3 extends AppCompatActivity {
    private TextView userAddress,userLenghtexp,name,profiletxt;
    private ImageView profileimage,backBtn,bell,messageImg;
    CardView messagebtn;
    private String chatRoomId;
    private String image;
    private String email;
    private String providerName;
    private String address,key;
    private String age,lengthOfservice;
    private String TAG = "providerProfile2";
    private boolean isOnline;
    private DatabaseReference databaseReference;
    private TextView servicesTab, reviewsTab;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    private ViewPager viewPager;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_layout);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        viewPager = findViewById(R.id.viewPager);
        Event_holderAdapter adapter = new Event_holderAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        servicesTab = findViewById(R.id.services);
        reviewsTab = findViewById(R.id.reviews);
        profileimage = findViewById(R.id.avatar);
        profiletxt = findViewById(R.id.profiletxt);
        name = findViewById(R.id.name);
        bell = findViewById(R.id.bell);
        backBtn = findViewById(R.id.back);
        messageImg = findViewById(R.id.messageImg);
        userAddress = findViewById(R.id.address);
        userLenghtexp = findViewById(R.id.lenghtofservice);
        messagebtn = findViewById(R.id.message);
        key = getIntent().getStringExtra("key");
        profiletxt.setText(R.string.event_details);
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
        userLenghtexp.setText((lengthOfservice != null ? lengthOfservice : "N/A")+" "+taon);
        Log.d(TAG, "Storing Provider Name: " + providerName);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        Log.d(TAG, "Storing Email: " + email);
        SPUtils.getInstance().put(AppConstans.providers, key);
        SPUtils.getInstance().put(AppConstans.email, email);
        name.setText(names+": " + (providerName != null ? providerName : "N/A"));
        userAddress.setText(location+": " + (address != null ? address : "N/A"));
        messageImg.setOnClickListener(view -> chat());
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

        initShowbook();

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

    private void chat() {
        Intent intent = new Intent(getApplicationContext(), User_list.class);
        startActivity(intent);
        finish();
    }

    private void initShowbook() {
        startService(new Intent(this, MessageNotificationService.class));
        TextView badgeCount = findViewById(R.id.badge_count);
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknum);
        if(badgenum == null){
            badgeCount.setText("0");
            SPUtils.getInstance().put(AppConstans.booknum, "0");
        }else{
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(badgenum);

        }
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), history_book.class);
                intent.putExtra("bookprovideremail", bookprovideremail);
                startActivity(intent);
            }
        });

    }

    private void initTabs() {
        selectTab(servicesTab);
        initialtab(reviewsTab);
        initialgray(reviewsTab);
        servicesTab.setOnClickListener(v -> viewPager.setCurrentItem(0));
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
        resetTabStyle(reviewsTab);
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }

    private void initialtab(TextView selectedTab) {
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }

    private void initialgray(TextView selectedTab) {
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.transaprent2)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }


    private void resetTabStyle(TextView tab) {
        SpannableString content = new SpannableString(tab.getText());
        content.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.transaprent2)), 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        finish();
        super.onBackPressed();
    }
}