package com.hair.booking.activity.tools.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.UserFragment.ReviewFragment;
import com.hair.booking.activity.Fragments.UserFragment.ServiceProvidersFragment;

public class ServiceProviderAdapter extends FragmentPagerAdapter {

    public ServiceProviderAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ServiceProvidersFragment();
            case 1:
                return new ReviewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
