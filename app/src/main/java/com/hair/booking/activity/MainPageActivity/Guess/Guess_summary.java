package com.hair.booking.activity.MainPageActivity.Guess;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.ServiceNameAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Guess_summary extends AppCompatActivity {
    private ImageView avatar, back,bell,msgbtn;
    private TextView name, servicename, headss, prices, datevalue,title,Package;
    private TextView timevalue, phonumbervalue;
    private String image;
    private String email;
    private String providerName;
    private String  key, price, heads, phonenumber;
    private String serviceName, date, time;
    private boolean isOnline;
    private TextView payment_spinner,service;
    private String TAG = "Paymentreceipt";
    private String paymentMethod;
    private RecyclerView package_recyler2;
    private String snapshotKey;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_summary);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        key = getIntent().getStringExtra("key");
        snapshotKey = getIntent().getStringExtra("snapshotkey");
        SPUtils.getInstance().put(AppConstans.key, key);
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        serviceName = getIntent().getStringExtra("serviceName");
        Log.d(TAG, "Is Online: " + isOnline);
        SPUtils.getInstance().put(AppConstans.KEY, key);
        Log.d("SavedKey", "userId: " + key);
        paymentMethod = getIntent().getStringExtra("paymentMethod");
        image = getIntent().getStringExtra("image");
        providerName = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        price = getIntent().getStringExtra("price");
        heads = getIntent().getStringExtra("heads");
        phonenumber = getIntent().getStringExtra("phonenumber");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        Package = findViewById(R.id.Package);
        package_recyler2 = findViewById(R.id.package_recyler2);
        Log.d(TAG, "email: " + email);
        initView();
        initShowbook();
        initRecentpackages(snapshotKey);
    }

    private void initRecentpackages(String snapshotKey) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SummaryUser").child(uid);

        databaseReference.orderByChild("snapshotKey").equalTo(snapshotKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                                String packages = serviceSnapshot.child("packages").getValue(String.class);
                                Log.d(TAG, "CompletePackages: " + serviceName + ", Packages: " + packages);
                                initFetchPackage(packages);
                            }
                        } else {
                            Log.d(TAG, "No matching service found for: " + serviceName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FetchFilteredPackages", "Failed to fetch data: " + error.getMessage());
                    }
                });

    }


    private void initShowbook() {
        startService(new Intent(this, MessageNotificationService.class));
        TextView badgeCount = findViewById(R.id.badge_count);
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknumAdmin);
        if(badgenum == null){
            badgeCount.setText("0");
            SPUtils.getInstance().put(AppConstans.booknumAdmin, "0");
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
    private void initView() {
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        service = findViewById(R.id.service);
        msgbtn = findViewById(R.id.messageImg);
        bell = findViewById(R.id.bell);
        payment_spinner = findViewById(R.id.payment_spinner);
        servicename = findViewById(R.id.servicename);
        title = findViewById(R.id.profiletxt);
        headss = findViewById(R.id.heads);
        back = findViewById(R.id.back);
        prices = findViewById(R.id.price);
        datevalue = findViewById(R.id.datevalue);
        timevalue = findViewById(R.id.timevalue);
        phonumbervalue = findViewById(R.id.phonumbervalue);

        Glide.with(this)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(avatar);

        name.setText("Name: " + providerName);
        servicename.setText(serviceName);
        headss.setText(heads + " Heads");
        prices.setText(price +" php");
        datevalue.setText(date);
        timevalue.setText(time);
        title.setText("Book Summary");
        phonumbervalue.setText(phonenumber);
        back.setOnClickListener(view -> onBackPressed());
        payment_spinner.setText(paymentMethod);
        service.setText("Service I availed");

    }

    private void initFetchPackage(String packages) {
        if (!TextUtils.isEmpty(packages)) {
            String[] packagesArray = packages.split(",\\s*");
            List<String> recentPackageList = new ArrayList<>(Arrays.asList(packagesArray));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            package_recyler2.setLayoutManager(layoutManager);
            ServiceNameAdapter adapter = new ServiceNameAdapter(recentPackageList);
            package_recyler2.setAdapter(adapter);
            package_recyler2.setVisibility(View.VISIBLE);
            Package.setVisibility(View.VISIBLE);
        } else {
            package_recyler2.setVisibility(View.GONE);
            Package.setVisibility(View.GONE);
        }
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }

}