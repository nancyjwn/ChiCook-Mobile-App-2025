package com.example.chicook.data.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    // Tabel bookmarks dan notes
    public static final String TABLE_BOOKMARKS = "bookmarks";
    public static final String TABLE_NOTES = "note";

    public static final class BookmarkColumn implements BaseColumns {
        public static final String MEAL_ID = "meal_id";
        public static final String TITLE = "title";
        public static final String CATEGORY = "category";
        public static final String AREA = "area";
        public static final String INSTRUCTIONS = "instructions";
        public static final String INGREDIENTS = "ingredients";
        public static final String IMAGE_URL = "image_url";
    }

    public static final class NoteColumn implements BaseColumns {
        public static final String JUDUL = "judul";
        public static final String DESKRIPSI = "deskripsi";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
    }
}
