package com.hair.booking.activity.tools.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.AdminFrag.CalendarAdminFragment;
import com.hair.booking.activity.Fragments.AdminFrag.MyServicePriceFragment;
import com.hair.booking.activity.Fragments.AdminFrag.admin_profileFragment;

public class AdminViewpagerAdapter extends FragmentPagerAdapter {

    public AdminViewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyServicePriceFragment();
            case 1:
                return new CalendarAdminFragment();
            case 2:
                return new admin_profileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // Number of fragments
    }
}
