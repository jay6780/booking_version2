package com.hair.booking.activity.MainPageActivity.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.Event_userchat;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.Message;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.MessageAdapter;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.kevalpatel2106.emoticonpack.android8.Android8EmoticonProvider;
import com.kevalpatel2106.gifpack.giphy.GiphyGifProvider;
import com.orhanobut.dialogplus.DialogPlus;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import app.m4ntis.blinkingloader.BlinkingLoader;
import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;

public class chatActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private ImageView back, avatar, send, imagePick, emoji, location,
            imageonline;
    private TextView username;
    private EditText messageText;
    private RecyclerView chatRecycler;
    private String chatRoomId, providerName,address,key,availedmessage;
    private DatabaseReference databaseReference, guessRef, adminRef,eventRef;
    private ArrayList<Message> messageList;
    private MessageAdapter messageAdapter;
    private String image;
    private Uri imageUri;
    private String cancelledmessage;
    private String providerEmail,schedule;
    private StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference2;
    RelativeLayout relativeLayout;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "chatActivty1";
    FrameLayout frameLayout,mapFrame;
    EmoticonGIFKeyboardFragment emoticonFragment;
    FragmentTransaction fragmentTransaction;
    private int defaultHeight;
    private static final int EXPANDED_WIDTH = 250;
    private static final int COLLAPSED_WIDTH = 190;
    private String locationUrl = "";
    private HashSet<String> sentCancelledMessages = new HashSet<>();
    private String lastCancelledMessage = null;
    private BlinkingLoader dotLoading1;
    private String currentUserEmail;

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    private boolean isOnline = SPUtils.getInstance().getBoolean(AppConstans.isOnline);
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private DialogPlus dialogPlus;
    String imageUrl = SPUtils.getInstance().getString(AppConstans.ImageUrl);
    String imageUrl2 = SPUtils.getInstance().getString(AppConstans.proccessImage);
    private String serviceName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Initialize views
        back = findViewById(R.id.back);
        avatar = findViewById(R.id.avatar);
        location = findViewById(R.id.location);
        imagePick = findViewById(R.id.imagePick);
        relativeLayout = findViewById(R.id.relativeLay);
        imageonline = findViewById(R.id.imgOnline2);
        frameLayout = findViewById(R.id.emojiContainer);
        mapFrame = findViewById(R.id.mapFrame);
        username = findViewById(R.id.username);
        send = findViewById(R.id.send);
        messageText = findViewById(R.id.chatText);
        dotLoading1 = findViewById(R.id.dotLoading1);
        chatRecycler = findViewById(R.id.chatRecycler);
        emoji = findViewById(R.id.emoji);
        mapView = findViewById(R.id.mapping);
        Log.d(TAG,"ImchatActivity1");
        defaultHeight = relativeLayout.getLayoutParams().height;
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        providerEmail = getIntent().getStringExtra("providerEmail");
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        address = getIntent().getStringExtra("address");
        serviceName = getIntent().getStringExtra("serviceName");
        SPUtils.getInstance().put(AppConstans.previousService,serviceName);
        key = getIntent().getStringExtra("key");
        SPUtils.getInstance().put(AppConstans.KEY, key);
        Log.d("chatActivity","key: "+key);
        providerName = getIntent().getStringExtra("providerName");
        image = getIntent().getStringExtra("image");
        schedule = getIntent().getStringExtra("schedule");
        guessRef = FirebaseDatabase.getInstance().getReference("Guess");
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        eventRef = FirebaseDatabase.getInstance().getReference("Events");
        Log.d(TAG,"isOnline:"+isOnline);
        availedmessage = getIntent().getStringExtra("availedmessage");
        cancelledmessage = getIntent().getStringExtra("cancelledmessage");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storageReference = FirebaseStorage.getInstance().getReference("chatImages"); // Reference to Firebase Storage
        username.setText(providerName);
        initTypingStatus();
        initAvatar();
        initMap(savedInstanceState);
        currentUserEmail = SPUtils.getInstance().getString(AppConstans.emailAuthenticaion);
        fetchUserDetails2(currentUserEmail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initSavedData();
        sendimageUrl();

        new Handler().postDelayed(() -> {
            sendCancelledmessage();
        }, 1500);

        new Handler().postDelayed(() -> {
            sendAvailedMessage();
        }, 1500);

        if (isOnline) {
            imageonline.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        }else{

            imageonline.setVisibility(View.GONE);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Get a reference to your database node
        databaseReference2 = firebaseDatabase.getReference();
        checkAndSendWelcomeMessage(providerEmail, providerName, image,address,key);
        checkAndSendWelcomeMessage2(providerEmail,providerName,image, address,key);
        messageList = new ArrayList<>();


        emoji.setVisibility(View.VISIBLE);

        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initKeyboard();
                hideKeyboard();
                setRelativeLayoutHeight(300);
                frameLayout.setVisibility(View.VISIBLE);
                mapFrame.setVisibility(View.GONE);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(chatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(chatActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    hideKeyboard();
                    frameLayout.setVisibility(View.GONE);
                    showCurrentLocation();
                    mapFrame.setVisibility(View.VISIBLE);
                    setRelativeLayoutHeight(300);
                }
            }
        });



        messageText.addTextChangedListener(new TextWatcher() {
            DatabaseReference typingRef = FirebaseDatabase.getInstance().getReference("typingResult").child(chatRoomId)
                    .child(currentUserEmail.replace(".", "_"));

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isTyping = charSequence.length() > 0;
                typingRef.setValue(isTyping);
                send.setVisibility(isTyping ? View.VISIBLE : View.GONE);
                location.setVisibility(isTyping ? View.GONE : View.VISIBLE);
                emoji.setVisibility(isTyping ? View.GONE : View.VISIBLE);
                ViewGroup.LayoutParams params = messageText.getLayoutParams();
                params.width = dpToPx(isTyping ? EXPANDED_WIDTH : COLLAPSED_WIDTH);
                messageText.setLayoutParams(params);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No implementation needed here.
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, this);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(messageAdapter);

        loadMessages();
        back.setOnClickListener(v -> onBackPressed());
        send.setOnClickListener(v -> sendMessage());
        imagePick.setOnClickListener(v -> openImagePicker());

    }

    private void initTypingStatus() {
        DatabaseReference typingRef = FirebaseDatabase.getInstance()
                .getReference("typingResult")
                .child(chatRoomId);
        typingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailKey = userSnapshot.getKey();
                    boolean isTyping = userSnapshot.getValue(Boolean.class);
                    String email = emailKey.replace("_", ".");
                    Log.d("TypingResult","providerEmail: "+ providerEmail);
                    if (email.equals(providerEmail)) {
                        if (isTyping) {
                            dotLoading1.setVisibility(View.VISIBLE);
                            username.setText(providerName + " is typing...");
                        } else {
                            dotLoading1.setVisibility(View.GONE);
                            username.setText(providerName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TypingResultError", "Failed to retrieve typing status: " + error.getMessage());
            }
        });

    }

    private void sendCancelledmessage() {
        if (cancelledmessage == null) {
            Log.d(TAG, "no availedmessage");
        } else {
            messageText.setText(cancelledmessage);
            messageText.requestFocus();
            messageText.setSelection(messageText.getText().length());
            sendMessage();
        }
    }

    private void checkAndSendWelcomeMessage2(String providerEmail, String username, String image, String address, String key) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("EventOrg").child(key);
        DatabaseReference messageRef = databaseReference2.child("chatRooms").child(chatRoomId).child("messages");

        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                if (serviceSnapshot.exists()) {
                    List<Service> services = new ArrayList<>();
                    StringBuilder serviceNames = new StringBuilder(); // Use StringBuilder to accumulate service names

                    for (DataSnapshot snapshot : serviceSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);
                        if (service != null) {
                            services.add(service);
                            if (serviceNames.length() > 0) {
                                serviceNames.append(", "); // Add a comma if this isn't the first service
                            }
                            serviceNames.append(service.getName()); // Append the service name
                        }
                    }

                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                // Check if the provider email exists in the admin list
                                eventRef.orderByChild("email").equalTo(providerEmail)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    String welcomeMessage = "Hi, my name is: " + username + "\n" +
                                                            "Address: " + address + "\n" +
                                                            "How may I help you?\n" +
                                                            "I offer Wedding events like: " + serviceNames.toString() + ".";
                                                    new Handler().postDelayed(() -> {
                                                        sendInitialMessage(providerEmail, welcomeMessage, username, image, key);
                                                    }, 1000);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(chatActivity.this, "Error checking admin", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(chatActivity.this, "Error checking messages", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(chatActivity.this, "Error retrieving service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendAvailedMessage() {
        if (availedmessage == null) {
            Log.d(TAG, "no availedmessage");
        } else {
            messageText.setText(availedmessage);
            messageText.requestFocus();
            messageText.setSelection(messageText.getText().length());
            sendMessage();
        }
    }

    private void sendimageUrl() {
        if (imageUrl == null) {
            Log.d(TAG, "no image url");
        } else {
            messageText.setText(imageUrl);
            messageText.requestFocus();
            messageText.setSelection(messageText.getText().length());
            sendMessage();
        }
    }



    private void fetchUserDetails2(String email) {
            guessRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String username = snapshot.child("username").getValue(String.class);
                            String userImageUrl = snapshot.child("image").getValue(String.class);
                            String address = snapshot.child("address").getValue(String.class);
                            if (schedule == null){
                                Log.d("setschedule","Set schedule first");
                            }else{
                                sendsetMessage(address, username, userImageUrl, email, schedule);
                            }

                            return;

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(chatActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
            });
    }




    private void checkAndSendWelcomeMessage(String providerEmail, String username, String image, String address, String key) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(key);
        DatabaseReference messageRef = databaseReference2.child("chatRooms").child(chatRoomId).child("messages");

        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                if (serviceSnapshot.exists()) {
                    List<Service> services = new ArrayList<>();
                    StringBuilder serviceNames = new StringBuilder(); // Use StringBuilder to accumulate service names

                    for (DataSnapshot snapshot : serviceSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);
                        if (service != null) {
                            services.add(service);
                            if (serviceNames.length() > 0) {
                                serviceNames.append(", "); // Add a comma if this isn't the first service
                            }
                            serviceNames.append(service.getName()); // Append the service name
                        }
                    }

                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                // Check if the provider email exists in the admin list
                                adminRef.orderByChild("email").equalTo(providerEmail)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    String welcomeMessage = "Hi, my name is: " + username + "\n" +
                                                            "Address: " + address + "\n" +
                                                            "How may I help you?\n" +
                                                            "My services are: " + serviceNames.toString() + ".";
                                                    new Handler().postDelayed(() -> {
                                                        sendInitialMessage(providerEmail, welcomeMessage, username, image, key);
                                                    }, 1500);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(chatActivity.this, "Error checking admin", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(chatActivity.this, "Error checking messages", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(chatActivity.this, "Error retrieving service", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void sendInitialMessage(String providerEmail, String welcomeMessage, String username, String image,String key) {
        String messageTextValue = welcomeMessage;
        if (!messageTextValue.isEmpty()) {
            DatabaseReference messageRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages").push();
            String messageId = messageRef.getKey();
            Message message = new Message(providerEmail, messageTextValue, System.currentTimeMillis(), username, image, "", messageId,key);
            messageRef.setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        messageText.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(chatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    });
        } else {
//            Toast.makeText(this, "Message is empty", Toast.LENGTH_SHORT).show();
        }
    }


    private void initSavedData() {
        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        SPUtils.getInstance().put(AppConstans.image, image);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        locationUrl = "https://www.google.com/maps/?q=" + location.getLatitude() + "," + location.getLongitude();
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
                        googleMap.setOnMarkerClickListener(marker -> {
                            setMessageText(locationUrl);
                            return false;
                        });
                    } else {
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MapSelectActivity", "Error getting user location: " + e.getMessage());
                });
    }

    private void setMessageText(String locationUrl) {
        new AlertDialog.Builder(this)
                .setTitle("Share Location")
                .setMessage("Do you want to share your location?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    messageText.setText(locationUrl);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    messageText.setText("");
                })
                .show();
    }


    private void setRelativeLayoutHeight(int heightInDp) {
        int heightInPx = (int) (heightInDp * getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) params;
            relativeParams.height = heightInPx;
            relativeLayout.setLayoutParams(relativeParams);
        } else if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) params;
            linearParams.height = heightInPx;
            relativeLayout.setLayoutParams(linearParams);
        }
    }


    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initKeyboard() {
        EmoticonGIFKeyboardFragment.GIFConfig giphyGifConfig = new EmoticonGIFKeyboardFragment.GIFConfig(
                GiphyGifProvider.create(chatActivity.this, "RszSYVv48NfSr9nsRSdtgCZYOnQs1veY")
        );

        FragmentManager fragmentManager = getSupportFragmentManager();
        emoticonFragment = (EmoticonGIFKeyboardFragment) fragmentManager.findFragmentByTag("EmoticonGIFFragment");

        if (emoticonFragment == null) {
            EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                    .setEmoticonProvider(Android8EmoticonProvider.create())
                    .setEmoticonSelectListener(new EmoticonSelectListener() {
                        @Override
                        public void emoticonSelected(Emoticon emoticon) {
                            // Add selected emoticon to EditText
                            String currentText = messageText.getText().toString();
                            messageText.setText(currentText + emoticon.getUnicode());
                        }

                        @Override
                        public void onBackSpace() {
                            String currentText = messageText.getText().toString();
                            if (!currentText.isEmpty()) {
                                messageText.setText(currentText.substring(0, currentText.length() - 1));
                            }
                        }
                    });

            giphyGifConfig.setGifSelectListener(gif -> {
                sendGifMessage(gif.getGifUrl());
            });

            emoticonFragment = EmoticonGIFKeyboardFragment.getNewInstance(findViewById(R.id.emojiContainer), emoticonConfig, giphyGifConfig);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.emojiContainer, emoticonFragment, "EmoticonGIFFragment");
            fragmentTransaction.commit();
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(emoticonFragment);
            fragmentTransaction.commit();
        }
    }

    private void sendGifMessage(String gifUrl) {
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchUserDetails(currentUserEmail, (username, userImageUrl) -> {
            DatabaseReference messageRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages").push();
            String messageId = messageRef.getKey();
            Message message = new Message(currentUserEmail, "", System.currentTimeMillis(), username, userImageUrl, gifUrl, messageId,key);
            messageRef.setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(),"Sent gif Success!",Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(chatActivity.this, "Failed to send GIF", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void initAvatar() {
        RequestOptions requestOptions = new RequestOptions().circleCrop();
        Glide.with(getApplicationContext())
                .load(image)
                .apply(requestOptions)
                .into(avatar);
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void loadMessages() {
        DatabaseReference chatRoomRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages");

        chatRoomRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                Message message = messageSnapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    SPUtils.getInstance().put(AppConstans.lastmessage,message.getMessage());
                    Collections.sort(messageList, new Comparator<Message>() {
                        @Override
                        public int compare(Message m1, Message m2) {
                            return Long.compare(m1.getTimestamp(), m2.getTimestamp());
                        }
                    });

                    messageAdapter.notifyDataSetChanged();
                    chatRecycler.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                Message updatedMessage = messageSnapshot.getValue(Message.class);
                if (updatedMessage != null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        if (messageList.get(i).getMessageId().equals(updatedMessage.getMessageId())) {
                            messageList.set(i, updatedMessage);
                            Collections.sort(messageList, new Comparator<Message>() {
                                @Override
                                public int compare(Message m1, Message m2) {
                                    return Long.compare(m1.getTimestamp(), m2.getTimestamp());
                                }
                            });
                            messageAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot messageSnapshot) {
                Message removedMessage = messageSnapshot.getValue(Message.class);
                if (removedMessage != null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        if (messageList.get(i).getMessageId().equals(removedMessage.getMessageId())) {
                            messageList.remove(i);
                            messageAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                // Handle moving messages if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(chatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageTextValue = messageText.getText().toString().trim();
        if (!messageTextValue.isEmpty()) {;
            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String currentUserEmail = SPUtils.getInstance().getString(AppConstans.emailAuthenticaion);
            fetchUserDetails(currentUserEmail, (username, userImageUrl) -> {
                DatabaseReference messageRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages").push();
                String messageId = messageRef.getKey();
                Message message = new Message(currentUserEmail, messageTextValue, System.currentTimeMillis(), username, SPUtils.getInstance().getString(AppConstans.guessImage), "", messageId,key);
                messageRef.setValue(message)
                        .addOnSuccessListener(aVoid -> {
                            imageUrl = null;
                            imageUrl2 = null;
                            String imageUrldelte = imageUrl;
                            messageText.setText("");
                            SPUtils spUtils = SPUtils.getInstance();
                            String availedMessage = AppConstans.availedMessage;
                            spUtils.remove(availedMessage);
                            SPUtils.getInstance().put(AppConstans.ImageUrl, imageUrldelte);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(chatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
//            Toast.makeText(this, "Message is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserDetails(String email, UserDetailsCallback callback) {
        guessRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String userImageUrl = snapshot.child("image").getValue(String.class);
                        Log.d("userImageUrl","userImageUrl: "+userImageUrl);
                        callback.onUserDetailsFetched(username, userImageUrl);
                        return;
                    }
                } else {
                    adminRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String username = snapshot.child("username").getValue(String.class);
                                    String userImageUrl = snapshot.child("image").getValue(String.class);
                                    callback.onUserDetailsFetched(username, userImageUrl);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                            Toast.makeText(chatActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(chatActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        PhotoPicker.startPhotoPickerForResult(this, REQUEST_CODE_PHOTO_PICKER);
    }

    private void sendsetMessage(String address, String username, String image, String providerEmail, String schedule) {
        DatabaseReference messageRef = databaseReference2.child("chatRooms").child(chatRoomId).child("messages");
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Check if the provider email exists in the admin list
                    guessRef.orderByChild("email").equalTo(providerEmail)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String welcomeMessage = "I book in: " + schedule + "\n" +
                                                "My Address is : " + address + "\n";
                                        sendInitialMessage(providerEmail, welcomeMessage, username, image,key);
                                    } else {
                                        Toast.makeText(chatActivity.this, "Provider email not found in admin list", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(chatActivity.this, "Error checking admin", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatActivity.this, "Error checking messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Photo[] photos = PhotoPicker.getResultPhotos(data);
                if (photos.length > 0) {
                    imageUri = Uri.parse(String.valueOf(photos[0].getUri()));
                    openCropActivity(imageUri);
                }
                Log.i("chatActivity", "User selected " + photos.length + " photos");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("chatActivity", "Photo Picking Cancelled: " + resultCode);
            } else {
                Log.i("dbotha", "Unknown result code: " + resultCode);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final Uri resultUri = UCrop.getOutput(data);
                uploadImageToFirebase(resultUri); // Pass the cropped image URI
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                Toast.makeText(this, "Crop failed: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCropActivity(Uri imageUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
        UCrop uCrop = UCrop.of(imageUri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.bgColor));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.bgColor));
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.purple_700));
        uCrop.withOptions(options);
        uCrop.start(this);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        dialogPlus = new Dialog().loadingDialog(this);
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                while (baos.toByteArray().length / 1024 > 500 && quality > 0) {
                    baos.reset();
                    quality -= 5;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                }

                byte[] data = baos.toByteArray();
                StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
                dialogPlus.show();

                UploadTask uploadTask = fileReference.putBytes(data);

                uploadTask.addOnSuccessListener(taskSnapshot ->
                                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    sendImageMessage(uri.toString());
                                    dialogPlus.dismiss();
                                }))
                        .addOnFailureListener(e -> {
                            dialogPlus.dismiss();
                            Toast.makeText(chatActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        });
            } catch (IOException e) {
                dialogPlus.dismiss(); // Dismiss on error
                Toast.makeText(chatActivity.this, "Failed to get image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendImageMessage(String imageUrl) {
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentUseremail = SPUtils.getInstance().getString(AppConstans.userEmail);
        fetchUserDetails(currentUseremail, (username, userImageUrl) -> {
            DatabaseReference messageRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages").push();
            String messageId = messageRef.getKey(); // Get the message ID
            Message message = new Message(currentUseremail, null, System.currentTimeMillis(), username, userImageUrl, imageUrl, messageId,key);
            messageRef.setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(),"Sent gif Success!",Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(chatActivity.this, "Failed to send image message", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    interface UserDetailsCallback {
        void onUserDetailsFetched(String username, String imageUrl);
    }

    @Override
    public void onBackPressed() {
        resetRelativeLayoutHeight();
        if (emoticonFragment != null && emoticonFragment.isVisible()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(emoticonFragment);
            fragmentTransaction.commit();
            mapFrame.setVisibility(View.GONE);
            return;
        }
        if (mapFrame.getVisibility() == View.VISIBLE) {
            mapFrame.setVisibility(View.GONE);
            return;
        }
        Intent userChat = new Intent(getApplicationContext(), User_list.class);
        startActivity(userChat);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
    private void resetRelativeLayoutHeight() {
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) params;
            relativeParams.height = defaultHeight;
            relativeLayout.setLayoutParams(relativeParams);
        } else if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) params;
            linearParams.height = defaultHeight;
            relativeLayout.setLayoutParams(linearParams);
        }
    }
}
