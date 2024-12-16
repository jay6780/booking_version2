package com.hair.booking.activity.MainPageActivity.addreview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.idlestar.ratingstar.RatingStarView;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;

public class addReviewActivity extends AppCompatActivity {
    String key = SPUtils.getInstance().getString(AppConstans.key);
    private DatabaseReference reviewRef, guessRef, adminRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private TextView nametext,titleText;
    private ImageView avatarimage, addimmage;
    private RatingStarView ratingBar;
    private EditText content;
    private String email, username, image, address, age, lengthOfservice,userimage;
    private boolean isOnline;
    private static final int REQUEST_CODE_PHOTO_PICKER = 1;
    private Uri imageUri = null;
    private String currentUserName;
    private DialogPlus dialogPlus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        addimmage = findViewById(R.id.addtask);
        initGuess();
        initAdmin();
        initSavedata();
        initView();
        initClickers();
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
    }

    private void initSavedata() {
        addimmage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.startPhotoPickerForResult(addReviewActivity.this, REQUEST_CODE_PHOTO_PICKER);
            }
        });
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReviewData();

            }
        });
    }


    private void saveReviewData() {
        final String reviewContent = content.getText().toString();
        final float rating = ratingBar.getRating();

        if (reviewContent.isEmpty()) {
            Toast.makeText(this, getString(R.string.reviewcontent_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(this, getString(R.string.addratingError), Toast.LENGTH_SHORT).show();
            return;
        }

        dialogPlus = new Dialog().loadingDialog(addReviewActivity.this);
        dialogPlus.show();

        if (imageUri != null) {

            uploadImageToStorage(new ImageUploadCallback() {
                @Override
                public void onImageUploaded(Uri downloadUri) {
                    saveReviewToDatabase(downloadUri.toString(), reviewContent, rating, userimage);
                }

                @Override
                public void onFailure(Exception e) {
                    dialogPlus.dismiss();
                    Toast.makeText(addReviewActivity.this, getString(R.string.errorupload), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            saveReviewToDatabase(null, reviewContent, rating, userimage);
        }
    }


    private void initAdmin() {
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(key);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usermodel user = dataSnapshot.getValue(Usermodel.class);
                    if (user != null) {
                     email = user.getEmail();
                     username = user.getUsername();
                     image = user.getImage();
                     address = user.getAddress();
                     age = user.getAge();
                     lengthOfservice = user.getLengthOfService();
                     isOnline = user.isOnline();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initView() {
        titleText = findViewById(R.id.titleText);
        content = findViewById(R.id.titleTextView);
        nametext = findViewById(R.id.nametext);
        avatarimage = findViewById(R.id.profileImageView);
        ratingBar = findViewById(R.id.star);
        titleText.setText(R.string.Add_comment);
    }

    private void initGuess() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);

            guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            String name = user.getUsername();
                            userimage = user.getImage();
                            loadUserdetails(name,userimage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void initClickers() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.back) {
                    onBackPressed();
                } else if (v.getId() == R.id.logout) {

                }

            }
        };

        idlisterners(clickListener);
    }
    private void idlisterners(View.OnClickListener clickListener) {
        findViewById(R.id.back).setOnClickListener(clickListener);
    }


    private void loadUserdetails(String name, String image) {
        currentUserName = name;
        nametext.setText(name);
        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(avatarimage);
        } else {
            avatarimage.setImageResource(R.drawable.baseline_person_24);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            Photo[] selectedPhotos = PhotoPicker.getResultPhotos(data);
            if (selectedPhotos != null && selectedPhotos.length > 0) {
                Uri selectedImageUri = selectedPhotos[0].getUri();
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(addimmage);
                imageUri = selectedImageUri;
            } else {
                Toast.makeText(this, getString(R.string.noPhotoselectError), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadImageToStorage(final ImageUploadCallback callback) {
        Uri imageUri = getImageUri();
        if (imageUri != null) {
            String randomImageName = "reviews/" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(randomImageName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(callback::onImageUploaded))
                    .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("No image selected"));
        }
    }

    private Uri getImageUri() {
        return imageUri;
    }

    private void saveReviewToDatabase(String imageUrl, String content, float rating, String userimage) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long timemilli = System.currentTimeMillis();
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(key).push();
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("username", currentUserName);
        reviewData.put("email", email);
        reviewData.put("image", imageUrl != null ? imageUrl : "");
        reviewData.put("content", content);
        reviewData.put("rating", rating);
        reviewData.put("userimage", userimage);
        reviewData.put("userId", userId);
        reviewData.put("reviewId", reviewRef.getKey());
        reviewData.put("timemilli", timemilli);

        reviewRef.setValue(reviewData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(addReviewActivity.this, getString(R.string.review_success), Toast.LENGTH_SHORT).show();
                    finish();
                    dialogPlus.dismiss();
                })
                .addOnFailureListener(e -> {
                    dialogPlus.dismiss();
                    Toast.makeText(addReviewActivity.this, getString(R.string.review_failed), Toast.LENGTH_SHORT).show();
                });
    }

    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }
    interface ImageUploadCallback {
        void onImageUploaded(Uri downloadUri);
        void onFailure(Exception e);
    }
}