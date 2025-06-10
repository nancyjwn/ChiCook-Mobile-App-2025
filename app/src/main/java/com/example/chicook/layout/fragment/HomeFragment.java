package com.example.chicook.layout.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chicook.NetworkUtil;
import com.example.chicook.R;
import com.example.chicook.ThemeHelper;
import com.example.chicook.data.api.ApiConfig;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.data.database.DatabaseHelper;
import com.example.chicook.model.meal.Meal;
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
    private DatabaseHelper dbHelper;
    private List<RandomMeal> randomMealsList = new ArrayList<>();
    private ArrayList<Meal> mealsList = new ArrayList<>();
    private ArrayList<RandomMeal> randomMealsListt = new ArrayList<>();
    private ArrayList<Category> categoriesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ThemeHelper.applyTheme(requireContext());
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());
        if (binding == null) {
            return null;
        }

        binding.topCategoryText.setVisibility(View.GONE);
        binding.recommendationText.setVisibility(View.GONE);
        binding.carouselCard.setVisibility(View.GONE);
        binding.recommendationRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Untuk makanan

        View rootView = binding.getRoot();
        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);
        fetchRandomMeals(apiService);
        fetchCategories(apiService);
        fetchMeals(apiService);

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        fetchRandomMeals(apiService);
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.imgToggleTheme.setOnClickListener(v -> {
            String currentTheme = ThemeHelper.getCurrentTheme(requireContext());
            if ("dark".equals(currentTheme)) {
                ThemeHelper.setTheme(requireContext(), "light");
            } else {
                ThemeHelper.setTheme(requireContext(), "dark");
            }
            AppCompatDelegate.setDefaultNightMode(
                    "dark".equals(currentTheme)
                            ? AppCompatDelegate.MODE_NIGHT_NO
                            : AppCompatDelegate.MODE_NIGHT_YES
            );
        });
        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            binding.progressBar.setVisibility(View.VISIBLE);
            fetchDataOnline(apiService);
            hideOfflineMessage();
        } else {
            fetchDataOffline();
            showOfflineMessage();
        }

        binding.refreshButton.setOnClickListener(v -> {
            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentScrollView.setVisibility(View.GONE);
                binding.offlineMessage.setVisibility(View.GONE);

                fetchDataOnline(ApiConfig.getCLient().create(ApiService.class));
            } else {
                binding.offlineMessage.setVisibility(View.VISIBLE);
                binding.refreshButton.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Still no internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        updateImageTheme();
        return rootView;
    }

    private void fetchDataOnline(ApiService apiService) {
        fetchRandomMeals(apiService);
        fetchCategories(apiService);
        fetchMeals(apiService);
    }

    private void fetchDataOffline() {
        showOfflineMessage();
    }

    private void showOfflineMessage() {
        binding.contentScrollView.setVisibility(View.GONE);
        binding.offlineMessage.setVisibility(View.VISIBLE);
        binding.refreshButton.setVisibility(View.VISIBLE);
    }

    private void hideOfflineMessage() {
        binding.contentScrollView.setVisibility(View.VISIBLE);
        binding.offlineMessage.setVisibility(View.GONE);
        binding.refreshButton.setVisibility(View.GONE);
    }

    private void updateImageTheme() {
        String currentTheme = ThemeHelper.getCurrentTheme(requireContext());
        if ("dark".equals(currentTheme)) {
            binding.imgToggleTheme.setImageResource(R.drawable.light_icon);
        } else {
            binding.imgToggleTheme.setImageResource(R.drawable.dark_icon);
        }
    }

    private void hideProgressBarIfFinished() {
        if (mealAdapter != null || categoryAdapter != null || randomMealAdapter != null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.contentScrollView.setVisibility(View.VISIBLE);
            binding.topCategoryText.setVisibility(View.VISIBLE);
            binding.recommendationText.setVisibility(View.VISIBLE);
            binding.carouselCard.setVisibility(View.VISIBLE);
        }
    }

    private void fetchRandomMeals(ApiService apiService) {
        Call<RandomMealResponse> call = apiService.randomMeals("");
        call.enqueue(new Callback<RandomMealResponse>() {
            @Override
            public void onResponse(Call<RandomMealResponse> call, Response<RandomMealResponse> response) {
                if (binding == null) return;
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
                hideProgressBarIfFinished();
            }
        });
    }

    private void fetchCategories(ApiService apiService) {
        Call<CategoryResponse> callCategory = apiService.categoryMeals("");
        callCategory.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (binding == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList.clear();
                    categoriesList.addAll(response.body().getCategories());

                    if (categoryAdapter == null) {
                        categoryAdapter = new CategoryAdapter(categoriesList, getContext());
                        binding.topCategoryRecycler.setAdapter(categoryAdapter);
                    } else {
                        categoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "No categories found", Toast.LENGTH_SHORT).show();
                }
                hideProgressBarIfFinished();
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call for categories failed: " + t.getMessage());
                hideProgressBarIfFinished();
            }
        });
    }

    private void fetchMeals(ApiService apiService) {
        Call<MealResponse> callMeal = apiService.searchMeals("");
        callMeal.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (binding == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());

                    if (mealAdapter == null) {
                        mealAdapter = new MealAdapter(mealsList, getContext());
                        binding.recommendationRecycler.setAdapter(mealAdapter);
                    } else {
                        mealAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }
                hideProgressBarIfFinished();
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                hideProgressBarIfFinished();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executorService.shutdownNow();
    }
}