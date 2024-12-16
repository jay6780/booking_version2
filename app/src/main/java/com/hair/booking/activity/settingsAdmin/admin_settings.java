package com.hair.booking.activity.settingsAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.hair.booking.R;
import com.hair.booking.activity.Application.TinkerApplications;
import com.hair.booking.activity.MainPageActivity.newHome;
import com.hair.booking.activity.settingsGues.switch_account;
import com.hair.booking.activity.tools.Utils.AppConstans;
import com.hair.booking.activity.tools.Utils.SPUtils;

public class admin_settings extends AppCompatActivity {
    private Spinner languageSpinner;
    private String selectedLanguage;
    private ImageView back;
    private RelativeLayout rl1;
    String textvalue = SPUtils.getInstance().getString(AppConstans.selectLang);
    private String TAG = "Change_langact";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_langact);

        back = findViewById(R.id.back);
        rl1 = findViewById(R.id.relative_switch);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        changeStatusBarColor(getResources().getColor(R.color.maroon2));
        Log.d(TAG,"selectedLang: "+textvalue);

        languageSpinner = findViewById(R.id.languageSpinner);
        String[] languageOptions = {"Default", "English", "Filipino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        int selectedPosition = 0;
        for (int i = 0; i < languageOptions.length; i++) {
            if (languageOptions[i].equals(textvalue)) {
                selectedPosition = i;
                break;
            }
        }
        languageSpinner.setSelection(selectedPosition);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedLanguage = "Default";
                        break;
                    case 1:
                        selectedLanguage = "en";
                        break;
                    case 2:
                        selectedLanguage = "fil";
                        break;
                }

                SPUtils.getInstance().put(AppConstans.selectLang, selectedLanguage);

                if (!selectedLanguage.equals("Default")) {
                    ((TinkerApplications) getApplication()).updateLocale(selectedLanguage);
                    Intent intent = new Intent(admin_settings.this, newHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        back.setOnClickListener(view -> onBackPressed());
        rl1.setOnClickListener(view -> switchaccount());
    }

    private void switchaccount() {
      Intent swtichaccount = new Intent(getApplicationContext(), switch_account_admin.class);
      startActivity(swtichaccount);
    }

    private void changeStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }
}
