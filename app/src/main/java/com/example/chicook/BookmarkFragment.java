package com.example.chicook;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chicook.data.sqlite.BookmarkHelper;
import com.example.chicook.databinding.FragmentBookmarkBinding;
import com.example.chicook.model.bookmark.BookmarkAdapter;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private BookmarkHelper bookmarkHelper; // BookmarkHelper
    private FragmentBookmarkBinding binding;
    public static final int REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        recyclerView = binding.bookmarkRecycler;

        bookmarkHelper = new BookmarkHelper(getContext()); // Initialize BookmarkHelper

        // Show ProgressBar before fetching data
        binding.progressBar.setVisibility(View.VISIBLE);

        // Fetch data in the background thread
        fetchBookmarksInBackground();

        return binding.getRoot();
    }

    private void fetchBookmarksInBackground() {
        new Thread(() -> {
            // Fetch data from SQLite using BookmarkHelper
            Cursor cursor = bookmarkHelper.getAllBookmarks();

            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // 1 second delay

            // After data is fetched, update UI on the main thread
            new Handler(getActivity().getMainLooper()).post(() -> {
                // Connect cursor with adapter
                adapter = new BookmarkAdapter(getContext(), cursor);

                // Set item click listener
                adapter.setOnBookmarkClickListener(intent -> {
                    startActivityForResult(intent, REQUEST_CODE);
                });

                // Set up GridLayoutManager for 2 columns
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Grid 2 columns
                recyclerView.setAdapter(adapter);

                // Hide ProgressBar after data is loaded
                binding.progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String status = data.getStringExtra("status");
            if ("added".equals(status) || "removed".equals(status)) {
                // After receiving the result, update data in BookmarkFragment
                refreshData();  // This will update RecyclerView in BookmarkFragment
            }
        }
    }

    // Method to refresh adapter after data is saved
    public void refreshData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            Cursor cursor = bookmarkHelper.getAllBookmarks();
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // 1 second delay
            new Handler(getActivity().getMainLooper()).post(() -> {
                adapter.swapCursor(cursor);
                binding.progressBar.setVisibility(View.GONE);
            });
        }).start();
    }
}
