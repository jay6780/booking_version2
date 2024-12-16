package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity3;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EventUserAdapter extends RecyclerView.Adapter<EventUserAdapter.ProviderViewHolder> {
    private ArrayList<Usermodel> providerList;
    private Context context;
    private DatabaseReference databaseReference,chatreference;

    public EventUserAdapter(ArrayList<Usermodel> providerList, Context context) {
        this.providerList = providerList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Guess");
        this.chatreference = FirebaseDatabase.getInstance().getReference("chatRooms");
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Usermodel provider = providerList.get(position);
        holder.nameTextView.setText(provider.getUsername());
        Glide.with(context)
                .load(provider.getImage())
                .placeholder(R.drawable.baseline_person_24)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView);

        DatabaseReference providerStatusRef = databaseReference.child(provider.getKey()).child("online");
        providerStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isOnline = dataSnapshot.getValue(Boolean.class);
                if (isOnline != null) {
                    if (isOnline) {
                        holder.time.setText("Active");
                        holder.imageOnline.setImageResource(R.mipmap.active);
                    } else {
                        String timestampString = provider.getTimestamp();

                        if (timestampString != null && !timestampString.isEmpty()) {
                            try {
                                long timestamp = Long.parseLong(timestampString);  // Convert to long

                                long currentTime = System.currentTimeMillis();
                                long diffInMillis = currentTime - timestamp;

                                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                                long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
                                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                                if (diffInMinutes < 60) {
                                    if (diffInMinutes == 0) {
                                        holder.time.setText("Just now");
                                    } else if (diffInMinutes == 1) {
                                        holder.time.setText("1 minute ago");
                                    } else {
                                        holder.time.setText(diffInMinutes + " minutes ago");
                                    }
                                } else if (diffInHours < 24) {
                                    if (diffInHours == 1) {
                                        holder.time.setText("1 hour ago");
                                    } else {
                                        holder.time.setText(diffInHours + " hours ago");
                                    }
                                } else {
                                    if (diffInDays == 1) {
                                        holder.time.setText("1 day ago");
                                    } else {
                                        holder.time.setText(diffInDays + " days ago");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Log.e("UserchatAdapter", "Invalid timestamp format: " + timestampString, e);
                                holder.time.setText("inactive");
                            }
                        } else {
                            holder.time.setText("inactive");
                        }

                        holder.imageOnline.setImageResource(R.mipmap.inactive);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserchatAdapter", "Error: " + databaseError.getMessage());
            }
        });



        String chatRoomId = createChatRoomId(FirebaseAuth.getInstance().getCurrentUser().getEmail(), provider.getEmail());
        chatreference.child(chatRoomId).child("messages").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot messagesSnapshot) {
                if (messagesSnapshot.exists()) {
                    String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    boolean hasMessages = false;
                    boolean isSentByCurrentUser = false;
                    String imageUrl = null;
                    String message = null;
                    for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                        message = messageSnapshot.child("message").getValue(String.class);
                        String senderEmail = messageSnapshot.child("senderEmail").getValue(String.class);
                        String username = messageSnapshot.child("username").getValue(String.class);
                        imageUrl = messageSnapshot.child("imageUrl").getValue(String.class);

                        if (!senderEmail.equals(currentUserEmail)) {
                            hasMessages = true;
                            if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("http")) {
                                holder.nameTextView.setVisibility(View.GONE);
                                holder.messageContents.setText(username+": "+"Sent a link");
                            } else if (message != null && !message.isEmpty()) {
                                holder.messageContents.setText(username + ": " + message);
                                holder.nameTextView.setVisibility(View.GONE);
                            } else {
                                holder.messageContents.setVisibility(View.GONE);
                            }
                            holder.messageContents.setVisibility(View.VISIBLE);
                        } else {
                            isSentByCurrentUser = true;
                        }
                    }

                    if (!hasMessages && isSentByCurrentUser) {
                        holder.nameTextView.setVisibility(View.GONE);
                        if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("http")) {
                            holder.messageContents.setText("You: Sent a link");
                        } else if (message != null && !message.isEmpty()) {
                            holder.messageContents.setText("You: " + message);
                        }
                        holder.messageContents.setVisibility(View.VISIBLE);
                    } else if (!hasMessages) {
                        holder.nameTextView.setVisibility(View.GONE);
                        holder.messageContents.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });


        holder.itemView.setOnClickListener(view -> {
            // Create or get existing chat room
            checkAndCreateChatRoom(provider.getEmail(), provider.getUsername(),provider.getImage(),provider.isOnline(),provider.getKey(),provider.getAddress());
        });
    }

    private void checkAndCreateChatRoom(String providerEmail, String providerName,String image,boolean isOnline,String key,String address) {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();;
        String chatRoomId = createChatRoomId(currentUserEmail, providerEmail);

        FirebaseDatabase.getInstance().getReference("chatRooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Chat room does not exist, create it
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUsers(Arrays.asList(currentUserEmail, providerEmail));
                    FirebaseDatabase.getInstance().getReference("chatRooms").child(chatRoomId).setValue(chatRoom)
                            .addOnSuccessListener(aVoid -> {
                                SPUtils.getInstance().put(AppConstans.address, address);
                                SPUtils.getInstance().put(AppConstans.providerName, providerName);
                                SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);
                                Intent intent = new Intent(context, chatActivity3.class);
                                intent.putExtra("chatRoomId", chatRoomId);
                                intent.putExtra("providerName", providerName);
                                intent.putExtra("providerEmail", providerEmail);
                                intent.putExtra("image",image);
                                intent.putExtra("isOnline",isOnline);
                                intent.putExtra("key",key);
                                context.startActivity(intent);
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to create chat room", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    SPUtils.getInstance().put(AppConstans.address, address);
                    SPUtils.getInstance().put(AppConstans.providerName, providerName);
                    SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);
                    Intent intent = new Intent(context, chatActivity3.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("providerName", providerName);
                    intent.putExtra("providerEmail", providerEmail);
                    intent.putExtra("image",image);
                    intent.putExtra("isOnline",isOnline);
                    intent.putExtra("key",key);
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to check chat room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String createChatRoomId(String email1, String email2) {
        String chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
    }


    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public void updateList(ArrayList<Usermodel> newList) {
        providerList = newList;
        notifyDataSetChanged();
    }


    public class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView,messageContents,time;
        ImageView imageView,imageOnline;
        CheckBox providerCheckBox;
        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContents = itemView.findViewById(R.id.messageContents);
            time =  itemView.findViewById(R.id.time);
            imageOnline = itemView.findViewById(R.id.imgOnline);
            nameTextView = itemView.findViewById(R.id.provider_name);
            imageView = itemView.findViewById(R.id.provider_image);
            providerCheckBox = itemView.findViewById(R.id.providerCheckBox);

        }
    }
}
