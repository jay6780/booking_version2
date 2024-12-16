package com.hair.booking.activity.MainPageActivity.calendar;

import android.app.AlertDialog;
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
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.OnScheduleLongClickListener;
import com.hair.booking.activity.tools.Model.Schedule;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.Model.Schedule2;
import com.hair.booking.activity.tools.adapter.ScheduleAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarAdmin extends AppCompatActivity implements OnScheduleLongClickListener {
    private RecyclerView myschedule;
    private DatabaseReference adminRef,mySched;
    private List<Schedule2> scheduleList = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String userName, userImageUrl;
    private Set<Date> selectedDates = new HashSet<>();
    private List<ColoredDate> highlightedDates = new ArrayList<>();
    private ScheduleAdapter scheduleAdapter;
    private ImageView clear;
    private TextView title;
    KalendarView mKalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_admin);

        changeStatusBarColor(getResources().getColor(R.color.bgColor));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mKalendarView = findViewById(R.id.kalendar);
        myschedule = findViewById(R.id.myschedule);
        title = findViewById(R.id.name);
        clear = findViewById(R.id.clear);
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        mySched = FirebaseDatabase.getInstance().getReference("Mysched");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        initFirebase();
        title.setText("My schedule");
        List<ColoredDate> datesColors = new ArrayList<>();
        datesColors.add(new ColoredDate(new Date(), getResources().getColor(R.color.red_holiday)));
        mKalendarView.setColoredDates(datesColors);
        clear.setVisibility(View.VISIBLE);
        scheduleAdapter = new ScheduleAdapter(scheduleList,this);
        myschedule.setLayoutManager(new LinearLayoutManager(this));
        myschedule.setAdapter(scheduleAdapter);

        fetchSchedules();
        List<EventObjects> events = new ArrayList<>();
        events.add(new EventObjects("meeting", new Date()));
        mKalendarView.setEvents(events);
        mKalendarView.setDateSelector(new KalendarView.DateSelector() {
            @Override
            public void onDateClicked(Date selectedDate) {
                handleDateSelection(selectedDate);
            }
        });

        mKalendarView.setMonthChanger(changedMonth -> Log.d("Changed", "month changed " + changedMonth));

        View.OnClickListener clickListener = v -> {
            if (v.getId() == R.id.back) {
                onBackPressed();
            } else if (v.getId() == R.id.clear) {
                showClearScheduleDialog();

            }
        };
        idListeners(clickListener);
    }
    private void fetchSchedules() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mySched.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    scheduleList.clear();
                    highlightedDates.clear();
                    for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                        Schedule schedule = scheduleSnapshot.getValue(Schedule.class);
                        if (schedule != null) {
                            String key = scheduleSnapshot.getKey();
                            scheduleList.add(new Schedule2(schedule.getName(), schedule.getImageUrl(), schedule.getDate(), key,schedule.getTime()));
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
    }


    private void initFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(userId);
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        } else {
            Log.e("FirebaseAuth", "No user is signed in.");
        }
    }

    private void handleDateSelection(Date selectedDate) {
        if (selectedDates.contains(selectedDate)) {
            selectedDates.remove(selectedDate);
            removeHighlightForDate(selectedDate);
        } else {
            if (selectedDates.size() < 3) {
                selectedDates.add(selectedDate);
                addHighlightForDate(selectedDate);
                if (selectedDates.size() == 3) {
                    showSaveScheduleDialog();
                }
            } else {
                Toast.makeText(this, "You can only select up to 3 dates!", Toast.LENGTH_SHORT).show();
            }
        }

        updateHighlightedDates();
    }

    private void updateHighlightedDates() {
        highlightedDates.clear();
        for (Date date : selectedDates) {
            highlightedDates.add(new ColoredDate(date, getResources().getColor(R.color.red_holiday)));
        }
        mKalendarView.setColoredDates(highlightedDates);
    }


    private void addHighlightForDate(Date date) {
        highlightedDates.add(new ColoredDate(date, getResources().getColor(R.color.red_holiday)));
        mKalendarView.setColoredDates(highlightedDates);
    }

    private void removeHighlightForDate(Date date) {
        for (int i = 0; i < highlightedDates.size(); i++) {
            if (highlightedDates.get(i).getDate().equals(date)) {
                highlightedDates.remove(i);
                break;
            }
        }
        mKalendarView.setColoredDates(highlightedDates);
    }

    private void showSaveScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Schedule")
                .setMessage("You have selected exactly 3 dates. Do you want to save your schedule?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    checkAndSaveSchedule();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss if not confirmed
                .show();
    }

    private void checkAndSaveSchedule() {
        saveScheduleToFirebase();
    }

    private void saveScheduleToFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Mysched").child(userId);

            for (Date date : selectedDates) {
                String scheduleId = scheduleRef.push().getKey();
                if (scheduleId != null) {
                    Schedule schedule = new Schedule(userName, userImageUrl, date);

                    // Save the schedule
                    scheduleRef.child(scheduleId).setValue(schedule)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CalendarAdmin.this, "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CalendarAdmin.this, "Failed to save schedule.", Toast.LENGTH_SHORT).show();
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

    private void showClearScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear Schedule")
                .setMessage("Are you sure you want to clear all scheduled dates?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearSchedulesFromFirebase();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearSchedulesFromFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Mysched").child(userId);
            scheduleRef.removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CalendarAdmin.this, "All scheduled dates cleared!", Toast.LENGTH_SHORT).show();
                            scheduleList.clear();
                            highlightedDates.clear();
                            selectedDates.clear();
                            mKalendarView.setColoredDates(highlightedDates);
                            scheduleAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CalendarAdmin.this, "Failed to clear scheduled dates.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), newHome.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onScheduleLongClick(Schedule2 schedule) {
        Dialog timepick = new Dialog();
        String key = schedule.getKey();
        timepick.timepicker(this, schedule.getTime(), userName, userImageUrl, key);
    }
}
