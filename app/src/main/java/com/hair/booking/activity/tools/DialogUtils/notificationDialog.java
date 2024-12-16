package com.hair.booking.activity.tools.DialogUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.bookingUi.Bookingmap;

public class notificationDialog {
    public void showNotificationDialog(Context context, String address, String age, String date, String email, String heads, String image,
                                       Boolean isOnline, String lengthOfService, String phoneNumber, String price, String providerName,
                                       String serviceName, String time, String cash,String key) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notigdialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView proceed = dialog.findViewById(R.id.go);
        ImageView providerAvatar = dialog.findViewById(R.id.provider_avatar);
        TextView name = dialog.findViewById(R.id.name);
        TextView location = dialog.findViewById(R.id.location);

        Glide.with(context)
                .load(image)
                .transform(new CircleCrop())
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(providerAvatar);

        name.setText(providerName);
        location.setText(serviceName);
        proceed.setOnClickListener(v -> {
            Intent intent = new Intent(context, Bookingmap.class);
            intent.putExtra("address", address);
            intent.putExtra("age", age);
            intent.putExtra("date", date);
            intent.putExtra("email", email);
            intent.putExtra("heads", heads);
            intent.putExtra("image", image);
            intent.putExtra("isOnline", isOnline);
            intent.putExtra("lengthOfService", lengthOfService);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("price", price);
            intent.putExtra("providerName", providerName);
            intent.putExtra("serviceName", serviceName);
            intent.putExtra("time", time);
            intent.putExtra("cash", cash);
            intent.putExtra("key", key);
            context.startActivity(intent);
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.show();
    }
}
