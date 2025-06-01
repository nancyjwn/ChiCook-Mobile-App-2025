package com.example.chicook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chicook.data.ApiConfig;
import com.example.chicook.data.ApiService;
import com.example.chicook.databinding.CategoryMealsDetailBinding;
import com.example.chicook.model.meal.MealAdapter;
import com.example.chicook.model.meal.MealResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryMealsActivity extends AppCompatActivity {

    private MealAdapter mealAdapter;
    private CategoryMealsDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CategoryMealsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }

    // Function to fetch meals based on selected category
    private void fetchMealsByCategory(ApiService apiService, String category) {
        // Calling the API to search meals based on category
        Call<MealResponse> callMeal = apiService.searchMealsByCategory(category);
        callMeal.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter = new MealAdapter(response.body().getMeals(), CategoryMealsActivity.this);
                    binding.categoryMealsRecycler.setAdapter(mealAdapter);  // Display meals based on category

                } else {
                    Toast.makeText(CategoryMealsActivity.this, "No meals found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(CategoryMealsActivity.this, "API call for meals failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
