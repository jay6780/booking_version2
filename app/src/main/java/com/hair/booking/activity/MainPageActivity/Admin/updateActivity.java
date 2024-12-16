package com.hair.booking.activity.MainPageActivity.Admin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.ApkDetails;

public class updateActivity extends AppCompatActivity {

    private EditText apkUrlEditText, apkVersionNameEditText, apkNameEditText;
    private Button saveButton;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        apkUrlEditText = findViewById(R.id.apk_url);
        apkVersionNameEditText = findViewById(R.id.apkVersionName);
        apkNameEditText = findViewById(R.id.apkname);
        back = findViewById(R.id.back);
        saveButton = findViewById(R.id.saved);
        saveButton.setOnClickListener(v -> saveApkDetailsToFirebase());
        back.setOnClickListener(view -> onBackPressed());
    }

    private void saveApkDetailsToFirebase() {
        String apkUrl = apkUrlEditText.getText().toString().trim();
        String apkVersionName = apkVersionNameEditText.getText().toString().trim();
        String apkName = apkNameEditText.getText().toString().trim();

        if (apkUrl.isEmpty() || apkVersionName.isEmpty() || apkName.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ApkDetails");
        String key = reference.push().getKey();
        if (key != null) {
            reference.child(key).setValue(new ApkDetails(apkUrl, apkVersionName, apkName))
                    .addOnSuccessListener(aVoid -> Toast.makeText(updateActivity.this, "APK Details saved successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(updateActivity.this, "Failed to save APK Details", Toast.LENGTH_SHORT).show());
        }
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
}
