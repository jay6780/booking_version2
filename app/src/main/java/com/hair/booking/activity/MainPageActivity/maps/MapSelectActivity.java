package com.hair.booking.activity.MainPageActivity.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.adapter.MapResultAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapSelectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapResultAdapter adapter;
    private List<String> searchResults = new ArrayList<>();
    private EditText mapSearch;
    private String selectedAddress;
    private AppCompatButton skip;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String userType = SPUtils.getInstance().getString(AppConstans.USERTYPE);
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_select);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        selectedAddress = getIntent().getStringExtra("address");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(userType).child(mAuth.getCurrentUser().getUid());
        skip = findViewById(R.id.skip);
        mapSearch = findViewById(R.id.map_search);
        RecyclerView mapResult = findViewById(R.id.mapResult);
        Button savedButton = findViewById(R.id.saved);
        mapView = findViewById(R.id.mapview);
        mapSearch.setText(selectedAddress);
        skip.setVisibility(View.GONE);
        mapResult.setLayoutManager(new LinearLayoutManager(this));
        initMap(savedInstanceState);

        adapter = new MapResultAdapter(searchResults, address -> {
            selectedAddress = address;
            mapSearch.setText(selectedAddress);
            moveCameraToAddress(selectedAddress);
            mapResult.setVisibility(View.GONE);
        });

        mapResult.setAdapter(adapter);

        mapSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mapResult.setVisibility(View.VISIBLE);
                    performSearch(charSequence.toString());
                } else {
                    mapResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        savedButton.setOnClickListener(v -> {
            if (selectedAddress != null) {
                databaseReference.child("address").setValue(selectedAddress)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Thank you for saving", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MapSelectActivity.this, newHome.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MapSelectActivity.this, "Failed to save address.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(MapSelectActivity.this, "Please search and select your address first.", Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void moveCameraToAddress(String selectedAddress) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(selectedAddress, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                googleMap.addMarker(new MarkerOptions().position(latLng).title(selectedAddress));
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MapSelectActivity", "Error moving camera: " + e.getMessage());
            Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.Apikey));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            setUserLocation();
        }
    }

    private void setUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
                    } else {
                        LatLng defaultLocation = new LatLng(12.8797, 121.7740);
                        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Philippines"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MapSelectActivity", "Error getting user location: " + e.getMessage());
                });
    }

    private void performSearch(String query) {
        PlacesClient placesClient = Places.createClient(this);
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setLocationBias(RectangularBounds.newInstance(
                        new LatLng(6.8452, 122.0452),
                        new LatLng(6.9969, 122.1016)))
                .setCountries("PH")
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            searchResults.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                if (prediction.getFullText(null).toString().toLowerCase().contains("zamboanga")) {
                    searchResults.add(prediction.getFullText(null).toString());
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e("MapSelectActivity", "Error fetching predictions: " + e.getMessage());
        });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
