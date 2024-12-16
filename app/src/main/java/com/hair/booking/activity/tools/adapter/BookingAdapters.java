package com.hair.booking.activity.tools.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.UserFragment.CompleteFragment_user;
import com.hair.booking.activity.Fragments.history.CancelFragment;
import com.hair.booking.activity.Fragments.history.HistoryBookFragment;

public class BookingAdapters extends FragmentPagerAdapter {
    public BookingAdapters(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HistoryBookFragment();
                break;
            case 1:
                fragment = new CancelFragment();
                break;
            case 2:
                fragment = new CompleteFragment_user();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3; // Number of fragments
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
