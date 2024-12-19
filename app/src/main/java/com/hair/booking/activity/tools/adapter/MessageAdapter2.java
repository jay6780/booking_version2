package com.hair.booking.activity.tools.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.activity.MainPageActivity.Admin.Bookingmap_Admin;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.DialogUtils.MapDialog;
import com.hair.booking.activity.tools.Model.LinkMovementMethods;
import com.hair.booking.activity.tools.Model.Message;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.MainPageActivity.FullScreenImageActivity;
import com.lee.avengergone.DisappearView;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MessageAdapter2 extends RecyclerView.Adapter<MessageAdapter2.MessageViewHolder> {
    private ArrayList<Message> messageList;
    private Context context;
    private String currentUserEmail;
    private DatabaseReference databaseReference;
    public MessageAdapter2(ArrayList<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        this.currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter2.MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        long timestamp = message.getTimestamp();
        String formattedDate = convertTimestampToDate(timestamp);
        holder.timestampTextView.setText(formattedDate);

        holder.message_content.setOnClickListener(view -> {
            String imageUrl = message.getImageUrl();
            String imageUrl2 = message.getMessage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                context.startActivity(intent);
            }
            else if (imageUrl2 != null && !imageUrl2.isEmpty() &&
                    imageUrl2.startsWith("https://") &&
                    (imageUrl2.endsWith(".jpeg") || imageUrl2.endsWith(".jpg"))) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image_url", imageUrl2);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No valid image URL found", Toast.LENGTH_SHORT).show();
            }
        });

        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            holder.messageBubble.setVisibility(View.GONE);
            holder.imagebubble.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.baseline_person_24)
                    .into(holder.message_content);
        } else if (message.getMessage() != null &&
                message.getMessage().startsWith("https://") &&
                (message.getMessage().endsWith(".jpeg") || message.getMessage().endsWith(".jpg"))) {
            holder.messageBubble.setVisibility(View.GONE);
            holder.imagebubble.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(message.getMessage())
                    .placeholder(R.drawable.baseline_person_24)
                    .into(holder.message_content);
        } else {
            holder.messageBubble.setVisibility(View.VISIBLE);
            holder.imagebubble.setVisibility(View.GONE);
            holder.messageTextView.setText(message.getMessage());
            Linkify.addLinks(holder.messageTextView, Linkify.WEB_URLS);
            holder.messageTextView.setMovementMethod(new LinkMovementMethods() {
                @Override
                public boolean onLinkClick(@NonNull TextView textView, @NonNull String url) {
                    if (url.startsWith("https://www.google.com/maps/?q=")) {
                        openMap(url, message.getKey(),message);
                        return true;
                    }
                    return false;
                }
            });

        }
            holder.senderTextView.setText(message.getUsername());
        Glide.with(context)
                .load(message.getUserImageUrl() != null && !message.getUserImageUrl().isEmpty() ? message.getUserImageUrl() : R.drawable.baseline_person_24)
                .circleCrop()
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.messageImage);

        boolean isCurrentUser = message.getSenderEmail().equals(currentUserEmail);
        setGravity(holder, isCurrentUser);

        holder.itemView.setOnLongClickListener(v -> {
            String msgContent = message.getMessage();
            new AlertDialog.Builder(context)
                    .setTitle("Choose an action")
                    .setItems(new String[]{"Delete Message", "View Map","Copy link"}, (dialog, which) -> {
                        if (which == 0) { // Delete Message
                            if (isCurrentUser) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Delete Message")
                                        .setMessage("Are you sure you want to delete this message?")
                                        .setPositiveButton("Yes", (dialog1, which1) -> {
                                            String chatRoomId = SPUtils.getInstance().getString(AppConstans.ChatRoomId);
                                            deleteMessage(chatRoomId, message.getMessageId(), holder.itemView, context);
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            } else {
                                Toast.makeText(context, "You can only delete your own messages.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (which == 1) { // View Map
                            if (msgContent != null && msgContent.startsWith("https://www.google.com/maps/?q=")) {
                                openMap(msgContent, message.getKey(),message);
                            } else {
                                Toast.makeText(context, "This message does not contain a map link.", Toast.LENGTH_SHORT).show();
                            }

                        } else if (which == 2) { // View Map
                            if (msgContent != null && Linkify.addLinks(new SpannableString(msgContent), Linkify.WEB_URLS)) {
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied Link", msgContent);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No valid URL to copy", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
            return true;
        });
    }

    private void openMap(String mapUrl, String key,Message message) {
        if (mapUrl != null && mapUrl.startsWith("https://www.google.com/maps/?q=")) {
            String[] parts = mapUrl.split("q=");
            if (parts.length > 1) {
                String latLng = parts[1];
                String[] latLngParts = latLng.split(",");
                if (latLngParts.length >= 2) {
                    try {
                        double latitude = Double.parseDouble(latLngParts[0]);
                        double longitude = Double.parseDouble(latLngParts[1]);
                        convertMapUrl(latitude,longitude);
                        String age = SPUtils.getInstance().getString(AppConstans.age);
                        Intent intent = new Intent(context, Bookingmap_Admin.class);
                        intent.putExtra("providerEmail", message.getSenderEmail());
                        intent.putExtra("address", SPUtils.getInstance().getString(AppConstans.userAddress));
                        intent.putExtra("image", message.getUserImageUrl());
                        intent.putExtra("providerName", message.getUsername());
                        intent.putExtra("age", age);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("key", key);
                        context.startActivity(intent);

                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Invalid coordinates", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Invalid map URL format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Invalid map URL format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "This message does not contain a map link.", Toast.LENGTH_SHORT).show();
        }
    }

    private void convertMapUrl(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        StringBuilder locationText = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                if (address.getLocality() != null) {
                    locationText.append(address.getLocality());
                }
                if (address.getAdminArea() != null) {
                    if (locationText.length() > 0) {
                        locationText.append(", ");
                    }
                    locationText.append(address.getAdminArea());
                }

                if (address.getCountryName() != null) {
                    if (locationText.length() > 0) {
                        locationText.append(", ");
                    }
                    locationText.append(address.getCountryName());
                }

            }
            SPUtils.getInstance().put(AppConstans.userAddress,String.valueOf(locationText));
        } catch (IOException e) {
        }
    }



    private String convertTimestampToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
    private void deleteMessage(String chatRoomId, String messageId, View view, Context context) {
        DatabaseReference messageRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages").child(messageId);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            DisappearView disappearView = DisappearView.attach(activity);
            disappearView.execute(view, 1500, new AccelerateInterpolator(0.5f), true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                messageRef.removeValue().addOnCompleteListener(task -> {
                    Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                    } else {
                        Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }, 1500);
        } else {
            Toast.makeText(context, "Error: Context is not an Activity", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, senderTextView,timestampTextView;
        ImageView messageImage, message_content;
        BubbleLayout imagebubble,messageBubble;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imagebubble = itemView.findViewById(R.id.imagebubble);
            messageBubble  = itemView.findViewById(R.id.messageBubble);
            messageTextView = itemView.findViewById(R.id.message_text);
            senderTextView = itemView.findViewById(R.id.message_sender);
            timestampTextView = itemView.findViewById(R.id.message_timestamp);
            messageImage = itemView.findViewById(R.id.message_image);
            message_content = itemView.findViewById(R.id.message_content);
        }
    }

    private void setGravity(MessageViewHolder holder, boolean isCurrentUser) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageTextView.getLayoutParams();
        params.gravity = isCurrentUser ? Gravity.END : Gravity.START;
        holder.messageBubble.setLayoutParams(params);
        holder.senderTextView.setLayoutParams(params);
        holder.timestampTextView.setLayoutParams(params);
        holder.messageImage.setLayoutParams(params);

        LinearLayout.LayoutParams timestampParams = (LinearLayout.LayoutParams) holder.timestampTextView.getLayoutParams();
        if (isCurrentUser) {
            timestampParams.setMarginEnd(20);
            timestampParams.setMarginStart(0);
        } else {
            timestampParams.setMarginStart(20);
            timestampParams.setMarginEnd(0);
        }
        holder.timestampTextView.setLayoutParams(timestampParams);
        if (isCurrentUser) {
            holder.messageBubble.setArrowDirection(ArrowDirection.RIGHT);
            holder.imagebubble.setArrowDirection(ArrowDirection.RIGHT);
        } else {
            holder.messageBubble.setArrowDirection(ArrowDirection.LEFT);
            holder.imagebubble.setArrowDirection(ArrowDirection.LEFT);

        }
    }
}
