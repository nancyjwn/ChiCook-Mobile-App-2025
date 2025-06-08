package com.example.chicook.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cookbook.db";
    private static final int DATABASE_VERSION = 3;  // Update versi database jika diperlukan

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel bookmarks
        String CREATE_TABLE_BOOKMARKS = "CREATE TABLE " + DatabaseContract.TABLE_BOOKMARKS + " (" +
                DatabaseContract.BookmarkColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.BookmarkColumn.MEAL_ID + " TEXT, " +
                DatabaseContract.BookmarkColumn.TITLE + " TEXT, " +
                DatabaseContract.BookmarkColumn.CATEGORY + " TEXT, " +
                DatabaseContract.BookmarkColumn.AREA + " TEXT, " +
                DatabaseContract.BookmarkColumn.INSTRUCTIONS + " TEXT, " +
                DatabaseContract.BookmarkColumn.INGREDIENTS + " TEXT, " +
                DatabaseContract.BookmarkColumn.IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_TABLE_BOOKMARKS);

        // Membuat tabel notes
        String CREATE_TABLE_NOTE = "CREATE TABLE " + DatabaseContract.TABLE_NOTES + " (" +
                DatabaseContract.NoteColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.NoteColumn.JUDUL + " TEXT NOT NULL, " +
                DatabaseContract.NoteColumn.DESKRIPSI + " TEXT NOT NULL, " +
                DatabaseContract.NoteColumn.CREATED_AT + " TEXT, " +
                DatabaseContract.NoteColumn.UPDATED_AT + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tabel jika versi database meningkat
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_BOOKMARKS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NOTES);
        onCreate(db);  // Membuat ulang tabel
    }

    // Menyimpan bookmark ke dalam database
    public long saveBookmark(String mealId, String title, String category, String area, String instructions, String ingredients, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.BookmarkColumn.MEAL_ID, mealId);
        values.put(DatabaseContract.BookmarkColumn.TITLE, title);
        values.put(DatabaseContract.BookmarkColumn.CATEGORY, category);
        values.put(DatabaseContract.BookmarkColumn.AREA, area);
        values.put(DatabaseContract.BookmarkColumn.INSTRUCTIONS, instructions);
        values.put(DatabaseContract.BookmarkColumn.INGREDIENTS, ingredients);
        values.put(DatabaseContract.BookmarkColumn.IMAGE_URL, imageUrl);
        return db.insert(DatabaseContract.TABLE_BOOKMARKS, null, values);
    }

    // Menyimpan note ke dalam database
    public long saveNote(String title, String description, String createdAt, String updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NoteColumn.JUDUL, title);
        values.put(DatabaseContract.NoteColumn.DESKRIPSI, description);
        values.put(DatabaseContract.NoteColumn.CREATED_AT, createdAt);
        values.put(DatabaseContract.NoteColumn.UPDATED_AT, updatedAt);
        return db.insert(DatabaseContract.TABLE_NOTES, null, values);
    }

    // Mengambil semua bookmarks
    public Cursor getAllBookmarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.TABLE_BOOKMARKS, null, null, null, null, null, null);
    }

    // Mengambil semua notes
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseContract.TABLE_NOTES, null, null, null, null, null, null);
    }

    // Mengecek apakah bookmark sudah ada
    public boolean isBookmarked(String mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.TABLE_BOOKMARKS, new String[]{DatabaseContract.BookmarkColumn.MEAL_ID},
                DatabaseContract.BookmarkColumn.MEAL_ID + " = ?", new String[]{mealId}, null, null, null);
        boolean isBookmarked = cursor.getCount() > 0;
        cursor.close();
        return isBookmarked;
    }

    // Menghapus bookmark berdasarkan `meal_id`
    public void removeBookmark(String mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseContract.TABLE_BOOKMARKS, DatabaseContract.BookmarkColumn.MEAL_ID + " = ?", new String[]{mealId});
    }
}
