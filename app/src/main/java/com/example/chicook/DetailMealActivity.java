package com.example.chicook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chicook.data.ApiConfig;
import com.example.chicook.data.ApiService;
import com.example.chicook.databinding.DetailResepBinding;
import com.example.chicook.model.meal.Meal;
import com.example.chicook.model.meal.MealResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMealActivity extends AppCompatActivity {

    private DetailResepBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DetailResepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mendapatkan data yang dikirim melalui Intent
        Intent intent = getIntent();
        String mealId = intent.getStringExtra("mealId");  // Dapatkan mealId yang diterima dari Intent
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        String area = intent.getStringExtra("area");
        String instructions = intent.getStringExtra("instructions");
        String ingridients = intent.getStringExtra("ingredients");
        String imageUrl = intent.getStringExtra("thumb");

        // Menampilkan data pada elemen UI
        binding.recipeTitle.setText(title);
        binding.recipeCategory.setText(category);
        binding.recipeArea.setText(area);
        binding.recipeInstructions.setText(instructions);
        binding.recipeIngredients.setText(ingridients);
        Picasso.get().load(imageUrl).into(binding.recipeImage);

        // Panggil API untuk mendapatkan detail resep berdasarkan mealId
        fetchMealDetail(mealId);

        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    private void fetchMealDetail(String mealId) {
        // API call untuk mendapatkan detail resep berdasarkan mealId
        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);
        Call<MealResponse> callMeal = apiService.getMealDetailById(mealId);  // Memanggil API untuk mendapatkan detail berdasarkan ID

        callMeal.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update UI dengan data tambahan yang didapat dari API
                    String additionalInstructions = response.body().getMeals().get(0).getInstructions();
                    binding.recipeInstructions.setText(additionalInstructions);  // Update dengan instruksi lebih lengkap
                    binding.recipeArea.setText(response.body().getMeals().get(0).getArea());  // Update area
                    binding.recipeCategory.setText(response.body().getMeals().get(0).getCategory());  // Update kategori
                    Picasso.get().load(response.body().getMeals().get(0).getMealThumb()).into(binding.recipeImage);  // Update gambar

                    // Ambil meal data dari respons
                    Meal meal = response.body().getMeals().get(0);

                    // Dapatkan ingredients dan measures
                    String[] ingredients = meal.getIngredients();
                    String[] measures = meal.getMeasures();

                    // Gabungkan ingredients dan measures menjadi satu string
                    StringBuilder ingredientsText = new StringBuilder();
                    for (int i = 0; i < ingredients.length; i++) {
                        if (ingredients[i] != null && !ingredients[i].isEmpty()) {
                            ingredientsText.append(ingredients[i])
                                    .append(": ")
                                    .append(measures[i])
                                    .append("\n");
                        }
                    }

                    // Tampilkan ingredients dan measures pada UI
                    binding.recipeIngredients.setText(ingredientsText.toString());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                // Menangani kegagalan jika API request gagal
                Log.e("DetailMealActivity", "Failed to fetch meal details: " + t.getMessage());  // Log error untuk debugging
            }
        });
    }
}