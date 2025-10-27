package com.example.remotecu;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

public class DraggableButton extends androidx.appcompat.widget.AppCompatButton {
    private boolean isDragging = false;
    private boolean isEditMode = false;
    private long lastClickTime = 0;
    private static final long CLICK_THRESHOLD = 150; // ms - reduced for better click detection
    private OnPositionChangedListener positionChangedListener;
    private OnButtonDeleteListener deleteListener;

    public interface OnPositionChangedListener {
        void onPositionChanged(float x, float y);
    }

    public interface OnButtonDeleteListener {
        void onDeleteRequested(DraggableButton button);
    }

    public DraggableButton(Context context) {
        super(context);
        setupButtonStyle();
        setupTouchListener();
    }

    private void setupButtonStyle() {
        // Set background drawable for remote button style
        setBackgroundResource(R.drawable.remote_button_selector);

        // Set text color - bright white for visibility
        setTextColor(0xFFFFFFFF); // White text

        // Set default text size (will be adjusted based on button size)
        setTextSize(36f);

        // Center text
        setGravity(Gravity.CENTER);

        // Add stronger elevation for realistic 3D effect
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setElevation(8f); // Higher elevation
            setTranslationZ(2f); // Additional depth
        }

        // Set minimal padding - icon should fill most of button
        setPadding(8, 8, 8, 8);

        // Single line - icons are single character/emoji
        setSingleLine(true);

        // Make text bold for better visibility
        setTypeface(getTypeface(), android.graphics.Typeface.BOLD);
    }

    /**
     * Set button size and automatically adjust icon size proportionally
     */
    public void setButtonSize(int width, int height) {
        // Calculate icon size to fill most of the button
        // Using 85% of smaller dimension to maximize icon size while leaving room for borders
        int smallerDimension = Math.min(width, height);
        float iconSize = smallerDimension * 0.15f;

        // Clamp between reasonable values (20sp min, 120sp max)
        iconSize = Math.max(20f, Math.min(120f, iconSize));

        setTextSize(iconSize);

        // Minimal padding - just enough for the border
        int padding = 2;
        setPadding(padding, padding, padding, padding);

        // Adjust line spacing to compress icon vertically
        setLineSpacing(0f, 0.8f);

        // DEBUG: Add debug borders to visualize sizing
        enableDebugMode();
    }

    /**
     * Enable debug mode with visible borders
     */
    private void enableDebugMode() {
        // Create debug border drawable
        android.graphics.drawable.GradientDrawable debugBorder = new android.graphics.drawable.GradientDrawable();
        debugBorder.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        debugBorder.setStroke(6, 0xFFFF0000); // Red border, 6px thick
        debugBorder.setColor(0x00000000); // Transparent fill

        // Add debug border as foreground (on top of button background)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            setForeground(debugBorder);
        }

        // Add yellow background to text/icon area to show icon bounds
        setShadowLayer(8f, 0f, 0f, 0xFFFFFF00); // Yellow glow around icon
    }

    /**
     * Set button with icon and name (name shown as tooltip)
     */
    public void setIconAndName(String icon, String name) {
        // Display icon on button face
        setText(icon != null ? icon : "▪️");

        // Set name as tooltip (shown on long press)
        setContentDescription(name);

        // Enable tooltip on long press for Android O+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setTooltipText(name);
        }
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setOnPositionChangedListener(OnPositionChangedListener listener) {
        this.positionChangedListener = listener;
    }

    public void setOnButtonDeleteListener(OnButtonDeleteListener listener) {
        this.deleteListener = listener;

        // Set up long-press listener for delete in edit mode
        setOnLongClickListener(v -> {
            if (isEditMode && deleteListener != null) {
                deleteListener.onDeleteRequested(this);
                return true;
            }
            return false;
        });
    }

    private void setupTouchListener() {
        // Enable hardware acceleration for smoother dragging
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        setOnTouchListener(new View.OnTouchListener() {
            private float offsetX, offsetY;
            private long pressStartTime = 0;
            private static final long LONG_PRESS_THRESHOLD = 500; // ms for long press
            private boolean isLongPress = false;
            private android.os.Handler longPressHandler = new android.os.Handler();
            private Runnable longPressRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isEditMode && !isDragging && deleteListener != null) {
                        isLongPress = true;
                        // Trigger delete
                        deleteListener.onDeleteRequested(DraggableButton.this);
                    }
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!isEditMode) {
                    return false; // Let normal click handling work
                }

                // Disable parent touch interception for smooth dragging
                if (view.getParent() != null) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        // Calculate offset from touch point to view position
                        offsetX = view.getX() - event.getRawX();
                        offsetY = view.getY() - event.getRawY();
                        lastClickTime = System.currentTimeMillis();
                        pressStartTime = System.currentTimeMillis();
                        isDragging = false;
                        isLongPress = false;

                        // Schedule long-press detection
                        longPressHandler.postDelayed(longPressRunnable, LONG_PRESS_THRESHOLD);

                        // Visual feedback on press
                        view.setAlpha(0.9f);
                        view.setScaleX(1.05f);
                        view.setScaleY(1.05f);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            view.setElevation(16f);
                        }
                        view.bringToFront();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        isDragging = true;

                        // Cancel long-press if user starts dragging
                        longPressHandler.removeCallbacks(longPressRunnable);

                        // Direct position update - follows finger exactly
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;

                        view.setX(newX);
                        view.setY(newY);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Cancel long-press if finger lifted
                        longPressHandler.removeCallbacks(longPressRunnable);

                        // If long-press delete was triggered, don't do anything else
                        if (isLongPress) {
                            isLongPress = false;
                            return true;
                        }

                        // Reset visual feedback
                        view.setAlpha(1.0f);
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            view.setElevation(4f);
                        }

                        // Save position if dragged
                        if (isDragging && positionChangedListener != null) {
                            positionChangedListener.onPositionChanged(view.getX(), view.getY());
                        }

                        // If it was a quick tap (not a drag), treat as click
                        long touchDuration = System.currentTimeMillis() - lastClickTime;
                        if (!isDragging && touchDuration < CLICK_THRESHOLD) {
                            performClick();
                        }

                        isDragging = false;
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
