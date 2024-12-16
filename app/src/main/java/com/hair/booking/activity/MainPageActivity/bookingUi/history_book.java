package com.hair.booking.activity.MainPageActivity.bookingUi;


import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.adapter.BookingAdapters;

public class history_book extends AppCompatActivity {
    private String bookprovideremail;
    private ImageView messageImg,logout;
    private String TAG = "history_book";
    private ViewPager bookingpager;
    private TextView schedule,completed,cancel;
    private BookingAdapters bookingAdapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_book);
        logout = findViewById(R.id.gunting);
        messageImg = findViewById(R.id.messageImg);
        bookingpager = findViewById(R.id.bookingpager);
        completed = findViewById(R.id.completed);
        cancel = findViewById(R.id.cancel);
        schedule = findViewById(R.id.schedule);
        bookprovideremail = getIntent().getStringExtra("bookprovideremail");
        logout.setVisibility(View.VISIBLE);
        logout.setOnClickListener(view -> onBackPressed());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        messageImg.setOnClickListener(view -> gotochat());
        startService(new Intent(getApplicationContext(), MessageNotificationService.class));
        bookingAdapters = new BookingAdapters(getSupportFragmentManager());
        bookingpager.setAdapter(bookingAdapters);
        initTabs();

    }

    private void initTabs() {
        selectTab(schedule);
        initialtab(completed);
        initialgray(completed);
        initialtab(cancel);
        initialgray(cancel);
        schedule.setOnClickListener(v -> bookingpager.setCurrentItem(0));
        cancel.setOnClickListener(v -> bookingpager.setCurrentItem(1));
        completed.setOnClickListener(v -> bookingpager.setCurrentItem(2));
        bookingpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        selectTab(schedule);
                        break;
                    case 1:
                        selectTab(cancel);
                        break;
                    case 2:
                        selectTab(completed);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initialtab(TextView selectedTab) {
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }

    private void initialgray(TextView selectedTab) {
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.transaprent2)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
    }


    private void resetTabStyle(TextView tab) {
        SpannableString content = new SpannableString(tab.getText());
        content.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.transaprent2)), 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab.setText(content);
    }




    private void selectTab(TextView selectedTab) {
        resetTabStyle(schedule);
        resetTabStyle(cancel);
        resetTabStyle(completed);
        SpannableString spannable = new SpannableString(selectedTab.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedTab.setText(spannable);
}

    private void gotochat() {
        Intent gotochat = new Intent(getApplicationContext(), User_list.class);
        startActivity(gotochat);
        finish();
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