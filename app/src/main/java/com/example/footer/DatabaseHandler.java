package com.example.footer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookdiary";

    // Table names
    private static final String TABLE_ACCOUNT = "account";
    private static final String TABLE_FINISHED = "finished";
    private static final String TABLE_WISHLIST = "whistlist";

    // Account Table Columns
    private static final String KEY_ID_USER = "id_user";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // Finished Table Columns
    private static final String KEY_ID_FINISHED = "id_finished";
    private static final String KEY_TITLE_FINISHED = "title_finished";
    private static final String KEY_AUTHOR_FINISHED = "author_finished";
    private static final String KEY_DATE_FINISHED = "date_finished";
    private static final String KEY_SUMMARY = "summary";
    private static final String KEY_RATE = "rate";
    private static final String KEY_COVER_FINISHED = "cover_finished";

    // Wishlist Table Columns
    private static final String KEY_ID_WISHLIST = "id_whistlist";
    private static final String KEY_TITLE_WISHLIST = "title_whistlist";
    private static final String KEY_AUTHOR_WISHLIST = "author_whistlist";
    private static final String KEY_COVER_WISHLIST = "cover_whistlist";
    private static final String KEY_LINK_WISHLIST = "link_whistlist";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Account table
        String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("
                + KEY_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_ACCOUNT_TABLE);

        // Create Finished table
        String CREATE_FINISHED_TABLE = "CREATE TABLE " + TABLE_FINISHED + "("
                + KEY_ID_FINISHED + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ID_USER + " INTEGER,"
                + KEY_TITLE_FINISHED + " TEXT,"
                + KEY_AUTHOR_FINISHED + " TEXT,"
                + KEY_DATE_FINISHED + " DATE,"
                + KEY_SUMMARY + " TEXT,"
                + KEY_RATE + " REAL,"
                + KEY_COVER_FINISHED + " BLOB,"
                + "FOREIGN KEY(" + KEY_ID_USER + ") REFERENCES " + TABLE_ACCOUNT + "(" + KEY_ID_USER + "))";
        db.execSQL(CREATE_FINISHED_TABLE);

        // Create Wishlist table
        String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHLIST + "("
                + KEY_ID_WISHLIST + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ID_USER + " INTEGER,"
                + KEY_TITLE_WISHLIST + " TEXT,"
                + KEY_AUTHOR_WISHLIST + " TEXT,"
                + KEY_COVER_WISHLIST + " BLOB,"
                + KEY_LINK_WISHLIST + " TEXT,"
                + "FOREIGN KEY(" + KEY_ID_USER + ") REFERENCES " + TABLE_ACCOUNT + "(" + KEY_ID_USER + "))";
        db.execSQL(CREATE_WISHLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINISHED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        onCreate(db);
    }

    // Add new user
    public long addUser(String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        long id = db.insert(TABLE_ACCOUNT, null, values);
        db.close();
        return id;
    }

    // Check login
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID_USER};
        String selection = KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_ACCOUNT, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Get user ID by username
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID_USER};
        String selection = KEY_USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_ACCOUNT, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    // Check if username exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID_USER};
        String selection = KEY_USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_ACCOUNT, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Tambahkan fungsi untuk cek email
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID_USER};
        String selection = KEY_EMAIL + "=?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_ACCOUNT, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Tambahkan method ini di dalam class DatabaseHandler
    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Hapus data user dari tabel account
        db.delete(TABLE_ACCOUNT, KEY_ID_USER + "=?", new String[]{String.valueOf(userId)});
        
        // Hapus data terkait di tabel finished
        db.delete(TABLE_FINISHED, KEY_ID_USER + "=?", new String[]{String.valueOf(userId)});
        
        // Hapus data terkait di tabel wishlist
        db.delete(TABLE_WISHLIST, KEY_ID_USER + "=?", new String[]{String.valueOf(userId)});
        
        db.close();
    }

    // Tambahkan method ini untuk mendapatkan email berdasarkan userId
    public String getEmailByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_EMAIL};
        String selection = KEY_ID_USER + "=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_ACCOUNT, columns, selection, selectionArgs, null, null, null);
        String email = "";
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return email;
    }

    public boolean updateUser(String oldUsername, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, newUsername);
        values.put(KEY_PASSWORD, newPassword);
        
        int result = db.update(TABLE_ACCOUNT, 
                              values, 
                              KEY_USERNAME + "=?", 
                              new String[]{oldUsername});
        return result > 0;
    }

    // Tambahkan method baru untuk update email
    public boolean updateEmail(int userId, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, newEmail);
        
        int result = db.update(TABLE_ACCOUNT, 
                              values, 
                              KEY_ID_USER + "=?", 
                              new String[]{String.valueOf(userId)});
        return result > 0;
    }

    public int getFinishedBooksCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FINISHED, 
            new String[]{"COUNT(*)"},
            KEY_ID_USER + "=?",
            new String[]{String.valueOf(userId)},
            null, null, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getWishlistBooksCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WISHLIST, 
            new String[]{"COUNT(*)"},
            KEY_ID_USER + "=?",
            new String[]{String.valueOf(userId)},
            null, null, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public boolean updateUserProfile(int userId, String newEmail, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(KEY_EMAIL, newEmail);
        values.put(KEY_USERNAME, newUsername);
        values.put(KEY_PASSWORD, newPassword);
        
        int result = db.update(TABLE_ACCOUNT, 
                             values, 
                             KEY_ID_USER + "=?", 
                             new String[]{String.valueOf(userId)});
        return result > 0;
    }

    public Cursor getFinishedBooksByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FINISHED,
                null,
                KEY_ID_USER + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                KEY_DATE_FINISHED + " DESC");
    }

    public Cursor getWishlistBooksByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WISHLIST,
                null,
                KEY_ID_USER + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                KEY_TITLE_WISHLIST + " ASC");
    }
}
