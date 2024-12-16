package com.hair.booking.activity.tools.Model;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hair.booking.activity.tools.Utils.AppConstans;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeupEffectProcessor {

    private Context context;
    private MakeupEffectCallback callback;
    private OkHttpClient client;

    public MakeupEffectProcessor(Context context, MakeupEffectCallback callback) {
        this.context = context;
        this.callback = callback;
        this.client = new OkHttpClient();
    }

    public void applyMakeupEffect(String imagePath, int resourceType) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Applying makeup effect...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        File imageFile = new File(imagePath);
        if (!imageFile.exists() || !imageFile.isFile()) {
            Log.e("API_ERROR", "Image file does not exist: " + imagePath);
            progressDialog.dismiss();
            Toast.makeText(context, "Invalid image file.", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/jpeg")))
                .addFormDataPart("resource_type", String.valueOf(resourceType))
                .addFormDataPart("strength", "1")
                .build();

        Request request = new Request.Builder()
                .url("https://api.magicapi.dev/api/v1/ailabtools/ai-makeup/portrait/effects/face-makeup")
                .post(requestBody)
                .addHeader("x-magicapi-key", AppConstans.Apikey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                Log.e("API_ERROR", "Request failed: " + e.getMessage());
                e.printStackTrace();
                callback.onFailure("Failed to apply makeup. Please try again.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        Log.d("API_RESPONSE", "Response received: " + jsonData);
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String processedImageUrl = jsonObject.getJSONObject("data").getString("image_url");
                        callback.onSuccess(processedImageUrl);
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error parsing response: " + e.getMessage());
                        e.printStackTrace();
                        callback.onFailure("Error processing the image. Please try again.");
                    }
                } else {
                    Log.e("API_ERROR", "Response was not successful: " + response.code());
                    callback.onFailure("Failed to apply makeup. Please try again.");
                }
            }
        });
    }

    public interface MakeupEffectCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }
}
