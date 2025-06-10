package com.example.chicook.layout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chicook.NetworkUtil;
import com.example.chicook.ThemeHelper;
import com.example.chicook.data.api.ApiConfig;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.databinding.CategoryMealsDetailBinding;
import com.example.chicook.model.meal.MealAdapter;
import com.example.chicook.model.meal.MealResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryMealsActivity extends AppCompatActivity {

    private MealAdapter mealAdapter;
    private CategoryMealsDetailBinding binding;
    private ExecutorService executorService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = CategoryMealsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(getMainLooper());

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.backButton.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        binding.categoryMealsName.setText(category);
        binding.categoryMealsRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);
        fetchMealsByCategory(apiService, category);

        if (NetworkUtil.isNetworkAvailable(this)) {
            showContent();
            fetchMealsByCategory(apiService, category);
        } else {
            showOfflineMessage();
        }

        binding.refreshButton.setOnClickListener(v -> {
            if (NetworkUtil.isNetworkAvailable(this)) {
                showContent();
                fetchMealsByCategory(apiService, category);
            } else {
                showOfflineMessage();
                showToast("Still no internet connection");
            }
        });
    }

    private void showOfflineMessage() {
        binding.categoryMealsRecycler.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.offlineMessage.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        binding.categoryMealsRecycler.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.offlineMessage.setVisibility(View.GONE);
    }

    private void fetchMealsByCategory(ApiService apiService, String category) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<MealResponse> callMeal = apiService.searchMealsByCategory(category);
                callMeal.enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mealAdapter = new MealAdapter(response.body().getMeals(), CategoryMealsActivity.this);
                                    binding.categoryMealsRecycler.setAdapter(mealAdapter);
                                }
                            });
                        } else {
                            showToast("No meals found");
                        }
                        hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<MealResponse> call, Throwable t) {
                        showToast("API call for meals failed: " + t.getMessage());
                        hideProgressBar();
                    }
                });
            }
        });
    }

    private void hideProgressBar() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showToast(String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CategoryMealsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}
