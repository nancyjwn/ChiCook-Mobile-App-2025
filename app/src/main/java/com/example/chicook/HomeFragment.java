package com.example.chicook;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chicook.data.api.ApiConfig;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.model.meal.MealAdapter;
import com.example.chicook.model.meal.MealResponse;

import com.example.chicook.databinding.FragmentHomeBinding;
import com.example.chicook.model.category.Category;
import com.example.chicook.model.category.CategoryAdapter;
import com.example.chicook.model.category.CategoryResponse;
import com.example.chicook.model.randomMeals.RandomMeal;
import com.example.chicook.model.randomMeals.RandomMealAdapter;
import com.example.chicook.model.randomMeals.RandomMealResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MealAdapter mealAdapter;
    private CategoryAdapter categoryAdapter;
    private RandomMealAdapter randomMealAdapter;
    private FragmentHomeBinding binding;
    private Handler handler = new Handler();
    private int currentMealIndex = 0;
    private List<RandomMeal> randomMealsList = new ArrayList<>();
    private List<String> selectedCategories = new ArrayList<String>() {{
        add("Beef");
        add("Chicken");
        add("Seafood");
    }};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Tampilkan ProgressBar saat memuat data
        binding.progressBar.setVisibility(View.VISIBLE);  // Menampilkan ProgressBar saat halaman pertama kali dibuka
        // Sembunyikan elemen lainnya
        binding.topCategoryText.setVisibility(View.GONE);
        binding.recommendationText.setVisibility(View.GONE);
        binding.garisDirandom.setVisibility(View.GONE);

        // Set up RecyclerView dengan GridLayoutManager
        binding.recommendationRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Untuk makanan

        // Return the root view of the fragment
        View rootView = binding.getRoot();

        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);

        // Memanggil API randomMeals() tiga kali untuk mendapatkan 3 resep acak
        fetchRandomMeals(apiService);

        // Menggunakan Handler untuk menjalankan pemanggilan random meal setiap 5 detik
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchRandomMeals(apiService);
                handler.postDelayed(this, 5000); // Mengulang setiap 5 detik
            }
        }, 3000);

        // Memanggil API untuk mendapatkan kategori makanan
        fetchCategories(apiService);

        // Memanggil API untuk mencari makanan berdasarkan nama (misalnya "Chicken")
        fetchMeals(apiService);

        return rootView;
    }

    // Fungsi untuk menyembunyikan ProgressBar dan menampilkan elemen lainnya setelah selesai
    private void hideProgressBarIfFinished() {
        if (randomMealsList.size() > 0 && categoryAdapter != null && mealAdapter != null) {
            binding.progressBar.setVisibility(View.GONE); // Sembunyikan ProgressBar
            binding.topCategoryText.setVisibility(View.VISIBLE); // Menampilkan Top Kategori
            binding.recommendationText.setVisibility(View.VISIBLE); // Menampilkan Rekomendasi untuk Anda
            binding.garisDirandom.setVisibility(View.VISIBLE); // Menampilkan garis pembatas
        }
    }

    // Fungsi untuk memanggil random meal API dan memperbarui RecyclerView
    private void fetchRandomMeals(ApiService apiService) {
        Call<RandomMealResponse> call = apiService.randomMeals("");
        call.enqueue(new Callback<RandomMealResponse>() {
            @Override
            public void onResponse(Call<RandomMealResponse> call, Response<RandomMealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RandomMeal meal = response.body().getMeals().get(0);
                    randomMealsList.add(meal);

                    // Menampilkan meal berikutnya setiap 5 detik
                    if (randomMealsList.size() > 5) {
                        randomMealsList.remove(0);
                    }

                    // Update adapter dan tampilkan resep acak
                    randomMealAdapter = new RandomMealAdapter(randomMealsList, getContext());
                    binding.carouselRecycler.setAdapter(randomMealAdapter);
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }

                // Sembunyikan ProgressBar dan TextView setelah API selesai
                hideProgressBarIfFinished();
            }

            @Override
            public void onFailure(Call<RandomMealResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call for random meal failed: " + t.getMessage());
                hideProgressBarIfFinished(); // Sembunyikan ProgressBar jika API gagal
            }
        });
    }

    private void fetchCategories(ApiService apiService) {
        Call<CategoryResponse> callCategory = apiService.categoryMeals("");
        callCategory.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> allCategories = response.body().getCategories();

                    // Memfilter kategori yang ingin ditampilkan berdasarkan daftar selectedCategories
                    List<Category> filteredCategories = new ArrayList<>();
                    for (Category category : allCategories) {
                        if (selectedCategories.contains(category.getStrCategory())) {
                            filteredCategories.add(category);
                        }
                    }

                    // Menampilkan kategori yang telah difilter
                    categoryAdapter = new CategoryAdapter(filteredCategories, getContext());
                    binding.topCategoryRecycler.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(getContext(), "No categories found", Toast.LENGTH_SHORT).show();
                }

                // Sembunyikan ProgressBar dan TextView setelah API selesai
                hideProgressBarIfFinished();
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call for categories failed: " + t.getMessage());
                hideProgressBarIfFinished(); // Sembunyikan ProgressBar jika API gagal
            }
        });
    }

    private void fetchMeals(ApiService apiService) {
        Call<MealResponse> callMeal = apiService.searchMeals("");
        callMeal.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter = new MealAdapter(response.body().getMeals(), getContext());
                    binding.recommendationRecycler.setAdapter(mealAdapter);
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }

                // Sembunyikan ProgressBar dan TextView setelah API selesai
                hideProgressBarIfFinished();
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call for meals failed: " + t.getMessage());
                hideProgressBarIfFinished(); // Sembunyikan ProgressBar jika API gagal
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Hapus binding saat view dihancurkan untuk mencegah memory leak
        handler.removeCallbacksAndMessages(null);
    }
}