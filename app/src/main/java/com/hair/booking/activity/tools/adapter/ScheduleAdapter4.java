package com.hair.booking.activity.tools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Schedule3;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter4 extends RecyclerView.Adapter<ScheduleAdapter4.ScheduleViewHolder> {

    private final List<Schedule3> scheduleList;
    private final Context context;

    public ScheduleAdapter4(List<Schedule3> scheduleList, Context context) {
        this.scheduleList = scheduleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule3, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule3 schedule = scheduleList.get(position);
        holder.timeTextView.setText(schedule.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(schedule.getDate());
        holder.date.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView,date;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
