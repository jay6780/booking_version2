package com.hair.booking.activity.tools.DialogUtils;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.AdminDialogCallback;
import com.hair.booking.activity.tools.adapter.AdminAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDialog {
    public void adminDialog(Activity activity, AdminDialogCallback callback) {
        List<Usermodel> adminList = new ArrayList<>();
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Events");

        // First, retrieve data from "ADMIN"
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot adminSnapshot) {
                Map<String, Usermodel> adminMap = new HashMap<>();
                for (DataSnapshot userSnapshot : adminSnapshot.getChildren()) {
                    Usermodel adminUser = userSnapshot.getValue(Usermodel.class);
                    if (adminUser != null) {
                        adminUser.setKey(userSnapshot.getKey());
                        adminMap.put(adminUser.getEmail(), adminUser); // Use email as key for easy lookup
                    }
                }

                // Next, retrieve data from "Events" and merge with "ADMIN"
                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot eventsSnapshot) {
                        for (DataSnapshot eventSnapshot : eventsSnapshot.getChildren()) {
                            Usermodel eventUser = eventSnapshot.getValue(Usermodel.class);
                            if (eventUser != null) {
                                eventUser.setKey(eventSnapshot.getKey());
                                // Merge data if email exists in "ADMIN"
                                if (adminMap.containsKey(eventUser.getEmail())) {
                                    Usermodel existingAdmin = adminMap.get(eventUser.getEmail());
                                    existingAdmin.setPhone(eventUser.getPhone());
                                    existingAdmin.setUsername(eventUser.getUsername());
                                    existingAdmin.setOnline(eventUser.isOnline());
                                    // Add other fields as needed
                                } else {
                                    // If not in "ADMIN", add as a new entry
                                    adminList.add(eventUser);
                                }
                            }
                        }
                        adminList.addAll(adminMap.values()); // Combine updated "ADMIN" data with merged results

                        // Display the dialog with the merged list
                        showAdminDialog(activity, adminList, callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("AdminDialog", "Error fetching data from Events: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AdminDialog", "Error fetching data from ADMIN: " + databaseError.getMessage());
            }
        });
    }

    private void showAdminDialog(Activity activity, List<Usermodel> adminList, AdminDialogCallback callback) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.admin_layout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        RecyclerView recyclerView = dialogView.findViewById(R.id.admin_recycler_view);
        AdminAdapter adapter = new AdminAdapter(activity, adminList, admin -> {
            Log.d("AdminDialog", "Clicked admin key: " + admin.getKey());
            dialog.dismiss();
            callback.onAdminSelected(admin.getKey());
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        dialog.show();
    }
}
