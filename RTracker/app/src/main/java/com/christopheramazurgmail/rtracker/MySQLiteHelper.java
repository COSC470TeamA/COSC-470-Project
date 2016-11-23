package com.christopheramazurgmail.rtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String LOG = MySQLiteHelper.class.getName();

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
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, " + COLUMN_PRICE + "integer);";

    private static final String TABLE_ITEM_CAT_CREATE
            = "create table " + TABLE_ITEM_CAT + "( " + COLUMN_ITEM_ID
            + " integer, " + COLUMN_CAT_ID + "integer, primary key ("
            + COLUMN_ITEM_ID + ", " + COLUMN_CAT_ID +"));";

    private static final String TABLE_LIBRARY_CREATE
            = "create table " + TABLE_LIBRARY + "( " + COLUMN_CAT_NAME
            + " text, " + COLUMN_ITEM_NAME
            + " text, primary key(" + COLUMN_CAT_NAME + ", " + COLUMN_ITEM_NAME + "));";

    private static final String TABLE_ITEM_RECEIPT_CREATE
            = "create table " + TABLE_ITEM_RECEIPT + "( " + COLUMN_ITEM_ID
            + " integer, " + COLUMN_R_ID
            + " text, primary key(" + COLUMN_ITEM_ID + ", " + COLUMN_R_ID + "));";

    private static final String TABLE_RECEIPT_STORE_CREATE
            = "create table " + TABLE_RECEIPT_STORE + "( " + COLUMN_R_ID
            + " text, " + COLUMN_STORE_ID
            + " text, primary key (" + COLUMN_R_ID + ", " + COLUMN_STORE_ID +"));";

    private static final String TABLE_RECEIPT_CREATE
            = "create table " + TABLE_RECEIPT + "( " + COLUMN_ID
            + " text, " + COLUMN_DATE
            + " text, primary key(" + COLUMN_ID + ", " + COLUMN_DATE + "));";

    private static final String TABLE_USER_CREATE
            = "create table " + TABLE_USER + "( " + COLUMN_USER_ID
            + " integer primary key autoincrement, " + COLUMN_DB_ID
            + " integer, " + COLUMN_USER_NAME
            + " text);";

    private static final String TABLE_STORE_CREATE
            = "create table " + TABLE_STORE + "( " + COLUMN_STORE_ID
            + " text, " + COLUMN_STORE_NAME
            + " text, primary key (" + COLUMN_STORE_ID + ", " + COLUMN_STORE_NAME + "));";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        System.out.println("Creating table cat");
        database.execSQL(TABLE_CAT_CREATE);
        System.out.println("Creating table item");
        database.execSQL(TABLE_ITEM_CREATE);
        System.out.println("Creating table item_cat");
        //database.execSQL(TABLE_ITEM_CAT_CREATE);
        System.out.println("Creating table library");
        database.execSQL(TABLE_LIBRARY_CREATE);
        System.out.println("Creating table item_receipt");
        database.execSQL(TABLE_ITEM_RECEIPT_CREATE);
        System.out.println("Creating table receipt_store");
        database.execSQL(TABLE_RECEIPT_STORE_CREATE);
        System.out.println("Creating table receipt");
        database.execSQL(TABLE_RECEIPT_CREATE);
        System.out.println("Creating table user");
        database.execSQL(TABLE_USER_CREATE);
        System.out.println("Creating table store");
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

    public void createItem(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, item.getDesc());
        db.insert(TABLE_ITEM, null, values);
    }

    public void createUser(String name, int data){
        System.out.println("Creating a new user");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT MAX(" + COLUMN_USER_ID + ") as user_id FROM " + TABLE_USER;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        int testSet = c.getInt(c.getColumnIndex(COLUMN_USER_ID));
        testSet = testSet + 1;
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, testSet);
        values.put(COLUMN_DB_ID, data);
        values.put(COLUMN_USER_NAME, name);
        db.insert(TABLE_USER, null, values);
        System.out.println("Retreiving from database user id: " + getUser(testSet));
    }

    public int getUser(int u_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + COLUMN_USER_ID + " = " + u_id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        int testGet = c.getInt(c.getColumnIndex(COLUMN_USER_ID));
        return testGet;
    }

    public ArrayList<String> getAllReceiptID() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> receipts = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECEIPT;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            String testGet = c.getString(c.getColumnIndex(COLUMN_ID));
            receipts.add(testGet);
        }
        return receipts;
    }

    public void insertReceipt(String r_id, String r_date){
        System.out.println("Creating a new receipt");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, r_id);
        values.put(COLUMN_DATE, r_date);
        db.insert(TABLE_RECEIPT, null, values);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}