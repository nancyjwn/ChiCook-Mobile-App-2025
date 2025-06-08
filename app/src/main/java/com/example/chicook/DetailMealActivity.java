package com.example.chicook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chicook.data.api.ApiConfig;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.data.sqlite.DatabaseHelper;
import com.example.chicook.databinding.DetailResepBinding;
import com.example.chicook.model.meal.Meal;
import com.example.chicook.model.meal.MealResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMealActivity extends AppCompatActivity {

    private DetailResepBinding binding;
    private boolean isBookmarked = false; // Menyimpan status bookmark
    private DatabaseHelper databaseHelper;
    private ImageButton bookmarkButton;
    private String mealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DetailResepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tampilkan ProgressBar saat memuat data
        binding.progressBar.setVisibility(View.VISIBLE); // Show ProgressBar while loading

        // Sembunyikan semua elemen UI (seperti TextView, RecyclerView, dll.) saat loading
        binding.recipeTitle.setVisibility(View.GONE);
        binding.recipeCategory.setVisibility(View.GONE);
        binding.recipeArea.setVisibility(View.GONE);
        binding.recipeInstructions.setVisibility(View.GONE);
        binding.recipeIngredients.setVisibility(View.GONE);
        binding.recipeImage.setVisibility(View.GONE);
        binding.bookmarkButton.setVisibility(View.GONE);

        // Mendapatkan data yang dikirim melalui Intent
        Intent intent = getIntent();
        mealId = intent.getStringExtra("mealId");
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        String area = intent.getStringExtra("area");
        String instructions = intent.getStringExtra("instructions");
        String ingredients = intent.getStringExtra("ingredients");
        String imageUrl = intent.getStringExtra("thumb");

        // Menampilkan data pada elemen UI (untuk yang sudah tersedia)
        binding.recipeTitle.setText(title);
        binding.recipeCategory.setText(category);
        binding.recipeArea.setText(area);
        binding.recipeInstructions.setText(instructions);
        binding.recipeIngredients.setText(ingredients);
        Picasso.get().load(imageUrl).into(binding.recipeImage);

        // Tombol kembali
        binding.backButton.setOnClickListener(v -> onBackPressed());

        // Inisialisasi DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Cek status bookmark saat activity dimulai
        checkBookmarkStatus();

        // Tombol bookmark
        bookmarkButton = findViewById(R.id.bookmarkButton);
        bookmarkButton.setOnClickListener(v -> {
            if (!isBookmarked) {
                saveBookmark(title, category, area, instructions, ingredients, imageUrl);
                bookmarkButton.setImageResource(R.drawable.bookmark_on);
                Toast.makeText(this, "Recipe bookmarked", Toast.LENGTH_SHORT).show();
                isBookmarked = true;
            } else {
                removeBookmark();
                bookmarkButton.setImageResource(R.drawable.bookmark_off);
                Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show();
                isBookmarked = false;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("status", isBookmarked ? "added" : "removed");
            setResult(RESULT_OK, resultIntent);
        });

        // Panggil API untuk mendapatkan detail resep berdasarkan mealId
        fetchMealDetail(mealId);
    }

    // Method untuk menyimpan bookmark ke SQLite
    private void saveBookmark(String title, String category, String area, String instructions, String ingredients, String imageUrl) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("meal_id", mealId);
        values.put("title", title);
        values.put("category", category);
        values.put("area", area);
        values.put("instructions", instructions);
        values.put("ingredients", ingredients);
        values.put("image_url", imageUrl);

        long result = db.insert("bookmarks", null, values);
        if (result == -1) {
            Toast.makeText(this, "Error saving bookmark", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Recipe bookmarked successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Method untuk menghapus bookmark dari SQLite
    private void removeBookmark() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete("bookmarks", "meal_id = ?", new String[]{mealId});
    }

    // Method untuk mengecek status bookmark di SQLite
    private void checkBookmarkStatus() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("bookmarks", new String[]{"meal_id"}, "meal_id = ?", new String[]{mealId}, null, null, null);

        // Jika resep sudah dibookmark, set ikon tombol sesuai status
        if (cursor.getCount() > 0) {
            isBookmarked = true;
            binding.bookmarkButton.setImageResource(R.drawable.bookmark_on);  // Set ikon menjadi "bookmarked"
        } else {
            isBookmarked = false;
            binding.bookmarkButton.setImageResource(R.drawable.bookmark_off);  // Set ikon menjadi "unbookmarked"
        }
        cursor.close();
    }

    private void fetchMealDetail(String mealId) {
        // API call untuk mendapatkan detail resep berdasarkan mealId
        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);
        Call<MealResponse> callMeal = apiService.getMealDetailById(mealId);

        callMeal.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update UI dengan data tambahan yang didapat dari API
                    String additionalInstructions = response.body().getMeals().get(0).getInstructions();
                    binding.recipeInstructions.setText(additionalInstructions);  // Update dengan instruksi lebih lengkap
                    binding.recipeArea.setText(response.body().getMeals().get(0).getArea());
                    binding.recipeCategory.setText(response.body().getMeals().get(0).getCategory());
                    Picasso.get().load(response.body().getMeals().get(0).getMealThumb()).into(binding.recipeImage);

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

                    // Menyembunyikan ProgressBar dan menampilkan elemen UI setelah selesai memuat data
                    hideProgressBarIfFinished();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                // Menangani kegagalan jika API request gagal
                Log.e("DetailMealActivity", "Failed to fetch meal details: " + t.getMessage());

                // Sembunyikan ProgressBar jika API gagal dan tampilkan pesan kesalahan
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(DetailMealActivity.this, "Failed to load meal details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideProgressBarIfFinished() {
        // Cek jika meal data telah dimuat
        if (binding.recipeTitle.getText() != null && !binding.recipeTitle.getText().toString().isEmpty()) {
            // Sembunyikan ProgressBar
            binding.progressBar.setVisibility(View.GONE);

            // Tampilkan elemen-elemen UI
            binding.recipeTitle.setVisibility(View.VISIBLE);
            binding.recipeCategory.setVisibility(View.VISIBLE);
            binding.recipeArea.setVisibility(View.VISIBLE);
            binding.recipeInstructions.setVisibility(View.VISIBLE);
            binding.recipeIngredients.setVisibility(View.VISIBLE);
            binding.recipeImage.setVisibility(View.VISIBLE);
            binding.bookmarkButton.setVisibility(View.VISIBLE);
        }
    }
}