package com.example.chicook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.chicook.data.sqlite.NoteHelper;
import com.example.chicook.databinding.FragmentNotesBinding;
import com.example.chicook.model.note.Note;
import com.example.chicook.model.note.NoteAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesFragment extends Fragment {

    private RecyclerView rvStudent;
    private NoteAdapter adapter;
    private NoteHelper noteHelper;
    private FragmentNotesBinding binding;  // Deklarasi ViewBinding
    private final int REQUEST_ADD = 100;
    private final int REQUEST_UPDATE = 200;

    public NotesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(inflater, container, false);  // Menggunakan ViewBinding
        View view = binding.getRoot();

        // Set up RecyclerView with GridLayoutManager
        rvStudent = binding.rvStudents;  // Menggunakan binding untuk akses RecyclerView
        rvStudent.setLayoutManager(new GridLayoutManager(getContext(), 2));  // Menggunakan GridLayoutManager dengan 2 kolom
        adapter = new NoteAdapter(getContext());
        rvStudent.setAdapter(adapter);

        // Initialize NoteHelper
        noteHelper = NoteHelper.getInstance(getContext());

        // Set up FloatingActionButton click listener
        binding.fabAdd.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), FormActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        });

        // Load notes into RecyclerView
        loadNotes();

        // Set up SearchView listener
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchNotes(newText);
                return true;
            }
        });

        return view;
    }

    private void loadNotes() {
        new LoadNotesAsync(getContext(), notes -> {
            if (notes.size() > 0) {
                adapter.setNotes(notes);
                binding.noData.setVisibility(View.GONE);
            } else {
                adapter.setNotes(new ArrayList<>());
                binding.noData.setVisibility(View.VISIBLE);
                showToast("No data available");
            }
        }).execute();
    }

    private void searchNotes(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            noteHelper.open();
            Cursor cursor = noteHelper.searchByJudul(query);
            ArrayList<Note> result = MappingHelper.mapCursorToArrayList(cursor);
            cursor.close();
            noteHelper.close();

            handler.post(() -> {
                if (result.size() > 0) {
                    adapter.setNotes(result);
                    binding.noData.setVisibility(View.GONE);
                } else {
                    adapter.setNotes(new ArrayList<>());
                    binding.noData.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD && resultCode == FormActivity.RESULT_ADD) {
            showToast("Notes added successfully");
            loadNotes();
        } else if (requestCode == REQUEST_UPDATE) {
            if (resultCode == FormActivity.RESULT_UPDATE) {
                showToast("Notes updated successfully");
                loadNotes();
            } else if (resultCode == FormActivity.RESULT_DELETE) {
                showToast("Notes deleted successfully");
                loadNotes();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (noteHelper != null) {
            noteHelper.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private static class LoadNotesAsync {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadNotesCallback> weakCallback;

        private LoadNotesAsync(Context context, LoadNotesCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        void execute() {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Context context = weakContext.get();
                if (context != null) {
                    NoteHelper noteHelper = NoteHelper.getInstance(context);
                    noteHelper.open();
                    Cursor notesCursor = noteHelper.queryAll();
                    ArrayList<Note> notes = MappingHelper.mapCursorToArrayList(notesCursor);
                    notesCursor.close();
                    noteHelper.close();

                    handler.post(() -> {
                        LoadNotesCallback callback = weakCallback.get();
                        if (callback != null) {
                            callback.postExecute(notes);
                        }
                    });
                }
            });
        }
    }

    interface LoadNotesCallback {
        void postExecute(ArrayList<Note> notes);
    }
}
