package com.hair.booking.activity.Application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hair.booking.activity.tools.Utils.SPUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Account_manager_admin {

    private static final String PASSWORD_KEY_admin = "password";
    private static final String EMAIL_KEY_admin = "email";
    private static final String IMAGE_URL_KEY_admin = "image_url";
    private static final String USERNAME_KEY_admin = "username";
    private static final String ACCOUNTS_LIST_KEY_admin = "accounts_list";

    private String password;
    private String email;
    private String imageUrl;
    private String username;

    public Account_manager_admin(String password, String email, String imageUrl, String username) {
        this.password = password;
        this.email = email;
        this.imageUrl = imageUrl;
        this.username = username;
        SPUtils.getInstance().put(PASSWORD_KEY_admin, password);
        SPUtils.getInstance().put(EMAIL_KEY_admin, email);
        SPUtils.getInstance().put(IMAGE_URL_KEY_admin, imageUrl);
        SPUtils.getInstance().put(USERNAME_KEY_admin, username);
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void saveAccounts(List<Account_manager_admin> accounts) {
        Gson gson = new Gson();
        String json = gson.toJson(accounts);
        SPUtils.getInstance().put(ACCOUNTS_LIST_KEY_admin, json);
    }

    public static List<Account_manager_admin> getAccounts() {
        String json = SPUtils.getInstance().getString(ACCOUNTS_LIST_KEY_admin, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Account_manager_admin>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
