package com.hair.booking.activity.tools.DialogUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class UserProviderDialog {

    public void serviceDialog(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.service_select))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button Saved = dialogView.findViewById(R.id.saved);
        Button skip = dialogView.findViewById(R.id.skip);

        skip.setOnClickListener(view ->
                dialog.dismiss());

        Saved.setOnClickListener(v -> {
            Saveddatafromservice(dialogView, dialog);
        });
        dialog.show();
    }
    private void Saveddatafromservice(View dialogView, DialogPlus dialog) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Service").child(user.getUid());
            List<String> selectedServices = new ArrayList<>();

            RadioButton light_makeup  = dialogView.findViewById(R.id.light_makeup);
            RadioButton smokey  = dialogView.findViewById(R.id.smokey);
            RadioButton weeding	 = dialogView.findViewById(R.id.weeding);
            RadioButton graduation  = dialogView.findViewById(R.id.graduation );


            if (light_makeup.isChecked()) {
                selectedServices.add("Light Makeup");
            }
            if (smokey.isChecked()) {
                selectedServices.add("Smokey-eye Makeup");
            }
            if (weeding.isChecked()) {
                selectedServices.add("Wedding makeup");
            }
            if (graduation .isChecked()) {
                selectedServices.add("Graduation light makeup look");
            }
            if (!selectedServices.isEmpty()) {
                String key = databaseRef.push().getKey();
                for (String serviceName : selectedServices) {
                    Service service = new Service(serviceName, 0,"","",key);
                    databaseRef.push().setValue(service).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(dialogView.getContext(), "Service saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(dialogView.getContext(), "Error saving service!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.dismiss();
            } else {
                Toast.makeText(dialogView.getContext(), "No service selected!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(dialogView.getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    public void savedData2(Context context, String serviceName, int prices) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.savedprice))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        EditText priceEditText = dialogView.findViewById(R.id.prices);
        Button savedButton = dialogView.findViewById(R.id.saved);
        TextView title = dialogView.findViewById(R.id.title);
        Button skip = dialogView.findViewById(R.id.skip);
        priceEditText.setText(String.valueOf(prices));
        skip.setOnClickListener(view -> dialog.dismiss());

        title.setText(serviceName);
        savedButton.setOnClickListener(v -> {
            String price = priceEditText.getText().toString();
            if (!price.isEmpty()) {
                savePrice2(serviceName, price, context);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a price", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void savePrice2(String serviceName, String price, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("EventOrg").child(user.getUid());
            serviceRef.orderByChild("name").equalTo(serviceName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().child("price").setValue(Integer.parseInt(price)).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Price updated successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error updating price!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MyservicePrice", "Error finding service: " + databaseError.getMessage());
                }
            });
        } else {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    public void savedData(Context context, String serviceName, int prices) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.savedprice))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        EditText priceEditText = dialogView.findViewById(R.id.prices);
        Button savedButton = dialogView.findViewById(R.id.saved);
        TextView title = dialogView.findViewById(R.id.title);
        Button skip = dialogView.findViewById(R.id.skip);
        priceEditText.setText(String.valueOf(prices));
        skip.setOnClickListener(view -> dialog.dismiss());

        title.setText(serviceName);
        savedButton.setOnClickListener(v -> {
            String price = priceEditText.getText().toString();
            if (!price.isEmpty()) {
                savePrice(serviceName, price, context);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a price", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void savePrice(String serviceName, String price, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(user.getUid());
            serviceRef.orderByChild("name").equalTo(serviceName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().child("price").setValue(Integer.parseInt(price)).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Price updated successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error updating price!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MyservicePrice", "Error finding service: " + databaseError.getMessage());
                }
            });
        } else {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }
}