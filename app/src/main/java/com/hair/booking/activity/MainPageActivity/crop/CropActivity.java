package com.hair.booking.activity.MainPageActivity.crop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.hair.booking.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        imageView = findViewById(R.id.imageView);
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);

        findViewById(R.id.cropButton).setOnClickListener(v -> cropImage());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }

    private void cropImage() {
        // Simple cropping logic; replace with your own implementation
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

        try (FileOutputStream out = new FileOutputStream(outputUri.getPath())) {
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            setResult(RESULT_OK, new Intent().putExtra("croppedImageUri", outputUri.toString()));
            finish();
        } catch (IOException e) {
            Toast.makeText(this, "Cropping failed", Toast.LENGTH_SHORT).show();
        }
    }
}
