package com.example.chicook.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nama database dan versi
    private static final String DATABASE_NAME = "cookbook.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Pembuatan tabel `bookmarks`
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_BOOKMARKS = "CREATE TABLE bookmarks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "meal_id TEXT, " +
                "title TEXT, " +
                "category TEXT, " +
                "area TEXT, " +
                "instructions TEXT, " +
                "ingredients TEXT, " +
                "image_url TEXT)";
        db.execSQL(CREATE_TABLE_BOOKMARKS);
    }

    // Update tabel jika versi database berubah
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db);
    }

    // Menyimpan bookmark ke dalam database
    public long saveBookmark(String mealId, String title, String category, String area, String instructions, String ingredients, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("meal_id", mealId);
        values.put("title", title);
        values.put("category", category);
        values.put("area", area);
        values.put("instructions", instructions);
        values.put("ingredients", ingredients);
        values.put("image_url", imageUrl);

        // Menyimpan data ke tabel `bookmarks`
        return db.insert("bookmarks", null, values);
    }

    // Menghapus bookmark berdasarkan `meal_id`
    public void removeBookmark(String mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("bookmarks", "meal_id = ?", new String[]{mealId});
    }

    // Mengecek apakah resep sudah dibookmark
    public boolean isBookmarked(String mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("bookmarks", new String[]{"meal_id"}, "meal_id = ?", new String[]{mealId}, null, null, null);
        boolean isBookmarked = cursor.getCount() > 0;
        cursor.close();
        return isBookmarked;
    }

    // Mengambil semua bookmark dari database
    public Cursor getAllBookmarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("bookmarks", null, null, null, null, null, null);
    }
}
