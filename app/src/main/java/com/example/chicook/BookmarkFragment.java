package com.example.chicook;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chicook.data.sqlite.BookmarkHelper;
import com.example.chicook.databinding.FragmentBookmarkBinding;
import com.example.chicook.model.bookmark.BookmarkAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private BookmarkHelper bookmarkHelper; // BookmarkHelper
    private FragmentBookmarkBinding binding;
    public static final int REQUEST_CODE = 1;

    private ExecutorService executorService;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ThemeHelper.applyTheme(requireContext());
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        recyclerView = binding.bookmarkRecycler;

        // Initialize ExecutorService and Handler
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper()); // Handler to update UI on main thread

        bookmarkHelper = new BookmarkHelper(getContext()); // Initialize BookmarkHelper

        // Show ProgressBar before fetching data
        binding.progressBar.setVisibility(View.VISIBLE);

        // Fetch data in the background thread
        fetchBookmarksInBackground();

        return binding.getRoot();
    }

    private void fetchBookmarksInBackground() {
        executorService.execute(() -> {
            // Fetch data from SQLite using BookmarkHelper
            Cursor cursor = bookmarkHelper.getAllBookmarks();

            // Add artificial delay (optional)
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            // After data is fetched, update UI on the main thread
            handler.post(() -> {
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
        });
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

        executorService.execute(() -> {
            // Fetch updated bookmarks from the database
            Cursor cursor = bookmarkHelper.getAllBookmarks();

            // Add artificial delay (optional)
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            // Update UI on the main thread
            handler.post(() -> {
                adapter.swapCursor(cursor);
                binding.progressBar.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bookmarkHelper != null) {
            bookmarkHelper.close();
        }
        executorService.shutdownNow(); // Shut down the executor when fragment is destroyed
    }
}