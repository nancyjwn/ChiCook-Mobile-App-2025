package com.example.chicook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.databinding.FragmentSearchBinding;
import com.example.chicook.model.meal.Meal;
import com.example.chicook.model.meal.MealAdapter;
import com.example.chicook.model.meal.MealResponse;
import com.example.chicook.model.randomMeals.RandomMeal;
import com.example.chicook.model.randomMeals.RandomMealAdapter;
import com.example.chicook.model.randomMeals.RandomMealResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private MealAdapter mealAdapter;
    private ArrayList<Meal> mealsList;
    private ApiService apiService;
    private List<RandomMeal> randomMealsList = new ArrayList<>();
    private RandomMealAdapter randomMealAdapter;

    private String selectedCategory = "";
    private String selectedArea = "";
    private String selectedIngredient = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Setup RecyclerView for search results (2 columns for search results)
        binding.searchRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mealsList = new ArrayList<>();
        mealAdapter = new MealAdapter(mealsList, getContext());
        binding.searchRecycler.setAdapter(mealAdapter);

        // Setup RecyclerView for random meals (1 column for random meals)
        randomMealsList = new ArrayList<>();
        randomMealAdapter = new RandomMealAdapter(randomMealsList, getContext());
        binding.randomSearchRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.randomSearchRecycler.setAdapter(randomMealAdapter);

        // Initially hide the search results RecyclerView
        binding.searchRecycler.setVisibility(View.GONE);
        binding.randomSearchRecycler.setVisibility(View.VISIBLE);  // Show random meals initially

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Fetch random meals to display
        getRandomMeal(7);  // Fetch 7 random meals

        // Set listener for the search query
        binding.search.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Hide search results if query is empty and show random meals
                    binding.searchRecycler.setVisibility(View.GONE);
                    binding.randomSearchRecycler.setVisibility(View.VISIBLE);
                    getRandomMeal(7);  // Fetch 7 random meals again
                }
                return true;
            }
        });

        // Set listener for category buttons
        setCategoryButtonListeners();

        return view;
    }

    // Fetch random meals (7 meals in this case)
    private void getRandomMeal(int count) {
        randomMealsList.clear();
        randomMealAdapter.notifyDataSetChanged();

        final int[] loadedCount = {0};
        for (int i = 0; i < count; i++) {
            Call<RandomMealResponse> call = apiService.randomMeals("");
            call.enqueue(new Callback<RandomMealResponse>() {
                @Override
                public void onResponse(Call<RandomMealResponse> call, Response<RandomMealResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                        randomMealsList.addAll(response.body().getMeals());
                    }
                    loadedCount[0]++;
                    if (loadedCount[0] == count) {
                        randomMealAdapter.notifyDataSetChanged();
                        binding.randomSearchRecycler.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<RandomMealResponse> call, Throwable t) {
                    loadedCount[0]++;
                    if (loadedCount[0] == count) {
                        randomMealAdapter.notifyDataSetChanged();
                        binding.randomSearchRecycler.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    // Handle search functionality based on query
    private void handleSearch(String query) {
        query = query.trim();
        if (query.isEmpty()) return;

        String[] ingredients = {"Chicken", "Beef", "Tomato", "Egg", "Lemon"};
        String[] areas = {"American", "British", "Canadian", "Chinese", "Dutch", "Egyptian", "French", "Greek", "Indian", "Irish", "Italian", "Jamaican", "Japanese", "Kenyan", "Malaysian", "Mexican", "Moroccan", "Russian", "Spanish", "Thai", "Tunisian", "Turkish", "Vietnamese"};
        String[] categories = {"Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat"};
        boolean isIngredient = false;
        boolean isArea = false;
        boolean isCategory = false;

        for (String ingredient : ingredients) {
            if (ingredient.equalsIgnoreCase(query)) {
                isIngredient = true;
                break;
            }
        }
        for (String area : areas) {
            if (area.equalsIgnoreCase(query)) {
                isArea = true;
                break;
            }
        }
        for (String category : categories) {
            if (category.equalsIgnoreCase(query)) {
                isCategory = true;
                break;
            }
        }

        binding.randomSearchRecycler.setVisibility(View.GONE);
        binding.searchRecycler.setVisibility(View.VISIBLE);

        if (isIngredient) {
            searchMealsByIngredient(query);
        } else if (isArea) {
            searchMealsByArea(query);
        } else if (isCategory) {
            searchMealsByCategory(query);
        } else {
            searchMeals(query);
        }
    }

    private void searchMealsByArea(String area) {
        Call<MealResponse> call = apiService.searchMealsByArea(area);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());
                    mealAdapter.notifyDataSetChanged();
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "No results found for area: " + area, Toast.LENGTH_SHORT).show();
                    binding.searchRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchMealsByIngredient(String ingredient) {
        Call<MealResponse> call = apiService.searchMealsByIngredient(ingredient);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());
                    mealAdapter.notifyDataSetChanged();
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "No results found for ingredient: " + ingredient, Toast.LENGTH_SHORT).show();
                    binding.searchRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to search meals by name
    private void searchMeals(String query) {
        Call<MealResponse> call = apiService.searchMeals(query);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());
                    mealAdapter.notifyDataSetChanged();
                    binding.searchRecycler.setVisibility(View.VISIBLE);  // Show RecyclerView with search results
                } else {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    binding.searchRecycler.setVisibility(View.GONE);  // Hide if no results
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to search meals by category
    private void searchMealsByCategory(String category) {
        Call<MealResponse> call = apiService.searchMealsByCategory(category);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());
                    mealAdapter.notifyDataSetChanged();
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "No results found for category: " + category, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to set listeners on category buttons
    private void setCategoryButtonListeners() {
        binding.genreButtons.findViewById(R.id.button_chicken).setOnClickListener(view -> {
            // Hide random meals and show search results for Chicken
            binding.randomSearchRecycler.setVisibility(View.GONE);
            binding.searchRecycler.setVisibility(View.VISIBLE);

            // Fetch and display Chicken meals
            searchMealsByCategory("Chicken");
        });
        binding.genreButtons.findViewById(R.id.button_beef).setOnClickListener(view -> {
            binding.randomSearchRecycler.setVisibility(View.GONE);
            binding.searchRecycler.setVisibility(View.VISIBLE);
            searchMealsByCategory("Beef");
        });
        binding.genreButtons.findViewById(R.id.button_seafood).setOnClickListener(view -> {
            binding.randomSearchRecycler.setVisibility(View.GONE);
            binding.searchRecycler.setVisibility(View.VISIBLE);
            searchMealsByCategory("Seafood");
        });
        binding.genreButtons.findViewById(R.id.button_pork).setOnClickListener(view -> {
            binding.randomSearchRecycler.setVisibility(View.GONE);
            binding.searchRecycler.setVisibility(View.VISIBLE);
            searchMealsByCategory("Pork");
        });
    }
}
