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
        String imageUrl = intent.getStringExtra("thumb");

        // Menampilkan data pada elemen UI
        binding.recipeTitle.setText(title);
        binding.recipeInstructions.setText(instructions);

        // Menampilkan kategori hanya jika tidak kosong
        if (category != null && !category.isEmpty()) {
            binding.recipeCategory.setText(category);
        } else {
            binding.recipeCategory.setVisibility(View.GONE);  // Menyembunyikan kategori jika kosong
        }

        // Menampilkan area hanya jika tidak kosong
        if (area != null && !area.isEmpty()) {
            binding.recipeArea.setText(area);
        } else {
            binding.recipeArea.setVisibility(View.GONE);  // Menyembunyikan area jika kosong
        }

        // Menampilkan gambar menggunakan Picasso
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(binding.recipeImage);
        } else {
            binding.recipeImage.setVisibility(View.GONE);  // Menyembunyikan gambar jika URL kosong
        }

        // Panggil API untuk mendapatkan detail resep berdasarkan mealId
        fetchMealDetail(mealId);
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
                    Picasso.get().load(response.body().getMeals().get(0).getMealThumb()).into(binding.recipeImage);  // Update gambar
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
