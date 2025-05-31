package com.example.chicook.model.randomMeals;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.databinding.CarouselItemBinding;
import com.example.chicook.databinding.ResepItemBinding; // Pastikan sudah ada layout file untuk item
import com.squareup.picasso.Picasso;

import java.util.List;

public class RandomMealAdapter extends RecyclerView.Adapter<RandomMealAdapter.RandomMealViewHolder> {

    private List<RandomMeal> meals;

    public RandomMealAdapter(List<RandomMeal> meals) {
        this.meals = meals;
    }

    @Override
    public RandomMealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout untuk item makanan menggunakan ViewBinding
        CarouselItemBinding binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RandomMealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RandomMealViewHolder holder, int position) {
        RandomMeal meal = meals.get(position);
        holder.binding.carouselTitle.setText(meal.getStrMeal());
        Picasso.get().load(meal.getStrMealThumb()).into(holder.binding.carouselImage);  // Menampilkan gambar makanan
    }

    @Override
    public int getItemCount() {
        return meals.size();  // Mengembalikan jumlah makanan
    }

    public static class RandomMealViewHolder extends RecyclerView.ViewHolder {
        private CarouselItemBinding binding;

        public RandomMealViewHolder(CarouselItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
