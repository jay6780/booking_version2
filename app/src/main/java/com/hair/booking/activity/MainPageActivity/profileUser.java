package com.hair.booking.activity.MainPageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hair.booking.activity.MainPageActivity.Provider.hmua;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

public class profileUser extends AppCompatActivity {
    String username,email,image,phone,name,address,age;
    ImageView myAvatar;
    TextView usernametv,useremailtv;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newprofile);
        initViews();
        initClickers();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.pink3));

        Intent intent = getIntent();
        if (intent != null) {
             username = intent.getStringExtra("username");
             email = intent.getStringExtra("email");
             image = intent.getStringExtra("image");
             phone = intent.getStringExtra("phone");
             name = intent.getStringExtra("name");
             address = intent.getStringExtra("address");
             age = intent.getStringExtra("age");
            initLoad(username,email,image);
        }
    }

    private void initLoad(String username,String email,String image) {
        usernametv.setText(username);
        useremailtv.setText(email);
        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(myAvatar);
        } else {
            myAvatar.setImageResource(R.drawable.baseline_person_24);
        }
    }

    private void initClickers() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.updatebtn) {
                    Dialog dialog = new Dialog();
                    dialog.updateProfile(profileUser.this,image,email,username,phone,name,address,age);
                } else if (v.getId() == R.id.logout) {
                    Dialog dialog = new Dialog();
                    dialog.logout(profileUser.this);
                } else if (v.getId() == R.id.back) {
                  onBackPressed();
                } else if (v.getId() == R.id.bookingHistory) {
                    Intent intent = new Intent(getApplicationContext(), history_book.class);
                    intent.putExtra("bookprovideremail", bookprovideremail);
                    startActivity(intent);
                    finish();
                } else if (v.getId() == R.id.booking) {
                    Intent intent = new Intent(getApplicationContext(), hmua.class);
                    startActivity(intent);
                    finish();
                }



            }
        };

        idlisterners(clickListener);
    }

    private void idlisterners(View.OnClickListener clickListener) {
        findViewById(R.id.logout).setOnClickListener(clickListener);
        findViewById(R.id.updatebtn).setOnClickListener(clickListener);
        findViewById(R.id.back).setOnClickListener(clickListener);
        findViewById(R.id.bookingHistory).setOnClickListener(clickListener);
        findViewById(R.id.booking).setOnClickListener(clickListener);
    }

    private void initViews() {
        myAvatar = findViewById(R.id.myAvatar);
        usernametv = findViewById(R.id.usernametv);
        useremailtv  = findViewById(R.id.useremailtv);
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), newHome.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
        super.onBackPressed();
    }
}