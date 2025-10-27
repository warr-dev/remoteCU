package com.example.remotecu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivityNew extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener {

    private SharedPrefManager sharedPrefManager;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private LinearLayout emptyState;
    private FloatingActionButton fab;

    private List<RemoteProfile> profiles;
    private String selectedIcon = "📺"; // Default icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new SharedPrefManager(this);

        // Initialize views
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        fab = findViewById(R.id.fab);

        // Setup RecyclerView
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadProfiles();

        // FAB click listener
        fab.setOnClickListener(v -> showCreateProfileDialog());

        // Optional: Scan button (for later)
        findViewById(R.id.scanButton).setOnClickListener(v -> {
            Toast.makeText(this, "BLE Scan feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles(); // Refresh list when returning from detail activity
    }

    private void loadProfiles() {
        profiles = sharedPrefManager.getAllProfiles();

        if (profiles.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            profilesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            profilesRecyclerView.setVisibility(View.VISIBLE);

            if (profileAdapter == null) {
                profileAdapter = new ProfileAdapter(profiles, this);
                profileAdapter.setActiveProfileId(sharedPrefManager.getActiveProfileId());
                profilesRecyclerView.setAdapter(profileAdapter);
            } else {
                profileAdapter.updateProfiles(profiles);
                profileAdapter.setActiveProfileId(sharedPrefManager.getActiveProfileId());
            }
        }
    }

    private void showCreateProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_profile, null);
        TextInputEditText profileNameInput = dialogView.findViewById(R.id.profileNameInput);
        TextView selectedIconPreview = dialogView.findViewById(R.id.selectedIconPreview);

        // Reset selected icon
        selectedIcon = "📺";
        selectedIconPreview.setText(selectedIcon);

        // Setup icon click listeners
        int[] iconIds = {
                R.id.icon_tv, R.id.icon_ac, R.id.icon_heater, R.id.icon_speaker,
                R.id.icon_light, R.id.icon_gaming, R.id.icon_antenna,
                R.id.icon_media, R.id.icon_door, R.id.icon_other
        };

        for (int iconId : iconIds) {
            TextView iconView = dialogView.findViewById(iconId);
            iconView.setOnClickListener(v -> {
                selectedIcon = (String) v.getTag();
                selectedIconPreview.setText(selectedIcon);
                // Visual feedback
                v.setAlpha(0.5f);
                v.postDelayed(() -> v.setAlpha(1.0f), 200);
            });
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancelProfile).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String name = profileNameInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a profile name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new profile
            RemoteProfile newProfile = new RemoteProfile(name, selectedIcon);
            sharedPrefManager.saveProfile(newProfile);

            // Set as active if it's the only profile
            if (profiles.isEmpty()) {
                sharedPrefManager.setActiveProfile(newProfile.getId());
            }

            // Refresh list
            loadProfiles();

            dialog.dismiss();
            Toast.makeText(this, "Remote created: " + name, Toast.LENGTH_SHORT).show();

            // Open the new profile immediately
            openProfileDetail(newProfile);
        });

        dialog.show();
    }

    @Override
    public void onProfileClick(RemoteProfile profile) {
        // Set as active profile
        sharedPrefManager.setActiveProfile(profile.getId());

        // Open detail activity
        openProfileDetail(profile);
    }

    @Override
    public void onProfileMenuClick(RemoteProfile profile, View view) {
        // Show context menu for edit/delete
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(profile.getIcon() + " " + profile.getName())
                .setItems(new String[]{"Edit Layout", "Delete"}, (dialogInterface, i) -> {
                    if (i == 0) {
                        // Edit - Open edit screen
                        Intent intent = new Intent(this, RemoteEditActivity.class);
                        intent.putExtra("PROFILE_ID", profile.getId());
                        startActivity(intent);
                    } else {
                        // Delete
                        showDeleteConfirmation(profile);
                    }
                })
                .create();
        dialog.show();
    }

    private void showDeleteConfirmation(RemoteProfile profile) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Remote?")
                .setMessage("Are you sure you want to delete \"" + profile.getName() + "\"? This will remove all its buttons.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    sharedPrefManager.deleteProfile(profile.getId());
                    loadProfiles();
                    Toast.makeText(this, "Remote deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openProfileDetail(RemoteProfile profile) {
        Intent intent = new Intent(this, RemoteDetailActivity.class);
        intent.putExtra(RemoteDetailActivity.EXTRA_PROFILE_ID, profile.getId());
        startActivity(intent);
    }
}
