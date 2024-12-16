package com.hair.booking.activity.Fragments.UserFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.ColoredDate;
import com.ak.EventObjects;
import com.ak.KalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.activity.tools.DialogUtils.AdminDialog;
import com.hair.booking.activity.tools.Model.AdminDialogCallback;
import com.hair.booking.activity.tools.Model.Schedule;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.R;
import com.hair.booking.activity.tools.adapter.ScheduleAdapterUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarUserFragment extends Fragment implements AdminDialogCallback {
    private RecyclerView myschedule;
    private List<Schedule> scheduleList = new ArrayList<>();
    private String userName, userImageUrl;
    private Set<Date> selectedDates = new HashSet<>();
    private List<ColoredDate> highlightedDates = new ArrayList<>();
    private ScheduleAdapterUser scheduleAdapter;
    private String key;
    KalendarView mKalendarView;
    private FirebaseAuth auth;
    private ImageView dots;
    private List<Usermodel> adminList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scheduleview_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        key = getArguments() != null ? getArguments().getString("key") : null;

        mKalendarView = view.findViewById(R.id.kalendar);
        myschedule = view.findViewById(R.id.myschedule);
        dots = view.findViewById(R.id.dots);
        initFirebase();
        auth = FirebaseAuth.getInstance();
        List<ColoredDate> datesColors = new ArrayList<>();
        datesColors.add(new ColoredDate(new Date(), getResources().getColor(R.color.red_holiday)));
        mKalendarView.setColoredDates(datesColors);
        scheduleAdapter = new ScheduleAdapterUser(scheduleList);
        myschedule.setLayoutManager(new LinearLayoutManager(getContext()));
        myschedule.setAdapter(scheduleAdapter);

        fetchSchedules();

        List<EventObjects> events = new ArrayList<>();
        events.add(new EventObjects("meeting", new Date()));
        mKalendarView.setEvents(events);

        mKalendarView.setMonthChanger(changedMonth -> Log.d("Changed", "month changed " + changedMonth));

        dots.setOnClickListener(view1 -> {
            AdminDialog adminDialog = new AdminDialog();
            adminDialog.adminDialog(getActivity(), CalendarUserFragment.this);
        });

    }

    private void fetchSchedules() {
        if (key == null || key.isEmpty()) {
        Log.e( "NoSched","No schedule for this provider");
        } else {
            FirebaseDatabase.getInstance().getReference("Mysched").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    scheduleList.clear();
                    highlightedDates.clear();
                    Date firstDate = null;
                    for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                        Schedule schedule = scheduleSnapshot.getValue(Schedule.class);
                        if (schedule != null) {
                            scheduleList.add(schedule);
                            highlightedDates.add(new ColoredDate(schedule.getDate(), getActivity().getResources().getColor(R.color.red_holiday)));
                            if (firstDate == null || schedule.getDate().before(firstDate)) {
                                firstDate = schedule.getDate();
                            }
                        }
                    }
                    mKalendarView.setColoredDates(highlightedDates);
                    scheduleAdapter.notifyDataSetChanged();
                    if (firstDate != null) {
                        mKalendarView.setMonth(firstDate);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
                }
            });
        }
    }

    private void initFirebase() {
        if (key == null || key.isEmpty()) {
            Log.d("nodata", "No Guess data");
        } else {
            FirebaseDatabase.getInstance().getReference("ADMIN").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.child("username").getValue(String.class);
                        userImageUrl = dataSnapshot.child("image").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
                }
            });
        }
    }

    @Override
    public void onAdminSelected(String adminKey) {
        key = adminKey;
        fetchSchedules();
    }
}
