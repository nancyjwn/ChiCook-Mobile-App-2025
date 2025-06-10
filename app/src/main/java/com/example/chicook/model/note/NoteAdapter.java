package com.example.chicook.model.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicook.layout.activity.FormActivity;
import com.example.chicook.databinding.NoteItemBinding;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<Note> notes;

    public NoteAdapter(Context context) {
        this.context = context;
        this.notes = new ArrayList<>();
    }

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

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final NoteItemBinding binding;

        NoteViewHolder(NoteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Note note) {
            binding.tvItemName.setText(note.getJudul());
            binding.tvItemNim.setText(note.getDeskripsi());

            if (note.getUpdatedAt() != null) {
                binding.tvItemTimestamp.setText("Updated at " + note.getUpdatedAt());
            } else if (note.getCreatedAt() != null) {
                binding.tvItemTimestamp.setText("Created at " + note.getCreatedAt());
            } else {
                binding.tvItemTimestamp.setText(note.getDeskripsi());
            }

            binding.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra(FormActivity.EXTRA_NOTE, note);
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, FormActivity.REQUEST_UPDATE);
                }
            });
        }
    }
}