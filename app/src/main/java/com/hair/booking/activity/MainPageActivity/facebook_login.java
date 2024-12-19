package com.hair.booking.activity.MainPageActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import org.json.JSONObject;

import java.util.Arrays;

public class facebook_login extends AppCompatActivity {
    private CallbackManager callbackManager;
    private String TAG = "facebook_login";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        mDatabase = FirebaseDatabase.getInstance().getReference("Guess");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        AccessToken token = loginResult.getAccessToken();
                        if (token.getPermissions().contains("email")) {
                            handleFacebookAccessToken(token);
                            requestUserEmail(token);
                        }
                    }
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Facebook login canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // Handle error event
                        Log.e(TAG, "Facebook login error: " + exception.getMessage());
                    }
                });

    }

    private void requestUserEmail(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (object != null) {
                                String email = object.optString("email");
                                Log.d(TAG, "Facebook Email: " + email);
                                SPUtils.getInstance().put(AppConstans.emailAuthenticaion, email);

                                // Update Firebase email if Facebook email is available
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && email != null) {
                                    user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Firebase email updated to: " + email);
                                                Toast.makeText(getApplicationContext(), "Email updated successfully in Firebase!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e(TAG, "Error updating Firebase email: " + task.getException());
                                                Toast.makeText(getApplicationContext(), "Error updating email in Firebase!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting email: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Error retrieving email from Facebook!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email"); // Request email field
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Access email here
                            String email = user.getEmail();  // This is the email used in Facebook login
                            Log.d(TAG, "User email: " + email);
                            updateUI(user);
                        } else {
                            Toast.makeText(facebook_login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String username = user.getDisplayName();
            String phone = user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A";

            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        saveUserDetailsToDatabase(uid, name, email, username, phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e(TAG, "Database error: " + error.getMessage());
                }
            });

            Toast.makeText(facebook_login.this, "Welcome, " + name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(facebook_login.this, newHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }

    private void saveUserDetailsToDatabase(String uid, String name, String email, String username, String phone) {
        DatabaseReference userRef = mDatabase.child(uid);
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("username").setValue(username);
        userRef.child("phone").setValue(phone);
        Log.d(TAG, "User details saved successfully.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(facebook_login.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}