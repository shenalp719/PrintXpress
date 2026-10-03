package com.dileesha.printxpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PrintXpress.db";
    public static final int DATABASE_VERSION = 5;

    // Users & Orders Tables
    public static final String TABLE_USERS = "users";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_NAME = "NAME";
    public static final String COL_ADDRESS = "ADDRESS";
    public static final String COL_PHONE = "PHONE";

    public static final String TABLE_ORDERS = "orders";
    public static final String COL_ORDER_ID = "ORDER_ID";
    public static final String COL_ORDER_EMAIL = "USER_EMAIL";
    public static final String COL_PRODUCT = "PRODUCT";
    public static final String COL_QUANTITY = "QUANTITY";
    public static final String COL_DETAILS = "DETAILS";
    public static final String COL_DELIVERY = "DELIVERY_METHOD";
    public static final String COL_PAYMENT = "PAYMENT_METHOD";
    public static final String COL_TOTAL = "TOTAL_PRICE";
    public static final String COL_STATUS = "STATUS";

    // Saved Designs Table
    public static final String TABLE_SAVED_DESIGNS = "saved_designs";
    public static final String COL_DESIGN_ID = "DESIGN_ID";
    // Reuses USER_EMAIL, PRODUCT, DETAILS columns

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT, NAME TEXT, ADDRESS TEXT, PHONE TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_EMAIL TEXT, PRODUCT TEXT, QUANTITY TEXT, DETAILS TEXT, DELIVERY_METHOD TEXT, PAYMENT_METHOD TEXT, TOTAL_PRICE TEXT, STATUS TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_SAVED_DESIGNS + " (DESIGN_ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_EMAIL TEXT, PRODUCT TEXT, DETAILS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_DESIGNS);
        onCreate(db);
    }

    // --- User Methods ---
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EMAIL, email);
        cv.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, cv) != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL=?", new String[]{email});
    }

    public boolean updateProfileDetails(String email, String name, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_PHONE, phone);
        cv.put(COL_ADDRESS, address);
        return db.update(TABLE_USERS, cv, COL_EMAIL + " = ?", new String[]{email}) > 0;
    }

    // Update Email securely (updates users, orders, and saved designs)
    public boolean updateSecureEmail(String oldEmail, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EMAIL, newEmail);
        int result = db.update(TABLE_USERS, cv, COL_EMAIL + "=?", new String[]{oldEmail});

        if (result > 0) {
            // Update orders and saved designs to match the new email
            ContentValues relatedCv = new ContentValues();
            relatedCv.put(COL_ORDER_EMAIL, newEmail);
            db.update(TABLE_ORDERS, relatedCv, COL_ORDER_EMAIL + "=?", new String[]{oldEmail});
            db.update(TABLE_SAVED_DESIGNS, relatedCv, COL_ORDER_EMAIL + "=?", new String[]{oldEmail});
            return true;
        }
        return false;
    }

    // --- Order Methods ---
    public boolean insertOrder(String email, String product, String quantity, String details, String delivery, String payment, String total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ORDER_EMAIL, email);
        cv.put(COL_PRODUCT, product);
        cv.put(COL_QUANTITY, quantity);
        cv.put(COL_DETAILS, details);
        cv.put(COL_DELIVERY, delivery);
        cv.put(COL_PAYMENT, payment);
        cv.put(COL_TOTAL, total);
        cv.put(COL_STATUS, "Processing");
        return db.insert(TABLE_ORDERS, null, cv) != -1;
    }

    public Cursor getOrders(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + COL_ORDER_EMAIL + "=?", new String[]{email});
    }

    public boolean cancelOrder(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, "Cancelled");
        return db.update(TABLE_ORDERS, cv, COL_ORDER_ID + " = ?", new String[]{orderId}) > 0;
    }

    // --- Saved Design Methods ---
    public boolean saveDesign(String email, String product, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ORDER_EMAIL, email);
        cv.put(COL_PRODUCT, product);
        cv.put(COL_DETAILS, details);
        return db.insert(TABLE_SAVED_DESIGNS, null, cv) != -1;
    }

    // Fetch saved designs (Aliases DESIGN_ID as _id for Android's CursorAdapter)
    public Cursor getSavedDesigns(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DESIGN_ID as _id, PRODUCT, DETAILS FROM " + TABLE_SAVED_DESIGNS + " WHERE USER_EMAIL=?", new String[]{email});
    }

    // Delete a saved design
    public boolean deleteSavedDesign(long designId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SAVED_DESIGNS, "DESIGN_ID = ?", new String[]{String.valueOf(designId)}) > 0;
    }
}