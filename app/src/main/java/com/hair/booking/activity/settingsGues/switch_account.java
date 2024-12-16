package com.hair.booking.activity.settingsGues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.Application.Account_manager;
import com.hair.booking.activity.Application.Account_manager_admin;
import com.hair.booking.activity.settingsAdmin.switch_account_admin;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.SwitchAccountAdapter;
import com.hair.booking.activity.tools.adapter.SwitchAccountAdapter_admin;

import java.util.ArrayList;
import java.util.List;

public class switch_account extends AppCompatActivity {
    RecyclerView switch_recycler;
    SwitchAccountAdapter adapter;
    List<Account_manager> accountList;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_account);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        switch_recycler = findViewById(R.id.switch_recycler);
        back = findViewById(R.id.back);
        switch_recycler.setLayoutManager(new LinearLayoutManager(this));
        accountList = new ArrayList<>();
        adapter = new SwitchAccountAdapter(this, accountList);
        switch_recycler.setAdapter(adapter);
        fetchAdminAccounts();
        back.setOnClickListener(view -> onBackPressed());
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAdminAccounts();
    }

    private void fetchAdminAccounts() {
        List<Account_manager> storedAccounts = Account_manager.getAccounts();
        List<String> storedEmails = new ArrayList<>();
        for (Account_manager account : storedAccounts) {
            storedEmails.add(account.getEmail());
        }
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Guess");
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accountList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String email = child.child("email").getValue(String.class);
                    String password = SPUtils.getInstance().getString(AppConstans.passwordkey_admin);
                    String username = child.child("username").getValue(String.class);
                    String imageUrl = child.child("image").getValue(String.class);

                    if (email != null && storedEmails.contains(email)) {
                        Account_manager admin = new Account_manager(password, email, imageUrl, username);
                        accountList.add(admin);
                    }
                }
                adapter.updateAccounts(accountList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(switch_account.this, "Failed to fetch admin accounts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}