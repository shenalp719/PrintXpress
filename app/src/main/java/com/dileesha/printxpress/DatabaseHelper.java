package com.dileesha.printxpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PrintXpress.db";
    public static final int DATABASE_VERSION = 7; // Bumped version to ensure new table creates

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "ID";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_NAME = "NAME";
    public static final String COL_ADDRESS = "ADDRESS";
    public static final String COL_PHONE = "PHONE";
    public static final String COL_PHOTO = "PHOTO";

    // Orders Table
    public static final String TABLE_ORDERS = "orders";
    public static final String COL_ORDER_ID = "ID";
    public static final String COL_USER_EMAIL = "USER_EMAIL";
    public static final String COL_PRODUCT = "PRODUCT";
    public static final String COL_QTY = "QUANTITY";
    public static final String COL_DETAILS = "DETAILS";
    public static final String COL_DELIVERY = "DELIVERY";
    public static final String COL_PAYMENT = "PAYMENT";
    public static final String COL_TOTAL = "TOTAL";
    public static final String COL_STATUS = "STATUS";

    // Saved Designs Table (RESTORED)
    public static final String TABLE_SAVED_DESIGNS = "saved_designs";
    public static final String COL_DESIGN_ID = "ID";
    public static final String COL_DESIGN_EMAIL = "USER_EMAIL";
    public static final String COL_DESIGN_PRODUCT = "PRODUCT";
    public static final String COL_DESIGN_DETAILS = "DETAILS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT, NAME TEXT, ADDRESS TEXT, PHONE TEXT, PHOTO TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_EMAIL TEXT, PRODUCT TEXT, QUANTITY TEXT, DETAILS TEXT, DELIVERY TEXT, PAYMENT TEXT, TOTAL TEXT, STATUS TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_SAVED_DESIGNS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_EMAIL TEXT, PRODUCT TEXT, DETAILS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_DESIGNS);
        onCreate(db);
    }

    // --- AUTHENTICATION & REGISTRATION ---

    public boolean checkUserExists(String identifier) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL = ? OR PHONE = ?", new String[]{identifier, identifier});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertUser(String identifier, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PASSWORD, password);

        if (identifier.contains("@")) {
            cv.put(COL_EMAIL, identifier);
            cv.put(COL_PHONE, "");
        } else {
            cv.put(COL_PHONE, identifier);
            cv.put(COL_EMAIL, "");
        }

        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public String authenticateUser(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT EMAIL, PHONE FROM " + TABLE_USERS + " WHERE (EMAIL = ? OR PHONE = ?) AND PASSWORD = ?", new String[]{identifier, identifier, password});

        String resolvedIdentifier = null;
        if (cursor.moveToFirst()) {
            String dbEmail = cursor.getString(0);
            String dbPhone = cursor.getString(1);

            if (dbEmail != null && !dbEmail.isEmpty()) {
                resolvedIdentifier = dbEmail;
            } else {
                resolvedIdentifier = dbPhone;
            }
        }
        cursor.close();
        return resolvedIdentifier;
    }

    public boolean checkUser(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE (EMAIL = ? OR PHONE = ?) AND PASSWORD = ?", new String[]{identifier, identifier, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // --- PROFILE MANAGEMENT ---

    public Cursor getUserDetails(String identifier) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL = ? OR PHONE = ?", new String[]{identifier, identifier});
    }

    public boolean updateProfileDetails(String identifier, String name, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_ADDRESS, address);

        if (identifier.contains("@")) {
            cv.put(COL_PHONE, phone);
        }

        return db.update(TABLE_USERS, cv, "EMAIL = ? OR PHONE = ?", new String[]{identifier, identifier}) > 0;
    }

    public boolean updateProfilePhoto(String identifier, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PHOTO, photoUri);
        return db.update(TABLE_USERS, cv, "EMAIL = ? OR PHONE = ?", new String[]{identifier, identifier}) > 0;
    }

    public boolean updateSecureIdentifier(String oldIdentifier, String newIdentifier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (newIdentifier.contains("@")) {
            cv.put(COL_EMAIL, newIdentifier);
            cv.put(COL_PHONE, "");
        } else {
            cv.put(COL_PHONE, newIdentifier);
            cv.put(COL_EMAIL, "");
        }
        return db.update(TABLE_USERS, cv, "EMAIL = ? OR PHONE = ?", new String[]{oldIdentifier, oldIdentifier}) > 0;
    }

    // --- ORDER MANAGEMENT ---

    public boolean insertOrder(String email, String product, String qty, String details, String delivery, String payment, String total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_PRODUCT, product);
        cv.put(COL_QTY, qty);
        cv.put(COL_DETAILS, details);
        cv.put(COL_DELIVERY, delivery);
        cv.put(COL_PAYMENT, payment);
        cv.put(COL_TOTAL, total);
        cv.put(COL_STATUS, "Processing");
        long result = db.insert(TABLE_ORDERS, null, cv);
        return result != -1;
    }

    public Cursor getOrders(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE USER_EMAIL = ? ORDER BY ID DESC", new String[]{email});
    }

    public boolean cancelOrder(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, "Cancelled");
        return db.update(TABLE_ORDERS, cv, "ID = ?", new String[]{orderId}) > 0;
    }

    public boolean completeOrder(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, "Completed");
        return db.update(TABLE_ORDERS, cv, "ID = ?", new String[]{orderId}) > 0;
    }

    // --- SAVED DESIGNS (RESTORED) ---

    public boolean saveDesign(String email, String product, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESIGN_EMAIL, email);
        cv.put(COL_DESIGN_PRODUCT, product);
        cv.put(COL_DESIGN_DETAILS, details);
        long result = db.insert(TABLE_SAVED_DESIGNS, null, cv);
        return result != -1;
    }

    public Cursor getSavedDesigns(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        // The "ID AS _id" alias satisfies SimpleCursorAdapter's strict naming rule
        return db.rawQuery("SELECT ID AS _id, USER_EMAIL, PRODUCT, DETAILS FROM " + TABLE_SAVED_DESIGNS + " WHERE USER_EMAIL = ? ORDER BY ID DESC", new String[]{email});
    }

    public boolean deleteSavedDesign(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SAVED_DESIGNS, COL_DESIGN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}