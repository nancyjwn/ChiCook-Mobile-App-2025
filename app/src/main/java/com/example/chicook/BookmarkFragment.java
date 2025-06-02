package com.example.chicook;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;  // Gunakan GridLayoutManager untuk grid
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chicook.data.sqlite.DatabaseHelper;
import com.example.chicook.databinding.FragmentBookmarkBinding;
import com.example.chicook.model.bookmark.BookmarkAdapter;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FragmentBookmarkBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        recyclerView = binding.bookmarkRecycler;

        databaseHelper = new DatabaseHelper(getContext());

        // Mengambil data dari SQLite
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("bookmarks", null, null, null, null, null, null);

        // Menghubungkan cursor dengan adapter
        adapter = new BookmarkAdapter(getContext(), cursor);

        // Menggunakan GridLayoutManager untuk menampilkan data dalam 2 kolom
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Grid 2 kolom
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    // Menambahkan method untuk memperbarui adapter setelah data disimpan
    public void refreshData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("bookmarks", null, null, null, null, null, null);
        adapter.swapCursor(cursor);  // Mengganti cursor di adapter
    }
}
