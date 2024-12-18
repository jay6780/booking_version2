package com.hair.booking.activity.MainPageActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.Application.TinkerApplications;
import com.hair.booking.activity.MainPageActivity.Guess.Createacc;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.singh.daman.proprogressviews.DottedArcProgress;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private AppCompatButton loginButton;
    private TextView forgotPasswordButton;
    private TextView createacc;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DialogPlus dialogPlus,dialogPlus2;
    private ImageView facebook,google;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startService(new Intent(this, MessageNotificationService.class));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mAuth = FirebaseAuth.getInstance();
//        facebook = findViewById(R.id.facebook);
        google = findViewById(R.id.google);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.plogin_password);
        createacc = findViewById(R.id.createaccount);
        loginButton = findViewById(R.id.btn_login);
        forgotPasswordButton = findViewById(R.id.txtforgot);
        firebaseAuth = FirebaseAuth.getInstance();
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //  create.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //  public void onClick(View v) {
        // showLoginPrompt();
        //  }
        //   });

        initShowGuide();
        inithash();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),google_login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

//        facebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),facebook_login.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                finish();
//            }
//        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotYourPassword();
            }
        });
        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Createacc.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

            }
        });

    }

    private void inithash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.hair.booking",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch (PackageManager.NameNotFoundException e) {
//        }
//        catch (NoSuchAlgorithmException e) {
//        }
    }

    private void initShowGuide() {
        NewbieGuide.with(this)
                .setLabel("login_guide")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        showMore();
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(forgotPasswordButton, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.recover_account)
                )
                .show();
    }

    private void showMore() {
        NewbieGuide.with(this)
                .setLabel("login_guide2")
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(emailEditText, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.email_guide)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(passwordEditText, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.password_guide)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(createacc, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.creat_accountguide)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(facebook, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.google_hint)
                )
                .show();
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }



    private void forgotYourPassword() {
        LayoutInflater li = LayoutInflater.from(login.this);
        View promptsView = li.inflate(R.layout.passforgot, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(login.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userMail = (EditText) promptsView.findViewById(R.id.forgot_pass);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = userMail.getText().toString().trim();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        userMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                String mailId = userMail.getText().toString().trim();
                if (mailId.isEmpty()) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            boolean isFacebookLogin = false;
            for (UserInfo userInfo : currentUser.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    isFacebookLogin = true;
                    break;
                }
            }
            if (isFacebookLogin || currentUser.isEmailVerified()) {
                // Fetch user data
                String userId = currentUser.getUid();
                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("Guess").child(userId);
                DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("ADMIN").child(userId);
                DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Events").child(userId);
                dialogPlus = new Dialog().loadingDialog(this);
                dialogPlus.show();

                // Listen to database nodes (Guess, ADMIN, Events)
                ValueEventListener studentListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            TinkerApplications app = (TinkerApplications) getApplicationContext();
                            app.updatelogin(true, login.this);
                            dialogPlus.dismiss();
                        } else {
                            // Check in ADMIN node
                            ValueEventListener adminListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        TinkerApplications app = (TinkerApplications) getApplicationContext();
                                        app.updatelogin(true, login.this);
                                        dialogPlus.dismiss();
                                    } else {
                                        // Check in Events node
                                        ValueEventListener eventsListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    TinkerApplications app = (TinkerApplications) getApplicationContext();
                                                    app.updatelogin(true, login.this);
                                                } else {
                                                    Toast.makeText(login.this, "User not found", Toast.LENGTH_SHORT).show();
                                                }
                                                dialogPlus.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialogPlus.dismiss();
                                            }
                                        };
                                        eventsRef.addListenerForSingleValueEvent(eventsListener);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    dialogPlus.dismiss();
                                }
                            };
                            adminRef.addListenerForSingleValueEvent(adminListener);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialogPlus.dismiss();
                    }
                };

                studentRef.addListenerForSingleValueEvent(studentListener);
            } else {
                // If email is not verified and it's an email login, show a message to verify
                Toast.makeText(login.this, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                mAuth.signOut();  // Sign out the user until verification is complete
            }
        }
    }


    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            passwordEditText.requestFocus();
            return;
        }

        dialogPlus2 = new Dialog().loadinglogin(this);
        dialogPlus2.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            dialogPlus2.dismiss();

            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        SPUtils.getInstance().put(AppConstans.passwordkey, password);
                        SPUtils.getInstance().put(AppConstans.passwordkey_admin, password);
                        String userId = user.getUid();
                        proceedWithLogin(userId);
                    } else {
                        Toast.makeText(login.this, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("blocked all requests from this device")) {
                        Toast.makeText(login.this, "Too many attempts. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(login.this, "Login failed. Please check your credentials", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void proceedWithLogin(String userId) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("Guess").child(userId);
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("ADMIN").child(userId);
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Events").child(userId);

        ValueEventListener studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TinkerApplications app = (TinkerApplications) getApplicationContext();
                    app.updatelogin(true, login.this);
                } else {
                    checkUserInOtherReferences(adminRef, eventsRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        studentRef.addListenerForSingleValueEvent(studentListener);
    }

    private void checkUserInOtherReferences(DatabaseReference adminRef, DatabaseReference eventsRef) {
        ValueEventListener adminListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TinkerApplications app = (TinkerApplications) getApplicationContext();
                    app.updatelogin(true, login.this);
                } else {
                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                TinkerApplications app = (TinkerApplications) getApplicationContext();
                                app.updatelogin(true, login.this);
                            } else {
                                Toast.makeText(login.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        adminRef.addListenerForSingleValueEvent(adminListener);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public  void onBackPressed(){
        finish();
        super.onBackPressed();
    }

}





