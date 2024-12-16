package com.hair.booking.activity.MainPageActivity.Guess;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hair.booking.activity.MainPageActivity.Admin.profile_updateAdmin;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.MainPageActivity.profileUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;

public class profile_update extends AppCompatActivity {

    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 100;
    Bitmap editedBitmap;
    private boolean isImagePicked = false;
    private boolean isFaceDetected = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to go back to the menu?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       onBackPressed();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ProgressDialog progressDialog;
    private EditText fullnameEditText, usernameEditText,phone,ageEditText;
    private Button updateButton;
    private ImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    TextView emailEditText;
    private Uri userImageUri;
    private EditText phoneNumberEditText,birthdays,addressUser;
    private FaceDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_updateprofile);

        // Set the title of the action bar
        getSupportActionBar().setTitle(R.string.myProfile);
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeStatusBarColor(getResources().getColor(R.color.white2));

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white2)));
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        updateButton = findViewById(R.id.btn23);
        fullnameEditText = findViewById(R.id.name);
        addressUser = findViewById(R.id.address);
        ageEditText = findViewById(R.id.age);
        phone = findViewById (R.id.Phone);
        profileImageView = findViewById(R.id.upload_img);
        phoneNumberEditText = findViewById(R.id.Phone);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Guess");
        storageReference = FirebaseStorage.getInstance().getReference().child("images");
        detector = new FaceDetector.Builder(profile_update.this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String email = intent.getStringExtra("email");
            String image = intent.getStringExtra("image");
            String phone = intent.getStringExtra("phone");
            String name = intent.getStringExtra("name");
            String address = intent. getStringExtra("address");
            String age = intent. getStringExtra("age");
            ageEditText.setText(age);
            usernameEditText.setText(username);
            addressUser.setText(address);
            addressUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog  dialog = new Dialog();
                    dialog.updateLocation2(profile_update.this,image,address,username,name,phone,email);

                }
            });
            emailEditText.setText(email);
            fullnameEditText.setText(name);
            phoneNumberEditText.setText(phone);
            if (image != null && !image.isEmpty()) {
                RequestOptions requestOptions = new RequestOptions().circleCrop();
                Glide.with(this)
                        .load(image)
                        .apply(requestOptions)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.baseline_person_24);
            }
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImagePicked && !isFaceDetected) {
                    Toast.makeText(getApplicationContext(), "No face detected. Please select a valid image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fullname = fullnameEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String userAddress = addressUser.getText().toString().trim();
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                String age = ageEditText.getText().toString().trim();

                if (TextUtils.isEmpty(age)) {
                    ageEditText.setError("Please enter your age");
                    return;
                } else {
                    ageEditText.setError(null); // Clear the error
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberEditText.setError("Please enter your phone number");
                    return;
                } else {
                    phoneNumberEditText.setError(null); // Clear the error
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    addressUser.setError("Please enter your address");
                    return;
                } else {
                    addressUser.setError(null); // Clear the error
                }

                // Check if all fields are filled
                if (TextUtils.isEmpty(fullname)) {
                    fullnameEditText.setError("Please enter your fullname");
                    return;
                } else {
                    fullnameEditText.setError(null); // Clear the error
                }

                if (TextUtils.isEmpty(username)) {
                    usernameEditText.setError("Please enter your username");
                    return;
                } else {
                    usernameEditText.setError(null); // Clear the error
                }



                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = databaseReference.child(userId);
                userRef.child("name").setValue(fullname);
                userRef.child("username").setValue(username);
                userRef.child("email").setValue(email);
                userRef.child("phone").setValue(phoneNumber);
                userRef.child("address").setValue(userAddress);
                userRef.child("age").setValue(age);
                Uri imageUri = getImageUri();
                if (imageUri != null) {
                    progressDialog = new ProgressDialog(profile_update.this);
                    progressDialog.setMessage("Updating profile...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    StorageReference imageRef = storageReference.child(userId + "/image.jpg"); // Use the user ID as the child key and a unique image name
                    imageRef.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image upload successful
                                    // Get the download URL of the uploaded image
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            userRef.child("image").setValue(imageUrl)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Profile update successful
                                                            if (progressDialog != null) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(profile_update.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(profile_update.this, newHome.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(0,0);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Profile update failed
                                                            if (progressDialog != null) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(profile_update.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Image upload failed
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(profile_update.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // No image selected
                    Toast.makeText(profile_update.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void processCameraPicture(Bitmap bitmap) throws Exception {
        if (detector.isOperational() && bitmap != null) {
            // Clear the image before processing
            profileImageView.setImageResource(0);  // Clears the profile image view

            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
            paint.setTextSize((int) (16 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6f);
            Canvas canvas = new Canvas(editedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);

            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);

            if (faces.size() > 0) {
                // Face detected, set the appropriate image
                isFaceDetected = true;
                profileImageView.setImageResource(R.mipmap.man);
            } else {
                // No face detected, show dialog and set default image
                isFaceDetected = false;
                showNoFaceDetectedDialog();
            }
        }
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 300;
        int targetH = 300;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    private void showNoFaceDetectedDialog() {
        Dialog dialog = new Dialog();
        dialog.shownoface(profile_update.this);
        profileImageView.setImageResource(R.mipmap.man);
    }
    private void showImagePickerDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(profile_update.this);
        builder.setTitle("Select Image")
                .setItems(new CharSequence[]{"Pick Photo", "Capture Image"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            PhotoPicker.startPhotoPickerForResult(profile_update.this, REQUEST_CODE_PHOTO_PICKER);
                        } else if (which == 1) {
                            openCameraForImageCapture();
                        }
                    }
                })
                .show();
    }

    private void openCameraForImageCapture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CODE_CAPTURE_IMAGE);
        } else {
            Toast.makeText(getApplicationContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraForImageCapture();
            } else {
                Toast.makeText(getApplicationContext(), "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);

                String formattedDate = android.text.format.DateFormat.format("MMMM d, yyyy", calendar).toString();
                birthdays.setText(formattedDate);

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = databaseReference.child(userId); // Use the user ID as the child key
                userRef.child("birthday").setValue(formattedDate);
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(profile_update.this,
                dateSetListener, year, month, day);
        datePickerDialog.show();
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }


    // ...

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            Photo[] selectedPhotos = PhotoPicker.getResultPhotos(data);
            if (selectedPhotos != null && selectedPhotos.length > 0) {
                userImageUri = selectedPhotos[0].getUri();
                Glide.with(this)
                        .load(userImageUri)
                        .apply(new RequestOptions().circleCrop())
                        .into(profileImageView);
                isImagePicked = true;
                isFaceDetected = true;
            } else {
                Toast.makeText(this, "No photos selected", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            userImageUri = getImageUriFromBitmap(photo);
            Glide.with(this)
                    .load(photo)
                    .apply(new RequestOptions().circleCrop())
                    .into(profileImageView);

            isImagePicked = true;
            try {
                processCameraPicture(photo);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "StyleSync", null);
        return Uri.parse(path);
    }
    private Uri getImageUri() {
        Drawable drawable = profileImageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();

            // Define the desired image size in kilobytes
            int maxSizeKB = 200; // Adjust this value as needed

            // Compress the bitmap to the desired size
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);
            while (outputStream.toByteArray().length / 1024 > maxSizeKB && compressQuality > 10) {
                compressQuality -= 10;
                outputStream.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);
            }

            // Save the compressed bitmap to a file and get the file URI
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
        return null; // Return null if the image URI couldn't be retrieved
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
