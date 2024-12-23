package com.hair.booking.activity.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.login;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.yariksoffice.lingver.Lingver;

import java.util.Calendar;

public class TinkerApplications extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    private DatabaseReference guessRef, adminRef,eventsRef;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Lingver.init(this);
        guessRef = FirebaseDatabase.getInstance().getReference("Guess");
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
        addChildBasedOnUser();
        registerActivityLifecycleCallbacks(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    public void updateLocale(String languageCode) {
        Lingver.getInstance().setLocale(this, languageCode);
    }
    public void addChildBasedOnUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (isNetworkConnected()) {
                checkUserLocationAndAddChild(userId);
            } else {
                System.out.println("No internet connection.");
            }
        } else {
            System.out.println("User not authenticated.");
        }
    }

    private void checkUserLocationAndAddChild(String userId) {
        guessRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String currentTime = String.valueOf(System.currentTimeMillis());
                guessRef.child(userId).child("online").setValue(true);
                guessRef.child(userId).child("timestamp").setValue(currentTime);
            } else {
                adminRef.child(userId).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && task2.getResult().exists()) {
                        String currentTime = String.valueOf(System.currentTimeMillis());
                        adminRef.child(userId).child("online").setValue(true);
                        adminRef.child(userId).child("timestamp").setValue(currentTime);
                    } else {
                        System.out.println("User not found in Guess or ADMIN.");
                    }
                });
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters foreground
            setUserOnlineStatus(true);
        }
        activityReferences++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityReferences--;
        isActivityChangingConfigurations = activity.isChangingConfigurations();

        if (activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
            setUserOnlineStatus(false);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void setUserOnlineStatus(boolean online) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (isNetworkConnected()) {
                checkUserLocationAndUpdateStatus(userId, online);
            } else {
                System.out.println("No internet connection to update status.");
            }
        } else {
            System.out.println("User not authenticated to update status.");
        }
    }

    private void checkUserLocationAndUpdateStatus(String userId, boolean online) {
        guessRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String currentTime = String.valueOf(System.currentTimeMillis());
                guessRef.child(userId).child("online").setValue(online);
                guessRef.child(userId).child("timestamp").setValue(currentTime);
            } else {
                adminRef.child(userId).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && task2.getResult().exists()) {
                        String currentTime = String.valueOf(System.currentTimeMillis());
                        adminRef.child(userId).child("online").setValue(online);
                        adminRef.child(userId).child("timestamp").setValue(currentTime);
                    } else {
                        eventsRef.child(userId).get().addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful() && task3.getResult().exists()) {
                                String currentTime = String.valueOf(System.currentTimeMillis());
                                eventsRef.child(userId).child("online").setValue(online);
                                eventsRef.child(userId).child("timestamp").setValue(currentTime);
                            } else {
                                System.out.println("User not found in Guess, ADMIN, or Events.");
                            }
                        });
                    }
                });
            }
        });
    }

    public void updatelogin(boolean logout, Activity activity) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            setOnlineStatus2(userId, logout, () -> {
                Intent intent = new Intent(activity, newHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            });
        }
    }

    public void updatelogin3(boolean logout, Activity activity) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            setOnlineStatus2(userId, logout, () -> {
            });
        }
    }
    public void updatelogin2(boolean logout, Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            setOnlineStatus2(userId, logout, () -> {
                Intent intent = new Intent(context, newHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                if(context instanceof Activity){
                    ((Activity) context).finish();
                }
            });
        }
    }

    private void setOnlineStatus2(String userId, boolean logout, Runnable onComplete) {
        String guesstime = getNextMinuteMillis();
        guessRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                guessRef.child(userId).child("online").setValue(logout).addOnCompleteListener(setTask -> {
                    if (setTask.isSuccessful() && onComplete != null) {
                        guessRef.child(userId).child("timestamp").setValue(guesstime);
                        onComplete.run();
                    } else {
                        System.out.println("Failed to update online status in Guess.");
                    }
                });
            } else {
                adminRef.child(userId).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && task2.getResult().exists()) {
                        adminRef.child(userId).child("online").setValue(logout).addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful() && onComplete != null) {
                                String admintime = getNextMinuteMillis();
                                adminRef.child(userId).child("timestamp").setValue(admintime);
                                onComplete.run();
                            } else {
                                System.out.println("Failed to update online status in ADMIN.");
                            }
                        });
                    } else {
                        eventsRef.child(userId).get().addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful() && task3.getResult().exists()) {
                                String eventTime = getNextMinuteMillis();
                                eventsRef.child(userId).child("online").setValue(logout).addOnCompleteListener(setTask -> {
                                    if (setTask.isSuccessful() && onComplete != null) {
                                        eventsRef.child(userId).child("timestamp").setValue(eventTime);
                                        onComplete.run();
                                    } else {
                                        System.out.println("Failed to update online status in Events.");
                                    }
                                });
                            } else {
                                System.out.println("User not found in Guess, ADMIN, or Events.");
                            }
                        });
                    }
                });
            }
        });
    }

    public void clearUserData(boolean logout, Activity activity) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(ContextCompat.getMainExecutor(activity), task -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        setOnlineStatus(userId, logout, () -> {
                            FirebaseAuth.getInstance().signOut();
                            System.out.println("User signed out successfully.");
                            Intent intent = new Intent(activity, login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        });
                    }
                });
    }

    private void setOnlineStatus(String userId, boolean logout, Runnable onComplete) {
        String guesstime = getNextMinuteMillis();
        guessRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                guessRef.child(userId).child("online").setValue(logout).addOnCompleteListener(setTask -> {
                    if (setTask.isSuccessful() && onComplete != null) {
                        guessRef.child(userId).child("timestamp").setValue(guesstime);
                        onComplete.run();
                    } else {
                        System.out.println("Failed to update online status in Guess.");
                    }
                });
            } else {
                adminRef.child(userId).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && task2.getResult().exists()) {
                        adminRef.child(userId).child("online").setValue(logout).addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful() && onComplete != null) {
                                String adminTime = getNextMinuteMillis();
                                adminRef.child(userId).child("timestamp").setValue(adminTime);
                                onComplete.run();
                            } else {
                                System.out.println("Failed to update online status in ADMIN.");
                            }
                        });
                    } else {
                        eventsRef.child(userId).get().addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful() && task3.getResult().exists()) {
                                eventsRef.child(userId).child("online").setValue(logout).addOnCompleteListener(setTask -> {
                                    if (setTask.isSuccessful() && onComplete != null) {
                                        String eventTime = getNextMinuteMillis();
                                        eventsRef.child(userId).child("timestamp").setValue(eventTime);
                                        onComplete.run();
                                    } else {
                                        System.out.println("Failed to update online status in Events.");
                                    }
                                });
                            } else {
                                System.out.println("User not found in Guess, ADMIN, or Events.");
                            }
                        });
                    }
                });
            }
        });
    }

    private String getNextMinuteMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);  // Move to the next minute
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the time in milliseconds and convert it to a String
        return String.valueOf(calendar.getTimeInMillis());
    }

}