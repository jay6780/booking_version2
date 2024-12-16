package com.hair.booking.activity.events;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hair.booking.R;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.adapter.SetEvntadapter;

import java.util.ArrayList;

public class setEvent_admin extends AppCompatActivity {

    RecyclerView admin_recycler;
    private ArrayList<Usermodel> providerList;
    private DatabaseReference databaseReference;
    private SetEvntadapter setAdminadapter;
    private SearchView searchProvider;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_event_admin);
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        admin_recycler = findViewById(R.id.admin_recycler);
        searchProvider = findViewById(R.id.search);
        back = findViewById(R.id.back);
        initFirebase();
        setupSearchView();
        back.setOnClickListener(view -> onBackPressed());

    }


    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        admin_recycler.setLayoutManager(new LinearLayoutManager(setEvent_admin.this));
        providerList = new ArrayList<>();
        setAdminadapter = new SetEvntadapter(providerList,setEvent_admin.this);
        admin_recycler.setAdapter(setAdminadapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                providerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usermodel usermodel = snapshot.getValue(Usermodel.class);
                    usermodel.setKey(snapshot.getKey());
                    providerList.add(usermodel);
                }
                setAdminadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User_list", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    private void setupSearchView() {
        searchProvider.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    setAdminadapter.updateList(providerList);
                } else {
                    filterProviderList(newText);
                }
                return true;
            }
        });
    }


    private void filterProviderList(String query) {
        ArrayList<Usermodel> filteredProviderList = new ArrayList<>();
        for (Usermodel user : providerList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredProviderList.add(user);
            }
        }
        setAdminadapter.updateList(filteredProviderList);
    }



    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}