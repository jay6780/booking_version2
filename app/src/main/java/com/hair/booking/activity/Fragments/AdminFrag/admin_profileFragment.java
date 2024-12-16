package com.hair.booking.activity.Fragments.AdminFrag;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hair.booking.R;
import com.hair.booking.activity.MainPageActivity.Admin.history_book_admin;
import com.hair.booking.activity.settingsAdmin.admin_settings;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

public class admin_profileFragment extends Fragment {

    private ImageView myAvatar;
    private TextView usernametv, useremailtv;
    private String username = SPUtils.getInstance().getString(AppConstans.AdminUsername);
    private String email = SPUtils.getInstance().getString(AppConstans.Adminemail);
    private String image = SPUtils.getInstance().getString(AppConstans.AdminImage);
    private String phone  = SPUtils.getInstance().getString(AppConstans.AdminPhone);
    private String name  = SPUtils.getInstance().getString(AppConstans.AdminFullname);
    private String address  = SPUtils.getInstance().getString(AppConstans.AdminAdress);
    private String age  = SPUtils.getInstance().getString(AppConstans.AdminAge);
    private String lengthOfService  = SPUtils.getInstance().getString(AppConstans.AdminLenght);
    public admin_profileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_profile, container, false);
        initViews(view);
        initClickers(view);
        initLoad(username, email, image);
        return view;
    }

    private void initLoad(String username, String email, String image) {
        usernametv.setText(username);
        useremailtv.setText(email);
        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(getActivity())
                    .load(image)
                    .apply(requestOptions)
                    .into(myAvatar);
        } else {
            myAvatar.setImageResource(R.drawable.baseline_person_24);
        }
    }

    private void initClickers(View view) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.updatebtn) {
                    Dialog dialog = new Dialog();
                    dialog.updateAdminProfile2(getActivity(),image,email,username,phone,name,address,age,lengthOfService);
                } else if (v.getId() == R.id.logout) {
                    Dialog dialog = new Dialog();
                    dialog.logout(getActivity());
                } else if (v.getId() == R.id.settings) {
                    Intent intent = new Intent(getActivity(), admin_settings.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.bookingHistory) {
                    Intent intent = new Intent(getActivity(), history_book_admin.class);
                    startActivity(intent);
                }
            }
        };

        idListeners(view, clickListener);
    }

    private void idListeners(View view, View.OnClickListener clickListener) {
        view.findViewById(R.id.logout).setOnClickListener(clickListener);
        view.findViewById(R.id.updatebtn).setOnClickListener(clickListener);
        view.findViewById(R.id.settings).setOnClickListener(clickListener);
        view.findViewById(R.id.bookingHistory).setOnClickListener(clickListener);

    }

    private void initViews(View view) {
        myAvatar = view.findViewById(R.id.myAvatar);
        usernametv = view.findViewById(R.id.usernametv);
        useremailtv = view.findViewById(R.id.useremailtv);
    }
}
