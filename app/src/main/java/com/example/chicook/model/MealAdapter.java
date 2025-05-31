package com.example.chicook.model;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.databinding.ResepItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals;

    public MealAdapter(List<Meal> meals) {
        this.meals = meals;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout dengan ViewBinding
        ResepItemBinding binding = ResepItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.binding.recipeTitle.setText(meal.getMealName());
        holder.binding.recipeCategory.setText(meal.getCategory());
        holder.binding.recipeArea.setText(meal.getArea());

        // Gunakan Picasso untuk menampilkan gambar dari URL
        Picasso.get().load(meal.getMealThumb()).into(holder.binding.recipeImage);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        // Gunakan binding untuk akses view
        private ResepItemBinding binding;

        public MealViewHolder(ResepItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
