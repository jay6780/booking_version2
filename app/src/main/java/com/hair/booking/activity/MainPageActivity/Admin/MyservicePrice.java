package com.hair.booking.activity.MainPageActivity.Admin;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.activity.tools.Model.Service;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.tools.adapter.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyservicePrice extends AppCompatActivity {
    private RecyclerView myserviceRecycler;
    private ServiceAdapter serviceAdapter;
    private DatabaseReference serviceRef;
    private TextView title;
    private ImageView back,add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myservice_price);
        changeStatusBarColor(getResources().getColor(R.color.bgColor));
        myserviceRecycler = findViewById(R.id.my_serviceRecycler);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        title = findViewById(R.id.name);
        myserviceRecycler.setLayoutManager(new LinearLayoutManager(this));
        title.setText("Service price");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(user.getUid());
            fetchServices();
        }
        add.setOnClickListener(view -> addactivity());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void addactivity() {
        Intent back = new Intent(getApplicationContext(), AddService.class);
        startActivity(back);
        overridePendingTransition(0,0);
        finish();
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

        private void fetchServices() {
            DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            serviceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Service> services = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);
                        if (service != null) {
                            services.add(service);
                        }
                    }
                    serviceAdapter = new ServiceAdapter(services,MyservicePrice.this);
                    myserviceRecycler.setAdapter(serviceAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MyservicePrice", "Error fetching services: " + databaseError.getMessage());
                }
            });
        }
  public  void onBackPressed(){
        Intent back = new Intent(getApplicationContext(), newHome.class);
        startActivity(back);
        overridePendingTransition(0,0);
        finish();
        super.onBackPressed();
  }
}