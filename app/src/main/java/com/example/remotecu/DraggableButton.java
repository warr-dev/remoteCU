package com.example.remotecu;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class DraggableButton extends androidx.appcompat.widget.AppCompatButton {
    private float dX, dY;
    private boolean isDragging = false;
    private boolean isEditMode = false;
    private long lastClickTime = 0;
    private static final long CLICK_THRESHOLD = 200; // ms
    private OnPositionChangedListener positionChangedListener;

    public interface OnPositionChangedListener {
        void onPositionChanged(float x, float y);
    }

    public DraggableButton(Context context) {
        super(context);
        setupTouchListener();
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
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!isEditMode) {
                    return false; // Let normal click handling work
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastClickTime = System.currentTimeMillis();
                        isDragging = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isEditMode) {
                            isDragging = true;
                            view.animate()
                                    .x(event.getRawX() + dX)
                                    .y(event.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isDragging && positionChangedListener != null) {
                            positionChangedListener.onPositionChanged(view.getX(), view.getY());
                        }

                        // If it was a quick tap (not a drag), treat as click
                        if (!isDragging && (System.currentTimeMillis() - lastClickTime) < CLICK_THRESHOLD) {
                            performClick();
                            return true;
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
