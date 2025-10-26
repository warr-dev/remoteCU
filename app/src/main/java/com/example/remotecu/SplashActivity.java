package com.example.remotecu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No need to set content view - the theme handles the splash screen

        // Navigate to MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start main activity
                Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
                startActivity(intent);

                // Close splash activity
                finish();
            }
        }, SPLASH_DURATION);
    }
}
