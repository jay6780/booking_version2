package com.hair.booking.activity.tools.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.eventFrag.CalendarEventFragment;
import com.hair.booking.activity.Fragments.eventFrag.MyEventPriceFragment;
import com.hair.booking.activity.Fragments.eventFrag.event_profileFragment;

public class EventViewpagerAdapter extends FragmentPagerAdapter {

    public EventViewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyEventPriceFragment();
            case 1:
                return new CalendarEventFragment();
            case 2:
                return new event_profileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // Number of fragments
    }
}
