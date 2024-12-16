package com.hair.booking.activity.MainPageActivity.bookingUi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Provider.Event_provider;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Model.Booking;
import com.hair.booking.activity.tools.Model.Booking2;
import com.hair.booking.activity.tools.Model.BookingId;
import com.hair.booking.activity.tools.Model.MychatId;
import com.hair.booking.activity.tools.Model.Schedule;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ServiceNameAdapter;
import com.orhanobut.dialogplus.DialogPlus;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Paymentreceipt2 extends AppCompatActivity {
    private ImageView avatar,back,bell,msgbtn;
    private TextView name,servicename,headss,prices,datevalue;
    private TextView timevalue,phonumbervalue,title,Package;
    private String image;
    private String email;
    private String providerName;
    private String address,key,price,heads,phonenumber;
    private String serviceName,date,time;
    private String addressadmin;
    private String age,lengthOfservice;
    private boolean isOnline;
    private Spinner payment_spinner;
    private String TAG = "Paymentreceipt";
    private AppCompatButton confirm;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference,databaseReference2;
    private DialogPlus loadingdilog;
    private String userName, userImageUrl,userAge,userEmail,userPhone,userAddress;
    private RecyclerView package_recyler2;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    private List<String> serviceNamesList = new ArrayList<>();
    private String packages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentreceipt);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        key = getIntent().getStringExtra("key");
        SPUtils.getInstance().put(AppConstans.key, key);
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        serviceName = getIntent().getStringExtra("serviceName");
        Log.d(TAG, "Is Online: " + isOnline);
        SPUtils.getInstance().put(AppConstans.KEY, key);
        Log.d("SavedKey", "userId: " + key);
        image = getIntent().getStringExtra("image");
        providerName = getIntent().getStringExtra("username");
        addressadmin = getIntent().getStringExtra("address");
        email = getIntent().getStringExtra("email");
        age = getIntent().getStringExtra("age");
        lengthOfservice = getIntent().getStringExtra("lengthOfservice");
        price = getIntent().getStringExtra("price");
        heads = getIntent().getStringExtra("heads");
        phonenumber = getIntent().getStringExtra("phonenumber");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        Log.d(TAG,"email: "+email);
        initView();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("MybookUser");
        databaseReference = FirebaseDatabase.getInstance().getReference("Mybook");
        initGuess();

    }

    private void initGuess() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Guess").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userName = dataSnapshot.child("username").getValue(String.class);
                    userImageUrl = dataSnapshot.child("image").getValue(String.class);
                    userAge = dataSnapshot.child("age").getValue(String.class);
                    userEmail = dataSnapshot.child("email").getValue(String.class);
                    userPhone = dataSnapshot.child("phone").getValue(String.class);
                    userAddress = dataSnapshot.child("address").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void initView() {
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        Package = findViewById(R.id.Package);
        package_recyler2 = findViewById(R.id.package_recyler2);
        title = findViewById(R.id.profiletxt);
        payment_spinner = findViewById(R.id.payment_spinner);
        servicename = findViewById(R.id.servicename);
        headss = findViewById(R.id.heads);
        back = findViewById(R.id.back);
        prices = findViewById(R.id.price);
        datevalue = findViewById(R.id.datevalue);
        timevalue = findViewById(R.id.timevalue);
        phonumbervalue = findViewById(R.id.phonumbervalue);
        confirm = findViewById(R.id.confirm);
        msgbtn = findViewById(R.id.messageImg);
        bell = findViewById(R.id.bell);
        Glide.with(this)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(avatar);

        name.setText("Name: "+providerName);
        servicename.setText(serviceName);
        headss.setText(heads+" Heads");
        prices.setText(price +" php");
        datevalue.setText(date);
        timevalue.setText(time);
        phonumbervalue.setText(phonenumber);
        title.setText("Confirmation");
        back.setOnClickListener(view -> onBackPressed());
        confirm.setOnClickListener(view -> confirmation());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Select Payment", "Cash"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payment_spinner.setAdapter(adapter);
        payment_spinner.setSelection(0);
        initShowbook();
        initFetchPackage();
    }

    private void initFetchPackage() {
        if (serviceNamesList == null) {
            serviceNamesList = new ArrayList<>();
        }
        if (serviceNamesList.isEmpty()) {
            String serviceListJson = SPUtils.getInstance().getString(AppConstans.serviceNamesList, "[]");
            Log.d(TAG, "Servicename list: " + serviceListJson);
            Type type = new TypeToken<List<String>>() {}.getType();
            serviceNamesList = new Gson().fromJson(serviceListJson, type);
        }
        if (serviceNamesList == null || serviceNamesList.isEmpty()) {
            package_recyler2.setVisibility(View.GONE);
            Package.setVisibility(View.GONE);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            package_recyler2.setLayoutManager(layoutManager);
            ServiceNameAdapter adapter = new ServiceNameAdapter(serviceNamesList);
            package_recyler2.setAdapter(adapter);
            package_recyler2.setVisibility(View.VISIBLE);
            Package.setVisibility(View.VISIBLE);
        }
    }


    private void initShowbook() {
        startService(new Intent(this, MessageNotificationService.class));
        TextView badgeCount = findViewById(R.id.badge_count);
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknum);
        if(badgenum == null){
            badgeCount.setText("0");
            SPUtils.getInstance().put(AppConstans.booknum, "0");
        }else{
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(badgenum);

        }
        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User_list.class);
                startActivity(intent);
            }
        });
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), history_book.class);
                intent.putExtra("bookprovideremail", bookprovideremail);
                startActivity(intent);
            }
        });

    }


    private void confirmation() {
        String selectedPayment = payment_spinner.getSelectedItem().toString();
        if (selectedPayment.equals("Select Payment")) {
            Toast.makeText(getApplicationContext(), "Please choose a payment method first", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingdilog = new Dialog().loadingDialog(Paymentreceipt2.this);
        loadingdilog.show();
        String useremail = SPUtils.getInstance().getString(AppConstans.userEmail);
        String userId = firebaseAuth.getCurrentUser().getUid();
        if (useremail != null) {
            String childKey = createChatRoomId(useremail, email);
            SPUtils.getInstance().put(AppConstans.bookprovideremail, email);


            String snapshotkey = databaseReference.push().getKey();
            String snapshotkey2 = databaseReference2.push().getKey();

            Booking2 booking = new Booking2(providerName, serviceName, price, heads, phonenumber, date, time, image, addressadmin, email, age, lengthOfservice, selectedPayment, key,snapshotkey);
            Booking2 booking2 = new Booking2(userName, serviceName, price, heads, userPhone, date, time, userImageUrl, userAddress, useremail, userAge, "", selectedPayment, userId,snapshotkey);

            // Add data to MybookUser
            DatabaseReference bookInfoRef2 = databaseReference2.child(childKey).child("bookInfo").child(snapshotkey);
            bookInfoRef2.setValue(booking2);
            String serviceListJson = SPUtils.getInstance().getString(AppConstans.serviceNamesList, "[]");
            Type type = new TypeToken<List<String>>() {}.getType();
            serviceNamesList = new Gson().fromJson(serviceListJson, type);

            if (serviceNamesList == null) {
                serviceNamesList = new ArrayList<>();
            }

            if (serviceNamesList == null || serviceNamesList.isEmpty()) {
                packages = "";
            } else {
                packages = String.join(" ,", serviceNamesList);
            }

            Log.d(TAG,"Packages: "+packages);
            DatabaseReference bookInfoRef = databaseReference.child(childKey).child("bookInfo").child(snapshotkey);
            bookInfoRef.setValue(booking)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Booking data saved successfully.");
                            Toast.makeText(getApplicationContext(), "Book Success", Toast.LENGTH_SHORT).show();
                            new android.os.Handler().postDelayed(() -> {
                                if (loadingdilog != null && loadingdilog.isShowing()) {
                                    loadingdilog.dismiss();
                                }
                                savedSchedule(userId, key, date);
                                savedSchedId(email);
                                savedBookId(childKey);
                                savedRecentAvailed(serviceName, packages,snapshotkey);
                                savedRecentAvailedFoEvent(serviceName, packages,key,snapshotkey);
                                savedBookCount(key);
                                savEBookIdforAdmin(childKey, key);
                                Intent intent = new Intent(Paymentreceipt2.this, history_book.class);
                                startActivity(intent);
                                finish();
                            }, 1500);
                        } else {
                            Log.e(TAG, "Failed to save booking data.", task.getException());
                            if (loadingdilog != null && loadingdilog.isShowing()) {
                                loadingdilog.dismiss();
                            }
                        }
                    });
        }
    }

    private void savedRecentAvailedFoEvent(String serviceName, String packages, String key,String snapshotkey) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecentAvailed_event").child(key);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("SummaryEvent").child(key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                String existingKey = null;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String existingServiceName = childSnapshot.child("serviceName").getValue(String.class);
                    if (serviceName.equals(existingServiceName)) {
                        exists = true;
                        existingKey = childSnapshot.getKey();
                        break;
                    }
                }

                if (exists) {
                    if (existingKey != null) {
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("snapshotKey", snapshotkey);
                        updatedData.put("packages", packages);

                        databaseReference2.child(existingKey).updateChildren(updatedData);
                        databaseReference.child(existingKey).updateChildren(updatedData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Packages updated successfully for service: " + serviceName);
                                    } else {
                                        Log.e(TAG, "Failed to update packages: " + task.getException().getMessage());
                                    }
                                });
                    }
                } else {
                    DatabaseReference uniqueKeyRef = databaseReference.push();
                    DatabaseReference uniqueKeyRef2 = databaseReference2.push();

                    Map<String, Object> serviceData = new HashMap<>();
                    serviceData.put("serviceName", serviceName);
                    serviceData.put("packages", packages);
                    serviceData.put("snapshotKey", snapshotkey);

                    uniqueKeyRef2.setValue(serviceData);
                    uniqueKeyRef.setValue(serviceData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Data saved successfully with key: " + uniqueKeyRef.getKey());
                        } else {
                            Log.e(TAG, "Failed to save data: " + task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking for existing service name: " + error.getMessage());
            }
        });
    }

    private void savedRecentAvailed(String serviceName, String packages,String Snapshotkey) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecentAvailed").child(userId);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("SummaryUser").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                String existingKey = null;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String existingServiceName = childSnapshot.child("serviceName").getValue(String.class);
                    if (serviceName.equals(existingServiceName)) {
                        exists = true;
                        existingKey = childSnapshot.getKey();
                        break;
                    }
                }

                if (exists) {
                    if (existingKey != null) {
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("snapshotKey", Snapshotkey);
                        updatedData.put("packages", packages);

                        databaseReference2.child(existingKey).updateChildren(updatedData);
                        databaseReference.child(existingKey).updateChildren(updatedData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Packages updated successfully for service: " + serviceName);
                                    } else {
                                        Log.e(TAG, "Failed to update packages: " + task.getException().getMessage());
                                    }
                                });
                    }
                } else {
                    DatabaseReference uniqueKeyRef = databaseReference.push();
                    DatabaseReference uniqueKeyRef2 = databaseReference2.push();

                    Map<String, Object> serviceData = new HashMap<>();
                    serviceData.put("serviceName", serviceName);
                    serviceData.put("packages", packages);
                    serviceData.put("snapshotKey", Snapshotkey);

                    uniqueKeyRef2.setValue(serviceData);
                    uniqueKeyRef.setValue(serviceData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Data saved successfully with key: " + uniqueKeyRef.getKey());
                        } else {
                            Log.e(TAG, "Failed to save data: " + task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking for existing service name: " + error.getMessage());
            }
        });
    }


    private void savEBookIdforAdmin(String chatId,String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String time = String.valueOf(System.currentTimeMillis());
        BookingId mychatId = new BookingId(time, chatId);
        databaseReference.child("BookIdAdmin").child(key).push().setValue(mychatId)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDB", "Data saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDB", "Error saving data", e);
                });
    }

    private void savedBookCount(String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference bookCountRef = databaseReference.child("BookCount").child(key);
        bookCountRef.child("count").get().addOnSuccessListener(snapshot -> {
            int currentCount = 0;
            if (snapshot.exists()) {
                currentCount = snapshot.getValue(Integer.class);
            }

            int newCount = currentCount + 1;
            bookCountRef.child("count").setValue(newCount)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDB", "Count updated successfully to " + newCount))
                    .addOnFailureListener(e -> Log.e("FirebaseDB", "Error updating count", e));
        }).addOnFailureListener(e -> Log.e("FirebaseDB", "Error fetching count", e));
    }

    private void savedBookId(String childKey) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String time = String.valueOf(System.currentTimeMillis());
        BookingId mychatId = new BookingId(time, childKey);
        databaseReference.child("BookingId").child(userId).push().setValue(mychatId)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDB", "Data saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDB", "Error saving data", e);
                });
    }


    private void savedSchedId(String email) {
        String useremail = SPUtils.getInstance().getString(AppConstans.userEmail);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (useremail != null) {
            String childKey = createChatRoomId(email, useremail);
            MychatId mychatId = new MychatId(userId, childKey);
            databaseReference.child("MybookId").child(userId).setValue(mychatId)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FirebaseDB", "Data saved successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseDB", "Error saving data", e);
                    });
        }
    }


    private void savedSchedule(String userId, String key,String dateString) {
        String mergedUserIdKey = userId + "_" + key;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid date format.", Toast.LENGTH_SHORT).show();
            return;
        }
        Schedule newSchedule = new Schedule(userName, userImageUrl,date);
        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("UserSchedule").child(mergedUserIdKey);
        String scheduleId = scheduleRef.push().getKey();
        if (scheduleId != null) {
            scheduleRef.child(scheduleId).setValue(newSchedule)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to save schedule.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private String createChatRoomId(String email1, String email2) {
        String chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        return chatRoomId;
    }



    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void onBackPressed(){
        SPUtils.getInstance().put(AppConstans.serviceNamesList, "");
        finish();
        super.onBackPressed();
    }

}