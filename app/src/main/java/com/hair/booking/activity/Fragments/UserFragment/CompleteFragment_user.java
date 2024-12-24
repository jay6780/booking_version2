package com.hair.booking.activity.Fragments.UserFragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Booking2;
import com.hair.booking.activity.tools.Model.BookingId;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.BookemptyAdapter;
import com.hair.booking.activity.tools.adapter.Completebook_adapter_user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CompleteFragment_user extends Fragment {
    private RecyclerView bookrecycler;
    private Completebook_adapter_user bookingAdapter;
    private List<Booking2> bookingList = new ArrayList<>();
    private BookemptyAdapter nodata;
    private String TAG = "history_book";
    private DatabaseReference bookingId;
    private LinearLayout ll_skeleton;
    private ViewSkeletonScreen skeletonScreen;
    int booknumber = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        bookrecycler = view.findViewById(R.id.historyRecycler);
        bookingAdapter = new Completebook_adapter_user(bookingList, getContext());
        bookrecycler.setAdapter(bookingAdapter);
        bookrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        nodata = new BookemptyAdapter(getContext());
        ll_skeleton = view.findViewById(R.id.ll_skeleton);
        ll_skeleton.setVisibility(View.VISIBLE);
        initShowbook();
        initSkeleton();
        return view;
    }
    private void initShowbook() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            bookingId = FirebaseDatabase.getInstance().getReference("BookingId").child(userId);
            bookingId.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        HashSet<String> chatIdSet = new HashSet<>();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            BookingId bookingId = childSnapshot.getValue(BookingId.class);
                            if (bookingId != null) {
                                String chatId = bookingId.getChatId();
                                chatIdSet.add(chatId);
                                Log.d("HistoryBook", "Chat Room ID: " + chatId);
                            }
                        }
                        ArrayList<String> chatIdList = new ArrayList<>(chatIdSet);
                        fetchBookIds(chatIdList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
                }
            });
        }
    }
    private void initSkeleton() {
        skeletonScreen = Skeleton.bind(ll_skeleton)
                .load(R.layout.skeletonlayout_2)
                .duration(1000)
                .color(R.color.colorFontGreyDark)
                .angle(20)
                .show();
        new Handler().postDelayed(() -> {
            skeletonScreen.hide();
            bookrecycler.setVisibility(View.VISIBLE);
            ll_skeleton.setVisibility(View.GONE);
        }, 1000);
    }

    private void fetchBookIds(List<String> chatIds) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mybookRef = database.getReference("CompletebookArtist");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            bookingList.clear();
            for (String chatId : chatIds) {
                Log.d("HistoryBook", "Chat Room ID: " + chatId);
                mybookRef.child(chatId).child("bookInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Loop through all bookInfo child nodes (since each chatId may have multiple entries under bookInfo)
                            for (DataSnapshot bookInfoSnapshot : dataSnapshot.getChildren()) {
                                // Get values from the snapshot
                                String address = bookInfoSnapshot.child("address").getValue(String.class);
                                String age = bookInfoSnapshot.child("age").getValue(String.class);
                                String date = bookInfoSnapshot.child("date").getValue(String.class);
                                String email = bookInfoSnapshot.child("email").getValue(String.class);
                                String heads = bookInfoSnapshot.child("heads").getValue(String.class);
                                String image = bookInfoSnapshot.child("image").getValue(String.class);
                                Boolean isOnline = bookInfoSnapshot.child("isOnline").getValue(Boolean.class);
                                String lengthOfService = bookInfoSnapshot.child("lengthOfservice").getValue(String.class);
                                String phoneNumber = bookInfoSnapshot.child("phonenumber").getValue(String.class);
                                String price = bookInfoSnapshot.child("price").getValue(String.class);
                                String providerName = bookInfoSnapshot.child("providerName").getValue(String.class);
                                String serviceName = bookInfoSnapshot.child("serviceName").getValue(String.class);
                                String time = bookInfoSnapshot.child("time").getValue(String.class);
                                String paymentMethod = bookInfoSnapshot.child("paymentMethod").getValue(String.class);
                                String key = bookInfoSnapshot.child("key").getValue(String.class);
                                String snapshotkey = bookInfoSnapshot.child("snapshotkey").getValue(String.class);
                                String timestamp = bookInfoSnapshot.child("timestamp").getValue(String.class);

                                // Create and add the booking object
                                Booking2 booking = new Booking2(
                                        providerName,
                                        serviceName,
                                        price,
                                        heads,
                                        phoneNumber,
                                        date,
                                        time,
                                        image,
                                        address,
                                        email,
                                        age,
                                        lengthOfService,
                                        paymentMethod,
                                        key,
                                        snapshotkey,
                                        timestamp
                                );
                                bookingList.add(booking);
                                booknumber++;
                            }
                            Collections.sort(bookingList, (b1, b2) -> b2.getTimestamp().compareTo(b1.getTimestamp()));
                        } else {
                            Log.d("HistoryBook", "No booking found for chat ID: " + chatId);
                        }

                        // Notify adapter after all data has been retrieved
                        if (chatIds.indexOf(chatId) == chatIds.size() - 1) {
                            if (bookingList.isEmpty()) {
                                bookingAdapter.notifyDataSetChanged();
                                bookrecycler.setAdapter(nodata);
                                SPUtils.getInstance().put(AppConstans.booknum, "0");
                            } else {
                                bookingAdapter.notifyDataSetChanged();
                                bookrecycler.setAdapter(bookingAdapter);
                                SPUtils.getInstance().put(AppConstans.booknum, String.valueOf(booknumber));
                                Log.d(TAG, "bookNum: " + booknumber);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("HistoryBook", "Error fetching data for chat ID " + chatId + ": " + databaseError.getMessage());
                    }
                });
            }
        }
    }
}