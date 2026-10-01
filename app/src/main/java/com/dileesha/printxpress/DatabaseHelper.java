package com.dileesha.printxpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PrintXpress.db";
    public static final int DATABASE_VERSION = 3; // Bumped to 3 for Profile fields

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "ID";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_NAME = "NAME";
    public static final String COL_ADDRESS = "ADDRESS";

    // Orders Table
    public static final String TABLE_ORDERS = "orders";
    public static final String COL_ORDER_ID = "ORDER_ID";
    public static final String COL_ORDER_EMAIL = "USER_EMAIL";
    public static final String COL_PRODUCT = "PRODUCT";
    public static final String COL_QUANTITY = "QUANTITY";
    public static final String COL_DETAILS = "DETAILS";
    public static final String COL_STATUS = "STATUS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT, NAME TEXT, ADDRESS TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_EMAIL TEXT, PRODUCT TEXT, QUANTITY TEXT, DETAILS TEXT, STATUS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // --- User Methods ---
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Fetch user profile data
    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL=?", new String[]{email});
    }

    // Update user profile data
    public boolean updateProfile(String email, String name, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_ADDRESS, address);
        int result = db.update(TABLE_USERS, contentValues, COL_EMAIL + " = ?", new String[]{email});
        return result > 0;
    }

    // --- Order Methods ---
    public boolean insertOrder(String email, String product, String quantity, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ORDER_EMAIL, email);
        contentValues.put(COL_PRODUCT, product);
        contentValues.put(COL_QUANTITY, quantity);
        contentValues.put(COL_DETAILS, details);
        contentValues.put(COL_STATUS, "Processing");
        long result = db.insert(TABLE_ORDERS, null, contentValues);
        return result != -1;
    }

    public Cursor getOrders(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + COL_ORDER_EMAIL + "=?", new String[]{email});
    }

    public boolean cancelOrder(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_STATUS, "Cancelled");
        int result = db.update(TABLE_ORDERS, contentValues, COL_ORDER_ID + " = ?", new String[]{orderId});
        return result > 0;
    }
}