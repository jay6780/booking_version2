package com.hair.booking.activity.tools.DialogUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hair.booking.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.IOException;
import java.util.List;

public class MapDialog {
    private GoogleMap googleMap;

    public void Mapdialog(Context context, String message, String username) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.activity_maps))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        MapView mapView = dialogView.findViewById(R.id.mapview);
        TextView textView = dialogView.findViewById(R.id.name);
        ImageView cancel = dialogView.findViewById(R.id.back);
        textView.setText(username + " Location");
        mapView.onCreate(null);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    setUserLocation(message,context,username);
                }
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setUserLocation(String message, Context context,String username) {
        if (message.contains("https://")) {
            String[] latLng = message.substring(message.indexOf("q=") + 2).split(",");
            if (latLng.length == 2) {
                double latitude = Double.parseDouble(latLng[0]);
                double longitude = Double.parseDouble(latLng[1]);
                LatLng location = new LatLng(latitude, longitude);

                googleMap.addMarker(new MarkerOptions().position(location).title(username+" Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }
        } else {
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocationName(message, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng selectedLocation = new LatLng(address.getLatitude(), address.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(selectedLocation).title(message));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                }
            } catch (IOException e) {
                Log.e("MapDialog", "Geocoder failed: " + e.getMessage());
            }
        }
    }
}
