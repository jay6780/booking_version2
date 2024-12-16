package com.hair.booking.activity.MainPageActivity.Guess;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.bookingUi.booknow2;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Model.ServiceInfo;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.Package_adapter;
import com.hair.booking.activity.tools.adapter.emptyAdapter_package;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class package_event extends AppCompatActivity implements Package_adapter.OnPriceChangeListener {

    private String chatRoomId;
    private String image;
    private String email;
    private String providerName;
    private String address,key,price,heads,phonenumber;
    private String servicename;
    private String age,lengthOfservice;
    private boolean isOnline;
    private String TAG = "package_event";
    private ImageView event_orgImage,bell,messageImg;
    private TextView nameservice,current_price;
    private String eventImage;
    private RecyclerView package_recyler;
    private DatabaseReference information;
    private List<ServiceInfo> serviceInfoList = new ArrayList<>();
    private Package_adapter adapter;
    private int totalCurrentPrice = 0;
    private AppCompatButton select_schedule,skip;
    private List<String> serviceNamesList = new ArrayList<>();
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    private DatabaseReference databaseReference;
    private ImageView back;
    private emptyAdapter_package emptyAdapter_package;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_event);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        key = getIntent().getStringExtra("key");
        SPUtils.getInstance().put(AppConstans.key, key);
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        Log.d(TAG,"servicename: "+servicename);
        Log.d(TAG, "Is Online: " + isOnline);
        SPUtils.getInstance().put(AppConstans.KEY, key);
        Log.d("SavedKey", "userId: " + key);
        image = getIntent().getStringExtra("image");
        providerName = getIntent().getStringExtra("username");
        address = getIntent().getStringExtra("address");
        email = getIntent().getStringExtra("email");
        age = getIntent().getStringExtra("age");
        lengthOfservice = getIntent().getStringExtra("lengthOfservice");
        price = getIntent().getStringExtra("price");
        heads = getIntent().getStringExtra("heads");
        phonenumber = getIntent().getStringExtra("phonenumber");
        Log.d(TAG,"price: "+price);
        Log.d(TAG,"heads: "+heads);
        Log.d(TAG,"Phone: "+phonenumber);
        event_orgImage = findViewById(R.id.event_orgImage);
        nameservice = findViewById(R.id.nameservice);
        current_price = findViewById(R.id.current_price);
        package_recyler = findViewById(R.id.package_recyler);
        select_schedule = findViewById(R.id.select_schedule);
        skip = findViewById(R.id.skip);
        back = findViewById(R.id.back);
        bell = findViewById(R.id.bell);
        messageImg = findViewById(R.id.messageImg);
        messageImg.setOnClickListener(view -> chat());
        back.setOnClickListener(view -> onBackPressed());
        package_recyler.setLayoutManager(new LinearLayoutManager(this));
        initLoadData();
        initFirebase();
        initShowbook();
        select_schedule.setEnabled(false);
        select_schedule.setAlpha(0.5f);
        skip.setEnabled(true);
        skip.setOnClickListener(v -> {
            String pricetag = SPUtils.getInstance().getString(AppConstans.pricetag);
            Intent intent = new Intent(getApplicationContext(), booknow2.class);
            intent.putExtra("email", email);
            intent.putExtra("username", providerName);
            intent.putExtra("image", image);
            intent.putExtra("address", address);
            intent.putExtra("age", age);
            intent.putExtra("lengthOfservice", lengthOfservice);
            intent.putExtra("key", key);
            intent.putExtra("isOnline", isOnline);
            intent.putExtra("serviceName", servicename);
            intent.putExtra("price", pricetag);
            intent.putExtra("heads", heads);
            intent.putExtra("phonenumber", phonenumber);
            startActivity(intent);
            finish();
        });


        select_schedule.setOnClickListener(v -> {
                String pricetag = SPUtils.getInstance().getString(AppConstans.pricetag);
                Intent intent = new Intent(getApplicationContext(), booknow2.class);
                intent.putExtra("email", email);
                intent.putExtra("username", providerName);
                intent.putExtra("image", image);
                intent.putExtra("address", address);
                intent.putExtra("age", age);
                intent.putExtra("lengthOfservice", lengthOfservice);
                intent.putExtra("key", key);
                intent.putExtra("isOnline", isOnline);
                intent.putExtra("serviceName", servicename);
                intent.putExtra("price", pricetag);
                intent.putExtra("heads", heads);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);
                finish();
        });

    }

    private void chat() {
        checkAndCreateChatRoom(email,providerName,image);
    }

    private void checkAndCreateChatRoom(String providerEmail, String providerName,String image) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
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


    private void initLoadData() {
        servicename = SPUtils.getInstance().getString(AppConstans.servicename);
        eventImage = SPUtils.getInstance().getString(AppConstans.imageService);
        nameservice.setText("Event name: "+servicename);
        current_price.setText("Price: "+price);
        Glide.with(this)
                .load(eventImage)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(event_orgImage);

    }

    private void initFirebase() {
        emptyAdapter_package = new emptyAdapter_package(package_event.this);
        information = FirebaseDatabase.getInstance().getReference("ServiceInfo").child(key);

        information.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceInfoList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServiceInfo serviceInfo = snapshot.getValue(ServiceInfo.class);
                    if (serviceInfo != null && serviceInfo.getServicename().equals(servicename)) {
                        serviceInfoList.add(serviceInfo);
                    }
                }
                if (!serviceInfoList.isEmpty()) {
                    adapter = new Package_adapter(package_event.this, serviceInfoList, package_event.this);
                    adapter.setSelectionChangeListener(isAnyItemSelected -> {
                        if (isAnyItemSelected) {
                            select_schedule.setEnabled(true);
                            select_schedule.setAlpha(1.0f);
                            skip.setEnabled(false);
                            skip.setAlpha(0.5f);
                        } else {
                            SPUtils.getInstance().put(AppConstans.serviceNamesList, "");
                            select_schedule.setEnabled(false);
                            select_schedule.setAlpha(0.5f);
                            skip.setEnabled(true);
                            skip.setAlpha(1.0f);
                        }
                    });
                    package_recyler.setAdapter(adapter);
                } else {
                    package_recyler.setAdapter(emptyAdapter_package);
                }
                select_schedule.setEnabled(false);
                select_schedule.setAlpha(0.5f);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value: " + databaseError.toException());
                Toast.makeText(package_event.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPriceChanged(int updatedPrice, String serviceInfo) {
        try {
            double currentPrice = Double.parseDouble(price);
            double newPrice = currentPrice + updatedPrice;
            price = String.valueOf(newPrice);

            String serviceWithPrice = serviceInfo + " - " + updatedPrice;
            if (updatedPrice < 0) {
                serviceNamesList.removeIf(service -> service.startsWith(serviceInfo));
            } else {
                if (!serviceNamesList.contains(serviceWithPrice)) {
                    serviceNamesList.add(serviceWithPrice);
                }
            }
            SPUtils.getInstance().put(AppConstans.pricetag, price);
            String serviceListJson = new Gson().toJson(serviceNamesList);
            SPUtils.getInstance().put(AppConstans.serviceNamesList, serviceListJson);
            current_price.setText("Price: " + price);

            Log.d("PriceUpdated", "Service: " + serviceInfo + ", Updated Price: " + price);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse price: " + e.getMessage());
            Toast.makeText(package_event.this, "Error updating price", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }


    @Override
    public void onBackPressed() {
        SPUtils.getInstance().put(AppConstans.discountservicename,"");
        SPUtils.getInstance().put(AppConstans.discount,"");
        finish();
        super.onBackPressed();
    }
}