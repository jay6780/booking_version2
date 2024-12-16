package com.hair.booking.activity.MainPageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.Application.TinkerApplications;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

public class google_login extends AppCompatActivity {

    private static final String TAG = "google_login";
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth mAuth;
    private DatabaseReference guess, event, admin;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;
    private boolean isGuess = false;
    private boolean isAdmin = false;
    private boolean isEvent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.maroon2));

        mAuth = FirebaseAuth.getInstance();
        guess = FirebaseDatabase.getInstance().getReference("Guess");
        event = FirebaseDatabase.getInstance().getReference("Events");
        admin = FirebaseDatabase.getInstance().getReference("ADMIN");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String email = account.getEmail();
                Log.d(TAG, "Google Sign-In email: " + email);

                firebaseAuthWithGoogle(account.getIdToken());
                SPUtils.getInstance().put(AppConstans.emailAuthenticaion, email);
                Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());

            } catch (ApiException e) {
                Log.w(TAG, "Google Sign-In failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
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
            checkAndSaveUserDetails(uid, name, email, username, phone);
        }
    }


    private void checkAndSaveUserDetails(String uid, String name, String email, String username, String phone) {
        // Create a reference for all three databases
        DatabaseReference guessRef = guess.child(uid);
        DatabaseReference adminRef = admin.child(uid);
        DatabaseReference eventRef = event.child(uid);

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    directohome(false, true, false);
                } else {
                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot eventSnapshot) {
                            if (eventSnapshot.exists()) {
                                directohome(false, true, false);
                            } else {
                                guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot guessSnapshot) {
                                        if (guessSnapshot.exists()) {
                                            directohome( false, true, false);
                                        } else {
                                           savedataandDirect(guessRef, name, email, username, phone);
                                           redirectToHome();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error here if needed
                                    }
                                });
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
                // Handle error here if needed
            }
        });
    }

    private void savedataandDirect(DatabaseReference guessRef, String name, String email, String username, String phone) {
        guessRef.child("name").setValue(name);
        guessRef.child("email").setValue(email);
        guessRef.child("username").setValue(username);
        guessRef.child("phone").setValue(phone);
    }

    private void directohome(boolean isAdmin, boolean isGuess, boolean isEvent) {
        if(isAdmin){
            redirectToHome();
        }else if(isEvent){
            redirectToHome();
        }else if(isGuess) {
            redirectToHome();
        }

    }

    private void redirectToHome() {
        Intent intent = new Intent(google_login.this, newHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        TinkerApplications app = (TinkerApplications) getApplicationContext();
        app.updatelogin3(true, google_login.this);
        startActivity(intent);
        finish();
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(google_login.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
