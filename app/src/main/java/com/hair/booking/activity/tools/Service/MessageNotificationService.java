package com.hair.booking.activity.tools.Service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity2;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity3;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageNotificationService extends Service {
    private static String TAG = "MessageNotificationService";
    private static final String CHANNEL_ID = "chat_notifications";
    private static final int NOTIFICATION_ID = 1001;
    private DatabaseReference chatRooms, admin, guess,events;
    private long lastProcessedMessageTimestamp = 0;
    private String currentUserEmail;
    private MediaPlayer mMediaPlayer;
    private  Intent intent;
    private String chatRoomId;
    private String adminEmail,eventEmail,userEmail;
    private  FirebaseUser currentUser;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        chatRooms = FirebaseDatabase.getInstance().getReference("chatRooms");
        admin = FirebaseDatabase.getInstance().getReference("ADMIN");
        events = FirebaseDatabase.getInstance().getReference("Events");
        guess = FirebaseDatabase.getInstance().getReference("Guess");
        mMediaPlayer = MediaPlayer.create(this, R.raw.soundnotification);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
        currentUserEmail = SPUtils.getInstance().getString(AppConstans.userEmail);

        createNotificationChannel();
        listenForNewMessages();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;
        if (currentUser == null && userId == null) {
            Log.e(TAG, "No authenticated user found. Service is stopping.");
            stopSelf();
            return;
        }
        initEventData(userId);
        initAdminData(userId);
        initGuessData(userId);
    }

    private void initGuessData(String userId) {
        FirebaseDatabase.getInstance().getReference("Guess").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userEmail = dataSnapshot.child("email").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void initAdminData(String userId) {
        FirebaseDatabase.getInstance().getReference("ADMIN").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    adminEmail = dataSnapshot.child("email").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
            }
        });

    }

    private void initEventData(String userId) {
        FirebaseDatabase.getInstance().getReference("Events").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventEmail = dataSnapshot.child("email").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void listenForNewMessages() {
        chatRooms.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                processMessageSnapshot(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                processMessageSnapshot(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to listen to chatRooms: " + databaseError.getMessage());
            }
        });
    }
    private void processMessageSnapshot(DataSnapshot dataSnapshot) {
        List<String> users = new ArrayList<>();
        for (DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()) {
            users.add(userSnapshot.getValue(String.class));
        }
        if (!users.contains(currentUserEmail)) {
            return;
        }
        DataSnapshot messagesSnapshot = dataSnapshot.child("messages");
        if (messagesSnapshot.exists()) {
            for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                long messageTimestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                boolean seen = messageSnapshot.child("seen").getValue(Boolean.class) != null && messageSnapshot.child("seen").getValue(Boolean.class);
                if (messageTimestamp > lastProcessedMessageTimestamp && !seen) { // Check if the message is unseen
                    String senderEmail = messageSnapshot.child("senderEmail").getValue(String.class);
                    Log.d(TAG, "senderemail: " + senderEmail);
                    String username = messageSnapshot.child("username").getValue(String.class);
                    String message = messageSnapshot.child("message").getValue(String.class);
                    String imageUrl = messageSnapshot.child("imageUrl").getValue(String.class);
                    String image = messageSnapshot.child("userImageUrl").getValue(String.class);
                    String key = messageSnapshot.child("key").getValue(String.class);
                    if (!senderEmail.equals(currentUserEmail)) {
                        checkSenderAndSendNotification(key,image,senderEmail, message, username, imageUrl, messageSnapshot.getRef());
                    }
                    lastProcessedMessageTimestamp = messageTimestamp;
                }
            }
        }
    }

    private void checkSenderAndSendNotification(String key, String image, String senderEmail, String message, String username, String imageUrl, DatabaseReference messageRef) {
        chatRoomId = createChatRoomId(currentUserEmail, senderEmail);
        if(chatRoomId == null){
            return;
        }
        admin.orderByChild("email").equalTo(senderEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sendNotification(key, image, chatRoomId, senderEmail, message, username, true, false, false, imageUrl, messageRef);
                } else {
                    events.orderByChild("email").equalTo(senderEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                sendNotification(key, image, chatRoomId, senderEmail, message, username, false, true, false, imageUrl, messageRef);
                            } else {
                                guess.orderByChild("email").equalTo(senderEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            sendNotification(key, image, chatRoomId, senderEmail, message, username, true, true, true, imageUrl, messageRef);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "Error checking guest status: " + databaseError.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Error checking event status: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error checking admin status: " + databaseError.getMessage());
            }
        });
    }

    private String createChatRoomId(String email1, String email2) {
        chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
    }

    private void sendNotification(String key, String image, String chatRoomId, String senderEmail, String message, String username, boolean isAdmin, boolean isEvent, boolean isGuess, String imageUrl, DatabaseReference messageRef) {
        if (isGuess) {
            if (eventEmail != null && eventEmail.equals(currentUserEmail)) {
                intent = new Intent(MessageNotificationService.this, chatActivity3.class);
            } else if (adminEmail != null && adminEmail.equals(currentUserEmail)) {
                intent = new Intent(MessageNotificationService.this, chatActivity2.class);
            }
            intent.putExtra("chatRoomId", chatRoomId);
            intent.putExtra("providerName", username);
            intent.putExtra("image", image);
            intent.putExtra("providerEmail", senderEmail);
            intent.putExtra("key", key);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.applogo)
                    .setContentTitle("New message from " + username)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                                        .bigPicture(resource)
                                        .setSummaryText(message);

                                builder.setStyle(style);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(NOTIFICATION_ID, builder.build());

                                // Update the message as seen
                                messageRef.child("seen").setValue(true);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Clean up any resources if needed
                            }
                        });
            } else {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

                // Update the message as seen
                messageRef.child("seen").setValue(true);
            }
        }else {
            if (isAdmin || isEvent) {
                if (userEmail != null && userEmail.equals(currentUserEmail)) {
                    intent = new Intent(MessageNotificationService.this, chatActivity.class);
                }
                intent.putExtra("chatRoomId", chatRoomId);
                intent.putExtra("providerName", username);
                intent.putExtra("image", image);
                intent.putExtra("providerEmail", senderEmail);
                intent.putExtra("key", key);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.applogo)
                        .setContentTitle("New message from " + username)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .asBitmap()
                            .load(imageUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                                            .bigPicture(resource)
                                            .setSummaryText(message);

                                    builder.setStyle(style);
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                                    // Update the message as seen
                                    messageRef.child("seen").setValue(true);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Clean up any resources if needed
                                }
                            });
                } else {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    // Update the message as seen
                    messageRef.child("seen").setValue(true);
                }
            }
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chat Notifications";
            String description = "Notifications for new messages in the chat";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release(); // Release MediaPlayer resources
            mMediaPlayer = null;
        }
    }
}