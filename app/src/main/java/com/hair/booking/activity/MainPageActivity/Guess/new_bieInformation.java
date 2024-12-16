package com.hair.booking.activity.MainPageActivity.Guess;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.facebook_login;
import com.hair.booking.activity.MainPageActivity.newHome;

import java.util.HashMap;
import java.util.Map;

public class new_bieInformation extends AppCompatActivity {

    private TextInputEditText  emailEditText;
    private DatabaseReference guessRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bie_information);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        emailEditText = findViewById(R.id.email);
        findViewById(R.id.saved).setOnClickListener(this::saveDataToFirebase);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        emailEditText.setText(userEmail);
        guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void saveDataToFirebase(View view) {
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

            guessRef.child("email").setValue(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(new_bieInformation.this, newHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Information saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save data. Try again.", Toast.LENGTH_SHORT).show();
                }
            });

    }
}
