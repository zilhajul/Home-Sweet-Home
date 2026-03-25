package com.example.homesweethome.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.example.homesweethome.model.User;

public class SessionManager {

    private static final String PREF_NAME = "house_rent_session";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER = "logged_user";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    // ---- Save full session after login ----
    public void saveSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    // ---- Save selected role before login ----
    public void saveSelectedRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    // ---- Getters ----
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson == null) return null;
        return gson.fromJson(userJson, User.class);
    }

    public boolean isLandlord() {
        return "landlord".equalsIgnoreCase(getRole());
    }

    // ---- Clear session on logout ----
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
