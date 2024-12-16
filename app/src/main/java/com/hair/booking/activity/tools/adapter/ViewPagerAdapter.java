package com.hair.booking.activity.tools.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hair.booking.activity.Fragments.UserFragment.CalendarUserFragment;
import com.hair.booking.activity.Fragments.UserFragment.FacedectionFragment;
import com.hair.booking.activity.Fragments.UserFragment.HomeFragment;
import com.hair.booking.activity.Fragments.UserFragment.MapFragment;
import com.hair.booking.activity.Fragments.UserFragment.ProfileUserFragment;
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String imageUrl;

    public ViewPagerAdapter(@NonNull FragmentManager fm, String imageUrl) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new CalendarUserFragment();
                break;
            case 2:
                fragment = new MapFragment();
                break;
            case 3:
                fragment = new FacedectionFragment();
                break;
            case 4:
                fragment = new ProfileUserFragment();
                break;
        }

        if (fragment != null && imageUrl != null) {
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", imageUrl);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 5; // Number of fragments
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
