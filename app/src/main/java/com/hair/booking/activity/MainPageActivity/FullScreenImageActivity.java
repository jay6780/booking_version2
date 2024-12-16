package com.hair.booking.activity.MainPageActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.hair.booking.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullScreenImageActivity extends AppCompatActivity {
    ImageView backbtn,download;
    String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.fullscreen_menu55);
        backbtn = findViewById(R.id.back);
        download = findViewById(R.id.download);
        PhotoView photoView = findViewById(R.id.photo_view);
         imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_person_24)
                .into(photoView);
        download.setOnClickListener(view -> savedphoto());
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void savedphoto() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = "downloaded_image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        Glide.with(FullScreenImageActivity.this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Toast.makeText(getApplicationContext(), "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }
}
