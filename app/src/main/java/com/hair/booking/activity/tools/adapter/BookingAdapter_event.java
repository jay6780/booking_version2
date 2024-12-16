package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity2;
import com.hair.booking.activity.MainPageActivity.chat.chatActivity3;
import com.hair.booking.activity.tools.Model.Booking;
import com.hair.booking.activity.tools.Model.Booking2;
import com.hair.booking.activity.tools.Model.BookingId;
import com.hair.booking.activity.tools.Model.ChatRoom;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BookingAdapter_event extends RecyclerView.Adapter<BookingAdapter_event.BookingViewHolder> {

    private List<Booking2> bookingList;
    private Context context;
    private String TAG ="BookAdapter";
    private String chatRoomId;
    private DatabaseReference databaseReference,chatroomIds,complete,book,CompletebookArtist,Mybook,
            transactionMap,RecentAvailed,RecentAvailed_event;
    private List<String> serviceNamesList;
    private  String eventUsername = SPUtils.getInstance().getString(AppConstans.EventUsername);
    private  String eventImage = SPUtils.getInstance().getString(AppConstans.EventImage);
    private  String eventPhone = SPUtils.getInstance().getString(AppConstans.EventPhone);
    private  String eventEmail = SPUtils.getInstance().getString(AppConstans.Eventemail);
    private  String eventAge = SPUtils.getInstance().getString(AppConstans.EventAge);
    private  String eventAddress = SPUtils.getInstance().getString(AppConstans.EventAdress);
    public BookingAdapter_event(List<Booking2> bookingList , Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.chatroomIds = FirebaseDatabase.getInstance().getReference();
        this.complete = FirebaseDatabase.getInstance().getReference("Completebook");
        this.CompletebookArtist = FirebaseDatabase.getInstance().getReference("CompletebookArtist");
        this.book = FirebaseDatabase.getInstance().getReference("MybookUser");
        this.Mybook = FirebaseDatabase.getInstance().getReference("Mybook");
        this.transactionMap = FirebaseDatabase.getInstance().getReference("transactionMap");
        this.RecentAvailed_event = FirebaseDatabase.getInstance().getReference("RecentAvailed_event");
        this.RecentAvailed = FirebaseDatabase.getInstance().getReference("RecentAvailed");
        this.serviceNamesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_adapter_admin, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking2 booking = bookingList.get(position);
        holder.nameTextView.setText(booking.getProviderName());
        holder.time.setText(booking.getTime());
        holder.date.setText(booking.getDate());

        holder.servicename.setText("Availed: "+ booking.getServiceName());
        holder.price.setText(context.getString(R.string.price)+": " + booking.getPrice() + " php");

        Glide.with(holder.itemView.getContext())
                .load(booking.getImage())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.avatar);

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Complete book?")
                        .setMessage("Are you sure want to complete this book?")
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Complete booking....");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String currentUserId = currentUser.getUid();
                                    String currentUserEmail = currentUser.getEmail();
                                    initRecentpackages(booking.getServiceName());
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Then, call checkIfUserIsGuess
                                            checkIfUserIsGuess(
                                                    currentUserId,
                                                    booking.getTime(),
                                                    booking.getHeads(),
                                                    booking.getPaymentMethod(),
                                                    booking.getServiceName(),
                                                    booking.getPrice(),
                                                    booking.getDate(),
                                                    booking.getAddress(),
                                                    booking.getEmail(),
                                                    currentUserEmail,
                                                    booking.getProviderName(),
                                                    booking.getImage(),
                                                    context,
                                                    booking.getKey(),
                                                    booking.getAge(),
                                                    booking.getLengthOfservice(),
                                                    booking.getPhonenumber(),
                                                    booking.getSnapshotkey()
                                            );
                                            progressDialog.dismiss();
                                        }
                                    }, 500); // Delay of 500 milliseconds to simulate processing time and allow UI to update
                                }
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), null)
                        .show();
            }
        });
    }

    private void initRecentpackages(String serviceName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecentAvailed_event").child(userId);

        databaseReference.orderByChild("serviceName").equalTo(serviceName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                                String packages = serviceSnapshot.child("packages").getValue(String.class);
                                Log.d(TAG, "Completepack: " + serviceName + ", Packages: " + packages);
                                SPUtils.getInstance().put(AppConstans.RecentPackage_event, packages);
                            }
                        } else {
                            Log.d(TAG, "No matching service found for: " + serviceName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FetchFilteredPackages", "Failed to fetch data: " + error.getMessage());
                    }
                });
    }


    private void checkIfUserIsGuess(String userId, String time, String heads, String cash, String serviceName, String price, String date, String address, String provideremail, String curruntUserEmail,
                                    String providerName, String image, Context context, String key, String age, String lengthOfservice, String phonenumber,String snapshotkey) {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Events").child(userId);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String locationLink = "";
                    if (address != null && !address.isEmpty()) {
                        try {
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addressList = geocoder.getFromLocationName(address, 1);
                            if (addressList != null && !addressList.isEmpty()) {
                                Address addr = addressList.get(0);
                                double latitude = addr.getLatitude();
                                double longitude = addr.getLongitude();
                                locationLink = "https://www.google.com/maps/?q=" + latitude + "," + longitude;
                            } else {
                                Log.e(TAG, "No coordinates found for the address");
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Geocoding failed", e);
                        }
                    }
                    String serviceListJson = SPUtils.getInstance().getString(AppConstans.serviceNamesList, "[]");
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    serviceNamesList = new Gson().fromJson(serviceListJson, type);
                    if (serviceNamesList == null || serviceNamesList.isEmpty()) {
                        String recentPackage = SPUtils.getInstance().getString(AppConstans.RecentPackage_event, "");
                        if (!TextUtils.isEmpty(recentPackage)) {
                            serviceNamesList = new ArrayList<>();
                            serviceNamesList.add(recentPackage);
                        } else {
                            serviceNamesList = new ArrayList<>();
                        }
                    }


                    String availedMessage = "Hi, I'm " + username + "\n" +
                            "The book has been complete with:\n" +
                            "Service Name: " + serviceName + "\n" +
                            "Which package: " + serviceNamesList + "\n" +
                            "Selected schedule: " + "time: " + time + "\n" +
                            "date: " + date + "\n" +
                            "Number of Heads: " + heads + "\n" +
                            "Payment Method: " + cash + "\n" +
                            "Price: " + price + " php" + "\n" +
                            "Thank you!";

                    Log.d(TAG, availedMessage);
                    SPUtils.getInstance().put(AppConstans.availedMessage, availedMessage);
                    checkAndCreateChatRoom(provideremail, providerName, image, curruntUserEmail, context, address, key, availedMessage);
                    Booking2 booking = new Booking2(providerName, serviceName, price, heads, phonenumber, date, time, image, address, provideremail, age, lengthOfservice, cash, key,snapshotkey);
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Booking2 booking2 = new Booking2(eventUsername, serviceName, price, heads, eventPhone, date, time, eventImage, eventAddress, eventEmail, eventAge, lengthOfservice, cash, uid,snapshotkey);
                    String chatRoomId = createChatRoomId(curruntUserEmail, provideremail);
                    CompletebookArtist.child(chatRoomId).child("bookInfo").child(snapshotkey).setValue(booking2)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "Cancel data saved successfully.");
                                }
                            });
                    complete.child(chatRoomId).child("bookInfo").child(snapshotkey).setValue(booking)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Cancel data saved successfully.");
                                    book.child(chatRoomId).child("bookInfo").child(snapshotkey).removeValue();
                                    Mybook.child(chatRoomId).child("bookInfo").child(snapshotkey).removeValue();
                                    RecentAvailed.child(key).removeValue();
                                    RecentAvailed_event.child(uid).removeValue();
                                    transactionMap.child(chatRoomId).removeValue();
                                    decreasebookCount();
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking Guess user: " + databaseError.getMessage());
            }
        });
    }

    private void decreasebookCount() {
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference bookCountRef = databaseReference.child("BookCount").child(key);
        Log.d("decreaseCount: ","Decrease: "+key);
        bookCountRef.child("count").get().addOnSuccessListener(snapshot -> {
            int currentCount = 0;
            if (snapshot.exists()) {
                currentCount = snapshot.getValue(Integer.class);
            }

            int newCount = currentCount - 1;
            bookCountRef.child("count").setValue(newCount)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDB", "Count updated successfully to " + newCount))
                    .addOnFailureListener(e -> Log.e("FirebaseDB", "Error updating count", e));
        }).addOnFailureListener(e -> Log.e("FirebaseDB", "Error fetching count", e));
    }


    private void savedCompletebookUser(String chatRoomId, String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String time = String.valueOf(System.currentTimeMillis());
        BookingId mychatId = new BookingId(time, chatRoomId);
        databaseReference.child("CompleteBookId_user").child(key).push().setValue(mychatId)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDB", "Data saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDB", "Error saving data", e);
                });
    }

    private void savedBookId(String childKey) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String time = String.valueOf(System.currentTimeMillis());
        BookingId mychatId = new BookingId(time, childKey);
        databaseReference.child("CompleteBookId").child(userId).push().setValue(mychatId)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDB", "Data saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDB", "Error saving data", e);
                });
    }


    private void checkAndCreateChatRoom(String provideremail, String providerName, String image, String curruntUserEmail,Context context,String address,String key,String availedmessage) {
        String chatRoomId = createChatRoomId(curruntUserEmail, provideremail);
        databaseReference.child("chatRooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUsers(Arrays.asList(curruntUserEmail, provideremail));
                    chatroomIds.child("chatRooms").child(chatRoomId).setValue(chatRoom)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Complete Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, chatActivity3.class);
                                intent.putExtra("chatRoomId", chatRoomId);
                                intent.putExtra("providerName", providerName);
                                intent.putExtra("image", image);
                                intent.putExtra("providerEmail", provideremail);
                                intent.putExtra("address", address);
                                intent.putExtra("key", key);
                                intent.putExtra("cancelledmessage", availedmessage);
                                context.startActivity(intent);
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to create chat room", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    String previousMessage  = SPUtils.getInstance().getString(AppConstans.lastmessage);
                    Intent intent = new Intent(context, chatActivity3.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("providerName", providerName);
                    intent.putExtra("image", image);
                    intent.putExtra("providerEmail", provideremail);
                    intent.putExtra("address", address);
                    intent.putExtra("key", key);
                    if (!availedmessage.equals(previousMessage)) {
                        Toast.makeText(context, "Complete Success", Toast.LENGTH_SHORT).show();
                        intent.putExtra("cancelledmessage", availedmessage);
                    }else{
                        Toast.makeText(context,"Already cancelled",Toast.LENGTH_SHORT).show();
                    }
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
        chatRoomId = email1.compareTo(email2) < 0
                ? email1.replace(".", "_") + "_" + email2.replace(".", "_")
                : email2.replace(".", "_") + "_" + email1.replace(".", "_");

        SPUtils.getInstance().put(AppConstans.ChatRoomId, chatRoomId);

        return chatRoomId;
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView,time,date,servicename,price;
        ImageView avatar;
        AppCompatButton cancel;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            cancel = itemView.findViewById(R.id.cancel);
            servicename = itemView.findViewById(R.id.servicename);
            nameTextView = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            avatar = itemView.findViewById(R.id.provider_avatar);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.time);
        }
    }
}
