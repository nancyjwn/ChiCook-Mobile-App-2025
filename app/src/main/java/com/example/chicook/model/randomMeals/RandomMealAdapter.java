package com.example.chicook.model.randomMeals;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.DetailMealActivity;
import com.example.chicook.databinding.CarouselItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RandomMealAdapter extends RecyclerView.Adapter<RandomMealAdapter.RandomMealViewHolder> {

    private List<RandomMeal> meals;
    private Context context;  // Menyimpan context untuk digunakan saat intent

    public RandomMealAdapter(List<RandomMeal> meals, Context context) {
        this.meals = meals;
        this.context = context;  // Inisialisasi context untuk digunakan di dalam adapter
    }

    @Override
    public RandomMealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout untuk item random meal menggunakan ViewBinding
        CarouselItemBinding binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RandomMealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RandomMealViewHolder holder, int position) {
        RandomMeal meal = meals.get(position);
        holder.binding.carouselTitle.setText(meal.getStrMeal());  // Menampilkan nama meal
        Picasso.get().load(meal.getStrMealThumb()).into(holder.binding.carouselImage);  // Menampilkan gambar meal

        // Set click listener untuk item random meal
        holder.itemView.setOnClickListener(v -> {
            // Mengirim data meal ke DetailMealActivity menggunakan Intent
            Intent intent = new Intent(context, DetailMealActivity.class);
            intent.putExtra("title", meal.getStrMeal());
            intent.putExtra("category", meal.getStrCategory());
            intent.putExtra("area", meal.getStrArea());
            intent.putExtra("thumb", meal.getStrMealThumb());
            intent.putExtra("instructions", meal.getStrInstructions());
            intent.putExtra("mealId", meal.getIdMeal());
            intent.putExtra("ingredients", meal.getStrIngredients());  // Mengirimkan daftar bahan
            context.startActivity(intent);  // Memulai DetailMealActivity
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();  // Mengembalikan jumlah item random meal
    }

    // ViewHolder untuk item random meal
    public static class RandomMealViewHolder extends RecyclerView.ViewHolder {
        private CarouselItemBinding binding;

        public RandomMealViewHolder(CarouselItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
