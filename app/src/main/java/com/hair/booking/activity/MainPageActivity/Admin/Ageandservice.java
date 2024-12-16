package com.hair.booking.activity.MainPageActivity.Admin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.newHome;

import java.util.HashMap;

public class Ageandservice extends AppCompatActivity {

    private EditText age, serviceLength;
    private Button savedBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference adminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ageandservice);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        age = findViewById(R.id.Age);
        serviceLength = findViewById(R.id.experience);
        savedBtn = findViewById(R.id.saved);
        mAuth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        savedBtn.setOnClickListener(view -> saveDataToFirebase());
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void saveDataToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = currentUser.getUid();
        String ageValue = age.getText().toString().trim();
        String serviceLengthValue = serviceLength.getText().toString().trim();
        if (TextUtils.isEmpty(ageValue) || TextUtils.isEmpty(serviceLengthValue)) {
            Toast.makeText(this, "Please fill in both fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> adminData = new HashMap<>();
        adminData.put("age", ageValue);
        adminData.put("lengthOfService", serviceLengthValue);
        adminRef.child(uid).updateChildren(adminData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Ageandservice.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Ageandservice.this, newHome.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Ageandservice.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
