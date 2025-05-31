package com.example.chicook.model.meal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.databinding.ResepItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals;  // Hanya untuk menyimpan makanan

    public MealAdapter(List<Meal> meals) {
        this.meals = meals;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout untuk makanan menggunakan ViewBinding
        ResepItemBinding resepBinding = ResepItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MealViewHolder(resepBinding);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        // Bind data makanan ke dalam UI
        holder.binding.recipeTitle.setText(meal.getMealName());
        holder.binding.recipeCategory.setText(meal.getCategory());
        holder.binding.recipeArea.setText(meal.getArea());
        Picasso.get().load(meal.getMealThumb()).into(holder.binding.recipeImage);  // Menampilkan gambar makanan
    }

    @Override
    public int getItemCount() {
        return meals.size();  // Mengembalikan jumlah makanan
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        private ResepItemBinding binding;

        public MealViewHolder(ResepItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
