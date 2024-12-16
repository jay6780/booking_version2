package com.hair.booking.activity.tools.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hair.booking.activity.tools.Model.Schedule;
import com.hair.booking.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapterUser extends RecyclerView.Adapter<ScheduleAdapterUser.ScheduleViewHolder> {
    private final List<Schedule> schedules;

    public ScheduleAdapterUser(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView dateTextView;
        private final ImageView myprofile;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.schedule_name);
            dateTextView = itemView.findViewById(R.id.schedule_date);
            myprofile = itemView.findViewById(R.id.myimage);
        }

        public void bind(Schedule schedule) {
            nameTextView.setText(schedule.getName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(schedule.getDate());
            dateTextView.setText(formattedDate);
            Glide.with(itemView.getContext())
                    .load(schedule.getImageUrl())
                    .transform(new CircleCrop())
                    .into(myprofile);
        }
    }
}
