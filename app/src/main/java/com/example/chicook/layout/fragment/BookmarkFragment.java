package com.example.chicook.layout.fragment;

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

import com.example.chicook.ThemeHelper;
import com.example.chicook.data.database.BookmarkHelper;
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
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        recyclerView = binding.bookmarkRecycler;

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        bookmarkHelper = new BookmarkHelper(getContext());

        binding.progressBar.setVisibility(View.VISIBLE);
        fetchBookmarksInBackground();
        return binding.getRoot();
    }

    private void fetchBookmarksInBackground() {
        executorService.execute(() -> {
            Cursor cursor = bookmarkHelper.getAllBookmarks();

            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            handler.post(() -> {
                if (cursor.getCount() == 0) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    adapter = new BookmarkAdapter(getContext(), cursor);
                    adapter.setOnBookmarkClickListener(intent -> {
                        startActivityForResult(intent, REQUEST_CODE);
                    });
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    recyclerView.setAdapter(adapter);
                }
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
                refreshData();
            }
        }
    }

    public void refreshData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            Cursor cursor = bookmarkHelper.getAllBookmarks();

            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            handler.post(() -> {
                if (cursor.getCount() == 0) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.swapCursor(cursor);
                }
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
        executorService.shutdownNow();
    }
}