package com.hair.booking.activity.tools.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.AdminFrag.CancelFragment_admin;
import com.hair.booking.activity.Fragments.AdminFrag.CompleteFragment;
import com.hair.booking.activity.Fragments.AdminFrag.HistoryBookFragment_admin;

public class BookingAdapters_admin extends FragmentPagerAdapter {
    public BookingAdapters_admin(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HistoryBookFragment_admin();
                break;
            case 1:
                fragment = new CancelFragment_admin();
                break;
            case 2:
                fragment = new CompleteFragment();

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
