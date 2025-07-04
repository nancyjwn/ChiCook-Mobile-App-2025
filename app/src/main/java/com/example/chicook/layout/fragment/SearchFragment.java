package com.example.chicook.layout.fragment;

import static android.os.Looper.getMainLooper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chicook.NetworkUtil;
import com.example.chicook.ThemeHelper;
import com.example.chicook.data.api.ApiConfig;
import com.example.chicook.data.api.ApiService;
import com.example.chicook.databinding.FragmentSearchBinding;
import com.example.chicook.model.meal.Meal;
import com.example.chicook.model.meal.MealAdapter;
import com.example.chicook.model.meal.MealResponse;
import com.example.chicook.model.randomMeals.RandomMeal;
import com.example.chicook.model.randomMeals.RandomMealAdapter;
import com.example.chicook.model.randomMeals.RandomMealResponse;
import com.example.chicook.model.search.SearchHistoryAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private MealAdapter mealAdapter;
    private ArrayList<Meal> mealsList;
    private ApiService apiService;
    private List<RandomMeal> randomMealsList = new ArrayList<>();
    private RandomMealAdapter randomMealAdapter;
    private Spinner lastChangedSpinner = null;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String PREF_SEARCH_HISTORY = "search_history";
    private ArrayList<String> searchHistory = new ArrayList<>();
    private SearchHistoryAdapter searchHistoryAdapter;
    private ExecutorService executorService;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ThemeHelper.applyTheme(requireContext());
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(getMainLooper()); // Handler to update UI on main thread
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSearchHistory();

        searchHistoryAdapter = new SearchHistoryAdapter(searchHistory, query -> {
            binding.search.setQuery(query, false);
            performSearch(query);
        });
        binding.searchHistoryRecycler.setAdapter(searchHistoryAdapter);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (!NetworkUtil.isNetworkAvailable(requireContext())) {
            showOfflineMessage();
        } else {
            hideOfflineMessage();
        }

        binding.refreshButton.setOnClickListener(v -> {
            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                hideOfflineMessage();
                getRandomMeal(7); // or reload data as needed
            } else {
                Toast.makeText(getContext(), "Still offline", Toast.LENGTH_SHORT).show();
            }
        });

        setupSearchRecycler();
        setupRandomMealsRecycler();
        showRandomMeals();

        apiService = ApiConfig.getCLient().create(ApiService.class);
        getRandomMeal(7);
        setupSearchViewListener();
        setupSearchViewFocusListener();
        setupSpinners();

        return view;
    }

    private void showOfflineMessage() {
        binding.offlineMessage.setVisibility(View.VISIBLE);
        binding.searchRecycler.setVisibility(View.GONE);
        binding.randomSearchRecycler.setVisibility(View.GONE);
        binding.filterCard.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void hideOfflineMessage() {
        binding.offlineMessage.setVisibility(View.GONE);
        binding.filterCard.setVisibility(View.VISIBLE);
    }

    private void setupSearchRecycler() {
        binding.searchRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mealsList = new ArrayList<>();
        mealAdapter = new MealAdapter(mealsList, getContext());
        binding.searchRecycler.setAdapter(mealAdapter);
    }

    private void setupRandomMealsRecycler() {
        randomMealsList = new ArrayList<>();
        randomMealAdapter = new RandomMealAdapter(randomMealsList, getContext());
        binding.randomSearchRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.randomSearchRecycler.setAdapter(randomMealAdapter);
    }

    private void showRandomMeals() {
        binding.searchRecycler.setVisibility(View.GONE);
        binding.randomSearchRecycler.setVisibility(View.VISIBLE);
    }

    private void loadSearchHistory() {
        executorService.execute(() -> {
            String savedHistory = sharedPreferences.getString(PREF_SEARCH_HISTORY, "");
            if (!savedHistory.isEmpty()) {
                String[] historyArray = savedHistory.split(",");
                for (String query : historyArray) {
                    searchHistory.add(query);
                }
            }
            handler.post(() -> searchHistoryAdapter.notifyDataSetChanged());
        });
    }

    private void saveSearchQuery(String query) {
        executorService.execute(() -> {
            if (!searchHistory.contains(query)) {
                searchHistory.add(0, query);
            }
            if (searchHistory.size() > 3) {
                searchHistory.remove(searchHistory.size() - 1);
            }
            sharedPreferences.edit().putString(PREF_SEARCH_HISTORY, String.join(",", searchHistory)).apply();
            handler.post(() -> searchHistoryAdapter.notifyDataSetChanged());
        });
    }

    private void performSearch(String query) {
        saveSearchQuery(query);
        handleSearch(query);
    }

    private void setupSearchViewListener() {
        binding.search.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showRandomMeals();
                    getRandomMeal(7);
                    binding.filterCard.setVisibility(View.VISIBLE);
                } else {
                    binding.filterCard.setVisibility(View.GONE);
                    handleSearch(newText);
                }
                return true;
            }
        });
    }

    private void setupSearchViewFocusListener() {
        binding.search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            binding.searchHistoryRecycler.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });
    }

    private void setupSpinners() {
        setupSpinner(binding.spinnerCategory, new String[]{"Select Category", "Beef", "Breakfast", "Chicken", "Dessert", "Goat", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian"});
        setupSpinner(binding.spinnerArea, new String[]{"Select Area", "American", "British", "Canadian", "Chinese", "Croatian", "Dutch", "Egyptian", "Filipino", "French", "Greek", "Indian", "Irish", "Italian", "Jamaican", "Japanese", "Kenyan", "Malaysian", "Mexican", "Moroccan", "Russian", "Spanish", "Thai", "Tunisian", "Turkish", "Ukrainian", "Uruguayan", "Vietnamese"});
        setupSpinner(binding.spinnerIngredient, new String[]{"Select Ingredient", "Avocado", "Asparagus", "Bacon", "Baking Powder", "Basil", "Black Paper", "Bread", "Broccoli", "Breadcrumbs", "Butter", "Cacao", "Carrot", "Celery", "Cheddar Cheese", "Cherry Tomatoes", "Chilli Powder", "Eggs", "Flour", "Fries", "Garlic", "Ginger", "Honey", "Ice Cream", "Jam", "Lemon", "Lime", "Macaroni", "Mayonaise", "Milk", "Mint", "Mushrooms", "Mustard", "Noodles", "Oil", "Onions", "Orange", "Paprika", "Parsley", "Peanuts", "Pepper", "Potatoes", "Rice", "Salt", "Spaghetti", "Sugar", "Tomatoes", "Tuna", "Vanilla", "Water", "Yougurt", "Cream Cheese", "Caramel", "Squid", "Salmon", "Pork", "Banana", "Blueberries", "Peaches", "Udon Noodles", "Ham", "Hazlenuts", "Almonds", "Cherry", "Oats", "Appels", "Tofu", "Gochujang", "Muffins"});
    }

    private void setupSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                lastChangedSpinner = spinner;
                binding.progressBar.setVisibility(View.VISIBLE);
                handleSpinnerSelection();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void handleSpinnerSelection() {
        binding.searchRecycler.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        boolean isCategorySelected = binding.spinnerCategory.getSelectedItemPosition() > 0;
        boolean isAreaSelected = binding.spinnerArea.getSelectedItemPosition() > 0;
        boolean isIngredientSelected = binding.spinnerIngredient.getSelectedItemPosition() > 0;

        int selectedCount = (isCategorySelected ? 1 : 0) + (isAreaSelected ? 1 : 0) + (isIngredientSelected ? 1 : 0);
        if (selectedCount > 1) {
            Toast.makeText(getContext(), "Please select only one filter at a time.", Toast.LENGTH_SHORT).show();
            resetSpinners();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        if (selectedCount == 0) {
            showRandomMeals();
            getRandomMeal(7);
        } else {
            binding.randomSearchRecycler.setVisibility(View.GONE);
            handleFilteredSearch(isCategorySelected, isAreaSelected, isIngredientSelected);
        }
    }

    private void resetSpinners() {
        if (lastChangedSpinner != null) {
            lastChangedSpinner.setSelection(0);
        }
    }

    private void handleFilteredSearch(boolean isCategorySelected, boolean isAreaSelected, boolean isIngredientSelected) {
        String selectedCategory = isCategorySelected ? binding.spinnerCategory.getSelectedItem().toString() : null;
        String selectedArea = isAreaSelected ? binding.spinnerArea.getSelectedItem().toString() : null;
        String selectedIngredient = isIngredientSelected ? binding.spinnerIngredient.getSelectedItem().toString() : null;

        if (selectedCategory != null) {
            searchMealsByCategory(selectedCategory);
        } else if (selectedArea != null) {
            searchMealsByArea(selectedArea);
        } else if (selectedIngredient != null) {
            searchMealsByIngredient(selectedIngredient);
        }
    }

    private void searchMealsByCategory(String category) {
        Call<MealResponse> call = apiService.searchMealsByCategory(category);
        handleMealSearch(call);
    }

    private void searchMealsByArea(String area) {
        Call<MealResponse> call = apiService.searchMealsByArea(area);
        handleMealSearch(call);
    }

    private void searchMealsByIngredient(String ingredient) {
        Call<MealResponse> call = apiService.searchMealsByIngredient(ingredient);
        handleMealSearch(call);
    }

    private void handleMealSearch(Call<MealResponse> call) {
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.searchRecycler.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());
                    mealAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRandomMeal(int count) {
        randomMealsList.clear();
        randomMealAdapter.notifyDataSetChanged();
        binding.progressBar.setVisibility(View.VISIBLE);

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
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<RandomMealResponse> call, Throwable t) {
                    loadedCount[0]++;
                    if (loadedCount[0] == count) {
                        randomMealAdapter.notifyDataSetChanged();
                        binding.randomSearchRecycler.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void handleSearch(String query) {
        query = query.trim();
        if (query.isEmpty()) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchRecycler.setVisibility(View.GONE);

        String[] ingredients = {"Avocado", "Asparagus", "Bacon", "Baking Powder", "Basil", "Black Paper", "Bread", "Broccoli", "Breadcrumbs", "Butter", "Cacao", "Carrot", "Celery", "Cheddar Cheese", "Cherry Tomatoes", "Chilli Powder", "Eggs", "Flour", "Fries", "Garlic", "Ginger", "Honey", "Ice Cream", "Jam", "Lemon", "Lime", "Macaroni", "Mayonaise", "Milk", "Mint", "Mushrooms", "Mustard", "Noodles", "Oil", "Onions", "Orange", "Paprika", "Parsley", "Peanuts", "Pepper", "Potatoes", "Rice", "Salt", "Spaghetti", "Sugar", "Tomatoes", "Tuna", "Vanilla", "Water", "Yougurt", "Cream Cheese", "Caramel", "Squid", "Salmon", "Pork", "Banana", "Blueberries", "Peaches", "Udon Noodles", "Ham", "Hazlenuts", "Almonds", "Cherry", "Oats", "Appels", "Tofu", "Gochujang", "Muffins"};
        String[] areas = {"American", "British", "Canadian", "Chinese", "Croatian", "Dutch", "Egyptian", "Filipino", "French", "Greek", "Indian", "Irish", "Italian", "Jamaican", "Japanese", "Kenyan", "Malaysian", "Mexican", "Moroccan", "Russian", "Spanish", "Thai", "Tunisian", "Turkish", "Ukrainian", "Uruguayan", "Vietnamese"};
        String[] categories = {"Beef", "Breakfast", "Chicken", "Dessert", "Goat", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian"};
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

    private void searchMeals(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Call<MealResponse> call = apiService.searchMeals(query);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    mealsList.clear();
                    mealsList.addAll(response.body().getMeals());
                    mealAdapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    binding.searchRecycler.setVisibility(View.GONE);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}