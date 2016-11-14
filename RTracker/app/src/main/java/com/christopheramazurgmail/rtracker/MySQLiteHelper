package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_COMMENTS = "comments";
    public static final String TABLE_CAT = "cat";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    public static final String TABLE_ITEM = "item";
    public static final String COLUMN_PRICE = "price";

    public static final String TABLE_ITEM_CAT = "item_cat";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_CAT_ID = "cat_id";

    public static final String TABLE_LIBRARY = "library";
    public static final String COLUMN_CAT_NAME = "cat_name";
    public static final String COLUMN_ITEM_NAME = "item_name";

    public static final String TABLE_ITEM_RECEIPT = "item_receipt";
    public static final String COLUMN_R_ID = "r_id";

    public static final String TABLE_RECEIPT_STORE = "receipt_store";
    public static final String COLUMN_STORE_ID = "store_id";

    public static final String TABLE_RECEIPT = "receipt";
    public static final String COLUMN_DATE = "date";

    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DB_ID = "db_id";
    public static final String COLUMN_USER_NAME = "user_name";

    public static final String TABLE_STORE = "store";
    public static final String COLUMN_STORE_NAME = "store_name";

    private static final String DATABASE_NAME = "ReceiptTrackerDB";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String TABLE_CAT_CREATE
            = "create table " + TABLE_CAT + "( " + COLUMN_ID
            + " integer primary key, " + COLUMN_NAME
            + " text not null);";

    private static final String TABLE_ITEM_CREATE
            = "create table " + TABLE_ITEM + "( " + COLUMN_ID
            + " integer primary key, " + COLUMN_NAME
            + " text not null" + COLUMN_PRICE + "integer);";

    private static final String TABLE_ITEM_CAT_CREATE
            = "create table " + TABLE_ITEM_CAT + "( " + COLUMN_ITEM_ID
            + " integer primary key, " + COLUMN_CAT_ID + "integer primary key);";

    private static final String TABLE_LIBRARY_CREATE
            = "create table " + TABLE_LIBRARY + "( " + COLUMN_CAT_NAME
            + " text primary key, " + COLUMN_ITEM_NAME
            + " text primary key);";

    private static final String TABLE_ITEM_RECEIPT_CREATE
            = "create table " + TABLE_ITEM_RECEIPT + "( " + COLUMN_ITEM_ID
            + " integer primary key, " + COLUMN_R_ID
            + " text primary key);";

    private static final String TABLE_RECEIPT_STORE_CREATE
            = "create table " + TABLE_RECEIPT_STORE + "( " + COLUMN_R_ID
            + " text primary key, " + COLUMN_STORE_ID
            + " text primary key);";

    private static final String TABLE_RECEIPT_CREATE
            = "create table " + TABLE_RECEIPT + "( " + COLUMN_ID
            + " text primary key, " + COLUMN_DATE
            + " text primary key);";

    private static final String TABLE_USER_CREATE
            = "create table " + TABLE_USER + "( " + COLUMN_USER_ID
            + " integer primary key, " + COLUMN_DB_ID
            + " integer primary key, " + COLUMN_USER_NAME
            + " text);";

    private static final String TABLE_STORE_CREATE
            = "create table " + TABLE_STORE + "( " + COLUMN_STORE_ID
            + " text primary key, " + COLUMN_STORE_NAME
            + " text primary key);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(TABLE_CAT_CREATE);
        database.execSQL(TABLE_ITEM_CREATE);
        database.execSQL(TABLE_ITEM_CAT_CREATE);
        database.execSQL(TABLE_LIBRARY_CREATE);
        database.execSQL(TABLE_ITEM_RECEIPT_CREATE);
        database.execSQL(TABLE_RECEIPT_STORE_CREATE);
        database.execSQL(TABLE_RECEIPT_CREATE);
        database.execSQL(TABLE_USER_CREATE);
        database.execSQL(TABLE_STORE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_CAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_RECEIPT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIPT_STORE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIPT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
        onCreate(db);
    }

}
