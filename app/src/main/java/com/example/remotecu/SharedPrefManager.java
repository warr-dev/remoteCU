package com.example.remotecu;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPrefManager {
    private static final String PREF_NAME = "ButtonPrefs";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Constructor
    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save button and IR code
    public void saveButton(String buttonName, String irCode) {
        editor.putString(buttonName, irCode);
        editor.apply(); // Apply changes
    }

    // Check if a button name already exists
    public boolean isButtonNameDuplicate(String buttonName) {
        return sharedPreferences.contains(buttonName);
    }

    // Remove a button and its IR code
    public void removeButton(String buttonName) {
        editor.remove(buttonName);
        editor.apply(); // Apply changes
    }

    // Get all saved buttons and IR codes
    public Map<String, ?> getAllButtons() {
        return sharedPreferences.getAll();
    }

    // Clear all data in SharedPreferences
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
