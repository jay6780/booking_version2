package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import com.hair.booking.activity.MainPageActivity.chat.chatActivity;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile;
import com.hair.booking.activity.MainPageActivity.profile.providerProfile3;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
    private ArrayList<Usermodel> providerList;
    private Context context;
    private DatabaseReference databaseReference,chatreference,event;
    private String TAG = "Adapter";
    private Set<Integer> selectedProviders = new HashSet<>();
    private boolean multiSelectMode = false;
    private OnSelectionChangeListener selectionChangeListener;
    public ProviderAdapter(ArrayList<Usermodel> providerList, Context context) {
        this.providerList = providerList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("ADMIN");
        this.event = FirebaseDatabase.getInstance().getReference("Events");
        this.chatreference = FirebaseDatabase.getInstance().getReference("chatRooms");
    }
    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
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

        holder.providerCheckBox.setVisibility(multiSelectMode ? View.VISIBLE : View.GONE);
        holder.providerCheckBox.setChecked(selectedProviders.contains(position));
        holder.providerCheckBox.setEnabled(false);
        String chatRoomId = createChatRoomId(SPUtils.getInstance().getString(AppConstans.userEmail), provider.getEmail());
        holder.itemView.setOnClickListener(view -> {
            if (multiSelectMode) {
                toggleSelection(position, holder.providerCheckBox);
            } else {
                checkEmailLocation(provider,chatRoomId);
                SPUtils.getInstance().put(AppConstans.isOnline,provider.isOnline());
            }
        });

        holder.providerCheckBox.setOnClickListener(v -> toggleSelection(position, holder.providerCheckBox));

        holder.itemView.setOnLongClickListener(v -> {
            if (!multiSelectMode) {
                multiSelectMode = true;
                notifyDataSetChanged();
                toggleSelection(position, holder.providerCheckBox);
            }
            return true;
        });

        DatabaseReference providerStatusRef = databaseReference.child(provider.getKey()).child("online");
        DatabaseReference eventRef = event.child(provider.getKey()).child("online");

        providerStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isProviderOnline = dataSnapshot.getValue(Boolean.class);

                eventRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot eventDataSnapshot) {
                        Boolean isEventOnline = eventDataSnapshot.getValue(Boolean.class);
                        // Combine the provider and event online status
                        boolean isOnline = (isProviderOnline != null && isProviderOnline) || (isEventOnline != null && isEventOnline);
                        // Set online status
                        if (isOnline) {
                            holder.time.setText("Active");
                            holder.imageOnline.setImageResource(R.mipmap.active);
                        } else {
                            String timestampString = provider.getTimestamp();
                            if (timestampString != null && !timestampString.isEmpty()) {
                                try {
                                    long timestamp = Long.parseLong(timestampString);
                                    String timeAgo = getTimeAgo(timestamp);
                                    holder.time.setText(timeAgo);
                                } catch (NumberFormatException e) {
                                    Log.e("UserchatAdapter", "Invalid timestamp format: " + timestampString, e);
                                    holder.time.setText("inactive");
                                }
                            } else {
                                Log.w("UserchatAdapter", "No timestamp available for provider: " + provider.getKey());
                                holder.time.setText("inactive");
                            }

                            holder.imageOnline.setImageResource(R.mipmap.inactive);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("UserchatAdapter", "Event Error: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserchatAdapter", "Provider Error: " + databaseError.getMessage());
            }
        });




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
    }
    private String getTimeAgo(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - timestamp;

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (diffInMinutes < 60) {
            return (diffInMinutes == 0) ? "Just now" : (diffInMinutes == 1) ? "1 minute ago" : diffInMinutes + " minutes ago";
        } else if (diffInHours < 24) {
            return (diffInHours == 1) ? "1 hour ago" : diffInHours + " hours ago";
        } else {
            return (diffInDays == 1) ? "1 day ago" : diffInDays + " days ago";
        }
    }
    private void toggleSelection(int position, CheckBox checkBox) {
        if (selectedProviders.contains(position)) {
            selectedProviders.remove(position);
            checkBox.setChecked(false);
        } else {
            selectedProviders.add(position);
            checkBox.setChecked(true);
        }

        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(selectedProviders.size());
        }

        if (selectedProviders.isEmpty()) {
            multiSelectMode = false;
            notifyDataSetChanged();
        }
    }


    public void deleteSelectedItems(Context context) {
        ArrayList<Usermodel> selectedItems = getSelectedProviders();

        for (Usermodel provider : selectedItems) {
            String chatRoomId = createChatRoomId(SPUtils.getInstance().getString(AppConstans.userEmail), provider.getEmail());
            chatreference.child(chatRoomId).removeValue();
            providerList.remove(provider);
            Toast.makeText(context, "Deleted chat with: " + provider.getUsername(), Toast.LENGTH_SHORT).show();
        }

        selectedProviders.clear();
        multiSelectMode = false;
        notifyDataSetChanged();
        deleteRecentAvailed();
    }

    private void deleteRecentAvailed() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference entryRef = databaseReference.child("RecentAvailed").child(userId);
            entryRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    System.out.println("Entry deleted successfully.");
                } else {
                    System.err.println("Failed to delete entry: " + task.getException());
                }
            });
        } else {
            System.err.println("User not authenticated. Unable to delete entry.");
        }
    }

    private ArrayList<Usermodel> getSelectedProviders() {
        ArrayList<Usermodel> selectedItems = new ArrayList<>();
        for (int position : selectedProviders) {
            selectedItems.add(providerList.get(position));
        }
        return selectedItems;
    }

    private void checkEmailLocation(Usermodel provider,String chatroomId) {
        databaseReference.child(provider.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    openProfile(provider,chatroomId, chatActivity.class);
                } else {
                    event.child(provider.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                openProfile(provider, chatroomId,chatActivity.class);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private String createChatRoomId(String email1, String email2) {
        String chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
    }
    private void openProfile(Usermodel provider, String chatroomId,Class<?> targetClass) {
        Intent intent = new Intent(context, targetClass);
        intent.putExtra("providerEmail", provider.getEmail());
        intent.putExtra("chatRoomId", chatroomId);
        intent.putExtra("providerName", provider.getUsername());
        intent.putExtra("image", provider.getImage());
        intent.putExtra("address", provider.getAddress());
        intent.putExtra("age", provider.getAge());
        intent.putExtra("lengthOfservice", provider.getLengthOfService());
        intent.putExtra("key", provider.getKey());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public void updateList(ArrayList<Usermodel> newList) {
        providerList = newList;
        notifyDataSetChanged();
    }

    public void cancelSelection() {
        multiSelectMode = false;
        selectedProviders.clear();
        notifyDataSetChanged();

        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(0);
        }
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

    public interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }
}
