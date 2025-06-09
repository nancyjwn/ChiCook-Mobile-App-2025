package com.example.chicook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

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

        // Initialize ExecutorService and Handler
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(getMainLooper());  // Using Handler to update the UI on the main thread

        // Show progress bar while loading data
        binding.progressBar.setVisibility(View.VISIBLE);

        // Get the selected category from the Intent
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        // Display the selected category
        binding.categoryMealsName.setText(category);

        // Setup RecyclerView to display meals
        binding.categoryMealsRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);

        // Fetch meals by category from the API
        fetchMealsByCategory(apiService, category);

        // Handle the back button click
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    // Function to fetch meals based on selected category
    private void fetchMealsByCategory(ApiService apiService, String category) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Calling the API to search meals based on category
                Call<MealResponse> callMeal = apiService.searchMealsByCategory(category);
                callMeal.enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Update UI with the meals data
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mealAdapter = new MealAdapter(response.body().getMeals(), CategoryMealsActivity.this);
                                    binding.categoryMealsRecycler.setAdapter(mealAdapter);  // Display meals based on category
                                }
                            });
                        } else {
                            showToast("No meals found");
                        }

                        // Hide ProgressBar after data is loaded
                        hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<MealResponse> call, Throwable t) {
                        showToast("API call for meals failed: " + t.getMessage());
                        hideProgressBar(); // Hide ProgressBar if API call fails
                    }
                });
            }
        });
    }

    private void hideProgressBar() {
        // Hide the progress bar when loading is complete
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showToast(String message) {
        // Show a toast on the main thread
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
        executorService.shutdownNow();  // Shut down the executor service when activity is destroyed
    }
}
