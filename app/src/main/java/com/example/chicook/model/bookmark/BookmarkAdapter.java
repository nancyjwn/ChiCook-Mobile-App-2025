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

import com.example.chicook.layout.activity.DetailMealActivity;
import com.example.chicook.data.database.BookmarkHelper;
import com.example.chicook.databinding.ResepItemBinding;
import com.squareup.picasso.Picasso;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context context;
    private Cursor cursor;
    private BookmarkHelper bookmarkHelper;
    private OnBookmarkClickListener listener;

    public BookmarkAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.bookmarkHelper = new BookmarkHelper(context);
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                holder.binding.recipeCategory.setVisibility(View.GONE);
                holder.binding.recipeCategoryIcon.setVisibility(View.GONE);
            } else {
                holder.binding.recipeCategory.setVisibility(View.VISIBLE);
                holder.binding.recipeCategoryIcon.setVisibility(View.VISIBLE);
                holder.binding.recipeCategory.setText(category);
            }
            if (area == null || area.isEmpty()) {
                holder.binding.recipeArea.setVisibility(View.GONE);
                holder.binding.recipeAreaIcon.setVisibility(View.GONE);
            } else {
                holder.binding.recipeArea.setVisibility(View.VISIBLE);
                holder.binding.recipeAreaIcon.setVisibility(View.VISIBLE);
                holder.binding.recipeArea.setText(area);
            }

            holder.binding.recipeTitle.setText(title);
            holder.binding.recipeCategory.setText(category);
            holder.binding.recipeArea.setText(area);
            Picasso.get().load(imageUrl).into(holder.binding.recipeImage);

            holder.itemView.setOnClickListener(v -> {
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

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
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