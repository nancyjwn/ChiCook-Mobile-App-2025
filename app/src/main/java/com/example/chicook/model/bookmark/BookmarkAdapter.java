package com.example.chicook.model.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.databinding.ResepItemBinding; // Gunakan ResepItemBinding untuk item
import com.squareup.picasso.Picasso;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context context;
    private Cursor cursor;

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

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            // Mengambil data dari cursor
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String imageUrl = cursor.getString(cursor.getColumnIndex("image_url"));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));  // Ambil kategori
            @SuppressLint("Range") String area = cursor.getString(cursor.getColumnIndex("area"));  // Ambil area

            // Set data ke views menggunakan binding
            holder.binding.recipeTitle.setText(title);
            holder.binding.recipeCategory.setText(category);  // Menampilkan kategori
            holder.binding.recipeArea.setText(area);  // Menampilkan area
            Picasso.get().load(imageUrl).into(holder.binding.recipeImage);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Method untuk mengupdate cursor
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close(); // Pastikan cursor lama ditutup
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
