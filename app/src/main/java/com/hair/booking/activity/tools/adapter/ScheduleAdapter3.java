package com.hair.booking.activity.tools.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Schedule3;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter3 extends RecyclerView.Adapter<ScheduleAdapter3.ScheduleViewHolder> {
    private List<Schedule3> schedules;

    public ScheduleAdapter3(List<Schedule3> schedules) {
        this.schedules = schedules;
    }

    // Method to update the schedule list
    public void updateSchedules(List<Schedule3> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemschedule3, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule3 schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time);
        }

        public void bind(Schedule3 schedule) {
            timeTextView.setText(schedule.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(schedule.getDate());
            Log.d("formmattedDate","Date: "+ formattedDate);
            SPUtils.getInstance().put(AppConstans.time, schedule.getTime());
            SPUtils.getInstance().put(AppConstans.date,formattedDate);
        }
    }
}
