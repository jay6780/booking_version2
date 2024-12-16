package com.hair.booking.activity.MainPageActivity.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.ColoredDate;
import com.ak.EventObjects;
import com.ak.KalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.activity.tools.Model.Schedule;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.tools.adapter.ScheduleAdapter2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scheduleview extends AppCompatActivity {
    private RecyclerView myschedule;
    private List<Schedule> scheduleList = new ArrayList<>();
    private String userName, userImageUrl;
    private Set<Date> selectedDates = new HashSet<>();
    private List<ColoredDate> highlightedDates = new ArrayList<>();
    private ScheduleAdapter2 scheduleAdapter;
    private ImageView clear;
    private TextView title;
    private String key;
    KalendarView mKalendarView;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    String providerName = SPUtils.getInstance().getString(AppConstans.providerName);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduleview);

        changeStatusBarColor(getResources().getColor(R.color.bgColor));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        key = getIntent().getStringExtra("key");
        mKalendarView = findViewById(R.id.kalendar);
        myschedule = findViewById(R.id.myschedule);
        title = findViewById(R.id.name);
        clear = findViewById(R.id.clear);
        initFirebase();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        title.setText(providerName+" schedule");
        title.setTextSize(15);
        List<ColoredDate> datesColors = new ArrayList<>();
        datesColors.add(new ColoredDate(new Date(), getResources().getColor(R.color.red_holiday)));
        mKalendarView.setColoredDates(datesColors);
        clear.setVisibility(View.GONE);
        scheduleAdapter = new ScheduleAdapter2(scheduleList);


        myschedule.setLayoutManager(new LinearLayoutManager(this));
        myschedule.setAdapter(scheduleAdapter);

        fetchSchedules();
        List<EventObjects> events = new ArrayList<>();
        events.add(new EventObjects("meeting", new Date()));
        mKalendarView.setEvents(events);
        mKalendarView.setMonthChanger(changedMonth -> Log.d("Changed", "month changed " + changedMonth));

        View.OnClickListener clickListener = v -> {
            if (v.getId() == R.id.back) {
                onBackPressed();

            }
        };
        idListeners(clickListener);
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
                        scheduleList.add(schedule);
                        highlightedDates.add(new ColoredDate(schedule.getDate(), getResources().getColor(R.color.red_holiday)));
                    }
                }
                mKalendarView.setColoredDates(highlightedDates);
                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void initFirebase() {
            FirebaseDatabase.getInstance().getReference("ADMIN").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.child("name").getValue(String.class);
                        userImageUrl = dataSnapshot.child("image").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
                }
            });

    }

    private void checkAndSaveSchedule() {
        saveScheduleToFirebase();
    }

    private void saveScheduleToFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("UserSchedule").child(userId);

            for (Date date : selectedDates) {
                String scheduleId = scheduleRef.push().getKey();
                if (scheduleId != null) {
                    Schedule schedule = new Schedule(userName, userImageUrl, date);

                    // Save the schedule
                    scheduleRef.child(scheduleId).setValue(schedule)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to save schedule.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }
    }

    private void idListeners(View.OnClickListener clickListener) {
        findViewById(R.id.back).setOnClickListener(clickListener);
        findViewById(R.id.clear).setOnClickListener(clickListener);
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        String chatRoomId = SPUtils.getInstance().getString(AppConstans.ChatRoomId);
        String image = SPUtils.getInstance().getString(AppConstans.image);
        String providerEmail = SPUtils.getInstance().getString(AppConstans.providerEmail);
        String address = SPUtils.getInstance().getString(AppConstans.address);
        boolean isOnline = SPUtils.getInstance().getBoolean(AppConstans.isOnline);
        Intent intent = new Intent(getApplicationContext(), chatActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("providerName", providerName);
        intent.putExtra("image",image);
        intent.putExtra("providerEmail",providerEmail);
        intent.putExtra("address",address);
        intent.putExtra("key",key);
        intent.putExtra("isOnline",isOnline);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
        super.onBackPressed();
    }
}
