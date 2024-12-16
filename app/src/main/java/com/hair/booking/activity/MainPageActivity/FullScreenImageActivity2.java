package com.hair.booking.activity.MainPageActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.MakeupEffectProcessor;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.lee.avengergone.DisappearView;
import com.orhanobut.dialogplus.DialogPlus;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Random;

public class FullScreenImageActivity2 extends AppCompatActivity {
    ImageView backbtn, downloadBtn;
    RelativeLayout relativeLayout;
    DialogPlus dialogPlus;
    String imageUrl;
    PhotoView photoView, photoView2;
    String processedImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_full_screen_image);
        backbtn = findViewById(R.id.back);
        downloadBtn = findViewById(R.id.download);
        relativeLayout = findViewById(R.id.rl23);
        photoView = findViewById(R.id.photo_view);
        photoView2 = findViewById(R.id.photoView2);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        imageUrl = getIntent().getStringExtra("image_url");
        SPUtils.getInstance().put(AppConstans.ImageUrl, imageUrl);
        loadAndDisplayImage();

        downloadBtn.setOnClickListener(view -> showDialog());
        backbtn.setOnClickListener(view -> onBackPressed());
    }

    private void loadAndDisplayImage() {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.baseline_person_24)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        photoView.setImageBitmap(resource);
                        DisappearView disappearView = DisappearView.attach(FullScreenImageActivity2.this);
                        disappearView.execute(photoView, 2500, new AccelerateInterpolator(0.5f), true);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Uri imageUri = Uri.parse(imageUrl);
                            openCropActivity(imageUri);
                        }, 2500);
                    }

                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                        // Handle placeholder or cleanup if needed
                    }
                });
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

    // Handle the crop result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
                guessRef.child("gender").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String gender = dataSnapshot.getValue(String.class);
                            if (gender != null && gender.equalsIgnoreCase("Female")) {
                                applyMakeupEffect(resultUri.getPath(), new Random().nextInt(7));
                            } else {
                                Toast.makeText(FullScreenImageActivity2.this, "Skipping makeup for male users", Toast.LENGTH_SHORT).show();
                                displayCroppedImage(resultUri);
                            }
                        } else {
                            displayCroppedImage(resultUri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Failed to retrieve gender: " + databaseError.getMessage());
                        displayCroppedImage(resultUri);
                    }
                });
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("CROP_ERROR", cropError.getMessage());
        }
    }

    private void displayCroppedImage(Uri resultUri) {
        Glide.with(FullScreenImageActivity2.this)
                .load(resultUri)
                .placeholder(R.drawable.baseline_person_24)
                .into(photoView2);
        photoView2.setVisibility(View.VISIBLE);
    }
    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void showDialog() {
        relativeLayout.setVisibility(View.GONE);
        dialogPlus = new Dialog().showdownload(FullScreenImageActivity2.this);
    }

    private void applyMakeupEffect(String imagePath, int resourceType) {
        MakeupEffectProcessor processor = new MakeupEffectProcessor(this, new MakeupEffectProcessor.MakeupEffectCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                processedImageUrl = imageUrl;
                SPUtils.getInstance().put(AppConstans.ImageUrl, processedImageUrl);
                Log.d("IMAGE_URL", "Extracted image URL: " + processedImageUrl);
                runOnUiThread(() -> {
                    Glide.with(FullScreenImageActivity2.this)
                            .load(processedImageUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .into(photoView2);
                    photoView2.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FullScreenImageActivity2.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
        processor.applyMakeupEffect(imagePath, resourceType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
