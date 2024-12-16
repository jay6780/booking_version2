package com.hair.booking.activity.events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.adapter.ServiceInfoAdapter;
import com.hair.booking.activity.tools.Model.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class delete_eventinfo extends AppCompatActivity {
    private RecyclerView informationRecycler;
    private String servicename,serviceimgUrl;
    private DatabaseReference information;
    private String TAG = "delete_eventinfo";
    private ServiceInfoAdapter adapter;
    private List<ServiceInfo> serviceInfoList = new ArrayList<>();
    private ImageView add,back;
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_eventinfo);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        add = findViewById(R.id.add);
        back = findViewById(R.id.back);
        titleText = findViewById(R.id.titleText);
        informationRecycler = findViewById(R.id.informationRecycler);
        informationRecycler.setLayoutManager(new LinearLayoutManager(this));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(view -> intentToadd());
        back.setOnClickListener(view -> onBackPressed());
        servicename = getIntent().getStringExtra("name");
        serviceimgUrl = getIntent().getStringExtra("imageUrl");
        titleText.setText("Add package");
        Log.d(TAG, "services: " + servicename);
        Log.d(TAG, "services: " + serviceimgUrl);

        initFirebase();
    }

    private void intentToadd() {
        Intent intent = new Intent(getApplicationContext(), add_informationevent.class);
        intent.putExtra("imageUrl", serviceimgUrl);
        intent.putExtra("name", servicename);
        startActivity(intent);
    }

    private void initFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        information = FirebaseDatabase.getInstance().getReference("ServiceInfo").child(userId);

        information.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceInfoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServiceInfo serviceInfo = snapshot.getValue(ServiceInfo.class);
                    if (serviceInfo != null && serviceInfo.getServicename().equals(servicename)) {
                        serviceInfoList.add(serviceInfo);
                    }
                }
                adapter = new ServiceInfoAdapter(delete_eventinfo.this, serviceInfoList);
                informationRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e(TAG, "Failed to read value: " + databaseError.toException());
                Toast.makeText(delete_eventinfo.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
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
