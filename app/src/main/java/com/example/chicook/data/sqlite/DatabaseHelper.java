package com.example.chicook.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cookbook.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db);
    }
}
