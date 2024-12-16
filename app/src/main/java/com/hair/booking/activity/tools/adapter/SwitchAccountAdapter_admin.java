package com.hair.booking.activity.tools.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.Application.Account_manager;
import com.hair.booking.activity.Application.Account_manager_admin;
import com.hair.booking.activity.Application.TinkerApplications;
import com.hair.booking.activity.tools.DialogUtils.Passworddialog;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class SwitchAccountAdapter_admin extends RecyclerView.Adapter<SwitchAccountAdapter_admin.ViewHolder> {
    private FirebaseAuth mAuth;
    private Context context;
    private List<Account_manager_admin> accountList;
    public SwitchAccountAdapter_admin(Context context, List<Account_manager_admin> accountList) {
        this.context = context;
        this.accountList = new ArrayList<>(accountList);
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_switch_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account_manager_admin account = accountList.get(position);
        holder.account_username.setText(account.getUsername());




       String usermail =  FirebaseAuth.getInstance().getCurrentUser().getEmail();
       if (account.getEmail().equals(usermail)){
           holder.signin.setVisibility(View.VISIBLE);
           holder.check.setVisibility(View.VISIBLE);
           holder.other_account.setVisibility(View.GONE);
       }else{
           holder.other_account.setVisibility(View.VISIBLE);
           holder.signin.setVisibility(View.GONE);
           holder.check.setVisibility(View.GONE);
       }

        Glide.with(context)
                .load(account.getImageUrl())
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.mipmap.man) // Fallback image
                .into(holder.accountImage);

        holder.itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Switch Account")
                    .setMessage("Are you sure you want to switch accounts?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        loginWithAccount(account);
                        SPUtils.getInstance().put(AppConstans.booknum, "0");
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

    }
    private void loginWithAccount(Account_manager_admin account) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            String accountEmail = account.getEmail();
            if (accountEmail != null && accountEmail.equals(currentUserEmail)) {
                Toast.makeText(context, "Your account is already logged in, please choose another account", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String email = account.getEmail();
        String password = account.getPassword();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            showRetryLoginDialog(account, context);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        SPUtils.getInstance().put(AppConstans.passwordkey_admin, password);
                        String userId = user.getUid();
                        proceedWithLogin(userId);
                    } else {
                        Toast.makeText(context, "Please verify your email before logging in", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            } else {
                showRetryLoginDialog(account, context);
            }
        });
    }


    private void showRetryLoginDialog(Account_manager_admin account,Context context) {
        Passworddialog passworddialog = new Passworddialog();
        passworddialog.retry_admin(context,account);
    }


    private void proceedWithLogin(String userId) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("ADMIN").child(userId);
        ValueEventListener studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (context instanceof Activity) {
                        TinkerApplications app = (TinkerApplications) context.getApplicationContext();
                        app.updatelogin(true, (Activity) context);
                    } else {
                        Log.e("ClassCastError", "Context is not an instance of TinkerApplications");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        studentRef.addListenerForSingleValueEvent(studentListener);
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    /**
     * Updates the account list and refreshes the RecyclerView.
     *
     * @param updatedAccounts The new list of accounts.
     */
    public void updateAccounts2(List<Account_manager_admin> updatedAccounts) {
        this.accountList.clear();
        this.accountList.addAll(updatedAccounts);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accountImage,check;
        TextView account_username,signin,other_account;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            signin = itemView.findViewById(R.id.signin);
            other_account = itemView.findViewById(R.id.other_account);
            check = itemView.findViewById(R.id.check);
            accountImage = itemView.findViewById(R.id.account_image);
            account_username = itemView.findViewById(R.id.account_username);
        }
    }
}
