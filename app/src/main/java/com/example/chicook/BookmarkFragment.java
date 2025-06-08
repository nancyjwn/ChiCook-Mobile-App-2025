package com.example.chicook;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;  // Gunakan GridLayoutManager untuk grid
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chicook.data.sqlite.BookmarkHelper;
import com.example.chicook.data.sqlite.DatabaseHelper;
import com.example.chicook.databinding.FragmentBookmarkBinding;
import com.example.chicook.model.bookmark.BookmarkAdapter;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private BookmarkHelper bookmarkHelper; // Gunakan BookmarkHelper
    private FragmentBookmarkBinding binding;
    public static final int REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        recyclerView = binding.bookmarkRecycler;

        bookmarkHelper = new BookmarkHelper(getContext()); // Inisialisasi BookmarkHelper

        // Mengambil data dari SQLite melalui BookmarkHelper
        Cursor cursor = bookmarkHelper.getAllBookmarks(); // Menggunakan BookmarkHelper untuk mendapatkan data

        // Menghubungkan cursor dengan adapter
        adapter = new BookmarkAdapter(getContext(), cursor);
        // Tambahkan baris ini agar klik item berfungsi
        adapter.setOnBookmarkClickListener(intent -> {
            startActivityForResult(intent, REQUEST_CODE);
        });

        // Menggunakan GridLayoutManager untuk menampilkan data dalam 2 kolom
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Grid 2 kolom
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String status = data.getStringExtra("status");
            if ("added".equals(status) || "removed".equals(status)) {
                // Setelah menerima hasil, perbarui data di BookmarkFragment
                refreshData();  // Ini akan memperbarui RecyclerView di BookmarkFragment
            }
        }
    }

    // Menambahkan method untuk memperbarui adapter setelah data disimpan
    public void refreshData() {
        Cursor cursor = bookmarkHelper.getAllBookmarks(); // Mengambil ulang data dari BookmarkHelper
        adapter.swapCursor(cursor);  // Update RecyclerView dengan data terbaru
    }
}
