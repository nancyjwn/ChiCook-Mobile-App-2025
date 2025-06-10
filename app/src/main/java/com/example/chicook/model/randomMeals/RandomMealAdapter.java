package com.example.chicook.model.randomMeals;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.layout.activity.DetailMealActivity;
import com.example.chicook.databinding.CarouselItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RandomMealAdapter extends RecyclerView.Adapter<RandomMealAdapter.RandomMealViewHolder> {

    private List<RandomMeal> meals;
    private Context context;

    public RandomMealAdapter(List<RandomMeal> meals, Context context) {
        this.meals = meals;
        this.context = context;
    }

    @Override
    public RandomMealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CarouselItemBinding binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RandomMealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RandomMealViewHolder holder, int position) {
        RandomMeal meal = meals.get(position);
        holder.binding.carouselTitle.setText(meal.getStrMeal());
        Picasso.get().load(meal.getStrMealThumb()).into(holder.binding.carouselImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMealActivity.class);
            intent.putExtra("title", meal.getStrMeal());
            intent.putExtra("category", meal.getStrCategory());
            intent.putExtra("area", meal.getStrArea());
            intent.putExtra("thumb", meal.getStrMealThumb());
            intent.putExtra("instructions", meal.getStrInstructions());
            intent.putExtra("mealId", meal.getIdMeal());
            intent.putExtra("ingredients", meal.getStrIngredients());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class RandomMealViewHolder extends RecyclerView.ViewHolder {
        private CarouselItemBinding binding;

        public RandomMealViewHolder(CarouselItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}