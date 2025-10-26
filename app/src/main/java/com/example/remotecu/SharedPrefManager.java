package com.example.remotecu;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedPrefManager {
    private static final String PREF_NAME = "ButtonPrefs";
    private static final String PREF_NAME_V2 = "ButtonPrefsV2"; // New version with full RemoteButton support
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences sharedPreferencesV2;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences.Editor editorV2;

    // Constructor
    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferencesV2 = context.getSharedPreferences(PREF_NAME_V2, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editorV2 = sharedPreferencesV2.edit();
    }

    // Save button and IR code (legacy method for backward compatibility)
    public void saveButton(String buttonName, String irCode) {
        editor.putString(buttonName, irCode);
        editor.apply(); // Apply changes
    }

    // Save RemoteButton object (new method)
    public void saveRemoteButton(RemoteButton button) {
        editorV2.putString(button.getName(), button.toJson());
        editorV2.apply();
    }

    // Get RemoteButton by name
    public RemoteButton getRemoteButton(String buttonName) {
        String json = sharedPreferencesV2.getString(buttonName, null);
        if (json != null) {
            return RemoteButton.fromJson(json);
        }
        return null;
    }

    // Get all RemoteButtons
    public List<RemoteButton> getAllRemoteButtons() {
        List<RemoteButton> buttons = new ArrayList<>();
        Map<String, ?> allEntries = sharedPreferencesV2.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = (String) entry.getValue();
            RemoteButton button = RemoteButton.fromJson(json);
            if (button != null) {
                buttons.add(button);
            }
        }
        return buttons;
    }

    // Update existing button
    public void updateRemoteButton(String oldName, RemoteButton newButton) {
        // Remove old entry if name changed
        if (!oldName.equals(newButton.getName())) {
            editorV2.remove(oldName);
        }
        // Save new button
        saveRemoteButton(newButton);
    }

    // Remove RemoteButton
    public void removeRemoteButton(String buttonName) {
        editorV2.remove(buttonName);
        editorV2.apply();
    }

    // Check if a button name already exists (legacy)
    public boolean isButtonNameDuplicate(String buttonName) {
        return sharedPreferences.contains(buttonName) || sharedPreferencesV2.contains(buttonName);
    }

    // Remove a button and its IR code (legacy)
    public void removeButton(String buttonName) {
        editor.remove(buttonName);
        editor.apply(); // Apply changes
        // Also remove from V2
        editorV2.remove(buttonName);
        editorV2.apply();
    }

    // Get all saved buttons and IR codes (legacy)
    public Map<String, ?> getAllButtons() {
        return sharedPreferences.getAll();
    }

    // Migrate old buttons to new format
    public void migrateToV2() {
        Map<String, ?> oldButtons = getAllButtons();
        for (Map.Entry<String, ?> entry : oldButtons.entrySet()) {
            String name = entry.getKey();
            String irCode = (String) entry.getValue();
            RemoteButton button = new RemoteButton(name, irCode);
            saveRemoteButton(button);
        }
    }

    // Clear all data in SharedPreferences
    public void clearAll() {
        editor.clear();
        editor.apply();
        editorV2.clear();
        editorV2.apply();
    }
}
