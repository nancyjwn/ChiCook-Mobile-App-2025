package com.example.chicook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.data.ApiConfig;
import com.example.chicook.data.ApiService;
import com.example.chicook.model.Meal;
import com.example.chicook.model.MealAdapter;
import com.example.chicook.model.MealResponse;
import com.example.chicook.databinding.FragmentHomeBinding;  // Import ViewBinding

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MealAdapter mealAdapter;
    private FragmentHomeBinding binding;  // Declare ViewBinding

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Setup RecyclerView with GridLayoutManager
        binding.recommendationRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Membuat instance Retrofit melalui ApiConfig
        ApiService apiService = ApiConfig.getCLient().create(ApiService.class);

        // Memanggil API untuk mencari makanan berdasarkan nama (kosong untuk random) ubah bagian search sama "..." misalnya kalau mau spesifik
        Call<MealResponse> call = apiService.searchMeals("Chicken");

        // Melakukan request dan mendapatkan respons
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter = new MealAdapter(response.body().getMeals());
                    binding.recommendationRecycler.setAdapter(mealAdapter);  // Menggunakan ViewBinding untuk mengakses RecyclerView
                } else {
                    Toast.makeText(getContext(), "No meals found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Log.e("HomeFragment", "API call failed: " + t.getMessage());
            }
        });

        // Return the root view of the fragment
        return binding.getRoot();  // Menggunakan ViewBinding untuk mengembalikan root view
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Hapus binding saat view dihancurkan untuk mencegah memory leak
    }
}
