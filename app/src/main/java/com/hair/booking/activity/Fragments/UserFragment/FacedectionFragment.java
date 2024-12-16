package com.hair.booking.activity.Fragments.UserFragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.FullScreenImageActivity2;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.ApiResponse;
import com.hair.booking.activity.tools.Model.ImageProcessResponse;
import com.hair.booking.activity.tools.Model.Makeup;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.MakeupAdapter;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FacedectionFragment extends Fragment implements View.OnClickListener {

    private String TAG = "facedetection";
    ImageView imageView, imgTakePicture;
    Button btnProcessNext, btnTakePicture;
    DialogPlus dialogPlus;
    RecyclerView recyclerView;
    private FaceDetector detector;
    private MakeupAdapter adapter;
    Bitmap editedBitmap;
    int currentIndex = 0;
    int[] imageArray;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 200;
    private static final int CAMERA_REQUEST = 101;
    private static final int CAMERA_REQUEST_CODE = 100;
    private String guessImage = SPUtils.getInstance().getString(AppConstans.guessImage);
    private String userEmail = SPUtils.getInstance().getString(AppConstans.userEmail);
    private String usernameText = SPUtils.getInstance().getString(AppConstans.usernameText);
    private String phone = SPUtils.getInstance().getString(AppConstans.phone);
    private String fullname = SPUtils.getInstance().getString(AppConstans.fullname);
    private String addressUser = SPUtils.getInstance().getString(AppConstans.addressUser);
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference,guess;
    private StorageReference storageReference;
    private String swap_url;
    private String target_url;
    private String requestId;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";
    private String imageUrl;
    private boolean isRequestInProgress = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_facedetection, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("myfaceswap");
        storageReference = FirebaseStorage.getInstance().getReference("myfaceswap");

        // Set up Firebase and Face Detector
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            guess = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
        }

        detector = new FaceDetector.Builder(requireContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        initViews(view);
        initStartCapture();

        return view;
    }

    private void initViews(View view) {
        imgTakePicture = view.findViewById(R.id.imgTakePic);
        btnTakePicture = view.findViewById(R.id.btnTakePicture);
        recyclerView = view.findViewById(R.id.makeupResult);
        btnTakePicture.setOnClickListener(this);
        imgTakePicture.setOnClickListener(this);
    }

    private void initStartCapture() {
        imageUrl = getArguments() != null ? getArguments().getString("imageUrl") : null;
        if (imageUrl == null) {
            Log.d(TAG, "imageUrl: " + imageUrl);
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            uploadImageToFirebaseStorage2();
        }
    }

    private void uploadImageToFirebaseStorage2() {
        if (imageUrl == null) return;

        // Load the image from the URL using Glide
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        // Compress the bitmap
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String fileName = UUID.randomUUID().toString() + ".jpg";
                            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(fileName);
                            UploadTask uploadTask = imageRef.putBytes(data);

                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    databaseReference.child(user.getUid()).child("target_url").setValue(downloadUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), "Image uploaded and URL updated!", Toast.LENGTH_SHORT).show();
                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                    initGetValue(); // Call initGetValue after a delay
                                                }, 10000);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Failed to update URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to get URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(requireContext().getExternalFilesDir(null), "photo.jpg");
        imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakePicture:
                imgTakePicture.setImageResource(R.mipmap.man);
                if (adapter != null) {
                    adapter.clear();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                break;
            case R.id.imgTakePic:
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (hasCameraPermission()) {
                startCamera();
            } else {
                requestCameraPermission();
            }

        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE );
    }
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            launchMediaScanIntent();
            try {
                processCameraPicture();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Failed to load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        requireActivity().sendBroadcast(mediaScanIntent);
    }

    private void processCameraPicture() throws Exception {
        Bitmap bitmap = decodeBitmapUri(getContext(), imageUri);
        if (detector.isOperational() && bitmap != null) {
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
                recyclerView.setVisibility(View.VISIBLE);
                uploadImageToFirebaseStorage();

                if (imageUrl == null) {
                    guess.child("gender").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String gender = dataSnapshot.getValue(String.class);
                                if (gender != null) {
                                    if (gender.equalsIgnoreCase("Female")) {
                                        displayFemaleMakeupOptions();
                                    } else if (gender.equalsIgnoreCase("Male")) {
                                        displayMaleMakeupOptions();

                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                } else {
                    imgTakePicture.setImageResource(R.drawable.baseline_person_24);
                }
            } else {
                showNoFaceDetectedDialog();
            }
        }
    }

    private void displayFemaleMakeupOptions() {
        List<Makeup> makeupOptions = new ArrayList<>();
        makeupOptions.add(new Makeup("Light Makeup", R.mipmap.imgesample1));
        makeupOptions.add(new Makeup("Light Makeup", R.mipmap.imagesample2));
        makeupOptions.add(new Makeup("Smokey-eye Makeup", R.mipmap.imagesample3));
        makeupOptions.add(new Makeup("Wedding Makeup", R.mipmap.imagesample4));
        makeupOptions.add(new Makeup("Wedding Makeup", R.mipmap.imagesample5));
        makeupOptions.add(new Makeup("Graduation light makeup look v2", R.mipmap.imagesample6));
        makeupOptions.add(new Makeup("Graduation light makeup look v3", R.mipmap.imagesample7));
        makeupOptions.add(new Makeup("Graduation light makeup look v4", R.mipmap.imagesample8));
        Collections.shuffle(makeupOptions);
        List<Makeup> selectedOptions = makeupOptions.subList(0, 1); // Choose one random option
        adapter = new MakeupAdapter(selectedOptions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void displayMaleMakeupOptions() {
        List<Makeup> maleMakeupOptions = new ArrayList<>();
        maleMakeupOptions.add(new Makeup("Subtle Grooming", R.mipmap.grooming));
        maleMakeupOptions.add(new Makeup("Sample", R.mipmap.sample2));
        int seed = Integer.hashCode(R.mipmap.grooming) + Integer.hashCode(R.mipmap.sample2);
        Random random = new Random(seed);
        Collections.shuffle(maleMakeupOptions, random);
        List<Makeup> selectedOptions = maleMakeupOptions.subList(0, 1);
        adapter = new MakeupAdapter(selectedOptions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void uploadImageToFirebaseStorage() {
        if (editedBitmap == null) return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        editedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(fileName);
            UploadTask uploadTask = imageRef.putBytes(data);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    databaseReference.child(user.getUid()).child("swap_url").setValue(downloadUrl)
                            .addOnSuccessListener(aVoid -> {
                                // Notify the user of successful upload and URL update
                                Toast.makeText(getContext(), "Image uploaded and URL updated!", Toast.LENGTH_SHORT).show();

                                // Use Handler to add a delay before calling initGetValue
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    initGetValue(); // Call initGetValue after a delay
                                }, 10000); // Delay for 3000 milliseconds (3 seconds)
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to get URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void initGetValue() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseDatabase.getInstance().getReference("myfaceswap").child(userId)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                            if (dataSnapshot.exists()) {
                                String key = dataSnapshot.getKey();
                                Object value = dataSnapshot.getValue();

                                if ("swap_url".equals(key)) {
                                    swap_url = (value != null) ? value.toString() : null;
                                    Log.d("URLs", "Swap URL: " + swap_url);
                                } else if ("target_url".equals(key)) {
                                    target_url = (value != null) ? value.toString() : null;
                                    Log.d("URLs", "Target URL: " + target_url);
                                }

                                if (swap_url != null && target_url != null) {
                                    passedtheurl(swap_url, target_url);
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                            onChildAdded(dataSnapshot, previousChildName);
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
                        }
                    });
        }
    }

    private void passedtheurl(String swap_url, String target_url) {
        OkHttpClient client = new OkHttpClient();
        String bodyContent = String.format(
                "target_url=%s&swap_url=%s",
                target_url,
                swap_url
        );
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, bodyContent);
        Request request = new Request.Builder()
                .url("https://api.magicapi.dev/api/v1/capix/faceswap/faceswap/v1/image")
                .post(body)
                .addHeader("x-magicapi-key", getString(R.string.faceswapapikey))
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("OkHttp", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.i("OkHttp", "Response: " + responseData);
                    Gson gson = new Gson();
                    ImageProcessResponse imageProcessResponse = gson.fromJson(responseData, ImageProcessResponse.class);
                    requestId = imageProcessResponse.getImageProcessResponse().getRequestId();
                    passedRequestId(requestId);
                    Log.i("OkHttp", "Request ID: " + requestId);
                } else {
                    Log.e("OkHttp", "Request failed with status code: " + response.code());
                }
            }
        });
    }

    private void passedRequestId(String requestId) {
        if (isRequestInProgress) {
            Log.w("Request", "Request is already in progress. Ignoring the new call.");
            return;
        }

        isRequestInProgress = true;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Create a ProgressDialog
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                if (!getActivity().isFinishing() && !getActivity().isDestroyed()) {
                    progressDialog.show();
                }

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "request_id=" + requestId);
                Request request = new Request.Builder()
                        .url("https://api.magicapi.dev/api/v1/capix/faceswap/result/")
                        .post(body)
                        .addHeader("x-magicapi-key", getString(R.string.faceswapapikey))
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("OkHttp", "Request failed: " + e.getMessage());
                        getActivity().runOnUiThread(() -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            isRequestInProgress = false;
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!isRequestInProgress) {
                            return;
                        }

                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.i("OkHttp", "imageResult: " + responseData);
                            Gson gson = new Gson();
                            ApiResponse apiResponse = gson.fromJson(responseData, ApiResponse.class);
                            String resultUrl = apiResponse.getImageProcessResponse().getResultUrl();
                            Log.i("OkHttp", "Original resultUrl: " + resultUrl);
                            String convertedUrl = convertToHttps(resultUrl);
                            Log.i("OkHttp", "Converted resultUrl: " + convertedUrl);

                            if (convertedUrl != null) {
                                getActivity().runOnUiThread(() -> {
                                    Glide.with(getActivity())
                                            .load(convertedUrl)
                                            .placeholder(R.drawable.baseline_person_24)
                                            .error(R.drawable.baseline_person_24)
                                            .into(imgTakePicture);

                                    Intent intent = new Intent(getActivity(), FullScreenImageActivity2.class);
                                    intent.putExtra("image_url", convertedUrl);
                                    startActivity(intent);
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (currentUser != null) {
                                        String userId = currentUser.getUid();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("myfaceswap").child(userId);
                                        databaseReference.removeValue().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.i("Firebase", "Swap data deleted successfully for userId: " + userId);
                                            } else {
                                                Log.e("Firebase", "Failed to delete swap data for userId: " + userId);
                                            }
                                        });
                                    } else {
                                        Log.e("Firebase", "User is not authenticated, can't delete swap data.");
                                    }
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                });
                            } else {
                                Log.e("OkHttp", "Result URL is null after conversion");
                            }
                        } else {
                            Log.e("OkHttp", "Request failed with status code: " + response.code());
                            Log.e("OkHttp", "Error response: " + response.body().string());
                        }

                        getActivity().runOnUiThread(() -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            isRequestInProgress = false; // Reset the flag after processing response
                        });

                        // Clear URLs
                        imageUrl = null;
                        swap_url = null;
                        target_url = null;
                    }
                });
            }
        });
    }


    private String convertToHttps(String url) {
        if (url != null && url.startsWith("http:")) {
            return url.replace("http:", "https:");
        }
        return url;
    }

    private void showNoFaceDetectedDialog() {
        Dialog dialog = new Dialog();
        dialog.shownoface(getActivity());
        imgTakePicture.setImageResource(R.drawable.baseline_person_24);
        if (adapter != null) {
            adapter.clear();
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        detector.release();
    }
}
