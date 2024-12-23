package com.hair.booking.activity.tools.DialogUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.azhon.appupdate.manager.DownloadManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hair.booking.R;
import com.hair.booking.activity.Application.TinkerApplications;
import com.hair.booking.activity.MainPageActivity.Admin.profile_updateAdmin;
import com.hair.booking.activity.MainPageActivity.Guess.facedetection;
import com.hair.booking.activity.MainPageActivity.Guess.profile_update;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.maps.MapSelectActivity_profile;
import com.hair.booking.activity.MainPageActivity.maps.MapSelectActivity_profile2;
import com.hair.booking.activity.MainPageActivity.maps.MapSelectActivity_profile3;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.events.profile_updateEvents;
import com.hair.booking.activity.tools.Model.ServiceInfo;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ServiceInfoAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Dialog {
    String selectedTime;
    public void updateDialog(Activity context,String apkUrl,String versionName) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.update_dialog))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button yes = dialogView.findViewById(R.id.Yes);
        Button no = dialogView.findViewById(R.id.no);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("New Update");
        yes.setText("Update");
        no.setText("Skip");
        yes.setOnClickListener(v -> {
            startUpdate(apkUrl,versionName,context);
            dialog.dismiss();
        });
        no.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void startUpdate(String apkUrl, String versionName,Activity context) {
        DownloadManager manager = new DownloadManager.Builder(context)
                .apkUrl(apkUrl)
                .apkName("StyleSync.apk")
                .smallIcon(R.mipmap.applogo)
                .apkVersionName(versionName)
                .apkDescription("App update is available")
                .build();
        manager.download();
    }


    public void deleteinfo(Context context, String serviceNameToDelete, int position, ServiceInfoAdapter adapter, List<ServiceInfo> serviceInfoList) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.logout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button yes = dialogView.findViewById(R.id.Yes);
        Button no = dialogView.findViewById(R.id.no);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText(context.getString(R.string.deletesure));
        yes.setOnClickListener(v -> {
            deletedata(serviceNameToDelete, context, position, adapter, serviceInfoList);
            dialog.dismiss();
        });
        no.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void deletedata(String serviceNameToDelete, Context context, int position, ServiceInfoAdapter adapter, List<ServiceInfo> serviceInfoList) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ServiceInfo").child(userId);
        databaseReference2.orderByChild("information").equalTo(serviceNameToDelete).get()
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        for (DataSnapshot snapshot2 : task2.getResult().getChildren()) {
                            snapshot2.getRef().removeValue()
                                    .addOnSuccessListener(aVoid2 -> {
                                        if (position >= 0 && position < serviceInfoList.size()) {
                                            if (serviceInfoList != null) {
                                                serviceInfoList.remove(position);
                                                adapter.notifyItemRemoved(position);
                                            }
                                            Toast.makeText(context, "Service deleted: " + serviceNameToDelete, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e2 -> {
                                        Toast.makeText(context, "Failed to delete from ServiceInfo: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Failed to query ServiceInfo: " + task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void clearDiscount(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.logout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button logoutButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText(activity.getString(R.string.clearedDiscount));
        logoutButton.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference("Discounts");
            discountRef.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot discountSnapshot : dataSnapshot.getChildren()) {
                            discountSnapshot.getRef().removeValue();
                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.discount_clearedsucess), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.discountError), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(activity.getApplicationContext(), "Error clearing discount: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }



    public void restartDialog(Activity activity,String restartapp) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.restart_layout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button restartbtn = dialogView.findViewById(R.id.saved);
        TextView text = dialogView.findViewById(R.id.text);
        text.setText(restartapp);
        restartbtn.setOnClickListener(v -> {
            activity.finishAffinity();
            System.exit(0);
        });

        dialog.show();
    }


    public void genderDialog(Activity activity, String userId) {
            DialogPlus dialog = DialogPlus.newDialog(activity)
                    .setContentHolder(new ViewHolder(R.layout.layout_gender))
                    .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .create();

            View dialogView = dialog.getHolderView();
            Button saveButton = dialogView.findViewById(R.id.saved);
            Spinner genderSpinner = dialogView.findViewById(R.id.genderspinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    activity, R.array.gender_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(adapter);
            genderSpinner.setSelection(0);

            saveButton.setOnClickListener(v -> {
                String selectedGender = genderSpinner.getSelectedItem().toString();
                if (selectedGender.equals("Gender")) {
                    Toast.makeText(activity, "Please select a valid gender", Toast.LENGTH_SHORT).show();
                } else {
                    // Save the selected gender to Firebase
                    DatabaseReference guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
                    guessRef.child("gender").setValue(selectedGender)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(activity, "Gender saved successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(activity, "Failed to save gender", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            dialog.show();
        }


    public void genderDialog2(Context context, String userId,String name) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.layout_gender))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        Button saveButton = dialogView.findViewById(R.id.saved);
        Spinner genderSpinner = dialogView.findViewById(R.id.genderspinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(0);

        saveButton.setOnClickListener(v -> {
            String selectedGender = genderSpinner.getSelectedItem().toString();
            if (selectedGender.equals("Gender")) {
                Toast.makeText(context, "Please select a valid gender", Toast.LENGTH_SHORT).show();
            } else {
                saveGender(userId,name,selectedGender, context);
                dialog.dismiss(); // Close the dialog after saving
            }
        });

        dialog.show();
    }


    public void discountdialog(Context context, String userId, String name, int drawableResId, String imageUrl,String gender,String key) {
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.layout_discount))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        Button saveButton = dialogView.findViewById(R.id.saved);
        Spinner discountSpinner = dialogView.findViewById(R.id.genderspinner);
        TextView title = dialogView.findViewById(R.id.title);
        TextView serviceNAME = dialogView.findViewById(R.id.serviceNAME);
        ImageView previewImage = dialogView.findViewById(R.id.preview_image);
        String discount = context.getString(R.string.discount);
        serviceNAME.setText(context.getString(R.string.service_name)+": "+name);
        title.setText(discount);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.discount_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discountSpinner.setAdapter(adapter);
        discountSpinner.setSelection(0);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).circleCrop().into(previewImage);
        } else if (drawableResId != 0) {
            Glide.with(context).load(drawableResId).circleCrop().into(previewImage);
        }

        saveButton.setOnClickListener(v -> {
            String discounts = discountSpinner.getSelectedItem().toString();
            if (discounts.equals("Discount")) {
                Toast.makeText(context, context.getString(R.string.select_discount), Toast.LENGTH_SHORT).show();
            } else {
                savedDiscount(userId, name, discounts, context, imageUrl, drawableResId,gender,key);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void savedDiscount(String userId, String serviceName, String discounts, Context context, String imageUrl, int drawableResId, String gender, String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discountRef;
        discountRef = databaseReference.child("Discounts").child(key);
        HashMap<String, Object> discountData = new HashMap<>();
        discountData.put("serviceName", serviceName);
        discountData.put("discount", discounts);
        discountData.put("userId", userId);
        discountData.put("gender", gender);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            discountData.put("imageUrl", imageUrl);
            saveDiscountData(discountRef, discountData, context);
        } else if (drawableResId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
            uploadBitmapToFirebaseStorage(bitmap, userId, serviceName, context, (uploadedImageUrl) -> {
                discountData.put("imageUrl", uploadedImageUrl);
                saveDiscountData(discountRef, discountData, context);
            });
        }
    }



    private void uploadBitmapToFirebaseStorage(Bitmap bitmap, String userId, String serviceName, Context context, OnImageUploadListener listener) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Saving image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("DiscountImages/" + userId + "/" + serviceName + ".png");

        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            progressDialog.dismiss();
                            listener.onUploadSuccess(uri.toString());
                            Toast.makeText(context, context.getString(R.string.discountSaved), Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.errorSavedDiscount), Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveDiscountData(DatabaseReference discountRef, HashMap<String, Object> discountData, Context context) {
        discountRef.setValue(discountData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, context.getString(R.string.discountSaved), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.errorSavedDiscount), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    interface OnImageUploadListener {
        void onUploadSuccess(String uploadedImageUrl);
    }

    private void saveGender(String userId, String serviceName, String gender, Context context) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(userId);

        serviceRef.orderByChild("name").equalTo(serviceName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().child("gender").setValue(gender).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Gender updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error updating gender!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyServiceGender", "Error finding service: " + databaseError.getMessage());
            }
        });
    }



    public void logout(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.logout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button logoutButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);

        logoutButton.setOnClickListener(v -> {
            Toast.makeText(activity, "Logging out...", Toast.LENGTH_SHORT).show();
            TinkerApplications app = (TinkerApplications) activity.getApplication();
            app.clearUserData(false,activity);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void timepicker(Activity activity, String time, String userName, String userImageUrl, String key) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.timepicker))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        TextView timeText = dialogView.findViewById(R.id.time);
        TextView title = dialogView.findViewById(R.id.title);
        Button savedButton = dialogView.findViewById(R.id.saved);
        if(time !=null){
            timeText.setText(time);
        }else{
            timeText.setText("0");
        }
        String[] formattedTimeHolder = new String[1];
        title.setText("Set time");
        timeText.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog tpd = TimePickerDialog.newInstance(
                    (view1, hourOfDay, minute, second) -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        formattedTimeHolder[0] = sdf.format(selectedTime.getTime());
                        timeText.setText(formattedTimeHolder[0]);
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
            tpd.show(((FragmentActivity) activity).getSupportFragmentManager(), "TimePickerDialog");
        });

        savedButton.setOnClickListener(v -> {
            if (formattedTimeHolder[0] != null) {
                saveToRealtimeDatabase(activity,formattedTimeHolder[0], key,userName,userImageUrl);
                dialog.dismiss();
            } else {
                Toast.makeText(activity, "Please select a time.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void saveToRealtimeDatabase(Activity activity, String formattedTime, String key,String username,String image) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Mysched");
        Map<String, Object> updates = new HashMap<>();
        updates.put("time", formattedTime);
        updates.put("imageUrl",image);
        updates.put("name", username);
        databaseReference.child(uid).child(key).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Scheduled time saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Failed to save scheduled time: " + e.getMessage());
                    Toast.makeText(activity, "Failed to save scheduled time: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void scannDialog(Context context, String name, String imageUrl, int drawableResId) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("Context must be an instance of Activity");
        }

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.scandialoglayout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button scann = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);
        TextView servicename = dialogView.findViewById(R.id.title);
        ImageView imageView = dialogView.findViewById(R.id.scanImage);
        servicename.setText("Do you want to scan?: " + name);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(imageView);
        } else if (drawableResId != 0) {
            Glide.with(context)
                    .load(drawableResId)
                    .into(imageView);
        }

        scann.setOnClickListener(v -> {
            Intent intent = new Intent(context, newHome.class);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                intent.putExtra("imageUrl", imageUrl);
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
                String bitmapPath = saveBitmapToFile(context, bitmap);
                intent.putExtra("imageUrl", bitmapPath);
            }

            context.startActivity(intent);
            ((Activity) context).finish();
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private String saveBitmapToFile(Context context, Bitmap bitmap) {
        String filePath = context.getExternalFilesDir(null) + "/tempImage.png";
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }


    public DialogPlus showdownload(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.showlayout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .create();

        View dialogView = dialog.getHolderView();
        RelativeLayout share = dialogView.findViewById(R.id.sharechat);
        RelativeLayout download = dialogView.findViewById(R.id.download);

        share.setOnClickListener(v -> {
            String imageUrl = SPUtils.getInstance().getString(AppConstans.ImageUrl);
            Intent intent = new Intent(activity, User_list.class);
            intent.putExtra("imageUrl", imageUrl);
            activity.startActivity(intent);
            activity.finish();
        });

        download.setOnClickListener(v -> {
            String imageUrl = SPUtils.getInstance().getString(AppConstans.ImageUrl);
            saveImageToStorage(imageUrl, activity);
            dialog.dismiss();
        });

        dialog.show();
        return dialog;

    }
    private void saveImageToStorage(String imageUrl,Activity activity) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), activity.getString(R.string.app_name));
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it does not exist
        }
        String fileName = "downloaded_image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        Glide.with(activity.getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Toast.makeText(activity, "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Failed to save image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle the cleanup here
                    }
                });
    }



    public DialogPlus loadingDialog(Context activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.loading_dialog))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.color.transparent)
                .setCancelable(false)
                .create();
        new Handler(Looper.getMainLooper()).postDelayed(() -> dialog.show(), 2500);

        return dialog;
    }
    public DialogPlus loadingDialog2(Context activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.loading_scan))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.color.transparent)
                .setCancelable(false)
                .create();
        new Handler(Looper.getMainLooper()).postDelayed(() -> dialog.show(), 2500);

        return dialog;
    }

    public DialogPlus loadingDialog3(Context activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.processing))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.color.transparent)
                .setCancelable(false)
                .create();
        new Handler(Looper.getMainLooper()).postDelayed(() -> dialog.show(), 2500);

        return dialog;
    }


    public DialogPlus messageDeletedialog(Context activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.loading_dialog))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.color.transparent)
                .setCancelable(false)
                .create();
          dialog.show();
          return  dialog;
    }
    public DialogPlus loadinglogin(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.loading_dialog))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.color.transparent)
                .setCancelable(true)
                .create();
        dialog.show();
        return dialog;
    }


    public void showOnline(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.connected))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.TOP)
                .setContentBackgroundResource(R.color.transparent)
                .setCancelable(false)
                .create();
        dialog.show();
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 1500);
    }




    public void notiffDialog(Activity activity) {
        boolean isNotificationEnabled = SPUtils.getInstance().getBoolean(AppConstans.Notification);
        if (isNotificationEnabled) {
            return;
        }

        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.notification))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button logout = dialogView.findViewById(R.id.Yes);
        Button cancel = dialogView.findViewById(R.id.no);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("Notification Enabled?");

        logout.setOnClickListener(v -> {
            openNotificationSettings(activity);
            SPUtils.getInstance().put(AppConstans.Notification,true);
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> {
            SPUtils.getInstance().put(AppConstans.Notification,false);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void openNotificationSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
        activity.startActivity(intent);
    }
    public void shownoface(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.noface_layout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button okay = dialogView.findViewById(R.id.Yes);
        okay.setOnClickListener(v -> {
            dialog.dismiss();

        });
        dialog.show();
    }

    public void shownoface2(Activity activity,String message) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.noface_layout))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        TextView trygain = dialogView.findViewById(R.id.tryagain);
        Button okay = dialogView.findViewById(R.id.Yes);

        trygain.setText(message);
        okay.setOnClickListener(v -> {
            dialog.dismiss();

        });
        dialog.show();
    }


    public void updateProfile(Activity activity, String guessImage, String userEmail, String usernameText,String phone,String fullname,String address,String age){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.cofirm))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);

        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(),profile_update.class);
            intent.putExtra("username", usernameText);
            intent.putExtra("phone", phone);
            intent.putExtra("name", fullname);
            intent.putExtra("email", userEmail);
            intent.putExtra("image", guessImage);
            intent.putExtra("address", address);
            intent.putExtra("age", age);
           activity.startActivity(intent);
           activity.overridePendingTransition(0, 0);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }



    public void updateLocation2(Activity activity, String image,String address,String username,String name,String phone,String email){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.update_location))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);
        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), MapSelectActivity_profile2.class);
            intent.putExtra("image", image);
            intent.putExtra("address", address);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("email", email);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
            activity.finish();
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void updateLocation(Activity activity, String image,String address,String username,String name,String phone,String email){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.update_location))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);
        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), MapSelectActivity_profile.class);
            intent.putExtra("image", image);
            intent.putExtra("address", address);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("email", email);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void updateEvent_location(Activity activity, String image,String address,String username,String name,String phone,String email){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.update_location))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);
        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), MapSelectActivity_profile3.class);
            intent.putExtra("image", image);
            intent.putExtra("address", address);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("email", email);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
            activity.finish();
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }




    public void updateAdminProfile2(Activity activity, String guessImage, String userEmail, String usernameText,String phone,String fullname,String address,String age,String lengthOfService){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.cofirm))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);

        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), profile_updateAdmin.class);
            intent.putExtra("username", usernameText);
            intent.putExtra("phone", phone);
            intent.putExtra("name", fullname);
            intent.putExtra("email", userEmail);
            intent.putExtra("image", guessImage);
            intent.putExtra("address", address);
            intent.putExtra("age", age);
            intent.putExtra("lengthOfService", lengthOfService);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void updateAdminProfile(Activity activity, ImageView guessImage, TextView userEmail, TextView usernameText,TextView phone,TextView fullname,TextView address,TextView age,String lengthOfService){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.cofirm))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);

        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), profile_updateAdmin.class);
            intent.putExtra("username", usernameText.getText().toString());
            intent.putExtra("phone", phone.getText().toString());
            intent.putExtra("name", fullname.getText().toString());
            intent.putExtra("email", userEmail.getText().toString());
            intent.putExtra("image", (String) guessImage.getTag());
            intent.putExtra("address", address.getText().toString());
            intent.putExtra("age", age.getText().toString());
            intent.putExtra("lengthOfService", lengthOfService);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }



    public void updateEventProfile(Activity activity, ImageView guessImage, TextView userEmail, TextView usernameText,TextView phone,TextView fullname,TextView address,TextView age,String lengthOfService){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.cofirm))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);

        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), profile_updateEvents.class);
            intent.putExtra("username", usernameText.getText().toString());
            intent.putExtra("phone", phone.getText().toString());
            intent.putExtra("name", fullname.getText().toString());
            intent.putExtra("email", userEmail.getText().toString());
            intent.putExtra("image", (String) guessImage.getTag());
            intent.putExtra("address", address.getText().toString());
            intent.putExtra("age", age.getText().toString());
            intent.putExtra("lengthOfService", lengthOfService);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }



    public void updateEventProfile2(Activity activity, String guessImage, String userEmail, String usernameText,String phone,String fullname,String address,String age,String lengthOfService){
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.cofirm))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(false)
                .create();

        View dialogView = dialog.getHolderView();
        Button proceedButton = dialogView.findViewById(R.id.Yes);
        Button cancelButton = dialogView.findViewById(R.id.no);

        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity.getApplicationContext(), profile_updateEvents.class);
            intent.putExtra("username", usernameText);
            intent.putExtra("phone", phone);
            intent.putExtra("name", fullname);
            intent.putExtra("email", userEmail);
            intent.putExtra("image", guessImage);
            intent.putExtra("address", address);
            intent.putExtra("age", age);
            intent.putExtra("lengthOfService", lengthOfService);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }







}
