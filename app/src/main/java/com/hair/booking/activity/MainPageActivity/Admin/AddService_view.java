package com.hair.booking.activity.MainPageActivity.Admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.Service;
import com.orhanobut.dialogplus.DialogPlus;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;

public class AddService_view extends AppCompatActivity {
    private ImageView addphoto,add,back;
    private EditText servicename, price;
    private DialogPlus dialogPlus;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private Uri imageUri;
    private AppCompatButton saved;
    private TextView name;
    private Spinner genderSpinner;
    private String gender,imageUrl,key,serviceName,Price;
    private String TAG = "AddService_view";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        changeStatusBarColor(getResources().getColor(R.color.bgColor));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        storageReference = FirebaseStorage.getInstance().getReference("services");
        databaseReference = FirebaseDatabase.getInstance().getReference("Service")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        servicename = findViewById(R.id.name);
        price = findViewById(R.id.price);
        addphoto = findViewById(R.id.ImageService);
        genderSpinner = findViewById(R.id.genderSpinner);
        saved = findViewById(R.id.saved);
        add = findViewById(R.id.add);
        name = findViewById(R.id.title);
        name.setText("Update service");
        back = findViewById(R.id.back);
        back.setOnClickListener(view -> onBackPressed());
        add.setVisibility(View.GONE);
        initClickers();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);



        gender = getIntent().getStringExtra("gender");
        imageUrl = getIntent().getStringExtra("imageUrl");
        key = getIntent().getStringExtra("key");
        serviceName = getIntent().getStringExtra("serviceName");
        Price = getIntent().getStringExtra("price");
        Log.d(TAG,"Price: "+Price);
        servicename.setText(serviceName);
        price.setText(Price);
        if (gender != null) {
            int spinnerPosition = adapter.getPosition(gender);
            genderSpinner.setSelection(spinnerPosition);

        }

        Glide.with(AddService_view.this)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.baseline_person_24)
                .into(addphoto);

    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initClickers() {
        addphoto.setOnClickListener(v -> openImagePicker());
        saved.setOnClickListener(view -> {
            if (imageUri != null || (imageUrl != null && !imageUrl.isEmpty())) {
                saveServiceData(imageUri);
            } else {
                Toast.makeText(AddService_view.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openImagePicker() {
        PhotoPicker.startPhotoPickerForResult(this, REQUEST_CODE_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Photo[] photos = PhotoPicker.getResultPhotos(data);
                if (photos.length > 0) {
                    imageUri = Uri.parse(String.valueOf(photos[0].getUri()));
                    openCropActivity(imageUri);
                }
                Log.i("AddService", "User selected " + photos.length + " photos");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("AddService", "Photo Picking Cancelled: " + resultCode);
            } else {
                Log.i("AddService", "Unknown result code: " + resultCode);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    addphoto.setImageURI(resultUri);
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                Toast.makeText(this, "Crop failed: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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

    private void saveServiceData(Uri imageUri) {
        String serviceName = servicename.getText().toString().trim();
        String priceText = price.getText().toString().trim();
        String selectedGender = genderSpinner.getSelectedItem().toString();

        if (serviceName.isEmpty() || priceText.isEmpty() || selectedGender.equals("Gender")) {
            Toast.makeText(this, "Please set Proper gender", Toast.LENGTH_SHORT).show();
            return;
        }



        int servicePrice;
        try {
            servicePrice = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null && (imageUrl == null || imageUrl.isEmpty())) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri != null) {
            uploadImageToFirebase(imageUri, serviceName, servicePrice, selectedGender);
        } else {
            saveServiceToDatabase(serviceName, servicePrice, imageUrl, selectedGender);
        }
    }


    private void uploadImageToFirebase(Uri imageUri, String serviceName, int servicePrice, String gender) {
        dialogPlus = new Dialog().loadingDialog(this);
        dialogPlus.show();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            while (baos.toByteArray().length / 1024 > 500 && quality > 0) {
                baos.reset();
                quality -= 5;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }

            byte[] data = baos.toByteArray();
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = fileReference.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot ->
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        dialogPlus.dismiss();
                        // Save the service data with the image URL after upload
                        saveServiceToDatabase(serviceName, servicePrice, uri.toString(), gender);
                    }).addOnFailureListener(e -> {
                        dialogPlus.dismiss();
                        Toast.makeText(AddService_view.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e -> {
                dialogPlus.dismiss();
                Toast.makeText(AddService_view.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            dialogPlus.dismiss();
            Toast.makeText(AddService_view.this, "Failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveServiceToDatabase(String serviceName, int servicePrice, String imageUrl, String gender) {
        Service service = new Service(serviceName, servicePrice, imageUrl, gender,key); // Include gender

        if (key != null) {
            databaseReference.child(key).setValue(service)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddService_view.this, "Service Updated: "+serviceName, Toast.LENGTH_SHORT).show();
                        Intent userChat = new Intent(getApplicationContext(), newHome.class);
                        startActivity(userChat);
                        overridePendingTransition(0, 0);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddService_view.this, "Failed to add service", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
