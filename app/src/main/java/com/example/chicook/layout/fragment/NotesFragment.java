package com.example.chicook.layout.fragment;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.chicook.ThemeHelper;
import com.example.chicook.data.database.NoteHelper;
import com.example.chicook.databinding.FragmentNotesBinding;
import com.example.chicook.layout.activity.FormActivity;
import com.example.chicook.model.note.MappingHelper;
import com.example.chicook.model.note.Note;
import com.example.chicook.model.note.NoteAdapter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesFragment extends Fragment {

    private RecyclerView rvStudent;
    private NoteAdapter adapter;
    private NoteHelper noteHelper;
    private FragmentNotesBinding binding;
    private final int REQUEST_ADD = 100;
    private final int REQUEST_UPDATE = 200;

    private ExecutorService executorService;
    private Handler handler;

    public NotesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ThemeHelper.applyTheme(requireContext());
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        noteHelper = NoteHelper.getInstance(getContext());

        rvStudent = binding.rvStudents;
        rvStudent.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new NoteAdapter(getContext());
        rvStudent.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), FormActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        });

        loadNotes();

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
        binding.progressBar.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            noteHelper.open();
            Cursor notesCursor = noteHelper.queryAll();
            ArrayList<Note> notes = MappingHelper.mapCursorToArrayList(notesCursor);
            notesCursor.close();
            noteHelper.close();

            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            handler.post(() -> {
                if (notes.size() > 0) {
                    adapter.setNotes(notes);
                    binding.noDataCard.setVisibility(View.GONE);
                } else {
                    adapter.setNotes(new ArrayList<>());
                    binding.noDataCard.setVisibility(View.VISIBLE);
                }
                binding.progressBar.setVisibility(View.GONE);
            });
        });
    }

    private void searchNotes(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvStudents.setVisibility(View.GONE);
        executorService.execute(() -> {
            noteHelper.open();
            Cursor cursor = noteHelper.searchByJudul(query);
            ArrayList<Note> result = MappingHelper.mapCursorToArrayList(cursor);
            cursor.close();
            noteHelper.close();

            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            handler.post(() -> {
                if (result.size() > 0) {
                    adapter.setNotes(result);
                    binding.noData.setVisibility(View.GONE);
                } else {
                    adapter.setNotes(new ArrayList<>());
                    binding.noData.setVisibility(View.VISIBLE);
                }
                binding.progressBar.setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.VISIBLE);
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
}