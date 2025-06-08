package com.example.chicook.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BookmarkHelper {

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    public BookmarkHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
    }

    // Menyimpan bookmark ke dalam database
    public long saveBookmark(String mealId, String title, String category, String area, String instructions, String ingredients, String imageUrl) {
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

    // Mengambil semua bookmark dari database
    public Cursor getAllBookmarks() {
        return db.query(DatabaseContract.TABLE_BOOKMARKS, null, null, null, null, null, null);
    }

    // Mengambil bookmark berdasarkan `mealId`
    public Cursor getBookmarkByMealId(String mealId) {
        Cursor cursor = db.query(DatabaseContract.TABLE_BOOKMARKS,
                null,
                DatabaseContract.BookmarkColumn.MEAL_ID + " = ?",
                new String[]{mealId},
                null, null, null);
        return cursor;
    }

    // Menghapus bookmark berdasarkan `meal_id`
    public void removeBookmark(String mealId) {
        db.delete(DatabaseContract.TABLE_BOOKMARKS, DatabaseContract.BookmarkColumn.MEAL_ID + " = ?", new String[]{mealId});
    }

    // Mengecek apakah resep sudah dibookmark
    public boolean isBookmarked(String mealId) {
        Cursor cursor = db.query(DatabaseContract.TABLE_BOOKMARKS, new String[]{DatabaseContract.BookmarkColumn.MEAL_ID},
                DatabaseContract.BookmarkColumn.MEAL_ID + " = ?", new String[]{mealId}, null, null, null);
        boolean isBookmarked = cursor.getCount() > 0;
        cursor.close();
        return isBookmarked;
    }

    // Pastikan untuk menutup database
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
