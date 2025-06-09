package com.example.chicook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter onboardingAdapter;
    private Button btnNext, btnBack, btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);

        // Data untuk onboarding
        int[] images = {
                R.drawable.agrofish, // Ganti dengan gambar Anda
                R.drawable.potato,
                R.drawable.kentang
        };

        String[] descriptions = {
                "Welcome to the app!",
                "Discover recipes from all over the world.",
                "Get started and find your favorite recipes."
        };

        // Setup Adapter untuk ViewPager2
        onboardingAdapter = new OnboardingAdapter(images, descriptions);
        viewPager.setAdapter(onboardingAdapter);

        // Button Next action
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < images.length - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        // Button Back action
        btnBack.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        // Button Continue action
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the onboarding activity
        });

        // ViewPager page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) {
                    btnBack.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnContinue.setVisibility(View.GONE);
                } else if (position == images.length - 1) {
                    btnBack.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    btnContinue.setVisibility(View.VISIBLE);
                } else {
                    btnBack.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnContinue.setVisibility(View.GONE);
                }
            }
        });
    }
}
