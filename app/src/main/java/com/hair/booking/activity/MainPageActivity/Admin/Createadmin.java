package com.hair.booking.activity.MainPageActivity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.login;

public class Createadmin extends AppCompatActivity {
    AppCompatButton back;
    private EditText nameEditText, emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private EditText phoneEditText;
    private AppCompatButton registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createadmin);
        back = findViewById(R.id.back);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        phoneEditText = findViewById(R.id.Phone);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.registered);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("ADMIN");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void registerUser() {
        final String name = nameEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Please enter your phone number");
            phoneEditText.requestFocus();
            return;
        }

        // Validate the input fields
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Please enter your full name");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Please enter a username");
            usernameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter a password");
            passwordEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password");
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Set display name for the user
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // Send email verification
                            sendEmailVerification(user);

                            // Save user details to Realtime Database
                            saveUserDetailsToDatabase(user.getUid(), name, email, username,phone);

                            Toast.makeText(getApplicationContext(), "Registration successful. Please check your email for verification. "+email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void saveUserDetailsToDatabase(String uid, String name, String email, String username, String phone) {
        DatabaseReference userRef = mDatabase.child(uid);
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("username").setValue(username);
        userRef.child("phone").setValue(phone); // Save phone number
    }

    private void sendEmailVerification(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EmailVerification", "Email sent.");
                        } else {
                            Log.e("EmailVerification", "Failed to send verification email.", task.getException());
                        }
                    }
                });

    }
}
