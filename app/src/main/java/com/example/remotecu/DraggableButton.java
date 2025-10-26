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

    public interface OnPositionChangedListener {
        void onPositionChanged(float x, float y);
    }

    public DraggableButton(Context context) {
        super(context);
        setupButtonStyle();
        setupTouchListener();
    }

    private void setupButtonStyle() {
        // Set background drawable for remote button style
        setBackgroundResource(R.drawable.remote_button_selector);

        // Set text color
        setTextColor(0xFFFFFFFF); // White text

        // Set text size - MUCH larger for icon display
        setTextSize(32f); // Large icon size

        // Center text
        setGravity(Gravity.CENTER);

        // Add elevation for 3D effect
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setElevation(4f);
        }

        // Set padding
        setPadding(16, 16, 16, 16);

        // Single line - icons are single character/emoji
        setSingleLine(true);
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

    private void setupTouchListener() {
        // Enable hardware acceleration for smoother dragging
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        setOnTouchListener(new View.OnTouchListener() {
            private float offsetX, offsetY;

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
                        isDragging = false;

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

                        // Direct position update - follows finger exactly
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;

                        view.setX(newX);
                        view.setY(newY);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
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
