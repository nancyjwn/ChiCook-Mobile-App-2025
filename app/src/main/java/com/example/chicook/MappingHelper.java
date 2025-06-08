package com.example.chicook;

import android.database.Cursor;

import com.example.chicook.data.sqlite.DatabaseContract;
import com.example.chicook.model.note.Note;

import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<Note> mapCursorToArrayList(Cursor cursor) {
        ArrayList<Note> bookList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                note.setJudul(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumn.JUDUL)));
                note.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumn.DESKRIPSI)));
                note.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumn.CREATED_AT)));
                note.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumn.UPDATED_AT)));
                bookList.add(note);
            }
            cursor.close();
        }
        return bookList;
    }
}

