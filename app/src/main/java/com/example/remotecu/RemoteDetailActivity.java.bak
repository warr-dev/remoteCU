package com.example.remotecu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class RemoteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PROFILE_ID = "profile_id";

    private String profileId;
    private RemoteProfile currentProfile;
    private SharedPrefManager sharedPrefManager;

    private TextView remoteIcon;
    private TextView remoteName;
    private TextView remoteButtonCount;
    private FrameLayout buttonContainer;
    private LinearLayout emptyButtonsState;
    private FloatingActionButton fab;
    private Button editModeButton;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_detail);

        sharedPrefManager = new SharedPrefManager(this);

        // Get profile ID from intent
        profileId = getIntent().getStringExtra(EXTRA_PROFILE_ID);
        if (profileId == null) {
            Toast.makeText(this, "Error: No profile selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load profile
        currentProfile = sharedPrefManager.getProfileById(profileId);
        if (currentProfile == null) {
            Toast.makeText(this, "Error: Profile not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        remoteIcon = findViewById(R.id.remoteIcon);
        remoteName = findViewById(R.id.remoteName);
        remoteButtonCount = findViewById(R.id.remoteButtonCount);
        buttonContainer = findViewById(R.id.buttonLayoutView); // Updated to new layout ID
        emptyButtonsState = findViewById(R.id.emptyButtonsState);
        fab = findViewById(R.id.fab);
        editModeButton = findViewById(R.id.layoutModeButton); // Updated to new layout ID

        // Setup toolbar
        toolbar.setTitle(currentProfile.getName());
        toolbar.setNavigationOnClickListener(v -> finish());

        // Edit Mode button click listener
        editModeButton.setOnClickListener(v -> toggleEditMode());

        // Display profile info
        updateProfileDisplay();

        // Load buttons
        loadButtons();

        // FAB click listener
        fab.setOnClickListener(v -> showAddButtonDialog());
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        // Update button text and appearance
        if (isEditMode) {
            editModeButton.setText("✓ Done");
            editModeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Edit Mode ON - Drag buttons to reposition", Toast.LENGTH_SHORT).show();
        } else {
            editModeButton.setText("✏️ Edit");
            editModeButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            Toast.makeText(this, "Edit Mode OFF", Toast.LENGTH_SHORT).show();
        }

        // Update all buttons' edit mode state
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            View child = buttonContainer.getChildAt(i);
            if (child instanceof DraggableButton) {
                DraggableButton button = (DraggableButton) child;
                button.setEditMode(isEditMode);
            }
        }
    }

    private void updateProfileDisplay() {
        remoteIcon.setText(currentProfile.getIcon());
        remoteName.setText(currentProfile.getName());
        int count = currentProfile.getButtons().size();
        remoteButtonCount.setText(count + " button" + (count != 1 ? "s" : ""));
    }

    private void loadButtons() {
        if (currentProfile.getButtons().isEmpty()) {
            emptyButtonsState.setVisibility(View.VISIBLE);
            buttonContainer.setVisibility(View.GONE);
        } else {
            emptyButtonsState.setVisibility(View.GONE);
            buttonContainer.setVisibility(View.VISIBLE);

            // Clear existing buttons
            buttonContainer.removeAllViews();

            // Add buttons with their saved positions
            for (RemoteButton button : currentProfile.getButtons()) {
                createDynamicButton(button, true);
            }
        }
    }

    private void showAddButtonDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_button, null);
        TextInputEditText nameInput = dialogView.findViewById(R.id.editButtonName);
        TextInputEditText irCodeInput = dialogView.findViewById(R.id.editIrCode);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String irCode = irCodeInput.getText().toString().trim();

            if (name.isEmpty() || irCode.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create button
            RemoteButton newButton = new RemoteButton(name, irCode);
            currentProfile.addButton(newButton);

            // Save profile
            sharedPrefManager.saveProfile(currentProfile);

            // Update UI
            loadButtons();
            updateProfileDisplay();

            dialog.dismiss();
            Toast.makeText(this, "Button added", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void createDynamicButton(RemoteButton buttonData, boolean isLoading) {
        DraggableButton newButton = new DraggableButton(this);

        // Set icon on button face, name as tooltip
        newButton.setIconAndName(buttonData.getIcon(), buttonData.getName());

        newButton.setTag(buttonData); // Store RemoteButton object in tag
        newButton.setEditMode(isEditMode); // Set current edit mode state

        // Set button size - compact like real remote buttons
        int buttonWidth = 160; // Compact width for icon-only buttons
        int buttonHeight = 120; // Compact height
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                buttonWidth,
                buttonHeight
        );
        newButton.setLayoutParams(params);

        // Set saved position if available, otherwise auto-position at top
        if (buttonData.getPosX() >= 0 && buttonData.getPosY() >= 0) {
            newButton.setX(buttonData.getPosX());
            newButton.setY(buttonData.getPosY());
        } else {
            // Auto-position new button at top in a grid layout
            buttonContainer.post(() -> {
                float[] position = calculateNextButtonPosition();
                newButton.setX(position[0]);
                newButton.setY(position[1]);
                // Save the auto-calculated position
                buttonData.setPosX(position[0]);
                buttonData.setPosY(position[1]);
                saveButtonPositions();
            });
        }

        // Set button click listener (only works when NOT in edit mode)
        newButton.setOnClickListener(v -> {
            if (!isEditMode) {
                Toast.makeText(this, "Sending IR: " + buttonData.getIrCode(), Toast.LENGTH_SHORT).show();
                // TODO: Send IR signal here
            }
        });

        // Save position when dragged (only in edit mode)
        newButton.setOnPositionChangedListener((x, y) -> {
            buttonData.setPosX(x);
            buttonData.setPosY(y);
            saveButtonPositions();
        });

        buttonContainer.addView(newButton);
    }

    private void saveButtonPositions() {
        // Update all button positions in the profile
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            View child = buttonContainer.getChildAt(i);
            if (child instanceof DraggableButton) {
                DraggableButton button = (DraggableButton) child;
                RemoteButton buttonData = (RemoteButton) button.getTag();
                if (buttonData != null) {
                    buttonData.setPosX(button.getX());
                    buttonData.setPosY(button.getY());
                }
            }
        }
        // Save profile
        sharedPrefManager.saveProfile(currentProfile);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save profile before leaving
        if (currentProfile != null) {
            sharedPrefManager.saveProfile(currentProfile);
        }
    }

    /**
     * Calculate the next available position for a new button in a grid layout
     * Positions buttons from top-left, moving right, then down to next row
     */
    private float[] calculateNextButtonPosition() {
        final int BUTTON_WIDTH = 160; // Compact icon button width
        final int BUTTON_HEIGHT = 120; // Compact icon button height
        final int MARGIN = 16; // Margin between buttons
        final int TOP_MARGIN = 80; // Top margin to avoid toolbar

        int containerWidth = buttonContainer.getWidth();
        if (containerWidth == 0) {
            containerWidth = 1080; // Default width if not yet measured
        }

        // Calculate how many buttons fit per row
        int buttonsPerRow = Math.max(1, (containerWidth - MARGIN) / (BUTTON_WIDTH + MARGIN));

        // Count existing buttons
        int existingButtons = 0;
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            if (buttonContainer.getChildAt(i) instanceof DraggableButton) {
                existingButtons++;
            }
        }

        // Calculate row and column for new button
        int row = existingButtons / buttonsPerRow;
        int col = existingButtons % buttonsPerRow;

        // Calculate x and y positions
        float x = MARGIN + col * (BUTTON_WIDTH + MARGIN);
        float y = TOP_MARGIN + row * (BUTTON_HEIGHT + MARGIN);

        return new float[]{x, y};
    }
}
