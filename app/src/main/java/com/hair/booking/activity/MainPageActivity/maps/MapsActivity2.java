package com.hair.booking.activity.MainPageActivity.maps;

import static com.hair.booking.activity.tools.Utils.AppConstans.KEY;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile2;

import java.io.IOException;
import java.util.List;

public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private ImageView back,add;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String address,providerName,age,lengthOfservice,image,email;
    String key;
    private TextView usernameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        changeStatusBarColor(getResources().getColor(R.color.bgColor));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mapView = findViewById(R.id.mapview);
        usernameText = findViewById(R.id.name);
        add = findViewById(R.id.add);
        back = findViewById(R.id.back);
        initMap(savedInstanceState);
        key = SPUtils.getInstance().getString(KEY);
        address = getIntent().getStringExtra("address");
        providerName = getIntent().getStringExtra("username");
        age = getIntent().getStringExtra("age");
        lengthOfservice = getIntent().getStringExtra("lengthOfservice");
        image= getIntent().getStringExtra("image");
        email = getIntent().getStringExtra("email");
        add.setVisibility(View.GONE);
        usernameText.setText(providerName+" Location");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }



    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(MapsActivity2.this);

    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            setUserLocation(address);
        }
    }
    private void setUserLocation(String selectedAddress) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (selectedAddress != null && !selectedAddress.isEmpty()) {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocationName(selectedAddress, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng selectedLocation = new LatLng(address.getLatitude(), address.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(selectedLocation).title(selectedAddress));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                    return;
                }
            } catch (IOException e) {
                Log.e("MapSelectActivity", "Geocoder failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(getApplicationContext(), providerProfile2.class);
        back.putExtra("username", providerName);
        back.putExtra("address",address);
        back.putExtra("age", age);
        back.putExtra("lengthOfservice",lengthOfservice);
        back.putExtra("image",image);
        back.putExtra("email",email);
        back.putExtra("key",key);
        startActivity(back);
        finish();
        super.onBackPressed();
    }
}