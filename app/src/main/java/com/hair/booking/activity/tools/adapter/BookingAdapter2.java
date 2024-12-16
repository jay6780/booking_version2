package com.hair.booking.activity.tools.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Booking;

import java.util.List;

public class BookingAdapter2 extends RecyclerView.Adapter<BookingAdapter2.BookingViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter2(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_layout, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(booking.getImage())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.avatar);

        holder.nameTextView.setText(booking.getProviderName());
        holder.ageTextView.setText(booking.getAge());
        holder.addressTextView.setText(booking.getAddress());
        holder.lengthOfServiceTextView.setText(booking.getLengthOfservice());
        // Populate other TextViews as needed
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView, addressTextView, lengthOfServiceTextView;
        ImageView avatar;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            ageTextView = itemView.findViewById(R.id.age);
            avatar = itemView.findViewById(R.id.provider_avatar);
            addressTextView = itemView.findViewById(R.id.address);
            lengthOfServiceTextView = itemView.findViewById(R.id.lenghtofservice);
        }
    }
}
