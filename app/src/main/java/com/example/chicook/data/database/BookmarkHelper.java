package com.example.chicook.data.database;

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

    public Cursor getAllBookmarks() {
        return db.query(DatabaseContract.TABLE_BOOKMARKS, null, null, null, null, null, null);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
