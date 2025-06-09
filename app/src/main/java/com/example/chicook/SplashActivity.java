package com.example.chicook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Menunggu 3 detik sebelum pindah ke OnboardingActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pindahkan ke OnboardingActivity setelah Splash Screen
                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish(); // Hapus SplashActivity dari stack
            }
        }, 3000); // Splash screen ditampilkan selama 3 detik
    }
}
