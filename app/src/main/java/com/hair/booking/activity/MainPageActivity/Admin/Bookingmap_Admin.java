package com.hair.booking.activity.MainPageActivity.Admin;


import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity2;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Model.RouteFetcher;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Bookingmap_Admin extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float POLYLINE_DISTANCE_THRESHOLD = 10f;
    private GoogleMap googleMap;
    private String TAG = "Bookingmap";
    private boolean isOnline;
    private boolean isMapReady = false;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;
    private Marker guessmarker;
    private Polyline polyline;
    private List<LatLng> routePoints = new ArrayList<>();
    private LatLng destinationLocation;
    String key,image,lengthOfService,age,providerEmail;
    double latitude;
    double longitude;
    private TextView ages, username,location;
    private ImageView profileimage, backBtn;
    private AppCompatButton messagebtn;
    private TextView title;
    String name;
    String address;
    String chatRoomId = SPUtils.getInstance().getString(AppConstans.ChatRoomId);
    private DatabaseReference databaseReference, chatroomIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingmap2);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        mapView = findViewById(R.id.mapview);
        profileimage = findViewById(R.id.avatar);
        location = findViewById(R.id.address);
        username = findViewById(R.id.name);
        backBtn = findViewById(R.id.back);
        title = findViewById(R.id.profiletxt);
        messagebtn = findViewById(R.id.message);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Bookingmap_Admin.this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        image = getIntent().getStringExtra("image");
        lengthOfService = getIntent().getStringExtra("lengthOfService");
        name = getIntent().getStringExtra("providerName");
        address = getIntent().getStringExtra("address");
        age = getIntent().getStringExtra("age");
        key = intent.getStringExtra("key");
        providerEmail = intent.getStringExtra("providerEmail");
        destinationLocation = new LatLng(latitude, longitude);
        location.setText(getString(R.string.address)+": "+address);
        username.setText(getString(R.string.name)+": "+(name != null ? name : "N/A"));
        title.setText(R.string.map);
        Log.d(TAG, "email: " + providerEmail);
        Log.d(TAG, "chatroomId: " + chatRoomId);
        Log.d(TAG, "providerName: " + name);
        Log.d(TAG, "image: " + image);
        Glide.with(this)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(profileimage);
        backBtn.setOnClickListener(view -> onBackPressed());
        messagebtn.setOnClickListener(view -> checkAndCreateChatRoom(providerEmail, name, image));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatroomIds = FirebaseDatabase.getInstance().getReference();

    }

    private void checkAndCreateChatRoom(String providerEmail, String providerName, String image) {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String chatRoomId = createChatRoomId(currentUserEmail, providerEmail);
        databaseReference.child("chatRooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUsers(Arrays.asList(currentUserEmail, providerEmail));
                    chatroomIds.child("chatRooms").child(chatRoomId).setValue(chatRoom)
                            .addOnSuccessListener(aVoid -> {
                                String availedmessage = SPUtils.getInstance().getString(AppConstans.availedMessage);
                                Intent intent = new Intent(getApplicationContext(), chatActivity2.class);
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
                    Intent intent = new Intent(getApplicationContext(), chatActivity2.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("providerName", providerName);
                    intent.putExtra("image", image);
                    intent.putExtra("providerEmail", providerEmail);
                    intent.putExtra("address", address);
                    intent.putExtra("key", key);
                    intent.putExtra("isOnline", isOnline);
                    SavedAta(chatRoomId, providerName, image, providerEmail, address, key, isOnline);
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


    private void moveCameraToAddress() {
        if (googleMap != null && destinationLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15));
            updateMapLine(destinationLocation);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid location", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateMapLine(LatLng latLng) {
        if (latLng == null || userLocation == null) {
            Log.e(TAG, "LatLng or userLocation is null");
            return; // Avoid proceeding if any is null
        }

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            currentLocationMarker = null;
        }
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
        fetchRouteData(latLng, userLocation);
    }
    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), newHome.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        isMapReady = true;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Bookingmap_Admin.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        requestAndSetUserLocation();
    }
    @SuppressLint("MissingPermission")
    private void requestAndSetUserLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(Bookingmap_Admin.this, location -> {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        moveCameraToAddress();
                        geocodeLocation(userLocation);
                        startLocationUpdates();
                    } else {
                        Log.e(TAG, "User location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user location: " + e.getMessage());
                });
    }

    private void geocodeLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(Bookingmap_Admin.this, Locale.getDefault());
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
                    updateCurrentLocationMarker(location);
                }
            }
        };
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private void updateCurrentLocationMarker(Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng nearestPoint = findNearestPointOnPolyline(currentLatLng, routePoints);
        if (nearestPoint != null) {
            if (currentLocationMarker == null) {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(nearestPoint).title("You are here"));
            } else {
                if (!currentLocationMarker.getPosition().equals(nearestPoint)) {
                    animateMarkerToPosition(currentLocationMarker, nearestPoint);
                }
            }
            adjustPolyline(nearestPoint);
            fetchUserDetailsAndAddMarker(nearestPoint);
            String adminEmail = SPUtils.getInstance().getString(AppConstans.adminemail);
            String guessEmail = SPUtils.getInstance().getString(AppConstans.useremail);
            saveTransactionData(nearestPoint, destinationLocation, adminEmail, guessEmail);
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
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Guess").child(key);
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            String useremail = user.getEmail();
                            SPUtils.getInstance().put(AppConstans.useremail, useremail);
                            loadUserImageAndAddMarker(user, destinationLocation);
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
                            loadAdminMarker(admin, userLocation);
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
                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }
                Glide.with(Bookingmap_Admin.this)
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
                                currentLocationMarker = googleMap.addMarker(markerOptions); // Add admin marker
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
                if (guessmarker != null) {
                    guessmarker.remove();
                }

                Glide.with(Bookingmap_Admin.this)
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
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(offsetLocation)
                                        .title(usermodel.getUsername())
                                        .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap));
                                guessmarker = googleMap.addMarker(markerOptions);
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

                        if (currentLocationMarker == null) {
                            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(start).title("Your Location"));
                        } else {
                            currentLocationMarker.setPosition(start);
                           String useremail = SPUtils.getInstance().getString(AppConstans.useremail);
                           String adminemail = SPUtils.getInstance().getString(AppConstans.adminemail);
                            saveTransactionData(start,end,adminemail,useremail);
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

    private void saveTransactionData(LatLng startLocation, LatLng destinationLocation, String adminEmail, String guessEmail) {
        DatabaseReference transactionMapRef = FirebaseDatabase.getInstance().getReference("transactionMap");
        String transactionId = createChatRoomId(adminEmail, guessEmail);
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("adminEmail", adminEmail);
        transactionData.put("guessEmail", guessEmail);
        transactionData.put("startLatitude", startLocation.latitude);
        transactionData.put("startLongitude", startLocation.longitude);
        transactionData.put("destinationLatitude", destinationLocation.latitude);
        transactionData.put("destinationLongitude", destinationLocation.longitude);
        transactionMapRef.child(transactionId).setValue(transactionData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction data saved successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving transaction data: " + e.getMessage()));
    }

    private String createChatRoomId(String email1, String email2) {
        return email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");
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
        if (mapView != null) {
            mapView.onStop();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
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