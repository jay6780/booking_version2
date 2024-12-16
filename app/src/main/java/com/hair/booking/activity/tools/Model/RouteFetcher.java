package com.hair.booking.activity.tools.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.hair.booking.activity.tools.Utils.AppConstans;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteFetcher {
    public interface RouteFetchListener {
        void onSuccess(JSONObject jsonResponse);
        void onError(String errorMessage);
    }

    public void fetchRouteData(LatLng end, LatLng start, RouteFetchListener listener) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("https://api.magicapi.dev/api/v1/trueway/routing/DirectionsService/FindDrivingRoute?stops=%f,%f;%f,%f&avoid_tolls=true&avoid_highways=false&avoid_ferries=false&optimize=true",
                start.latitude, start.longitude, end.latitude, end.longitude);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-magicapi-key", AppConstans.Apikey)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("RouteFetcher", "Error fetching route data: " + e.getMessage());
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("RouteFetcher","RouteFetcher: "+ responseBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        listener.onSuccess(jsonResponse);
                    } catch (Exception e) {
                        Log.e("RouteFetcher", "Error parsing JSON response: " + e.getMessage());
                        listener.onError(e.getMessage());
                    }
                } else {
                    Log.e("RouteFetcher", "Unexpected response code: " + response.code());
                    listener.onError("Unexpected response code: " + response.code());
                }
            }
        });
    }
}
