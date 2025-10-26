package com.example.remotecu;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedPrefManager {
    private static final String PREF_NAME = "ButtonPrefs";
    private static final String PREF_NAME_V2 = "ButtonPrefsV2"; // New version with full RemoteButton support
    private static final String PREF_PROFILES = "RemoteProfiles"; // Profile storage
    private static final String KEY_PROFILES_LIST = "profiles_list";
    private static final String KEY_ACTIVE_PROFILE = "active_profile_id";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences sharedPreferencesV2;
    private final SharedPreferences profilePreferences;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences.Editor editorV2;
    private final SharedPreferences.Editor profileEditor;

    // Constructor
    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferencesV2 = context.getSharedPreferences(PREF_NAME_V2, Context.MODE_PRIVATE);
        profilePreferences = context.getSharedPreferences(PREF_PROFILES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editorV2 = sharedPreferencesV2.edit();
        profileEditor = profilePreferences.edit();

        // Initialize default profile if none exists
        if (getAllProfiles().isEmpty()) {
            createDefaultProfile();
        }
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

    // ========== PROFILE MANAGEMENT ==========

    // Create default profile
    private void createDefaultProfile() {
        RemoteProfile defaultProfile = new RemoteProfile("My Remote", "📺");
        saveProfile(defaultProfile);
        setActiveProfile(defaultProfile.getId());
    }

    // Save profile
    public void saveProfile(RemoteProfile profile) {
        try {
            List<RemoteProfile> profiles = getAllProfiles();
            // Check if profile already exists and update it
            boolean found = false;
            for (int i = 0; i < profiles.size(); i++) {
                if (profiles.get(i).getId().equals(profile.getId())) {
                    profiles.set(i, profile);
                    found = true;
                    break;
                }
            }
            // If not found, add as new profile
            if (!found) {
                profiles.add(profile);
            }

            // Convert profiles list to JSON
            JSONArray jsonArray = new JSONArray();
            for (RemoteProfile p : profiles) {
                jsonArray.put(profileToJson(p));
            }

            profileEditor.putString(KEY_PROFILES_LIST, jsonArray.toString());
            profileEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get all profiles
    public List<RemoteProfile> getAllProfiles() {
        List<RemoteProfile> profiles = new ArrayList<>();
        try {
            String jsonString = profilePreferences.getString(KEY_PROFILES_LIST, null);
            if (jsonString != null) {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    profiles.add(profileFromJson(jsonObject));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    // Get profile by ID
    public RemoteProfile getProfileById(String profileId) {
        List<RemoteProfile> profiles = getAllProfiles();
        for (RemoteProfile profile : profiles) {
            if (profile.getId().equals(profileId)) {
                return profile;
            }
        }
        return null;
    }

    // Delete profile
    public void deleteProfile(String profileId) {
        List<RemoteProfile> profiles = getAllProfiles();
        profiles.removeIf(profile -> profile.getId().equals(profileId));

        try {
            JSONArray jsonArray = new JSONArray();
            for (RemoteProfile p : profiles) {
                jsonArray.put(profileToJson(p));
            }
            profileEditor.putString(KEY_PROFILES_LIST, jsonArray.toString());
            profileEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If deleted profile was active, set first profile as active
        if (profileId.equals(getActiveProfileId()) && !profiles.isEmpty()) {
            setActiveProfile(profiles.get(0).getId());
        }
    }

    // Set active profile
    public void setActiveProfile(String profileId) {
        profileEditor.putString(KEY_ACTIVE_PROFILE, profileId);
        profileEditor.apply();
    }

    // Get active profile ID
    public String getActiveProfileId() {
        return profilePreferences.getString(KEY_ACTIVE_PROFILE, null);
    }

    // Get active profile
    public RemoteProfile getActiveProfile() {
        String activeId = getActiveProfileId();
        if (activeId != null) {
            return getProfileById(activeId);
        }
        // Return first profile if no active set
        List<RemoteProfile> profiles = getAllProfiles();
        return profiles.isEmpty() ? null : profiles.get(0);
    }

    // Helper: Convert RemoteProfile to JSON
    private JSONObject profileToJson(RemoteProfile profile) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", profile.getId());
        json.put("name", profile.getName());
        json.put("icon", profile.getIcon());
        json.put("createdAt", profile.getCreatedAt());
        json.put("modifiedAt", profile.getModifiedAt());

        JSONArray buttonsArray = new JSONArray();
        for (RemoteButton button : profile.getButtons()) {
            buttonsArray.put(button.toJson());
        }
        json.put("buttons", buttonsArray);

        return json;
    }

    // Helper: Convert JSON to RemoteProfile
    private RemoteProfile profileFromJson(JSONObject json) throws JSONException {
        RemoteProfile profile = new RemoteProfile();
        profile.setId(json.getString("id"));
        profile.setName(json.getString("name"));
        profile.setIcon(json.getString("icon"));
        profile.setCreatedAt(json.getLong("createdAt"));
        profile.setModifiedAt(json.getLong("modifiedAt"));

        JSONArray buttonsArray = json.getJSONArray("buttons");
        List<RemoteButton> buttons = new ArrayList<>();
        for (int i = 0; i < buttonsArray.length(); i++) {
            String buttonJson = buttonsArray.getString(i);
            RemoteButton button = RemoteButton.fromJson(buttonJson);
            if (button != null) {
                buttons.add(button);
            }
        }
        profile.setButtons(buttons);

        return profile;
    }
}
