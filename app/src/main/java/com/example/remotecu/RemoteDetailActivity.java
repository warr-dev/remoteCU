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

        // Clear All button click listener
        Button clearAllButton = findViewById(R.id.clearAllButton);
        clearAllButton.setOnClickListener(v -> showClearAllDialog());

        // Edit Mode button click listener
        editModeButton.setOnClickListener(v -> toggleEditMode());

        // Display profile info
        updateProfileDisplay();

        // Load buttons
        loadButtons();

        // Long-press on button container to open edit screen
        buttonContainer.setOnLongClickListener(v -> {
            openEditScreen();
            return true;
        });

        // Hide all edit UI elements - this is now a view-only screen
        findViewById(R.id.appBarLayout).setVisibility(View.GONE);
        findViewById(R.id.remoteInfoCard).setVisibility(View.GONE);
        fab.hide();
        isEditMode = false; // Always in view mode
    }

    private void openEditScreen() {
        android.content.Intent intent = new android.content.Intent(this, RemoteEditActivity.class);
        intent.putExtra("PROFILE_ID", currentProfile.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile and buttons when returning from edit screen
        currentProfile = sharedPrefManager.getProfileById(currentProfile.getId());
        if (currentProfile != null) {
            loadButtons();
        }
    }

    private void showClearAllDialog() {
        if (currentProfile.getButtons().isEmpty()) {
            Toast.makeText(this, "No buttons to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Clear All Buttons")
                .setMessage("Are you sure you want to delete ALL " + currentProfile.getButtons().size() + " buttons?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    // Remove all buttons from container
                    buttonContainer.removeAllViews();
                    // Clear all buttons from profile
                    currentProfile.getButtons().clear();
                    // Save profile
                    sharedPrefManager.saveProfile(currentProfile);
                    // Update UI
                    updateProfileDisplay();
                    loadButtons();
                    Toast.makeText(this, "All buttons cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        // Update button text and appearance
        if (isEditMode) {
            editModeButton.setText("✓ Done");
            editModeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Edit Mode ON - Drag buttons to reposition or long-press to delete", Toast.LENGTH_SHORT).show();

            // Show toolbar and FAB in edit mode
            findViewById(R.id.appBarLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.remoteInfoCard).setVisibility(View.VISIBLE);
            fab.show();
        } else {
            editModeButton.setText("✏️ Edit");
            editModeButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            // Hide toolbar and FAB in normal mode for full-screen remote experience
            findViewById(R.id.appBarLayout).setVisibility(View.GONE);
            findViewById(R.id.remoteInfoCard).setVisibility(View.GONE);
            fab.hide();
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
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_button, null);
        LinearLayout templatePickerContainer = dialogView.findViewById(R.id.templatePickerContainer);
        LinearLayout iconPickerContainer = dialogView.findViewById(R.id.iconPickerContainer);
        TextInputEditText nameInput = dialogView.findViewById(R.id.buttonNameInput);
        TextInputEditText irCodeInput = dialogView.findViewById(R.id.irCodeInput);
        android.widget.RadioGroup sizeRadioGroup = dialogView.findViewById(R.id.buttonSizeRadioGroup);

        final String[] selectedIcon = {RemoteButton.AVAILABLE_ICONS[0]}; // Default to first icon

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Populate template buttons
        for (ButtonTemplate template : ButtonTemplate.COMMON_TEMPLATES) {
            Button templateBtn = new Button(this);
            templateBtn.setText(template.getIcon() + "\n" + template.getName());
            templateBtn.setTextSize(12f);
            templateBtn.setPadding(24, 16, 24, 16);
            templateBtn.setAllCaps(false);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            templateBtn.setLayoutParams(params);

            templateBtn.setOnClickListener(v -> {
                // Fill form with template data
                nameInput.setText(template.getName());
                selectedIcon[0] = template.getIcon();
                Toast.makeText(this, "Template selected: " + template.getName(), Toast.LENGTH_SHORT).show();
            });

            templatePickerContainer.addView(templateBtn);
        }

        // Populate icon picker
        for (String icon : RemoteButton.AVAILABLE_ICONS) {
            Button iconBtn = new Button(this);
            iconBtn.setText(icon);
            iconBtn.setTextSize(24f);
            iconBtn.setPadding(20, 20, 20, 20);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            iconBtn.setLayoutParams(params);

            iconBtn.setOnClickListener(v -> {
                selectedIcon[0] = icon;
                Toast.makeText(this, "Icon selected: " + icon, Toast.LENGTH_SHORT).show();
            });

            iconPickerContainer.addView(iconBtn);
        }

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.saveButton).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String irCode = irCodeInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter button name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected button size
            int selectedRadioId = sizeRadioGroup.getCheckedRadioButtonId();
            int width, height;
            if (selectedRadioId == R.id.sizeSmall) {
                width = 120;
                height = 90;
            } else if (selectedRadioId == R.id.sizeLarge) {
                width = 200;
                height = 150;
            } else { // Medium (default)
                width = 160;
                height = 120;
            }

            // Create button with icon and custom size
            RemoteButton newButton = new RemoteButton(name, irCode.isEmpty() ? "0x0000" : irCode);
            newButton.setIcon(selectedIcon[0]);
            newButton.setWidth(width);
            newButton.setHeight(height);
            currentProfile.addButton(newButton);

            // Save profile
            sharedPrefManager.saveProfile(currentProfile);

            // Update UI
            loadButtons();
            updateProfileDisplay();

            dialog.dismiss();
            Toast.makeText(this, "Button added: " + name, Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void createDynamicButton(RemoteButton buttonData, boolean isLoading) {
        DraggableButton newButton = new DraggableButton(this);

        // Set icon on button face, name as tooltip
        newButton.setIconAndName(buttonData.getIcon(), buttonData.getName());

        newButton.setTag(buttonData); // Store RemoteButton object in tag
        newButton.setEditMode(isEditMode); // Set current edit mode state

        // Get button size from buttonData, or use default if not set
        int buttonWidth = buttonData.getWidth() > 0 ? buttonData.getWidth() : 200; // Default width
        int buttonHeight = buttonData.getHeight() > 0 ? buttonData.getHeight() : 150; // Default height

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                buttonWidth,
                buttonHeight
        );
        newButton.setLayoutParams(params);

        // Automatically adjust icon size based on button size
        newButton.setButtonSize(buttonWidth, buttonHeight);

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

        // Long-press to delete button with confirmation (only in edit mode)
        newButton.setOnButtonDeleteListener(button -> {
            showDeleteButtonDialog(button, buttonData);
        });

        buttonContainer.addView(newButton);
    }

    private void showDeleteButtonDialog(DraggableButton button, RemoteButton buttonData) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Button")
                .setMessage("Delete \"" + buttonData.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from container
                    buttonContainer.removeView(button);
                    // Remove from profile
                    currentProfile.getButtons().remove(buttonData);
                    // Save profile
                    sharedPrefManager.saveProfile(currentProfile);
                    // Update UI
                    updateProfileDisplay();
                    Toast.makeText(this, "Button deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
        final int BUTTON_WIDTH = 200; // Icon button width
        final int BUTTON_HEIGHT = 150; // Icon button height
        final int MARGIN = 24; // Margin between buttons to account for shadows/elevation
        final int TOP_MARGIN = 16; // Top margin

        int containerWidth = buttonContainer.getWidth();
        if (containerWidth == 0) {
            containerWidth = 1080; // Default width if not yet measured
        }

        // Calculate how many buttons fit per row (accounting for total button space)
        int totalButtonSpace = BUTTON_WIDTH + MARGIN;
        int buttonsPerRow = Math.max(1, (containerWidth - MARGIN) / totalButtonSpace);

        // Count existing buttons that don't have saved positions
        int existingButtons = 0;
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            View child = buttonContainer.getChildAt(i);
            if (child instanceof DraggableButton) {
                RemoteButton buttonData = (RemoteButton) child.getTag();
                // Only count buttons that are in default positions or new
                if (buttonData != null && (buttonData.getPosX() < 0 || buttonData.getPosY() < 0)) {
                    existingButtons++;
                }
            }
        }

        // Calculate row and column for new button
        int row = existingButtons / buttonsPerRow;
        int col = existingButtons % buttonsPerRow;

        // Calculate x and y positions with proper spacing
        float x = MARGIN + col * totalButtonSpace;
        float y = TOP_MARGIN + row * (BUTTON_HEIGHT + MARGIN);

        return new float[]{x, y};
    }
}
