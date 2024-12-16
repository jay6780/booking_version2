package com.hair.booking.activity.MainPageActivity.discount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Discounts;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.DiscountAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class discount_activity extends AppCompatActivity {
    private static final String TAG = "DiscountActivity";
    private String discount,userId;
    private RecyclerView discount_recycler;
    private DiscountAdapter discountAdapter;
    private List<Discounts> discountList;
    private TextView titleText;
    private ImageView back;
    private DatabaseReference databaseReference3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        discount_recycler = findViewById(R.id.discount_recycler);
        titleText = findViewById(R.id.titleText);
        back = findViewById(R.id.back);
        discount = getIntent().getStringExtra("discount");
        userId = getIntent().getStringExtra("userId");
        discountList = new ArrayList<>();
        discountAdapter = new DiscountAdapter(this, discountList);
        discount_recycler.setLayoutManager(new LinearLayoutManager(this));
        discount_recycler.setAdapter(discountAdapter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        initgender();
        titleText.setText(getString(R.string.discountTitle));
        back.setOnClickListener(view -> onBackPressed());
    }

    private void initgender() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference3 = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
            databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                        if (user != null) {
                            String gender = user.getGender();
                            fetchDiscountData(gender);
                        }
                    }
                }
                @SuppressLint("LongLogTag")
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

            });
        }
    }



    private void fetchDiscountData(String gender) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Discounts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                discountList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Discounts discountItem = data.getValue(Discounts.class);
                    if (discountItem != null && discountItem.getDiscount().equals(discount)) {
                        if (discountItem.getGender() == null || discountItem.getGender().isEmpty()) {
                            discountList.add(discountItem);
                        } else if ("Female".equals(gender) && "Female".equals(discountItem.getGender())) {
                            discountList.add(discountItem);
                        } else if ("Male".equals(gender) && "Male".equals(discountItem.getGender())) {
                            discountList.add(discountItem);
                        }
                    }
                }
                discountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }


    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void onBackPressed(){
        SPUtils.getInstance().put(AppConstans.discountservicename,"");
        SPUtils.getInstance().put(AppConstans.discount,"");
        finish();
        super.onBackPressed();
    }
}
