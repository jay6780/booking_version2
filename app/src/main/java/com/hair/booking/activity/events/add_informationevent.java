package com.hair.booking.activity.events;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.ServiceInfo;

public class add_informationevent extends AppCompatActivity {
    private TextView titleText, price,servicename, informationText;
    private LinearLayout rowContainer;
    private ImageView serviceimg,back,add;
    private ImageView addMoreButton, decrease,imgInfo;
    private int rowCount = 1;
    private String name;
    private String imageUrl;
    private Button saved;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_informationevent);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        name = getIntent().getStringExtra("name");
        Log.d("name","servicename: "+name);
        imageUrl = getIntent().getStringExtra("imageUrl");
        initView();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving service information...");
        progressDialog.setCancelable(false);
        databaseReference = FirebaseDatabase.getInstance().getReference("ServiceInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void initView() {
        titleText = findViewById(R.id.titleText);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        serviceimg = findViewById(R.id.serviceimg);
        titleText.setText(getString(R.string.addinfo));
        servicename = findViewById(R.id.servicename);
        rowContainer = findViewById(R.id.rowContainer);
        addMoreButton = findViewById(R.id.addmore);
        decrease = findViewById(R.id.decrease);
        saved = findViewById(R.id.saved);
        add.setVisibility(View.GONE);
        addMoreButton.setOnClickListener(v -> addNewRow());
        decrease.setOnClickListener(v -> removeLastRow());
        saved.setOnClickListener(v -> saveServiceInfo());
        back.setOnClickListener(view -> onBackPressed());
        initLoadata();
    }

    private void saveServiceInfo() {
        progressDialog.show();
        for (int i = 0; i < rowContainer.getChildCount(); i++) {
            View row = rowContainer.getChildAt(i);
            TextView informationText = row.findViewById(R.id.informationText);
            TextView price = row.findViewById(R.id.price);
            String info = informationText.getText().toString();
            String priceValue = price.getText().toString();

            if (info.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Information field in row " + (i + 1) + " is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (priceValue.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Price field in row " + (i + 1) + " is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            String serviceId = databaseReference.push().getKey();
            saveRowDataToDatabase(info, priceValue, "", name, serviceId);
        }
    }


    private void saveRowDataToDatabase(String info, String price, String imageUrl, String servicename, String serviceId) {
        ServiceInfo service = new ServiceInfo(info, price, imageUrl, servicename,serviceId);
        if (serviceId != null) {
            databaseReference.child(serviceId).setValue(service)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(add_informationevent.this, "Event added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(add_informationevent.this, "Failed to add service", Toast.LENGTH_SHORT).show());
        }
    }


    private void initLoadata() {
        servicename.setText(name);
        Glide.with(this)
                .load(imageUrl)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(serviceimg);
    }

    private void addNewRow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View newRow = inflater.inflate(R.layout.row_information, rowContainer, false);
        informationText = newRow.findViewById(R.id.informationText);
        servicename = newRow.findViewById(R.id.servicename);
        price = newRow.findViewById(R.id.price);
        price.setHint("price");
        informationText.setHint("Information " + rowCount);
        rowContainer.addView(newRow);
        rowCount++;
    }

    private void removeLastRow() {
        if (rowContainer.getChildCount() > 0) {
            View lastRow = rowContainer.getChildAt(rowContainer.getChildCount() - 1);
            rowContainer.removeView(lastRow);
            rowCount--;
        } else {
            Toast.makeText(this, "No rows to remove", Toast.LENGTH_SHORT).show();
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
