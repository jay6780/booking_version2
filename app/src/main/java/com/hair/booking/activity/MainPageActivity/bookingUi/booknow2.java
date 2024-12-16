package com.hair.booking.activity.MainPageActivity.bookingUi;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.ColoredDate;
import com.ak.EventObjects;
import com.ak.KalendarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile3;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Model.Schedule;
import com.hair.booking.activity.tools.Model.Schedule3;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ScheduleAdapter3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class booknow2 extends AppCompatActivity {
    private String chatRoomId;
    private String image;
    private String email;
    private String providerName;
    private String address,key,price,heads,phonenumber;
    private String serviceName;
    private String age,lengthOfservice;
    private boolean isOnline;
    private String TAG = "Booknow2";
    private TextView userAddress,userLenghtexp,name,profiletxt;
    private ImageView profileimage,backBtn,bell,messageImg;
    private DatabaseReference databaseReference;
    private RecyclerView myschedule;
    private DatabaseReference adminRef,mySched;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String userName, userImageUrl;
    private List<Schedule3> scheduleList = new ArrayList<>();
    private List<ColoredDate> highlightedDates = new ArrayList<>();
    private Set<Date> selectedDates = new HashSet<>();
    private ScheduleAdapter3 scheduleAdapter;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    AppCompatButton procced;
    KalendarView mKalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booknow2);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        key = getIntent().getStringExtra("key");
        SPUtils.getInstance().put(AppConstans.key, key);
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        serviceName = getIntent().getStringExtra("serviceName");
        Log.d(TAG,"servicename: "+serviceName);
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
        initUi();
        initShowbook();
        messageImg.setOnClickListener(view -> chat());
    }

    private void chat() {
        checkAndCreateChatRoom(email,providerName,image);
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initUi() {
        mKalendarView = findViewById(R.id.kalendar);
        messageImg = findViewById(R.id.messageImg);
        myschedule = findViewById(R.id.myschedule);
        profiletxt = findViewById(R.id.profiletxt);
        bell = findViewById(R.id.bell);
        profileimage = findViewById(R.id.avatar);
        procced = findViewById(R.id.proceed);
        name = findViewById(R.id.name);
        backBtn = findViewById(R.id.back);
        userAddress = findViewById(R.id.address);
        userLenghtexp = findViewById(R.id.lenghtofservice);
        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(view -> onBackPressed());
        procced.setOnClickListener(view -> proccedIntent());
        Glide.with(this)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(profileimage);
        String location = getString(R.string.address);
        String names = getString(R.string.name);
        String taon = getString(R.string.yearss);

        userLenghtexp.setText((lengthOfservice != null ? lengthOfservice : "N/A")+" "+taon);
        Log.d(TAG, "Storing Provider Name: " + providerName);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        Log.d(TAG, "Storing Email: " + email);
        SPUtils.getInstance().put(AppConstans.providers, key);
        SPUtils.getInstance().put(AppConstans.email, email);
        name.setText(names+": " + (providerName != null ? providerName : "N/A"));
        userAddress.setText(location+": " + (address != null ? address : "N/A"));
        scheduleAdapter = new ScheduleAdapter3(scheduleList);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        myschedule.setLayoutManager(horizontalLayoutManager);
        myschedule.setAdapter(scheduleAdapter);
        List<ColoredDate> datesColors = new ArrayList<>();
        datesColors.add(new ColoredDate(new Date(), getResources().getColor(R.color.red_holiday)));
        mKalendarView.setColoredDates(datesColors);
        fetchSchedules();
        profiletxt.setText("Book process");
        List<EventObjects> events = new ArrayList<>();
        events.add(new EventObjects("meeting", new Date()));
        mKalendarView.setEvents(events);
        mKalendarView.setDateSelector(new KalendarView.DateSelector() {
            @Override
            public void onDateClicked(Date selectedDate) {
                handleDateSelection(selectedDate);
            }
        });
        procced.setEnabled(false);
        procced.setAlpha(0.5f);

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


    private void proccedIntent() {
        if (scheduleList.isEmpty()) {
            Toast.makeText(booknow2.this, "No schedule data available for this provider.", Toast.LENGTH_SHORT).show();
            SPUtils spUtils = SPUtils.getInstance();
            String price = AppConstans.pricetag;
            String heads = AppConstans.heads;
            String date = AppConstans.date;
            String time = AppConstans.time;
            spUtils.remove(price);
            spUtils.remove(heads);
            spUtils.remove(date);
            spUtils.remove(time);
            String mergemessageData = price+", "+heads+", "+date+", "+time;
            Log.d(TAG,"Data clear: "+ mergemessageData);
            return;
        }
        String price = SPUtils.getInstance().getString(AppConstans.pricetag);
        String heads = SPUtils.getInstance().getString(AppConstans.heads);
        String date = SPUtils.getInstance().getString(AppConstans.date);
        String time = SPUtils.getInstance().getString(AppConstans.time);
        String servicename = SPUtils.getInstance().getString(AppConstans.servicename);
        Intent intent = new Intent(getApplicationContext(), Paymentreceipt2.class);
        intent.putExtra("email",email);
        intent.putExtra("username", providerName);
        intent.putExtra("image", image);
        intent.putExtra("address", address);
        intent.putExtra("age", age);
        intent.putExtra("lengthOfservice", lengthOfservice);
        intent.putExtra("key", key);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("serviceName", servicename);
        intent.putExtra("price",price);
        intent.putExtra("heads",heads);
        intent.putExtra("phonenumber",phonenumber);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private void fetchSchedules() {
        FirebaseDatabase.getInstance().getReference("Mysched").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scheduleList.clear();
                highlightedDates.clear();
                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    Schedule schedule = scheduleSnapshot.getValue(Schedule.class);
                    if (schedule != null) {
                        scheduleList.add(new Schedule3(schedule.getName(), schedule.getImageUrl(), schedule.getDate(), key, schedule.getTime()));
                        highlightedDates.add(new ColoredDate(schedule.getDate(), getResources().getColor(R.color.red_holiday)));
                    }
                }
                mKalendarView.setColoredDates(highlightedDates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void handleDateSelection(Date selectedDate) {
        // Check if the selected date is highlighted
        boolean isHighlighted = false;
        for (ColoredDate coloredDate : highlightedDates) {
            if (isSameDay(coloredDate.getDate(), selectedDate)) {
                isHighlighted = true;
                break;
            }
        }
        if (!isHighlighted) {
            Toast.makeText(this, "Please select a date that is highlighted.", Toast.LENGTH_SHORT).show();
            procced.setEnabled(false);
            procced.setAlpha(0.5f);
            return;
        }else{
            procced.setEnabled(true);
            procced.setAlpha(1.0f);
        }

        // If highlighted, proceed with the selection
        if (selectedDates.contains(selectedDate)) {
            selectedDates.remove(selectedDate);
        } else {
            selectedDates.add(selectedDate);
        }

        List<Schedule3> filteredSchedules = new ArrayList<>();
        for (Schedule3 schedule : scheduleList) {
            if (isSameDay(schedule.getDate(), selectedDate)) {
                filteredSchedules.add(schedule);
            }
        }
        scheduleAdapter.updateSchedules(filteredSchedules);
    }


    private boolean isSameDay(Date date1, Date date2) {
        return date1.getYear() == date2.getYear() &&
                date1.getMonth() == date2.getMonth() &&
                date1.getDate() == date2.getDate();
    }



    private void checkAndCreateChatRoom(String providerEmail, String providerName,String image) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
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

    private String createChatRoomId(String email1, String email2) {
        chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
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

    public void onBackPressed(){
        SPUtils.getInstance().put(AppConstans.discountservicename,"");
        SPUtils.getInstance().put(AppConstans.discount,"");
        Intent back = new Intent(getApplicationContext(), providerProfile3.class);
        back.putExtra("email", email);
        back.putExtra("username", providerName);
        back.putExtra("image", image);
        back.putExtra("address", address);
        back.putExtra("age", age);
        back.putExtra("lengthOfservice", lengthOfservice);
        back.putExtra("key", key);
        back.putExtra("isOnline", isOnline);
        startActivity(back);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}
