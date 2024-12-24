package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Booking2;

import java.util.List;

public class CancelBookAdapter extends RecyclerView.Adapter<CancelBookAdapter.BookingViewHolder> {

    private List<Booking2> bookingList;
    private Context context;
    private String TAG ="BookAdapter";
    private String chatRoomId;
    private DatabaseReference databaseReference,chatroomIds,cancelled,book;
    public CancelBookAdapter(List<Booking2> bookingList ,Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.chatroomIds = FirebaseDatabase.getInstance().getReference();
        this.cancelled = FirebaseDatabase.getInstance().getReference("Cancelbook");
        this.book = FirebaseDatabase.getInstance().getReference("Mybook");
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_adapterlayout, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking2 booking = bookingList.get(position);
        holder.nameTextView.setText(booking.getProviderName());
        holder.time.setText(booking.getTime());
        holder.date.setText(booking.getDate());
        holder.cancel.setText(context.getString(R.string.appointment_failed));
        holder.servicename.setText(context.getString(R.string.service_name)+": "+booking.getServiceName());
        holder.price.setText(context.getString(R.string.price)+": "+booking.getPrice());
        Glide.with(holder.itemView.getContext())
                .load(booking.getImage())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.avatar);
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
            price = itemView.findViewById(R.id.price);
            servicename = itemView.findViewById(R.id.servicename);
            nameTextView = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            avatar = itemView.findViewById(R.id.provider_avatar);
            time = itemView.findViewById(R.id.time);
        }
    }
}
