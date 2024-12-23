package com.hair.booking.activity.MainPageActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.azhon.appupdate.manager.DownloadManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.hair.booking.activity.Fragments.UserFragment.HomeFragment;
import com.hair.booking.activity.MainPageActivity.Admin.Ageandservice;
import com.hair.booking.activity.MainPageActivity.Admin.Createadmin;
import com.hair.booking.activity.MainPageActivity.Admin.Event_userchat;
import com.hair.booking.activity.MainPageActivity.Admin.MyservicePrice;
import com.hair.booking.activity.MainPageActivity.Admin.UserChat;
import com.hair.booking.activity.MainPageActivity.Admin.history_book_admin;
import com.hair.booking.activity.MainPageActivity.Admin.set_superAdmin;
import com.hair.booking.activity.MainPageActivity.Admin.updateActivity;
import com.hair.booking.activity.MainPageActivity.Guess.new_bieInformation;
import com.hair.booking.activity.MainPageActivity.bookingUi.history_book;
import com.hair.booking.activity.MainPageActivity.calendar.CalendarAdmin;
import com.hair.booking.activity.MainPageActivity.chat.User_list;
import com.hair.booking.activity.MainPageActivity.maps.MapSelectActivity;
import com.hair.booking.activity.events.CreateEventorganizer;
import com.hair.booking.activity.events.history_book_event;
import com.hair.booking.activity.events.setEvent_admin;
import com.hair.booking.activity.myfavorites;
import com.hair.booking.activity.tools.DialogUtils.Dialog;
import com.hair.booking.activity.tools.DialogUtils.UserProviderDialog;
import com.hair.booking.activity.tools.Model.MychatId;
import com.hair.booking.activity.tools.Model.Usermodel;
import com.hair.booking.activity.tools.Service.MessageNotificationService;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;
import com.hair.booking.activity.tools.adapter.AdminViewpagerAdapter;
import com.hair.booking.activity.tools.adapter.EventViewpagerAdapter;
import com.hair.booking.activity.tools.adapter.ViewPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class newHome extends AppCompatActivity {
    private static final String FIRST_RUN_KEY = "firstRun";
    private ViewPager viewPager,adminPager,eventPager;
    private ImageView drawerToggle,avatar,gunting,drawerToggle2,avatar2;
    private LinearLayout profile, createacc, login, logout,userMenus,pleaseLog,createadmin;
    private ViewPagerAdapter viewPagerAdapter;
    private AdminViewpagerAdapter adminViewpagerAdapter;
    private EventViewpagerAdapter eventViewpagerAdapter;
    private LinearLayout adminMenu,event_viewpager,update;
    private DatabaseReference guessRef, adminRef,serviceRef,bookId,events;
    private FirebaseAuth mAuth;
    private String email ="s.realamiler@gmail.com";
    private String eventorganizer_email ="cyroberto22@gmail.com";
    private static  String TAG = "Hompage";
    private String guessImage,userEmail,usernameText,phone,fullname,addressUser,age;
    private RelativeLayout  newhome,menu2,root_relative,event_menu;
    private LinearLayout guessviewpager,updateAdmin,adminviewpager,selectortab_event,addEventoraganizer,sidenav;
    private  BottomNavigationView  bottomNavigationView,adminBottom,event_bottomnav;
    private ImageView guessImageAdmin,bell,messageImg;
    private LinearLayout eventsProfile;
    private DrawerLayout drawerLayout;
    private TextView   userEmailAdmin, addressUserAdmin,fullnameAdmin,usernameTextAdin,phoneAdmin,ageAdmin,username,location_address;
    //event
    private TextView username_event,location_address_event,email_event;
    private LinearLayout admin_linear,user_linear,event_linear;
    //user
    private TextView username_user,location_address_user;
    private SkeletonScreen skeletonScreen;
    private ImageView event_toggle, eventMessageimg,avatar_user;
    private TextView email2,username2,email3;
    private String lenght;
    private TextView profiletxt;
    private String imageUrl;
    String bookprovideremail = SPUtils.getInstance().getString(AppConstans.bookprovideremail);
    private LinearLayout home,privacy;
    private RelativeLayout user_viewsmenu,admin_menus,default_menu;
    private LinearLayout setSuperAdmin,setEventAdmin;
    private ImageView adminbell,event_bell;
    private TextView badge_count_admin,event_badge;
    private boolean isGuess = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_pageroot);
        startService(new Intent(this, MessageNotificationService.class));
        initUserView();
        initadminview();
        initEventView();
        initClear();
        initAppUpdate();
        sidenav = findViewById(R.id.sidenav);
        changeStatusBarColor(getResources().getColor(R.color.white2));
        initSkeleton();
        initGone();

        mAuth = FirebaseAuth.getInstance();
        bookId = FirebaseDatabase.getInstance().getReference("MybookId");
        guessRef = FirebaseDatabase.getInstance().getReference("Guess");
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        serviceRef = FirebaseDatabase.getInstance().getReference("Service");
        events = FirebaseDatabase.getInstance().getReference("Events");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),imageUrl);
        adminViewpagerAdapter = new AdminViewpagerAdapter(getSupportFragmentManager());
        eventViewpagerAdapter = new EventViewpagerAdapter(getSupportFragmentManager());

        adminPager.setAdapter(adminViewpagerAdapter);
        viewPager.setAdapter(viewPagerAdapter);
        eventPager.setAdapter(eventViewpagerAdapter);
        default_menu = findViewById(R.id.default_menu);
        loadUserDetails();
        initDraWtoggle();
        initDraWtoggle2();
        initEvenToggle();
        bell = findViewById(R.id.bell);
        initClickers();
        intiClick_nav();
        imageUrl = getIntent().getStringExtra("imageUrl");
        adminPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        adminBottom.setSelectedItemId(R.id.adminHome);
                        break;
                    case 1:
                        adminBottom.setSelectedItemId(R.id.adminCalendar);
                        break;
                    case 2:
                        adminBottom.setSelectedItemId(R.id.admin_profile);
                        break;
                }
        }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        eventPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        event_bottomnav.setSelectedItemId(R.id.eventHome);
                        break;
                    case 1:
                        event_bottomnav.setSelectedItemId(R.id.eventCalendar);
                        break;
                    case 2:
                        event_bottomnav.setSelectedItemId(R.id.eventprofile);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_location);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_camera);
                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }

        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        avatar_user.setVisibility(View.VISIBLE);
                        username_user.setVisibility(View.VISIBLE);
                        location_address_user.setVisibility(View.VISIBLE);
                        //gone
                        profiletxt.setVisibility(View.GONE);
                        gunting.setVisibility(View.GONE);
                        return true;
                    case R.id.nav_calendar:
                        viewPager.setCurrentItem(1);
                        avatar_user.setVisibility(View.VISIBLE);
                        username_user.setVisibility(View.VISIBLE);
                        location_address_user.setVisibility(View.VISIBLE);
                        //gone
                        profiletxt.setVisibility(View.GONE);
                        gunting.setVisibility(View.GONE);
                        return true;
                    case R.id.nav_location:
                        viewPager.setCurrentItem(2);
                        avatar_user.setVisibility(View.VISIBLE);
                        username_user.setVisibility(View.VISIBLE);
                        location_address_user.setVisibility(View.VISIBLE);
                        //gone
                        profiletxt.setVisibility(View.GONE);
                        gunting.setVisibility(View.GONE);
                        return true;
                    case R.id.nav_camera:
                            viewPager.setCurrentItem(3);
                            avatar_user.setVisibility(View.VISIBLE);
                            username_user.setVisibility(View.VISIBLE);
                            location_address_user.setVisibility(View.VISIBLE);
                            //gone
                            profiletxt.setVisibility(View.GONE);
                            gunting.setVisibility(View.GONE);
                        return true;
                    case R.id.nav_profile:
                        viewPager.setCurrentItem(4);
                        avatar_user.setVisibility(View.GONE);
                        username_user.setVisibility(View.GONE);
                        location_address_user.setVisibility(View.GONE);
                        //gone
                        profiletxt.setVisibility(View.VISIBLE);
                        gunting.setVisibility(View.VISIBLE);
                        return true;
                }
                viewPager.getAdapter().notifyDataSetChanged();
                return true;
            }
        });

        event_bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.eventHome:
                        eventPager.setCurrentItem(0);
                        return true;
                    case R.id.eventCalendar:
                        eventPager.setCurrentItem(1);
                        return true;
                    case R.id.eventprofile:
                        eventPager.setCurrentItem(2);
                        return true;
                }
                eventPager.getAdapter().notifyDataSetChanged();
                return true;
            }
        });


        adminBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.adminHome:
                        adminPager.setCurrentItem(0);
                        return true;
                    case R.id.adminCalendar:
                        adminPager.setCurrentItem(1);
                        return true;
                    case R.id.admin_profile:
                        adminPager.setCurrentItem(2);
                        return true;
                }
                adminPager.getAdapter().notifyDataSetChanged();
                return true;
            }
        });


    }

    private void initAppUpdate() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference apkDetailsRef = database.getReference("ApkDetails");
        String currentVersion = getCurrentAppVersion();

        apkDetailsRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String latestVersion = snapshot.child("versionName").getValue(String.class);
                    String apkUrl = snapshot.child("apkUrl").getValue(String.class);
                    if (latestVersion != null && apkUrl != null) {
                        if (!latestVersion.equals(currentVersion)) {
                            startAppUpdate(apkUrl, latestVersion);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to fetch update details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startAppUpdate(String apkUrl, String versionName) {
        Dialog updatedialog = new Dialog();
        updatedialog.updateDialog(newHome.this,apkUrl,versionName);

    }

    private String getCurrentAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }


    private void initClear() {
        SPUtils.getInstance().put(AppConstans.discountservicename,"");
        SPUtils.getInstance().put(AppConstans.discount,"");
        SPUtils.getInstance().put(AppConstans.serviceNamesList,"");
        SPUtils.getInstance().put(AppConstans.locationlink,"");
    }

    private void intiClick_nav() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.home_user) {
                    Intent intent = new Intent(getApplicationContext(), newHome.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.favorites) {
                    Intent intent = new Intent(getApplicationContext(), myfavorites.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (v.getId() == R.id.logout_user) {
                    Dialog dialog = new Dialog();
                    dialog.logout(newHome.this);
                } else if (v.getId() == R.id.logout_admin) {
                    Dialog dialog = new Dialog();
                    dialog.logout(newHome.this);
                }

            }
        };

        idlisterners2(clickListener);
    }

    private void idlisterners2(View.OnClickListener clickListener) {
        findViewById(R.id.home_user).setOnClickListener(clickListener);
        findViewById(R.id.favorites).setOnClickListener(clickListener);
        findViewById(R.id.logout_user).setOnClickListener(clickListener);
        findViewById(R.id.logout_admin).setOnClickListener(clickListener);

    }


    private void initDraWtoggle2() {
        drawerToggle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.maroon2));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white2));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void initUserInfo(String image, String usernamedata, String email) {
        username_user = findViewById(R.id.username_user);
        location_address_user = findViewById(R.id.location_address_user);
        avatar_user = findViewById(R.id.avatar_user);
        avatar2 = findViewById(R.id.avatar2);
        email2 = findViewById(R.id.email2);
        username2 = findViewById(R.id.username2);
        profiletxt = findViewById(R.id.profiletxt);
        gunting = findViewById(R.id.gunting);
        email3 = findViewById(R.id.email);

        email3.setVisibility(View.GONE);

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            if (!isDestroyed()) {
                Glide.with(this)
                        .load(image)
                        .apply(requestOptions)
                        .into(avatar_user);
            }
        } else {
            avatar_user.setImageResource(R.mipmap.man);
        }

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            if (!isDestroyed()) {
                Glide.with(this)
                        .load(image)
                        .apply(requestOptions)
                        .into(avatar2);
            }
        } else {
            avatar2.setImageResource(R.mipmap.man);
        }

        username_user.setText(usernamedata);
        username2.setText(usernamedata);
        email2.setText(email);
        fetchCurrentLocation();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        List<Account_manager> accountList = Account_manager.getAccounts();
        boolean exists = false;
        String password = SPUtils.getInstance().getString(AppConstans.passwordkey);

        if (userEmail != null && !userEmail.isEmpty()) {
            // Check if password is not null or empty
            if (password != null && !password.isEmpty()) {
                for (Account_manager account : accountList) {
                    if (userEmail.equals(account.getEmail())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    accountList.add(new Account_manager(
                            password,
                            userEmail,
                            image,
                            usernamedata
                    ));
                    Account_manager.saveAccounts(accountList);
                }
            } else {
                Log.e("initUserInfo", "Password key is missing. Skipping account processing.");
            }
        } else {
            Log.e("initUserInfo", "Email is null or empty. Skipping account processing.");
        }

    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(newHome.this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        String streetAndCity = getAddressFromLocation(location);
                        SPUtils.getInstance().put(AppConstans.homeAddress,streetAndCity);
                        location_address_user.setText(streetAndCity);
                    }
                });
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        StringBuilder locationText = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String locationLink = "https://www.google.com/maps/?q=" + location.getLatitude() + "," + location.getLongitude();
            SPUtils.getInstance().put(AppConstans.locationlink, locationLink);
            Log.d(TAG, "User location: " + locationLink);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                if (address.getLocality() != null) {
                    locationText.append(address.getLocality());
                }
                if (address.getCountryName() != null) {
                    if (locationText.length() > 0) {
                        locationText.append(", ");
                    }
                    locationText.append(address.getCountryName());
                }
                int maxLength = 50;
                if (locationText.length() > maxLength) {
                    return locationText.substring(0, maxLength) + " ...";
                }
                return locationText.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Location not found";
    }


    private void initEvenToggle() {
        event_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }
    private void initShowbook() {
        TextView badgeCount = findViewById(R.id.badge_count);
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknum,"0");
        Log.d("bookCount","BookCountUser: "+badgenum);
        if(badgenum == null) {
            badgeCount.setText("0");
          return;
        }
        badgeCount.setVisibility(View.VISIBLE);
        badgeCount.setText(badgenum);
    }
    private void initAdminBook() {
        startService(new Intent(this, MessageNotificationService.class));
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknumAdmin, "0");
        if (badgenum == null) {
            badge_count_admin.setText("0");
            return;
        }
        badge_count_admin.setVisibility(View.VISIBLE);
        badge_count_admin.setText(badgenum);

        adminbell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), history_book_admin.class);
                intent.putExtra("bookprovideremail", bookprovideremail);
                startActivity(intent);
            }
        });

    }


    private void initGone() {
        newhome.setVisibility(View.GONE);
        guessviewpager.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        menu2.setVisibility(View.GONE);
        adminMenu.setVisibility(View.GONE);
    }

    private void initSkeleton() {
        newhome.setVisibility(View.VISIBLE);
    }

    private boolean isFirstRun() {
        SPUtils spUtils = SPUtils.getInstance();
        boolean isFirstRun = spUtils.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            spUtils.put(FIRST_RUN_KEY, false);
        }

        return isFirstRun;
    }

    private void initEventView() {
        event_bell = findViewById(R.id.event_bell);
        event_badge = findViewById(R.id.event_badge);
        setEventAdmin = findViewById(R.id.setEventAdmin);
        event_menu = findViewById(R.id.event_menu);
        event_linear = findViewById(R.id.event_linear);
        selectortab_event = findViewById(R.id.selectortab_event);
        addEventoraganizer = findViewById(R.id.addEventoraganizer);
        eventsProfile = findViewById(R.id.eventsProfile);
        eventMessageimg = findViewById(R.id.eventMessageimg);
        event_toggle = findViewById(R.id.event_toggle);
        eventPager = findViewById(R.id.event_menus);
        event_viewpager = findViewById(R.id.event_viewpager);
        event_bottomnav = findViewById(R.id.event_bottomnav);

    }
    private void initadminview() {
        //adminview
        adminbell = findViewById(R.id.adminbell);
        badge_count_admin = findViewById(R.id.badge_count_admin);
        setSuperAdmin = findViewById(R.id.setSuperAdmin);
        ageAdmin = findViewById(R.id.age);
        update = findViewById(R.id.update);
        admin_linear = findViewById(R.id.admin_linear);
        adminBottom = findViewById(R.id.admin_bottomnav);
        admin_menus = findViewById(R.id.admin_menus);
        adminviewpager = findViewById(R.id.admin_viewpager);
        updateAdmin = findViewById(R.id.adminprofile);
        profile = findViewById(R.id.profile);
        createadmin = findViewById(R.id.addAdmin);
        createacc = findViewById(R.id.register);
        login = findViewById(R.id.login);
        logout = findViewById(R.id.logoutAdmin);
        phoneAdmin = findViewById(R.id.phone);
        guessImageAdmin = findViewById(R.id.avatar);
        menu2 = findViewById(R.id.menu2);
        fullnameAdmin = findViewById(R.id.fullname);
        addressUserAdmin= findViewById(R.id.address);
        adminMenu = findViewById(R.id.adminMenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = findViewById(R.id.toggle);
        userEmailAdmin = findViewById(R.id.email);
        usernameTextAdin = findViewById(R.id.username);
        adminPager = findViewById(R.id.adminMenus);
    }

    private void initUserView() {
        //userview
        root_relative = findViewById(R.id.root_relative);
        user_linear = findViewById(R.id.user_linear);
        messageImg = findViewById(R.id.messageImg);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        user_viewsmenu = findViewById(R.id.user_viewsmenu);
        guessImageAdmin = findViewById(R.id.avatar);
        viewPager = findViewById(R.id.userMenus);
        newhome = findViewById(R.id.newhome);
        guessviewpager = findViewById(R.id.guessviewpager);
        drawerToggle2 = findViewById(R.id.newtoggle);
        userEmailAdmin = findViewById(R.id.email);
        usernameTextAdin = findViewById(R.id.username);
        home = findViewById(R.id.home);
        privacy = findViewById(R.id.favorites);

    }

    private void initDraWtoggle() {
        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.maroon2));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white2));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
}

    private void initClickers() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.messageImg) {
                    Intent intent = new Intent(getApplicationContext(), User_list.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.messageImg2) {
                    Intent intent = new Intent(getApplicationContext(), UserChat.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (v.getId() == R.id.eventMessageimg) {
                    Intent intent = new Intent(getApplicationContext(), Event_userchat.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else if (v.getId() == R.id.home) {
                    Intent intent = new Intent(getApplicationContext(), newHome.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.addAdmin) {
                    Intent intent = new Intent(getApplicationContext(), Createadmin.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else if (v.getId() == R.id.addEventoraganizer) {
                    Intent intent = new Intent(getApplicationContext(), CreateEventorganizer.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else if (v.getId() == R.id.adminprofile) {
                    Dialog dialog = new Dialog();
                    dialog.updateAdminProfile(newHome.this,guessImageAdmin,userEmailAdmin,usernameTextAdin,phoneAdmin,fullnameAdmin,addressUserAdmin,ageAdmin,lenght);
                } else if (v.getId() == R.id.logoutAdmin) {
                    Dialog dialog = new Dialog();
                    dialog.logout(newHome.this);
                } else if (v.getId() == R.id.service_manage) {
                    Intent intent = new Intent(getApplicationContext(), MyservicePrice.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.admin_Calendar) {
                    Intent intent = new Intent(getApplicationContext(), CalendarAdmin.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (v.getId() == R.id.eventsProfile) {
                    Dialog dialog = new Dialog();
                    dialog.updateEventProfile(newHome.this,guessImageAdmin,userEmailAdmin,usernameTextAdin,phoneAdmin,fullnameAdmin,addressUserAdmin,ageAdmin,lenght);

                } else if (v.getId() == R.id.update) {
                    Intent intent = new Intent(getApplicationContext(), updateActivity.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.setSuperAdmin) {
                    Intent intent = new Intent(getApplicationContext(), set_superAdmin.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.setEventAdmin) {
                    Intent intent = new Intent(getApplicationContext(), setEvent_admin.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.event_bell) {
                    Intent intent = new Intent(getApplicationContext(), history_book_event.class);
                    startActivity(intent);
                } else if (v.getId() == R.id.bell) {
                    Intent intent = new Intent(getApplicationContext(), history_book.class);
                    startActivity(intent);
                }
            }
        };

    idlisterners(clickListener);
}
    private void idlisterners(View.OnClickListener clickListener) {
        findViewById(R.id.messageImg).setOnClickListener(clickListener);
        findViewById(R.id.messageImg2).setOnClickListener(clickListener);
        findViewById(R.id.home).setOnClickListener(clickListener);
        findViewById(R.id.addAdmin).setOnClickListener(clickListener);
        findViewById(R.id.adminprofile).setOnClickListener(clickListener);
        findViewById(R.id.logoutAdmin).setOnClickListener(clickListener);
        findViewById(R.id.service_manage).setOnClickListener(clickListener);
        findViewById(R.id.admin_Calendar).setOnClickListener(clickListener);
        findViewById(R.id.addEventoraganizer).setOnClickListener(clickListener);
        findViewById(R.id.eventsProfile).setOnClickListener(clickListener);
        findViewById(R.id.eventMessageimg).setOnClickListener(clickListener);
        findViewById(R.id.update).setOnClickListener(clickListener);
        findViewById(R.id.setSuperAdmin).setOnClickListener(clickListener);
        findViewById(R.id.setEventAdmin).setOnClickListener(clickListener);
        findViewById(R.id.event_bell).setOnClickListener(clickListener);
        findViewById(R.id.bell).setOnClickListener(clickListener);
    }


    private void loadUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userEmail = currentUser.getEmail();
            adminRef = FirebaseDatabase.getInstance().getReference("ADMIN").child(userId);
            guessRef = FirebaseDatabase.getInstance().getReference("Guess").child(userId);
            serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(userId);
            events = FirebaseDatabase.getInstance().getReference("Events").child(userId);

            new Handler().postDelayed(() -> {
                adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Usermodel user = dataSnapshot.getValue(Usermodel.class);
                            initAdminSaveData(user.getUsername(),user.getEmail(),user.getImage(),user.getPhone(),
                                    user.getName(),user.getAddress(),user.getAge(),user.getLengthOfService());
                            startService(new Intent(newHome.this, MessageNotificationService.class));
                            requestPermissions2();
                            showNotiff();
                            boolean isSuperAdmin = dataSnapshot.hasChild("isSuperAdmin") && Boolean.TRUE.equals(dataSnapshot.child("isSuperAdmin").getValue(Boolean.class));
                            if (isSuperAdmin) {
                                setSuperAdmin.setVisibility(View.VISIBLE);
                                createadmin.setVisibility(View.VISIBLE);
                                update.setVisibility(View.VISIBLE);
                            } else {
                                setSuperAdmin.setVisibility(View.GONE);
                                createadmin.setVisibility(View.GONE);
                                update.setVisibility(View.GONE);
                            }

                            lenght = user.getLengthOfService();

                            serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                                    if (!serviceSnapshot.exists()) {
//                                        UserProviderDialog serviceDialog = new UserProviderDialog();
//                                        serviceDialog.serviceDialog(newHome.this);
                                    }
                                    boolean hasAddress = dataSnapshot.hasChild("address");
                                    boolean hasAge = dataSnapshot.hasChild("age");

                                    if (hasAddress && hasAge) {
                                        String name = user.getName();
                                        storeUserInDatabase(name);
                                        updateUserUI(user, true,false,false);

                                        initAdminBook();
                                        initUserInfo_admin(user.getImage(),user.getUsername(),user.getAddress(),user.getEmail());
                                    } else {
                                        if (!hasAge) {
                                            Intent startAgeService = new Intent(getApplicationContext(), Ageandservice.class);
                                            startActivity(startAgeService);
                                            finish();
                                            SavedUserType("ADMIN");
                                        } else if (!hasAddress) {
                                            Intent startMapSelect = new Intent(getApplicationContext(), MapSelectActivity.class);
                                            startActivity(startMapSelect);
                                            finish();
                                            SavedUserType("ADMIN");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        } else {
                            guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        boolean hasGender = dataSnapshot.hasChild("gender");
                                        boolean hasEmail = dataSnapshot.hasChild("email");
                                        Usermodel user = dataSnapshot.getValue(Usermodel.class);
                                        if (user != null) {
                                            String name = user.getName();
                                            guessImage = user.getImage();
                                            userEmail = user.getEmail();
                                            usernameText = user.getUsername();
                                            phone = user.getPhone();
                                            fullname = user.getName();
                                            addressUser = user.getAddress();
                                            age = user.getAge();
                                            initShowbook();
                                            savedMyProfile(guessImage, userEmail, usernameText, phone, fullname, addressUser,age);
                                            storeUserInDatabase(name);
                                            updateUserUI(user, false,false,true);
                                            SavedUserType("Guess");
                                            initUserInfo(user.getImage(),user.getUsername(),user.getEmail());
                                            if (!hasEmail) {

                                                guessRef.child("email").setValue(SPUtils.getInstance().getString(AppConstans.emailAuthenticaion)).addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Information saved successfully!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Failed to save data. Try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                SavedUserType("Guess");
                                            } else if (!hasGender) {
                                                Dialog genderDialog = new Dialog();
                                                genderDialog.genderDialog(newHome.this, userId);

                                            }
                                        }
                                    } else {
                                        events.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    requestPermissions2();
                                                    showNotiff();
                                                    Usermodel user = dataSnapshot.getValue(Usermodel.class);
                                                    boolean isSuperAdmin = dataSnapshot.hasChild("isSuperAdmin") && Boolean.TRUE.equals(dataSnapshot.child("isSuperAdmin").getValue(Boolean.class));
                                                    if (isSuperAdmin) {
                                                        lenght = user.getLengthOfService();
                                                        update.setVisibility(View.VISIBLE);
                                                        addEventoraganizer.setVisibility(View.VISIBLE);
                                                        setEventAdmin.setVisibility(View.VISIBLE);
                                                    }else{
                                                        update.setVisibility(View.GONE);
                                                        addEventoraganizer.setVisibility(View.GONE);
                                                        setEventAdmin.setVisibility(View.GONE);
                                                    }
                                                    updateUserUI(user, false,true,false);
                                                    SavedUserType("Events");
                                                    initEventbook();
                                                    initEventSaveData(user.getUsername(),user.getEmail(),user.getImage(),user.getPhone(),
                                                            user.getName(),user.getAddress(),user.getAge(),user.getLengthOfService());
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }, 1000);
        } else {
            loadDefault();
        }
    }

    private void showNotiff() {
        Dialog notification = new Dialog();
        notification.notiffDialog(newHome.this);
    }

    private void initEventbook() {
        startService(new Intent(this, MessageNotificationService.class));
        String badgenum = SPUtils.getInstance().getString(AppConstans.booknumEvent);
        Log.d("booknumEvent","Mybooknum: "+badgenum);
        if (badgenum == null) {
            event_badge.setText("0");
            return;
        }
        event_badge.setVisibility(View.VISIBLE);
        event_badge.setText(badgenum);
    }

    private void initShowGuide() {
            NewbieGuide.with(this)
                    .setLabel("Guide")
                    .setOnGuideChangedListener(new OnGuideChangedListener() {
                        @Override
                        public void onShowed(Controller controller) {

                        }

                        @Override
                        public void onRemoved(Controller controller) {
                            showMore();
                        }
                    })
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(bell, HighLight.Shape.ROUND_RECTANGLE, 1)
                            .setLayoutRes(R.layout.bell_guide)
                    )
                    .show();
    }




    private void initAdminSaveData(String username, String email, String image, String phone, String name, String address, String age, String lengthOfService) {
        SPUtils.getInstance().put(AppConstans.AdminUsername,username);
        SPUtils.getInstance().put(AppConstans.Adminemail,email);
        SPUtils.getInstance().put(AppConstans.userEmail, userEmail);
        SPUtils.getInstance().put(AppConstans.AdminImage,image);
        SPUtils.getInstance().put(AppConstans.AdminPhone,phone);
        SPUtils.getInstance().put(AppConstans.AdminFullname,name);
        SPUtils.getInstance().put(AppConstans.AdminAdress,address);
        SPUtils.getInstance().put(AppConstans.AdminAge,age);
        SPUtils.getInstance().put(AppConstans.AdminLenght,lengthOfService);
    }

    private void initEventSaveData(String username, String email, String image, String phone, String name, String address, String age, String lengthOfService) {
        SPUtils.getInstance().put(AppConstans.EventUsername,username);
        SPUtils.getInstance().put(AppConstans.Eventemail,email);
        SPUtils.getInstance().put(AppConstans.EventImage,image);
        SPUtils.getInstance().put(AppConstans.userEmail, userEmail);
        SPUtils.getInstance().put(AppConstans.EventPhone,phone);
        SPUtils.getInstance().put(AppConstans.EventFullname,name);
        SPUtils.getInstance().put(AppConstans.EventAdress,address);
        SPUtils.getInstance().put(AppConstans.EventAge,age);
        SPUtils.getInstance().put(AppConstans.EventLenght,lengthOfService);

        List<Account_manager_admin> accountList_admin = Account_manager_admin.getAccounts();
        boolean exists = false;
        for (Account_manager_admin account : accountList_admin) {
            if (account.getEmail().equals(email)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            accountList_admin.add(new Account_manager_admin(SPUtils.getInstance().getString(AppConstans.passwordkey_admin), email, image, username));
            Account_manager_admin.saveAccounts(accountList_admin);
        }


    }

    private void initUserInfo_admin(String image, String usernamedata, String address, String email) {
        username_event = findViewById(R.id.username_event);
        location_address_event = findViewById(R.id.location_address_admin);
        avatar = findViewById(R.id.avatar_event);
        avatar2 = findViewById(R.id.avatar2);
        username = findViewById(R.id.username2);
        email_event = findViewById(R.id.email2);
        email3 = findViewById(R.id.email);
        email3.setVisibility(View.GONE);
        if (!newHome.this.isFinishing() && !newHome.this.isDestroyed()) {
            if (image != null && !image.isEmpty()) {
                RequestOptions requestOptions = new RequestOptions().circleCrop();
                Glide.with(newHome.this)
                        .load(image)
                        .apply(requestOptions)
                        .into(avatar);
            } else {
                avatar.setImageResource(R.mipmap.man);
            }
        }

        if (!newHome.this.isFinishing() && !newHome.this.isDestroyed()) {
            if (image != null && !image.isEmpty()) {
                RequestOptions requestOptions = new RequestOptions().circleCrop();
                Glide.with(this)
                        .load(image)
                        .apply(requestOptions)
                        .into(avatar2);
            } else {
                avatar2.setImageResource(R.mipmap.man);
            }
        }
        username_event.setText(usernamedata);
        email_event.setText(email);
        username.setText(usernamedata);

        List<Account_manager_admin> accountList_admin = Account_manager_admin.getAccounts();
        boolean exists = false;
        for (Account_manager_admin account : accountList_admin) {
            if (account.getEmail().equals(email)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            accountList_admin.add(new Account_manager_admin(SPUtils.getInstance().getString(AppConstans.passwordkey_admin), email, image, usernamedata));
            Account_manager_admin.saveAccounts(accountList_admin);
        }
        fetchCurrentLocationAdmin();

    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentLocationAdmin() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(newHome.this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        String streetAndCity = getAddressFromLocation(location);
                        SPUtils.getInstance().put(AppConstans.homeAddress,streetAndCity);
                        location_address_event.setText(streetAndCity);
                    }
                });
    }

    private String extractStreetAndCity(String address) {
        if (address == null) return "";

        String[] parts = address.split(",");
        String street = parts.length > 0 ? parts[0].trim() : "";
        String city = parts.length > 1 ? parts[1].trim() : "";

        return street + "\n" + city;
    }

    private void initUserInfo_event(String image, String usernamedata, String address, String email) {
        username_event = findViewById(R.id.username_event);
        location_address_event = findViewById(R.id.location_address_event);
        avatar = findViewById(R.id.avatar_event);
        avatar2 = findViewById(R.id.avatar2);
        username = findViewById(R.id.username2);
        email_event = findViewById(R.id.email2);
        email3 = findViewById(R.id.email);
        email3.setVisibility(View.GONE);
        String streetAndCity = extractStreetAndCity(address);

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(avatar);
        } else {
            avatar.setImageResource(R.mipmap.man);
        }

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(avatar2);
        } else {
            avatar2.setImageResource(R.mipmap.man);
        }
        username_event.setText(usernamedata);
        email_event.setText(email);
        username.setText(usernamedata);

        fetchCurrentLocationEvent();

    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentLocationEvent() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(newHome.this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        String streetAndCity = getAddressFromLocation(location);
                        SPUtils.getInstance().put(AppConstans.homeAddress,streetAndCity);
                        location_address_event.setText(streetAndCity);
                    }
                });
    }

    private void initProfile(Usermodel user) {
        String image = user.getImage();
        guessImageAdmin.setTag(image);
        userEmailAdmin.setText(user.getEmail());
        ageAdmin.setText(user.getAge());
        addressUserAdmin.setText(user.getAddress());
        fullnameAdmin.setText(user.getName());
        usernameTextAdin.setText(user.getUsername());
        phoneAdmin.setText(user.getPhone());

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(getApplicationContext())
                    .load(image)
                    .apply(requestOptions)
                    .into(guessImageAdmin);
        } else {
            guessImageAdmin.setImageResource(R.drawable.baseline_person_24);
        }

    }


    private void savedMyProfile(String guessImage, String userEmail, String usernameText, String phone, String fullname, String addressUser,String age) {
        SPUtils.getInstance().put(AppConstans.guessImage, guessImage);
        SPUtils.getInstance().put(AppConstans.userEmail, userEmail);
        SPUtils.getInstance().put(AppConstans.emailAuthenticaion, userEmail);
        SPUtils.getInstance().put(AppConstans.usernameText, usernameText);
        SPUtils.getInstance().put(AppConstans.phone, phone);
        SPUtils.getInstance().put(AppConstans.fullname, fullname);
        SPUtils.getInstance().put(AppConstans.addressUser, addressUser);
        SPUtils.getInstance().put(AppConstans.age, age);

    }

    private void storeUserInDatabase(String name) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userRef.setValue(userData).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "User data stored successfully");
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to store user data: " + e.getMessage());
        });
    }
    private void SavedUserType(String userType) {
        SPUtils.getInstance().put(AppConstans.USERTYPE, userType);

    }
    private void updateUserUI(Usermodel user, boolean isAdmin,boolean isEvent,boolean Client) {
        usernameTextAdin.setVisibility(View.VISIBLE);
        userEmailAdmin.setVisibility(View.VISIBLE);
        if (isAdmin) {
            isGuess = false;
            default_menu.setVisibility(View.GONE);
            event_bell.setVisibility(View.GONE);
            event_badge.setVisibility(View.GONE);
            adminbell.setVisibility(View.VISIBLE);
            badge_count_admin.setVisibility(View.VISIBLE);
            sidenav.setVisibility(View.VISIBLE);
            admin_menus.setVisibility(View.VISIBLE);
            admin_linear.setVisibility(View.VISIBLE);
            event_linear.setVisibility(View.GONE);
            event_bottomnav.setVisibility(View.GONE);
            adminBottom.setVisibility(View.VISIBLE);
            user_viewsmenu.setVisibility(View.GONE);
            user_linear.setVisibility(View.GONE);
            adminviewpager.setVisibility(View.VISIBLE);
            updateAdmin.setVisibility(View.VISIBLE);
            menu2.setVisibility(View.VISIBLE);
            createacc.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            eventPager.setVisibility(View.GONE);
            event_viewpager.setVisibility(View.GONE);
            adminPager.setVisibility(View.VISIBLE);
            //drawviews
            home.setVisibility(View.GONE);
            privacy.setVisibility(View.GONE);
        }else if (isEvent){
            isGuess = false;
            default_menu.setVisibility(View.GONE);
            event_bell.setVisibility(View.VISIBLE);
            event_badge.setVisibility(View.VISIBLE);
            adminbell.setVisibility(View.GONE);
            badge_count_admin.setVisibility(View.GONE);
            setSuperAdmin.setVisibility(View.GONE);
            selectortab_event.setVisibility(View.GONE);
            adminviewpager.setVisibility(View.GONE);
            admin_menus.setVisibility(View.VISIBLE);
            updateAdmin.setVisibility(View.GONE);
            menu2.setVisibility(View.GONE);
            event_menu.setVisibility(View.VISIBLE);
            createacc.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            sidenav.setVisibility(View.VISIBLE);
            adminPager.setVisibility(View.GONE);
            usernameTextAdin.setVisibility(View.VISIBLE);
            userEmailAdmin.setVisibility(View.VISIBLE);
            eventsProfile.setVisibility(View.VISIBLE);
            eventPager.setVisibility(View.VISIBLE);
            event_viewpager.setVisibility(View.VISIBLE);
            user_linear.setVisibility(View.VISIBLE);
            home.setVisibility(View.GONE);
            privacy.setVisibility(View.GONE);
            event_linear.setVisibility(View.VISIBLE);
            event_bottomnav.setVisibility(View.VISIBLE);
            initProfile(user);
            initUserInfo_event(user.getImage(),user.getUsername(),user.getAddress(),user.getEmail());
        }else if (Client){
            isGuess = true;
            initShowGuide();
            default_menu.setVisibility(View.GONE);
            event_bell.setVisibility(View.GONE);
            event_badge.setVisibility(View.GONE);
            adminbell.setVisibility(View.GONE);
            badge_count_admin.setVisibility(View.GONE);
            setSuperAdmin.setVisibility(View.GONE);
            sidenav.setVisibility(View.VISIBLE);
            newhome.setVisibility(View.VISIBLE);
            admin_linear.setVisibility(View.GONE);
            adminPager.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            admin_menus.setVisibility(View.GONE);
            event_linear.setVisibility(View.GONE);
            event_bottomnav.setVisibility(View.GONE);
            user_viewsmenu.setVisibility(View.VISIBLE);
            user_linear.setVisibility(View.VISIBLE);
            eventPager.setVisibility(View.GONE);
            event_viewpager.setVisibility(View.GONE);
            adminviewpager.setVisibility(View.GONE);
            guessviewpager.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            //drawviews
            home.setVisibility(View.VISIBLE);
            privacy.setVisibility(View.VISIBLE);
        }

        String image = user.getImage();
        guessImageAdmin.setTag(image);
        userEmailAdmin.setText(user.getEmail());
        ageAdmin.setText(user.getAge());
        addressUserAdmin.setText(user.getAddress());
        fullnameAdmin.setText(user.getName());
        usernameTextAdin.setText(user.getUsername());
        phoneAdmin.setText(user.getPhone());

        if (image != null && !image.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions().circleCrop();
            Glide.with(getApplicationContext())
                    .load(image)
                    .apply(requestOptions)
                    .into(guessImageAdmin);
        } else {
            guessImageAdmin.setImageResource(R.drawable.baseline_person_24);
        }

    }

    private void loadDefault() {
        //user
        newhome.setVisibility(View.GONE);
        guessviewpager.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        //admin
        menu2.setVisibility(View.GONE);
        adminMenu.setVisibility(View.GONE);
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }
    private void openNotificationSettings() {
        Dialog notification = new Dialog();
        notification.notiffDialog(this);
    }
    private void showMore() {
        if (hasRequiredPermissions()) {
            showNewbieGuide();
        } else {
            requestPermissions();
        }
    }

    private boolean hasRequiredPermissions() {
        openNotificationSettings();
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions2() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                100
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                100
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                showNewbieGuide();
            } else {
                Toast.makeText(this, "Permissions are required to continue.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNewbieGuide() {
        if(isGuess){
            NewbieGuide.with(this)
                    .setLabel("guideNew")
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(bottomNavigationView, HighLight.Shape.ROUND_RECTANGLE, 1)
                            .setLayoutRes(R.layout.bottom_guide)
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(messageImg, HighLight.Shape.RECTANGLE, 1)
                            .setLayoutRes(R.layout.chat_guide)
                    )
                    .show();
        }

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}
