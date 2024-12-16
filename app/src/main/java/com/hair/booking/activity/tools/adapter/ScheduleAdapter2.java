package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter2 extends RecyclerView.Adapter<ScheduleAdapter2.ScheduleViewHolder> {
    private final List<Schedule> schedules;

    public ScheduleAdapter2(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule2, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView dateTextView;
        private final ImageView myprofile;
        private final AppCompatButton setbtn;
        private String userName, userImageUrl,address; // Variables for user data

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            setbtn = itemView.findViewById(R.id.savedsched);
            nameTextView = itemView.findViewById(R.id.schedule_name);
            dateTextView = itemView.findViewById(R.id.schedule_date);
            myprofile = itemView.findViewById(R.id.myimage);
        }

        public void bind(Schedule schedule) {
            nameTextView.setText(schedule.getName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(schedule.getDate());
            dateTextView.setText(formattedDate);
            Glide.with(itemView.getContext())
                    .load(schedule.getImageUrl())
                    .transform(new CircleCrop())
                    .into(myprofile);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("Guess").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.child("username").getValue(String.class);
                        userImageUrl = dataSnapshot.child("image").getValue(String.class);
                        address = dataSnapshot.child("email").getValue(String.class);
                    }

                    // Set up the button click listener
                    setbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an AlertDialog for confirmation
                            new AlertDialog.Builder(itemView.getContext())
                                    .setTitle("Confirm Schedule")
                                    .setMessage("Are you sure you want to set this schedule for " + userName + " on " + formattedDate + "?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Schedule newSchedule = new Schedule(userName, userImageUrl, schedule.getDate());
                                            FirebaseAuth auth = FirebaseAuth.getInstance();
                                            String userId = auth.getCurrentUser().getUid();
                                            String key = SPUtils.getInstance().getString(AppConstans.key);
                                            String mergedUserIdKey = userId + "_" + key;
                                            DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("UserSchedule").child(mergedUserIdKey);
                                            String scheduleId = scheduleRef.push().getKey();
                                            if (scheduleId != null) {
                                                scheduleRef.child(scheduleId).setValue(newSchedule)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                String chatRoomId = SPUtils.getInstance().getString(AppConstans.ChatRoomId);
                                                                String image = SPUtils.getInstance().getString(AppConstans.image);
                                                                String providerEmail = SPUtils.getInstance().getString(AppConstans.providerEmail);
                                                                String address = SPUtils.getInstance().getString(AppConstans.address);
                                                                boolean isOnline = SPUtils.getInstance().getBoolean(AppConstans.isOnline);
                                                                String providerName = SPUtils.getInstance().getString(AppConstans.providerName);
                                                                Intent intent = new Intent(itemView.getContext(), chatActivity.class);
                                                                intent.putExtra("chatRoomId", chatRoomId);
                                                                intent.putExtra("providerName", providerName);
                                                                intent.putExtra("image",image);
                                                                intent.putExtra("providerEmail",providerEmail);
                                                                intent.putExtra("address",address);
                                                                intent.putExtra("key",key);
                                                                intent.putExtra("isOnline",isOnline);
                                                                intent.putExtra("schedule", formattedDate);
                                                                itemView.getContext().startActivity(intent);
                                                                if (itemView.getContext() instanceof Activity) {
                                                                    ((Activity) itemView.getContext()).finish();
                                                                }
                                                                Toast.makeText(itemView.getContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(itemView.getContext(), "Failed to save schedule.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
                }
            });
        }
    }
}