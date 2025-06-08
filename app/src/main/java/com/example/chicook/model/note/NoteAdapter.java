package com.example.chicook.model.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.FormActivity;
import com.example.chicook.databinding.NoteItemBinding;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<Note> notes;

    // Constructor yang menerima Context (bukan Activity)
    public NoteAdapter(Context context) {
        this.context = context;
        this.notes = new ArrayList<>();
    }

    // Mengubah metode untuk menambahkan daftar notes
    public void setNotes(ArrayList<Note> notes) {
        this.notes.clear();
        if (notes.size() > 0) {
            this.notes.addAll(notes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan ViewBinding untuk mengikat layout item
        NoteItemBinding binding = NoteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // ViewHolder menggunakan ViewBinding
    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final NoteItemBinding binding; // Membuat binding untuk item

        // Menerima ViewBinding sebagai parameter
        NoteViewHolder(NoteItemBinding binding) {
            super(binding.getRoot());  // Mengambil root view dari ViewBinding
            this.binding = binding;  // Menyimpan binding
        }

        void bind(Note note) {
            // Menggunakan ViewBinding untuk mengikat data ke layout
            binding.tvItemName.setText(note.getJudul());
            binding.tvItemNim.setText(note.getDeskripsi());

            // Menampilkan waktu pembaruan atau pembuatan
            if (note.getUpdatedAt() != null) {
                binding.tvItemTimestamp.setText("Updated at " + note.getUpdatedAt());
            } else if (note.getCreatedAt() != null) {
                binding.tvItemTimestamp.setText("Created at " + note.getCreatedAt());
            } else {
                binding.tvItemTimestamp.setText(note.getDeskripsi());
            }

            // Mengatur aksi ketika card diklik untuk mengedit note
            binding.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra(FormActivity.EXTRA_NOTE, note);
                // Memastikan bahwa activity bisa menangani hasil
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, FormActivity.REQUEST_UPDATE);
                }
            });
        }
    }
}
