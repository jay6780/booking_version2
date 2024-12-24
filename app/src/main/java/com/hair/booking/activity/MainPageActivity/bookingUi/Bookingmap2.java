package com.hair.booking.activity.MainPageActivity.bookingUi;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Model.Maplink;
import com.hair.booking.activity.tools.Model.RouteFetcher;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Bookingmap2 extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float POLYLINE_DISTANCE_THRESHOLD = 10f;
    private GoogleMap googleMap;
    private TextView ages, userAddress, userLenghtexp, name,profiletxt;
    private ImageView profileimage, backBtn;
    AppCompatButton messagebtn;
    private String chatRoomId;
    private String image;
    private String email;
    private String providerName;
    private String address, key, time, cash, serviceName, heads, price, date;
    private String age, lengthOfservice;
    private String TAG = "Bookingmap";
    private boolean isOnline;
    private boolean isMapReady = false;
    private DatabaseReference databaseReference, chatroomIds;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;
    private Marker adminmarker;
    private Polyline polyline;
    private List<LatLng> routePoints = new ArrayList<>();
    private String transactionId;
    private LatLng destinationLocation,startLocation;
    private ProgressDialog progressDialog;
    private String availedmess;
    private LatLng previousStartLocation = null;
    private LatLng previousDestinationLocation = null;
    private Marker providermarker;
    private boolean isLocationFetchInProgress = false;
    private String locationLink;
    private boolean isLocationFetchDelayed = false;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingmap);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        profileimage = findViewById(R.id.avatar);
        ages = findViewById(R.id.age);
        name = findViewById(R.id.name);
        backBtn = findViewById(R.id.back);
        profiletxt = findViewById(R.id.profiletxt);
        userAddress = findViewById(R.id.address);
        userLenghtexp = findViewById(R.id.lenghtofservice);
        messagebtn = findViewById(R.id.message);
        mapView = findViewById(R.id.mapview);
        profiletxt.setText(R.string.map);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Bookingmap2.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatroomIds = FirebaseDatabase.getInstance().getReference();
        time = getIntent().getStringExtra("time");
        cash = getIntent().getStringExtra("cash");
        serviceName = getIntent().getStringExtra("serviceName");
        heads = getIntent().getStringExtra("heads");
        price = getIntent().getStringExtra("price");
        date = getIntent().getStringExtra("date");
        providerName = getIntent().getStringExtra("providerName");
        image = getIntent().getStringExtra("image");
        providerName = getIntent().getStringExtra("providerName");
        address = getIntent().getStringExtra("address");
        email = getIntent().getStringExtra("email");
        age = getIntent().getStringExtra("age");
        lengthOfservice = getIntent().getStringExtra("lengthOfService");
        key = getIntent().getStringExtra("key");
        ages.setText(getString(R.string.age)+": "+ (age != null ? age : "N/A"));
        userLenghtexp.setText(getString(R.string.years) +": "+ (lengthOfservice != null ? lengthOfservice : "N/A"));
        Log.d(TAG, "Storing Provider Name: " + providerName);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        Log.d(TAG, "Storing Email: " + email);
        SPUtils.getInstance().put(AppConstans.providers, key);
        SPUtils.getInstance().put(AppConstans.email, email);
        name.setText(getString(R.string.name) +": "+ (providerName != null ? providerName : "N/A"));
        userAddress.setText(getString(R.string.address) +": "+ (address != null ? address : "N/A"));
        Log.d(TAG, "email: " + email);
        Log.d(TAG, "providerName: " + providerName);
        Log.d(TAG, "image: " + image);
        String currentUserEmail = SPUtils.getInstance().getString(AppConstans.userEmail);
        transactionId = createChatRoomId(currentUserEmail, email);
        Glide.with(this)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(profileimage);
        messagebtn.setOnClickListener(view -> checkAndCreateChatRoom(email, providerName, image,serviceName,availedmess));
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            checkIfUserIsGuess(currentUserId, time, heads, cash, serviceName, price, date);
        }
        initShowbook();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void checkAndCreateChatRoom(String providerEmail, String providerName, String image,String serviceName,String availedmessage) {
        String currentUserEmail = SPUtils.getInstance().getString(AppConstans.userEmail);
        String chatRoomId = createChatRoomId(currentUserEmail, providerEmail);
        databaseReference.child("chatRooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ChatRoom chatRoom = new ChatRoom();
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    chatRoom.setUsers(Arrays.asList("timestamp: "+timestamp,currentUserEmail, providerEmail));
                    chatroomIds.child("chatRooms").child(chatRoomId).setValue(chatRoom)
                            .addOnSuccessListener(aVoid -> {
                                Intent intent = new Intent(getApplicationContext(), chatActivity.class);
                                intent.putExtra("chatRoomId", chatRoomId);
                                intent.putExtra("providerName", providerName);
                                intent.putExtra("image", image);
                                intent.putExtra("providerEmail", providerEmail);
                                intent.putExtra("address", address);
                                intent.putExtra("key", key);
                                intent.putExtra("isOnline", isOnline);
                                intent.putExtra("availedmessage", availedmessage);
                                SavedAta(chatRoomId, providerName, image, providerEmail, address, key, isOnline);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Failed to create chat room", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    String previousServicename = SPUtils.getInstance().getString(AppConstans.previousService);
                    Intent intent = new Intent(getApplicationContext(), chatActivity.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("providerName", providerName);
                    intent.putExtra("image", image);
                    intent.putExtra("providerEmail", providerEmail);
                    intent.putExtra("address", address);
                    intent.putExtra("key", key);
                    intent.putExtra("isOnline", isOnline);
                    if (!serviceName.equals(previousServicename)) {
                        Toast.makeText(getApplicationContext(), "Availed Success", Toast.LENGTH_SHORT).show();
                        intent.putExtra("availedmessage", availedmessage);
                        intent.putExtra("serviceName", serviceName);
                    }else{
                        Toast.makeText(getApplicationContext(),"Already Availed",Toast.LENGTH_SHORT).show();
                        SPUtils spUtils = SPUtils.getInstance();
                        String previousdata = null;
                        spUtils.put(AppConstans.previousService,previousdata);
                        intent.putExtra("availedmessage", previousdata);
                        intent.putExtra("serviceName", previousdata);
                        Log.d(TAG,"Cleared: "+previousdata);
                    }
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to check chat room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SavedAta(String chatRoomId, String providerName, String image, String providerEmail, String address, String key, boolean isOnline) {
        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);
        SPUtils.getInstance().put(AppConstans.providerName, providerName);
        SPUtils.getInstance().put(AppConstans.image, image);
        SPUtils.getInstance().put(AppConstans.providerEmail, providerEmail);
        SPUtils.getInstance().put(AppConstans.address, address);
        SPUtils.getInstance().put(AppConstans.key, key);
        SPUtils.getInstance().put(AppConstans.isOnline, isOnline);
    }

    private String createChatRoomId(String email1, String email2) {
        chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initShowbook() {
        startService(new Intent(this, MessageNotificationService.class));
        TextView badgeCount = findViewById(R.id.badge_count);
        ImageView bell = findViewById(R.id.bell);
        ImageView messageImg = findViewById(R.id.messageImg);
        messageImg.setOnClickListener(view -> intentchat());
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknum);
        if(badgenum == null){
            badgeCount.setText("0");
            SPUtils.getInstance().put(AppConstans.booknum, "0");
        }else{
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(badgenum);

        }
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), history_book.class);
                intent.putExtra("bookprovideremail", bookprovideremail);
                startActivity(intent);
                finish();
            }
        });

    }

    private void intentchat() {
        Intent intent = new Intent(getApplicationContext(), User_list.class);
        SPUtils.getInstance().put(AppConstans.discount,"");
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
        Intent intent = new Intent(getApplicationContext(), history_book.class);
        intent.putExtra("bookprovideremail", bookprovideremail);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }

    private void startLocationFetch(String transactionId) {
        if (isLocationFetchInProgress) {
            return;
        }
        if (isLocationFetchDelayed) {
            return;
        }

        isLocationFetchInProgress = true;
        DatabaseReference transactionMapRef = FirebaseDatabase.getInstance().getReference("transactionMap").child(transactionId);
        transactionMapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> transactionData = (Map<String, Object>) dataSnapshot.getValue();
                    Double startLatitude = (Double) transactionData.get("startLatitude");
                    Double startLongitude = (Double) transactionData.get("startLongitude");
                    Double destinationLatitude = (Double) transactionData.get("destinationLatitude");
                    Double destinationLongitude = (Double) transactionData.get("destinationLongitude");
                    startLocation = new LatLng(startLatitude, startLongitude);
                    destinationLocation = new LatLng(destinationLatitude, destinationLongitude);
                    updateMapLine(startLocation,destinationLocation);
                    moveCameraToAddress();
                }
                isLocationFetchInProgress = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching transaction data: " + databaseError.getMessage());
                isLocationFetchInProgress = false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        isMapReady = true;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Bookingmap2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        startLocationFetch(transactionId);
        requestAndSetUserLocation();
    }
    @SuppressLint("MissingPermission")
    private void requestAndSetUserLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Bookingmap2.this, location -> {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        startLocationUpdates();
                        geocodeLocation(userLocation);
                    } else {
                        Log.e(TAG, "User location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user location: " + e.getMessage());
                });
    }

    private void geocodeLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(Bookingmap2.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder locationText = new StringBuilder();
                if (address.getSubThoroughfare() != null) {
                    locationText.append(" ").append(address.getSubThoroughfare());
                }
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(locationText.toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                currentLocationMarker.showInfoWindow();
            }
        } catch (IOException e) {
            Log.e("MapFragment", "Geocoding failed: " + e.getMessage());
        }
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    locationLink = "https://www.google.com/maps/?q=" + location.getLatitude() + "," + location.getLongitude();
                    SPUtils.getInstance().put(AppConstans.locationlink,locationLink);
                    Log.d(TAG, "User location: " + locationLink);
                    updateCurrentLocationMarker();
                }
            }
        };
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(200);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void moveCameraToAddress() {
        if (googleMap != null && userLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
            fetchUserDetailsAndAddMarker(userLocation);

        }
    }

    private void updateMapLine(LatLng startLocation, LatLng destinationLocation) {
        if (destinationLocation == null || userLocation == null) {
            Log.e(TAG, "LatLng or userLocation is null");
            return;
        }
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            currentLocationMarker = null;
        }
        fetchRouteData(startLocation, destinationLocation);
    }
    private void updateCurrentLocationMarker() {
        LatLng nearestPoint = findNearestPointOnPolyline(userLocation, routePoints);
        if (nearestPoint != null) {
            if (currentLocationMarker == null) {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(nearestPoint).title("You are here"));
            } else {
                if (!currentLocationMarker.getPosition().equals(nearestPoint)) {
                    animateMarkerToPosition(currentLocationMarker, nearestPoint);
                }
            }
            adjustPolyline(nearestPoint);
        }
    }

    private void adjustPolyline(LatLng nearestPoint) {
        int nearestIndex = routePoints.indexOf(nearestPoint);
        if (nearestIndex != -1 && nearestIndex < routePoints.size() - 1) {
            List<LatLng> newPolylinePoints = new ArrayList<>(routePoints.subList(nearestIndex, routePoints.size()));
            if (polyline != null) {
                polyline.setPoints(newPolylinePoints);
            } else {
                Log.e("MapFragment", "Polyline is null. Cannot adjust points.");
            }
        }
    }
    private void animateMarkerToPosition(final Marker marker, final LatLng targetPosition) {
        if (marker == null || targetPosition == null) return;
        final LatLng startPosition = marker.getPosition();
        final long duration = 1000;
        final long delay = 1200;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                float t = (float) animation.getAnimatedValue();
                double lat = t * targetPosition.latitude + (1 - t) * startPosition.latitude;
                double lng = t * targetPosition.longitude + (1 - t) * startPosition.longitude;
                LatLng newPosition = new LatLng(lat, lng);
                marker.setPosition(newPosition);
            });
            valueAnimator.start();
        }, delay);
    }

    private LatLng findNearestPointOnPolyline(LatLng userLocation, List<LatLng> polylinePoints) {
        LatLng nearestPoint = null;
        float minDistance = Float.MAX_VALUE;

        for (LatLng point : polylinePoints) {
            float[] results = new float[1];
            Location.distanceBetween(userLocation.latitude, userLocation.longitude, point.latitude, point.longitude, results);
            float distance = results[0];
            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }
    private void fetchUserDetailsAndAddMarker(LatLng userLocation) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(key);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            String useremail = user.getEmail();
                            SPUtils.getInstance().put(AppConstans.useremail, useremail);
                            loadUserImageAndAddMarker(user, userLocation);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MapFragment", "Error fetching user details: " + databaseError.getMessage());
                }
            });

            adminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel admin = dataSnapshot.getValue(Usermodel.class);
                        if (admin != null) {
                            String adminEmail = admin.getEmail();
                            SPUtils.getInstance().put(AppConstans.adminemail, adminEmail);
                            Log.d(TAG, "Admin data retrieved: " + admin.getUsername());
                            loadAdminMarker(admin, startLocation);
                        } else {
                            Log.e(TAG, "Admin data is null");
                        }
                    } else {
                        Log.e(TAG, "Admin not found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MapFragment", "Error fetching admin details: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e(TAG, "Current user is null");
        }
    }
    private void loadAdminMarker(Usermodel admin, LatLng userLocation) {
        if (isDestroyed() || isFinishing()) {
            Log.e(TAG, "Activity is destroyed or finishing, skipping image load.");
            return;
        }
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                StringBuilder locationText = new StringBuilder();
                android.location.Address address = addresses.get(0);
                if (address.getSubThoroughfare() != null) {
                    locationText.append(" ").append(address.getSubThoroughfare());
                }
                if (address.getLocality() != null) {
                    locationText.append(", ").append(address.getLocality());
                }
                if (address.getCountryName() != null) {
                    locationText.append(", ").append(address.getCountryName());
                }
                if (providermarker != null) {
                    providermarker.remove();
                }
                Glide.with(Bookingmap2.this)
                        .asBitmap()
                        .load(admin.getImage())
                        .override(100, 100)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                if (isDestroyed() || isFinishing()) {
                                    Log.e(TAG, "Activity is destroyed or finishing, skipping image processing.");
                                    return;
                                }

                                Bitmap circularBitmap = getCircularBitmap(resource);
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(userLocation)
                                        .title(admin.getUsername())
                                        .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap));
                                providermarker = googleMap.addMarker(markerOptions); // Add admin marker
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {}
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserImageAndAddMarker(Usermodel usermodel, LatLng offsetLocation) {
        if (isDestroyed() || isFinishing()) {
            Log.e(TAG, "Activity is destroyed or finishing, skipping image load.");
            return;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(destinationLocation.latitude, destinationLocation.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                StringBuilder locationText = new StringBuilder();
                android.location.Address address = addresses.get(0);
                if (address.getSubThoroughfare() != null) {
                    locationText.append(" ").append(address.getSubThoroughfare());
                }
                if (address.getLocality() != null) {
                    locationText.append(", ").append(address.getLocality());
                }
                if (address.getCountryName() != null) {
                    locationText.append(", ").append(address.getCountryName());
                }
                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }

                Glide.with(Bookingmap2.this)
                        .asBitmap()
                        .load(usermodel.getImage())
                        .override(100, 100)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                if (isDestroyed() || isFinishing()) {
                                    Log.e(TAG, "Activity is destroyed or finishing, skipping image processing.");
                                    return;
                                }
                                Bitmap circularBitmap = getCircularBitmap(resource);
                                LatLng mylocation;
                                if (offsetLocation != null) {
                                    mylocation = offsetLocation;
                                } else {
                                    mylocation = destinationLocation;
                                }
                                if (mylocation != null) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(mylocation)
                                            .title(usermodel.getUsername())
                                            .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap));
                                    currentLocationMarker = googleMap.addMarker(markerOptions);
                                } else {
                                    Log.e(TAG, "Both destinationLocation and offsetLocation are null. Skipping marker creation.");
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchRouteData(LatLng end, LatLng start) {
        RouteFetcher routeFetcher = new RouteFetcher();
        routeFetcher.fetchRouteData(end, start, new RouteFetcher.RouteFetchListener() {
            @Override
            public void onSuccess(JSONObject jsonResponse) {
                try {
                    JSONArray coordinates = jsonResponse
                            .getJSONObject("route")
                            .getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    routePoints.clear();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray latLngArray = coordinates.getJSONArray(i);
                        double lat = latLngArray.getDouble(0);
                        double lng = latLngArray.getDouble(1);
                        routePoints.add(new LatLng(lat, lng));
                    }
                    runOnUiThread(() -> {
                        if (polyline != null) {
                            polyline.remove();
                        }
                        polyline = googleMap.addPolyline(new PolylineOptions()
                                .addAll(routePoints)
                                .width(7)
                                .color(Color.RED)
                                .geodesic(true));

                        if (providermarker == null) {
                            providermarker = googleMap.addMarker(new MarkerOptions().position(end).title("provider Location"));
                        } else {
                            providermarker.setPosition(end);

                        }
                    });

                } catch (JSONException e) {
                    Log.e("MapFragment", "Error parsing JSON response: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MapFragment", "Error fetching route data: " + errorMessage);
            }
        });
    }

    private void checkIfUserIsGuess(String userId, String time, String heads, String cash, String serviceName, String price, String date) {
        DatabaseReference guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
        guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("Range")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String locationLink = SPUtils.getInstance().getString(AppConstans.locationlink);
                    savedlocationLink(userId,locationLink);
                        availedmess = "Hi, I'm " + username + "\n" +
                                "I availed:\n" +
                                "Service Name: " + serviceName + "\n" +
                                "Selected schedule: " + "time: " + time + "\n" +
                                "date: " + date + "\n" +
                                "Number of Heads: " + heads + "\n" +
                                "Payment Method: " + cash + "\n" +
                                "Price: " + price + " php" + "\n" +
                                "My location: " + locationLink + "\n" +
                                "Thank you!";
                        Log.d(TAG, "availedmess: " + availedmess);
                        SPUtils.getInstance().put(AppConstans.availedMessage, availedmess);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking Guess user: " + databaseError.getMessage());
            }
        });
    }

    private void savedlocationLink(String userId, String locationLink) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Maplink maplink = new Maplink(locationLink);
        databaseReference.child("maplink").child(userId).setValue(maplink)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Location link saved successfully for userId: " + userId);
                    } else {
                        // Handle errors
                        Log.e("Firebase", "Error saving location link", task.getException());
                    }
                });
    }


    private Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null && googleMap !=null) {
            mapView.onStop();
            googleMap.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null && googleMap !=null) {
            mapView.onDestroy();
            googleMap.clear();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

}