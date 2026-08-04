package com.holkar.institute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HolkarInstitute.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USER = "users";
    private static final String COL_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Register User
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email.toLowerCase().trim());
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    // Check if Email exists in database
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COL_ID},
                COL_EMAIL + "=?",
                new String[]{email.toLowerCase().trim()},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Check exact Login Credentials (Email + Password)
    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COL_ID},
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email.toLowerCase().trim(), password},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}
