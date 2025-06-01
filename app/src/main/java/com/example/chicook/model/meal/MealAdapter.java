package com.example.chicook.model.meal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.DetailMealActivity;
import com.example.chicook.databinding.ResepItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals;
    private Context context;

    public MealAdapter(List<Meal> meals, Context context) {
        this.meals = meals;
        this.context = context;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout for meal item using ViewBinding
        ResepItemBinding resepBinding = ResepItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MealViewHolder(resepBinding);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        // Bind meal data to UI components
        holder.binding.recipeTitle.setText(meal.getMealName());
        holder.binding.recipeCategory.setText(meal.getCategory());
        holder.binding.recipeArea.setText(meal.getArea());
        Picasso.get().load(meal.getMealThumb()).into(holder.binding.recipeImage);  // Set meal image

        // If category is null or empty, hide category and icon
        if (meal.getCategory() == null || meal.getCategory().isEmpty()) {
            holder.binding.recipeCategory.setVisibility(View.GONE);
            holder.binding.recipeCategoryIcon.setVisibility(View.GONE);
        } else {
            holder.binding.recipeCategory.setVisibility(View.VISIBLE);
            holder.binding.recipeCategoryIcon.setVisibility(View.VISIBLE);
        }

        // If area is null or empty, hide area and icon
        if (meal.getArea() == null || meal.getArea().isEmpty()) {
            holder.binding.recipeArea.setVisibility(View.GONE);
            holder.binding.recipeAreaIcon.setVisibility(View.GONE);
        } else {
            holder.binding.recipeArea.setVisibility(View.VISIBLE);
            holder.binding.recipeAreaIcon.setVisibility(View.VISIBLE);
        }

        // Handle click on meal item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMealActivity.class);
            intent.putExtra("mealId", meal.getIdMeal());  // Kirimkan ID meal ke DetailMealActivity
            intent.putExtra("title", meal.getMealName());
            intent.putExtra("category", meal.getCategory());
            intent.putExtra("area", meal.getArea());
            intent.putExtra("thumb", meal.getMealThumb());
            intent.putExtra("instructions", meal.getInstructions());
            intent.putExtra("linkYoutube", meal.getYoutubeLink());
            intent.putExtra("ingredients", meal.getIngredients());  // Mengirimkan daftar ingredients
            intent.putExtra("measures", meal.getMeasures());  // Mengirimkan daftar measures
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();  // Return the number of meals
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        private ResepItemBinding binding;

        public MealViewHolder(ResepItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}