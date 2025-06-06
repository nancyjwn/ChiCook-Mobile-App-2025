package com.example.chicook.model.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.DetailMealActivity;
import com.example.chicook.databinding.ResepItemBinding; // Gunakan ResepItemBinding untuk item
import com.squareup.picasso.Picasso;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context context;
    private Cursor cursor;
    private OnBookmarkClickListener listener;

    public BookmarkAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout resep_item.xml menggunakan ViewBinding
        ResepItemBinding binding = ResepItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new BookmarkViewHolder(binding);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(Intent intent);
    }

    public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            // Mengambil data dari cursor
            @SuppressLint("Range") String mealId = cursor.getString(cursor.getColumnIndex("meal_id"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("image_url"));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") String area = cursor.getString(cursor.getColumnIndex("area"));
            @SuppressLint("Range") String instructions = cursor.getString(cursor.getColumnIndex("instructions"));
            @SuppressLint("Range") String ingredients = cursor.getString(cursor.getColumnIndex("ingredients"));

            // Menangani kategori dan area kosong atau null
            if (category == null || category.isEmpty()) {
                holder.binding.recipeCategory.setVisibility(View.GONE);  // Sembunyikan kategori
                holder.binding.recipeCategoryIcon.setVisibility(View.GONE);  // Sembunyikan ikon kategori
            } else {
                holder.binding.recipeCategory.setVisibility(View.VISIBLE);  // Tampilkan kategori
                holder.binding.recipeCategoryIcon.setVisibility(View.VISIBLE);  // Tampilkan ikon kategori
                holder.binding.recipeCategory.setText(category);  // Menampilkan kategori
            }

            if (area == null || area.isEmpty()) {
                holder.binding.recipeArea.setVisibility(View.GONE);  // Sembunyikan area
                holder.binding.recipeAreaIcon.setVisibility(View.GONE);  // Sembunyikan ikon area
            } else {
                holder.binding.recipeArea.setVisibility(View.VISIBLE);  // Tampilkan area
                holder.binding.recipeAreaIcon.setVisibility(View.VISIBLE);  // Tampilkan ikon area
                holder.binding.recipeArea.setText(area);  // Menampilkan area
            }

            // Set data ke views menggunakan binding
            holder.binding.recipeTitle.setText(title);
            holder.binding.recipeCategory.setText(category);
            holder.binding.recipeArea.setText(area);
            Picasso.get().load(imageUrl).into(holder.binding.recipeImage);

            // Set listener on item click
            holder.itemView.setOnClickListener(v -> {
                // Mengirim data ke DetailMealActivity
                Intent intent = new Intent(context, DetailMealActivity.class);
                intent.putExtra("mealId", mealId);
                intent.putExtra("title", title);
                intent.putExtra("category", category);
                intent.putExtra("area", area);
                intent.putExtra("instructions", instructions);
                intent.putExtra("ingredients", ingredients);
                intent.putExtra("thumb", imageUrl);
                if (listener != null) {
                    listener.onBookmarkClick(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Method untuk mengupdate cursor
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();  // Pastikan cursor lama ditutup
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();  // Memberitahu adapter bahwa data telah diperbarui
        }
    }

    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ResepItemBinding binding;

        public BookmarkViewHolder(ResepItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}