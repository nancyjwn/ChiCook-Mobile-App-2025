package com.example.chicook.layout.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chicook.NetworkUtil;
import com.example.chicook.R;
import com.example.chicook.ThemeHelper;
import com.example.chicook.data.api.ApiConfig;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.data.database.DatabaseHelper;
import com.example.chicook.databinding.DetailResepBinding;
import com.example.chicook.model.meal.Meal;
import com.example.chicook.model.meal.MealResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailMealActivity extends AppCompatActivity {

    private DetailResepBinding binding;
    private boolean isBookmarked = false;
    private DatabaseHelper databaseHelper;
    private ImageButton bookmarkButton;
    private String mealId;
    private ExecutorService executorService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DetailResepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(getMainLooper());

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.backButton.setOnClickListener(v -> onBackPressed());
        hideAllUIElements();

        Intent intent = getIntent();
        mealId = intent.getStringExtra("mealId");
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        String area = intent.getStringExtra("area");
        String instructions = intent.getStringExtra("instructions");
        String ingredients = intent.getStringExtra("ingredients");
        String imageUrl = intent.getStringExtra("thumb");
        displayMealData(title, category, area, instructions, ingredients, imageUrl);

        databaseHelper = new DatabaseHelper(this);
        checkBookmarkStatus();

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

        // handle offline/online
        if (NetworkUtil.isNetworkAvailable(this)) {
            fetchMealDetail(mealId);
            hideOfflineMessage();
        } else {
            displayMealData(title, category, area, instructions, ingredients, imageUrl);
            hideProgressBarIfFinished();
            showOfflineMessage();
        }

        binding.refreshButton.setOnClickListener(v -> {
            if (NetworkUtil.isNetworkAvailable(this)) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentScrollView.setVisibility(View.GONE);

                fetchMealDetail(mealId);
            } else {
                Toast.makeText(this, "Still no internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOfflineMessage() {
        binding.offlineMessage.setVisibility(View.VISIBLE);
        binding.contentScrollView.setVisibility(View.GONE);
    }

    private void hideOfflineMessage() {
        binding.offlineMessage.setVisibility(View.GONE);
        binding.contentScrollView.setVisibility(View.VISIBLE);
    }

    private void hideAllUIElements() {
        binding.recipeTitle.setVisibility(View.GONE);
        binding.recipeCategory.setVisibility(View.GONE);
        binding.recipeArea.setVisibility(View.GONE);
        binding.recipeInstructions.setVisibility(View.GONE);
        binding.recipeIngredients.setVisibility(View.GONE);
        binding.recipeImage.setVisibility(View.GONE);
        binding.bookmarkButton.setVisibility(View.GONE);
        binding.semuaui.setVisibility(View.GONE);
    }

    private void displayMealData(String title, String category, String area, String instructions, String ingredients, String imageUrl) {
        binding.recipeTitle.setText(title);
        binding.recipeCategory.setText(category);
        binding.recipeArea.setText(area);
        binding.recipeInstructions.setText(instructions);
        binding.recipeIngredients.setText(ingredients);
        Picasso.get().load(imageUrl).into(binding.recipeImage);
    }

    // Method to save bookmark to SQLite
    private void saveBookmark(String title, String category, String area, String instructions, String ingredients, String imageUrl) {
        executorService.execute(() -> {
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
            handler.post(() -> {
                if (result == -1) {
                    Toast.makeText(this, "Error saving bookmark", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Recipe bookmarked successfully", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void removeBookmark() {
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.delete("bookmarks", "meal_id = ?", new String[]{mealId});
        });
    }

    private void checkBookmarkStatus() {
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.query("bookmarks", new String[]{"meal_id"}, "meal_id = ?", new String[]{mealId}, null, null, null);
            handler.post(() -> {
                if (cursor.getCount() > 0) {
                    isBookmarked = true;
                    binding.bookmarkButton.setImageResource(R.drawable.bookmark_on);
                } else {
                    isBookmarked = false;
                    binding.bookmarkButton.setImageResource(R.drawable.bookmark_off);
                }
                cursor.close();
            });
        });
    }

    private void fetchMealDetail(String mealId) {
        executorService.execute(() -> {
            ApiService apiService = ApiConfig.getCLient().create(ApiService.class);
            Call<MealResponse> callMeal = apiService.getMealDetailById(mealId);

            callMeal.enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Meal meal = response.body().getMeals().get(0);

                        handler.post(() -> {
                            binding.recipeInstructions.setText(meal.getInstructions());
                            binding.recipeArea.setText(meal.getArea());
                            binding.recipeCategory.setText(meal.getCategory());
                            Picasso.get().load(meal.getMealThumb()).into(binding.recipeImage);

                            hideProgressBarIfFinished();

                            String[] ingredients = meal.getIngredients();
                            String[] measures = meal.getMeasures();
                            StringBuilder ingredientsText = new StringBuilder();
                            for (int i = 0; i < ingredients.length; i++) {
                                if (ingredients[i] != null && !ingredients[i].isEmpty()) {
                                    ingredientsText.append(ingredients[i]).append(": ").append(measures[i]).append("\n");
                                }
                            }
                            binding.recipeIngredients.setText(ingredientsText.toString());
                            hideProgressBarIfFinished();
                            hideOfflineMessage();
                        });
                    }
                }

                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    handler.post(() -> {
                        Log.e("DetailMealActivity", "Failed to fetch meal details: " + t.getMessage());
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(DetailMealActivity.this, "Failed to load meal details", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void hideProgressBarIfFinished() {
        if (binding.recipeTitle.getText() != null && !binding.recipeTitle.getText().toString().isEmpty()) {
            handler.post(() -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.recipeTitle.setVisibility(View.VISIBLE);
                binding.recipeCategory.setVisibility(View.VISIBLE);
                binding.recipeArea.setVisibility(View.VISIBLE);
                binding.recipeInstructions.setVisibility(View.VISIBLE);
                binding.recipeIngredients.setVisibility(View.VISIBLE);
                binding.recipeImage.setVisibility(View.VISIBLE);
                binding.bookmarkButton.setVisibility(View.VISIBLE);
                binding.semuaui.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}