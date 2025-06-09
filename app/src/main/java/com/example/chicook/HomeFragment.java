package com.example.chicook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MealAdapter mealAdapter;
    private CategoryAdapter categoryAdapter;
    private RandomMealAdapter randomMealAdapter;
    private FragmentHomeBinding binding;
    private ExecutorService executorService;
    private List<RandomMeal> randomMealsList = new ArrayList<>();

    private List<String> selectedCategories = new ArrayList<String>() {{
        add("Beef");
        add("Chicken");
        add("Seafood");
    }};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ThemeHelper.applyTheme(requireContext());

        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Pastikan binding selesai sebelum memulai proses lainnya
        if (binding == null) {
            return null;  // Menghindari NullPointerException
        }

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

        // Inisialisasi ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Memanggil API randomMeals() tiga kali untuk mendapatkan 3 resep acak
        fetchRandomMeals(apiService);

        // Menggunakan ExecutorService untuk menjalankan pemanggilan random meal setiap 5 detik
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        fetchRandomMeals(apiService);
                        Thread.sleep(5000); // Mengulang setiap 5 detik
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Memanggil API untuk mendapatkan kategori makanan
        fetchCategories(apiService);

        // Memanggil API untuk mencari makanan berdasarkan nama (misalnya "Chicken")
        fetchMeals(apiService);

        // Aksi klik ImageView untuk toggle tema
        binding.imgToggleTheme.setOnClickListener(v -> {
            String currentTheme = ThemeHelper.getCurrentTheme(requireContext());

            if ("dark".equals(currentTheme)) {
                ThemeHelper.setTheme(requireContext(), "light");
            } else {
                ThemeHelper.setTheme(requireContext(), "dark");
            }

            // Ubah tema tanpa memanggil recreate() untuk mencegah keluar
            AppCompatDelegate.setDefaultNightMode(
                    "dark".equals(currentTheme)
                            ? AppCompatDelegate.MODE_NIGHT_NO
                            : AppCompatDelegate.MODE_NIGHT_YES
            );

            // Hanya update gambar tema setelah perubahan
            updateImageTheme();
        });

        return rootView;
    }

    // Memperbarui gambar tema saat ganti tema
    private void updateImageTheme() {
        String currentTheme = ThemeHelper.getCurrentTheme(requireContext());
        if ("dark".equals(currentTheme)) {
            binding.imgToggleTheme.setImageResource(R.drawable.ic_light_mode);
        } else {
            binding.imgToggleTheme.setImageResource(R.drawable.ic_dark_mode);
        }
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
                if (binding == null) return; // Prevent crash if view is destroyed
                if (response.isSuccessful() && response.body() != null) {
                    RandomMeal meal = response.body().getMeals().get(0);
                    randomMealsList.add(meal);

                    if (randomMealsList.size() > 5) {
                        randomMealsList.remove(0);
                    }

                    if (randomMealAdapter == null) {
                        randomMealAdapter = new RandomMealAdapter(randomMealsList, getContext());
                        binding.carouselRecycler.setAdapter(randomMealAdapter);
                    } else {
                        randomMealAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }
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
                if (binding == null) return; // Prevent crash if view is destroyed
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> allCategories = response.body().getCategories();
                    List<Category> filteredCategories = new ArrayList<>();
                    for (Category category : allCategories) {
                        if (selectedCategories.contains(category.getStrCategory())) {
                            filteredCategories.add(category);
                        }
                    }
                    categoryAdapter = new CategoryAdapter(filteredCategories, getContext());
                    binding.topCategoryRecycler.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(getContext(), "No categories found", Toast.LENGTH_SHORT).show();
                }
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
                if (binding == null) return; // Prevent crash if view is destroyed
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter = new MealAdapter(response.body().getMeals(), getContext());
                    binding.recommendationRecycler.setAdapter(mealAdapter);
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }
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
        executorService.shutdownNow(); // Matikan executor saat fragment dihancurkan
    }
}