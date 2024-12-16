package com.hair.booking.activity.tools.DialogUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.Application.Account_manager;
import com.hair.booking.activity.Application.Account_manager_admin;
import com.hair.booking.activity.Application.TinkerApplications;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

public class Passworddialog {
    private FirebaseAuth mAuth;

    public void retry(Context context, Account_manager account_manager) {
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.retry, null);
        builder.setView(dialogView);
        Button cancel = dialogView.findViewById(R.id.cancel);
        Button retry = dialogView.findViewById(R.id.retry);
        EditText passwordInput = dialogView.findViewById(R.id.etPassword);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        retry.setOnClickListener(v -> {
            String newPassword = passwordInput.getText().toString().trim();
            account_manager.setPassword(newPassword);
            loginWithAccount(account_manager, context);
            dialog.dismiss();
        });

        cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void loginWithAccount(Account_manager account, Context context) {
        String email = account.getEmail();
        String password = account.getPassword();
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        SPUtils.getInstance().put(AppConstans.passwordkey, password);
                        String userId = user.getUid();
                        proceedWithLogin(userId, context);
                    } else {
                        Toast.makeText(context, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("blocked all requests from this device")) {
                        Toast.makeText(context, "Too many attempts. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void proceedWithLogin(String userId, Context context) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("Guess").child(userId);
        ValueEventListener studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (context instanceof Activity) {
                        TinkerApplications app = (TinkerApplications) context.getApplicationContext();
                        app.updatelogin(true, (Activity) context);
                    } else {
                        Log.e("ClassCastError", "Context is not an instance of TinkerApplications");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        studentRef.addListenerForSingleValueEvent(studentListener);
    }

    public void retry_admin(Context context, Account_manager_admin account_manager) {
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.retry, null);
        builder.setView(dialogView);
        Button cancel = dialogView.findViewById(R.id.cancel);
        Button retry = dialogView.findViewById(R.id.retry);
        EditText passwordInput = dialogView.findViewById(R.id.etPassword);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        retry.setOnClickListener(v -> {
            String newPassword = passwordInput.getText().toString().trim();
            account_manager.setPassword(newPassword);
            loginWithAccount_admin(account_manager, context);
            dialog.dismiss();
        });

        cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void loginWithAccount_admin(Account_manager_admin account, Context context) {
        String email = account.getEmail();
        String password = account.getPassword();
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        SPUtils.getInstance().put(AppConstans.passwordkey_admin, password);
                        String userId = user.getUid();
                        proceedWithLogin_admin(userId, context);
                    } else {
                        Toast.makeText(context, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("blocked all requests from this device")) {
                        Toast.makeText(context, "Too many attempts. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void proceedWithLogin_admin(String userId, Context context) {
        DatabaseReference adminref = FirebaseDatabase.getInstance().getReference().child("ADMIN").child(userId);
        ValueEventListener adminListerner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (context instanceof Activity) {
                        TinkerApplications app = (TinkerApplications) context.getApplicationContext();
                        app.updatelogin(true, (Activity) context);
                    } else {
                        Log.e("ClassCastError", "Context is not an instance of TinkerApplications");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        adminref.addListenerForSingleValueEvent(adminListerner);
    }


    public void retry_event(Context context, Account_manager_admin account_manager) {
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.retry, null);
        builder.setView(dialogView);
        Button cancel = dialogView.findViewById(R.id.cancel);
        Button retry = dialogView.findViewById(R.id.retry);
        EditText passwordInput = dialogView.findViewById(R.id.etPassword);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        retry.setOnClickListener(v -> {
            String newPassword = passwordInput.getText().toString().trim();
            account_manager.setPassword(newPassword);
            loginWithAccount_event(account_manager, context);
            dialog.dismiss();
        });

        cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void loginWithAccount_event(Account_manager_admin account, Context context) {
        String email = account.getEmail();
        String password = account.getPassword();
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        SPUtils.getInstance().put(AppConstans.passwordkey_admin, password);
                        String userId = user.getUid();
                        proceedWithLogin_event(userId, context);
                    } else {
                        Toast.makeText(context, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("blocked all requests from this device")) {
                        Toast.makeText(context, "Too many attempts. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void proceedWithLogin_event(String userId, Context context) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(userId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (context instanceof Activity) {
                        TinkerApplications app = (TinkerApplications) context.getApplicationContext();
                        app.updatelogin(true, (Activity) context);
                    } else {
                        Log.e("ClassCastError", "Context is not an instance of TinkerApplications");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        eventRef.addListenerForSingleValueEvent(eventListener);
    }
}
