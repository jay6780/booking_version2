package com.hair.booking.activity.Fragments.UserFragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.RouteFetcher;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.adapter.MapResultAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private MapResultAdapter adapter;
    private List<String> searchResults = new ArrayList<>();
    private SearchView mapSearch;
    private String selectedAddress;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private LatLng userLocation;
    private OkHttpClient client;
    private Polyline polyline;
    private LatLng destinationLocation;
    private List<LatLng> routePoints = new ArrayList<>();
    private Marker currentLocationMarker;
    private LocationCallback locationCallback;
    private boolean isMapReady = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentmap, container, false);
        mapSearch = view.findViewById(R.id.map_search);
        RecyclerView mapResult = view.findViewById(R.id.mapResult);
        mapResult.setVisibility(View.GONE);
        mapView = view.findViewById(R.id.mapview);
        mapResult.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MapResultAdapter(searchResults, address -> {
            selectedAddress = address;
            mapSearch.setQuery(selectedAddress, false);
            moveCameraToAddress(selectedAddress);
            mapResult.setVisibility(View.GONE);
        });
        mapResult.setAdapter(adapter);

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mapResult.setVisibility(View.VISIBLE);
                performSearch(query); // Perform search on submit
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    mapResult.setVisibility(View.VISIBLE);
                    performSearch(newText); // Perform search on text change
                } else {
                    mapResult.setVisibility(View.GONE);
                    if (polyline != null) {
                        polyline.remove();
                        polyline = null;
                    }
                }
                return true;
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        client = new OkHttpClient();

        initMap(mapView, savedInstanceState);
        return view;
    }
    private void moveCameraToAddress(String selectedAddress) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(selectedAddress, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                destinationLocation = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15));
                // googleMap.addMarker(new MarkerOptions().position(destinationLocation).title(selectedAddress));
                updateMapLine(destinationLocation);
                fetchProvidersAndShowMarkers(selectedAddress);
            } else {
                Toast.makeText(getContext(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MapFragment", "Error moving camera: " + e.getMessage());
            Toast.makeText(getContext(), "Error fetching location", Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchProvidersAndShowMarkers(String selectedAddress) {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean providerFound = false;
                Map<LatLng, Integer> locationCountMap = new HashMap<>();
                googleMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usermodel usermodel = snapshot.getValue(Usermodel.class);
                    usermodel.setKey(snapshot.getKey());

                    if (usermodel.getAddress() != null && usermodel.getAddress().equalsIgnoreCase(selectedAddress)) {
                        providerFound = true;
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<android.location.Address> addresses = geocoder.getFromLocationName(usermodel.getAddress(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                android.location.Address address = addresses.get(0);
                                LatLng providerLocation = new LatLng(address.getLatitude(), address.getLongitude());
                                int count = locationCountMap.getOrDefault(providerLocation, 0);
                                locationCountMap.put(providerLocation, count + 1);
                                LatLng offsetLocation = getOffsetLocation(providerLocation, count);
                                Glide.with(getContext())
                                        .asBitmap()
                                        .load(usermodel.getImage())
                                        .override(100, 100)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Bitmap circularBitmap = getCircularBitmap(resource);
                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .position(offsetLocation)
                                                        .title(usermodel.getUsername())
                                                        .snippet("Address: " + usermodel.getAddress())
                                                        .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap)); // Use the circular bitmap as the marker icon

                                                googleMap.addMarker(markerOptions);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            }
                        } catch (IOException e) {
                            Log.e("MapFragment", "Error geocoding address: " + e.getMessage());
                        }
                    }
                }

                if (!providerFound) {
                    notExistprovider();
                }

            }
                @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                Log.e("MapFragment", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    private void notExistprovider() {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String addressText = "Unknown location";
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(destinationLocation.latitude, destinationLocation.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                if (address.getThoroughfare() != null) {
                    sb.append(address.getThoroughfare());
                }
                if (address.getLocality() != null) {
                    sb.append(", ").append(address.getLocality());
                }
                addressText = sb.toString();
            }
        } catch (IOException e) {
            Log.e("MapFragment", "Error geocoding destination location: " + e.getMessage());
        }
        MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .position(destinationLocation)
                .title(addressText)
                .snippet("No providers found")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        googleMap.addMarker(destinationMarkerOptions);
        Toast.makeText(getContext(), "No providers found at this address", Toast.LENGTH_SHORT).show();
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
    private LatLng getOffsetLocation(LatLng originalLocation, int index) {
        double offset = 0.0001 * index;
        return new LatLng(originalLocation.latitude + offset, originalLocation.longitude + offset);
    }
    private void updateMapLine(LatLng latLng) {
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
                    getActivity().runOnUiThread(() -> {
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
    private void initMap(MapView mapView, Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.Apikey));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        isMapReady = true;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        requestAndSetUserLocation();
    }

    private void requestAndSetUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        geocodeLocation(userLocation);
                        startLocationUpdates();
                    } else {
                        Log.e("MapFragment", "User location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MapFragment", "Error getting user location: " + e.getMessage());
                });
    }
    private void geocodeLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
    private void startLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateCurrentLocationMarker(location);
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    fetchUserDetailsAndAddMarker(currentLatLng);
                }
            }
        };
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1500);
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
            DatabaseReference guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
            guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            loadUserImageAndAddMarker(user, userLocation);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MapFragment", "Error fetching user details: " + databaseError.getMessage());
                }
            });
        }
    }
    private void loadUserImageAndAddMarker(Usermodel usermodel, LatLng offsetLocation) {
        Context context = getContext();
        if (context == null) {
            Log.e("MapFragment", "Context is null, cannot create Geocoder.");
            return;
        }

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
                Glide.with(getContext())
                        .asBitmap()
                        .load(usermodel.getImage())
                        .override(100, 100)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Bitmap circularBitmap = getCircularBitmap(resource);
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(offsetLocation)
                                        .title(usermodel.getUsername())
                                        .snippet("Address: " + locationText.toString())
                                        .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap));
                                if (currentLocationMarker != null) {
                                    currentLocationMarker.remove();
                                }
                                currentLocationMarker = googleMap.addMarker(markerOptions);
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
    private void performSearch(String query) {
        if (userLocation != null) {
            PlacesClient placesClient = Places.createClient(getContext());
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .setLocationBias(RectangularBounds.newInstance(
                            new LatLng(userLocation.latitude - 0.05, userLocation.longitude - 0.05),
                            new LatLng(userLocation.latitude + 0.05, userLocation.longitude + 0.05)))
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
                Log.e("MapFragment", "Error fetching predictions: " + e.getMessage());
            });
        } else {
            Log.e("MapFragment", "User location is not available for performing search.");
        }
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
