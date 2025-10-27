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
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class RemoteEditActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    private RemoteProfile currentProfile;
    private FrameLayout buttonContainer;
    private LinearLayout emptyButtonsState;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView remoteIcon, remoteName, remoteButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_edit);

        sharedPrefManager = new SharedPrefManager(this);

        // Get profile ID from intent
        String profileId = getIntent().getStringExtra("PROFILE_ID");
        currentProfile = sharedPrefManager.getProfileById(profileId);

        if (currentProfile == null) {
            Toast.makeText(this, "Remote not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        buttonContainer = findViewById(R.id.buttonContainer);
        emptyButtonsState = findViewById(R.id.emptyButtonsState);
        fab = findViewById(R.id.fab);
        remoteIcon = findViewById(R.id.remoteIcon);
        remoteName = findViewById(R.id.remoteName);
        remoteButtonCount = findViewById(R.id.remoteButtonCount);

        // Setup toolbar
        toolbar.setTitle("Edit " + currentProfile.getName());
        toolbar.setNavigationOnClickListener(v -> finish());

        // Clear All button
        Button clearAllButton = findViewById(R.id.clearAllButton);
        clearAllButton.setOnClickListener(v -> showClearAllDialog());

        // Save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Layout saved", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Display profile info
        updateProfileDisplay();

        // Load buttons in edit mode
        loadButtons();

        // FAB click listener
        fab.setOnClickListener(v -> showAddButtonDialog());
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
                    buttonContainer.removeAllViews();
                    currentProfile.getButtons().clear();
                    sharedPrefManager.saveProfile(currentProfile);
                    updateProfileDisplay();
                    loadButtons();
                    Toast.makeText(this, "All buttons cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
            buttonContainer.removeAllViews();

            for (RemoteButton buttonData : currentProfile.getButtons()) {
                createDynamicButton(buttonData);
            }
        }
    }

    private void createDynamicButton(RemoteButton buttonData) {
        DraggableButton newButton = new DraggableButton(this);

        newButton.setIconAndName(buttonData.getIcon(), buttonData.getName());
        newButton.setTag(buttonData);
        newButton.setEditMode(true); // Always in edit mode

        int buttonWidth = buttonData.getWidth() > 0 ? buttonData.getWidth() : 200;
        int buttonHeight = buttonData.getHeight() > 0 ? buttonData.getHeight() : 150;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                buttonWidth,
                buttonHeight
        );
        newButton.setLayoutParams(params);
        newButton.setButtonSize(buttonWidth, buttonHeight);

        if (buttonData.getPosX() >= 0 && buttonData.getPosY() >= 0) {
            newButton.setX(buttonData.getPosX());
            newButton.setY(buttonData.getPosY());
        } else {
            buttonContainer.post(() -> {
                float[] position = calculateNextButtonPosition();
                newButton.setX(position[0]);
                newButton.setY(position[1]);
                buttonData.setPosX(position[0]);
                buttonData.setPosY(position[1]);
                saveButtonPositions();
            });
        }

        newButton.setOnPositionChangedListener((x, y) -> {
            buttonData.setPosX(x);
            buttonData.setPosY(y);
            saveButtonPositions();
        });

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
                    buttonContainer.removeView(button);
                    currentProfile.getButtons().remove(buttonData);
                    sharedPrefManager.saveProfile(currentProfile);
                    updateProfileDisplay();
                    Toast.makeText(this, "Button deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveButtonPositions() {
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
        sharedPrefManager.saveProfile(currentProfile);
    }

    private float[] calculateNextButtonPosition() {
        final int BUTTON_WIDTH = 200;
        final int BUTTON_HEIGHT = 150;
        final int MARGIN = 24;
        final int TOP_MARGIN = 16;

        int containerWidth = buttonContainer.getWidth();
        if (containerWidth == 0) {
            containerWidth = 1080;
        }

        int totalButtonSpace = BUTTON_WIDTH + MARGIN;
        int buttonsPerRow = Math.max(1, (containerWidth - MARGIN) / totalButtonSpace);

        int existingButtons = 0;
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            View child = buttonContainer.getChildAt(i);
            if (child instanceof DraggableButton) {
                RemoteButton buttonData = (RemoteButton) child.getTag();
                if (buttonData != null && (buttonData.getPosX() < 0 || buttonData.getPosY() < 0)) {
                    existingButtons++;
                }
            }
        }

        int row = existingButtons / buttonsPerRow;
        int col = existingButtons % buttonsPerRow;

        float x = MARGIN + col * totalButtonSpace;
        float y = TOP_MARGIN + row * (BUTTON_HEIGHT + MARGIN);

        return new float[]{x, y};
    }

    private void showAddButtonDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_button, null);
        android.widget.LinearLayout templatePickerContainer = dialogView.findViewById(R.id.templatePickerContainer);
        android.widget.LinearLayout iconPickerContainer = dialogView.findViewById(R.id.iconPickerContainer);
        TextInputEditText nameInput = dialogView.findViewById(R.id.buttonNameInput);
        TextInputEditText irCodeInput = dialogView.findViewById(R.id.irCodeInput);
        android.widget.RadioGroup sizeRadioGroup = dialogView.findViewById(R.id.buttonSizeRadioGroup);

        final String[] selectedIcon = {RemoteButton.AVAILABLE_ICONS[0]};

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

            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            templateBtn.setLayoutParams(params);

            templateBtn.setOnClickListener(v -> {
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

            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
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

            int selectedRadioId = sizeRadioGroup.getCheckedRadioButtonId();
            int width, height;
            if (selectedRadioId == R.id.sizeSmall) {
                width = 120;
                height = 90;
            } else if (selectedRadioId == R.id.sizeLarge) {
                width = 200;
                height = 150;
            } else {
                width = 160;
                height = 120;
            }

            RemoteButton newButton = new RemoteButton(name, irCode.isEmpty() ? "0x0000" : irCode);
            newButton.setIcon(selectedIcon[0]);
            newButton.setWidth(width);
            newButton.setHeight(height);
            currentProfile.addButton(newButton);

            sharedPrefManager.saveProfile(currentProfile);

            loadButtons();
            updateProfileDisplay();

            dialog.dismiss();
            Toast.makeText(this, "Button added: " + name, Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentProfile != null) {
            sharedPrefManager.saveProfile(currentProfile);
        }
    }
}
