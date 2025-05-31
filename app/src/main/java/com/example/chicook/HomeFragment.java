package com.example.chicook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chicook.data.ApiConfig;
import com.example.chicook.data.ApiService;
import com.example.chicook.model.Meal;
import com.example.chicook.model.MealAdapter;
import com.example.chicook.model.MealResponse;

import com.example.chicook.databinding.FragmentHomeBinding;
import com.example.chicook.model.category.Category;
import com.example.chicook.model.category.CategoryAdapter;
import com.example.chicook.model.category.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MealAdapter mealAdapter;
    private CategoryAdapter categoryAdapter;  // Adapter untuk kategori
    private FragmentHomeBinding binding;

    private List<String> selectedCategories = new ArrayList<String>() {{
        add("Beef");
        add("Chicken");
        add("Seafood");
    }};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Setup RecyclerView dengan GridLayoutManager untuk makanan dan kategori
        binding.recommendationRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Untuk makanan

        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);

        // Memanggil API untuk mendapatkan kategori makanan
        Call<CategoryResponse> callCategory = apiService.categoryMeals("");  // Memanggil kategori
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

                    // Menampilkan kategori yang telah difilter tapi harus di inisialkan di atas apa apa mau di filter
                    categoryAdapter = new CategoryAdapter(filteredCategories);  // panggil filter
                    binding.topCategoryRecycler.setAdapter(categoryAdapter);  // Menampilkan kategori
                } else {
                    Toast.makeText(getContext(), "No categories found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call for categories failed: " + t.getMessage());
            }
        });

        // Memanggil API untuk mencari makanan berdasarkan nama (misalnya "Chicken")
        Call<MealResponse> callMeal = apiService.searchMeals("");  // Makanan berdasarkan nama
        callMeal.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter = new MealAdapter(response.body().getMeals());  // Menggunakan adapter untuk makanan
                    binding.recommendationRecycler.setAdapter(mealAdapter);  // Menampilkan makanan
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call for meals failed: " + t.getMessage());
            }
        });

        // Return the root view of the fragment
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Hapus binding saat view dihancurkan untuk mencegah memory leak
    }
}
