package com.hair.booking.activity.tools.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.eventFrag.EventReviewFragment;
import com.hair.booking.activity.Fragments.eventFrag.EventsServiceFragment;
import com.hair.booking.activity.Fragments.eventFrag.portfolioEventFragment;

public class Event_holderAdapter extends FragmentPagerAdapter {

    public Event_holderAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new EventsServiceFragment();
            case 1:
                return new EventReviewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
