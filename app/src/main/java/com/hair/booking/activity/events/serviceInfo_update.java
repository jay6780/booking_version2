package com.hair.booking.activity.events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.ServiceInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;
import android.app.ProgressDialog;

public class serviceInfo_update extends AppCompatActivity {
    private String imgInfo, information, price, servicename;
    private ImageView serviceImg,back;
    private EditText informationText, priceEditText;
    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private Uri imageInfoUri;
    private Button saved;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private String key;
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_info_update);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        serviceImg = findViewById(R.id.imageUrl);
        informationText = findViewById(R.id.informationText);
        back = findViewById(R.id.back);
        saved = findViewById(R.id.saved);
        priceEditText = findViewById(R.id.price);
        titleText = findViewById(R.id.titleText);
        if (actionBar != null) {
            actionBar.hide();
        }
        key = getIntent().getStringExtra("key");
        imgInfo = getIntent().getStringExtra("imgInfo");
        information = getIntent().getStringExtra("information");
        servicename = getIntent().getStringExtra("servicename");
        price = getIntent().getStringExtra("price");
        databaseReference = FirebaseDatabase.getInstance().getReference("ServiceInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("serviceimage");

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(serviceInfo_update.this);
        progressDialog.setMessage("Saving data...");
        progressDialog.setCancelable(false);
        titleText.setText(information);

        initLoad();

        serviceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.startPhotoPickerForResult(serviceInfo_update.this, REQUEST_CODE_PHOTO_PICKER);
            }
        });
        back.setOnClickListener(view -> onBackPressed());

        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = informationText.getText().toString().trim();
                String price = priceEditText.getText().toString().trim();

                if (info.isEmpty() || price.isEmpty()) {
                    Toast.makeText(serviceInfo_update.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();

                Uri compressedImageUri = getImageUri();
                if (compressedImageUri != null) {
                    String storagePath = "ServiceImages/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + info + ".jpg";

                    storageReference.child(storagePath).putFile(compressedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            String imageUrl = uri.toString();
                                            checkAndSaveData(info, price, imageUrl); // Check for existing entry before saving
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(serviceInfo_update.this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss(); // Dismiss the progress dialog on failure
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(serviceInfo_update.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss(); // Dismiss the progress dialog on failure
                            });
                } else {
                    Toast.makeText(serviceInfo_update.this, "No image selected", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss(); // Dismiss the progress dialog if no image is selected
                }
            }
        });
    }

    private void checkAndSaveData(String info, String price, String imageUrl) {
        databaseReference.orderByChild("information").equalTo(info)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                saveRowDataToDatabase(info, price, imageUrl, servicename, key);
                                return;
                            }
                        } else {
                            saveRowDataToDatabase(info, price, imageUrl, servicename, key);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(serviceInfo_update.this, "Failed to check data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void saveRowDataToDatabase(String info, String price, String imageUrl, String serviceName, String serviceId) {
        ServiceInfo service = new ServiceInfo(info, price, imageUrl, serviceName,serviceId);
        if (serviceId != null) {
            databaseReference.child(serviceId).setValue(service)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Service info saved", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to save service", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
        }
    }

    private void initLoad() {
        priceEditText.setText(price);
        informationText.setText(information);
        Glide.with(this)
                .load(imgInfo)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(serviceImg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            Photo[] selectedPhotos = PhotoPicker.getResultPhotos(data);
            if (selectedPhotos != null && selectedPhotos.length > 0) {
                imageInfoUri = selectedPhotos[0].getUri();
                Glide.with(this)
                        .load(imageInfoUri)
                        .apply(new RequestOptions().circleCrop())
                        .into(serviceImg);
            } else {
                Toast.makeText(this, "No photos selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri() {
        Drawable drawable = serviceImg.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            int maxSizeKB = 200;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);
            while (outputStream.toByteArray().length / 1024 > maxSizeKB && compressQuality > 10) {
                compressQuality -= 10;
                outputStream.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);
            }
            try {
                File cachePath = new File(getCacheDir(), "temp_image.jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(cachePath);
                fileOutputStream.write(outputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();

                // Get the file URI from the cache path
                return Uri.fromFile(cachePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
