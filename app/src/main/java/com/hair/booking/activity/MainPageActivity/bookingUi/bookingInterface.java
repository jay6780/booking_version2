package com.hair.booking.activity.MainPageActivity.bookingUi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.HomePage;
import com.hair.booking.activity.MainPageActivity.Provider.hmua;

public class bookingInterface extends AppCompatActivity {
     private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_interface);
        changeStatusBarColor(getResources().getColor(R.color.brown));
        title = findViewById(R.id.name);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        title.setText("");
        initClick();
    }
    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void initClick() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.back) {
                   onBackPressed();
                } else if (v.getId() == R.id.hmua) {
                    Intent intent = new Intent(getApplicationContext(), hmua.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        };
        clicklisteners(clickListener);
    }

    private void clicklisteners(View.OnClickListener clickListener) {
        findViewById(R.id.back).setOnClickListener(clickListener);
        findViewById(R.id.hmua).setOnClickListener(clickListener);
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
}