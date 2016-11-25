package com.christopheramazurgmail.rtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    //These strings represent the names of the tables and columns making up the database
    //they are used in methods that incorperate SQL statements
    private static final String LOG = MySQLiteHelper.class.getName();

    public static final String TABLE_COMMENTS = "comments";
    public static final String TABLE_CAT = "cat";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ITEM_COUNT = "item_count";

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

    // Table creation sql statements
    private static final String TABLE_CAT_CREATE
            = "create table " + TABLE_CAT + "( " + COLUMN_ID
            + " integer primary key, " + COLUMN_NAME
            + " text not null);";

    private static final String TABLE_ITEM_CREATE
            = "create table " + TABLE_ITEM + "( " + COLUMN_ID
            + " text, " + COLUMN_NAME
            + " text not null, " + COLUMN_PRICE
            + " real, " + COLUMN_ITEM_COUNT + " integer, primary key ("
            + COLUMN_ID + ", " + COLUMN_NAME +"));";

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

    //This method creates the database and populates the tables when invoked
    @Override
    public void onCreate(SQLiteDatabase database) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

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

        createUser("test", 1);
        insertReceipt("red681", currentDateandTime);
        insertReceipt("red682", currentDateandTime);
        insertReceipt("red683", currentDateandTime);
        insertCat("Pickles");
        insertCat("Coke");
        insertItem("red681", "Pickles", 12.14);
        insertItem("red681", "Coke", 10.14);
        insertItem("red681", "Coke", 10.14);
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

    //Returns a receipt object of the corresponding receipt id
    public Receipt getReceiptObject(String n_id){
        Receipt receipt = new Receipt();
        ArrayList<String> itemNames = new ArrayList<>();
        double itemPrice;
        int itemCount;
        System.out.println("Getting all the names on the receipt");
        itemNames = getAllItemNamesOnReceipt(n_id);
        for (String itemName : itemNames) {
            itemPrice = getItemPrice(n_id, itemName);
            itemCount = getNumberOfItems(n_id, itemName);
            Item item = new Item(itemName, itemPrice);
            for (int i = 0; i < itemCount; i++) {
                receipt.add(item);
            }
        }
        return receipt;
    }

    public void insertReceiptObject(String n_id, Receipt receipt){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        List<Item> itemNames = new ArrayList<>();
        double itemPrice;
        String itemName;
        int counter = 0;
        itemNames = receipt.items.getItems();
        insertReceipt(n_id, currentDateandTime);
        String store = receipt.getStore();
        String store_id;
        if (store != null){
            insertStore(store);
            store_id = getStoreID(store);
            insertReceiptStore(n_id, store_id);
        }
        for (Item item : itemNames) {
            item = itemNames.get(counter);
            itemName = item.getDesc();
            itemPrice = item.getPriceD();
            insertItem(n_id, itemName, itemPrice);
            counter++;
        }
    }

    //Methods for the Item Table
    //============================================================================================
    public void insertItem(String i_id, String i_name, double i_price){
        System.out.println("Creating a new item");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT COUNT(*) as test_column FROM " + TABLE_ITEM + " WHERE " + COLUMN_ID
                + " =? AND " + COLUMN_NAME + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {i_id, i_name});
        c.moveToFirst();
        int testSet = c.getInt(c.getColumnIndex("test_column"));
        ContentValues values = new ContentValues();
        if (testSet > 0){
            testSet = testSet + 1;
            values.put(COLUMN_ITEM_COUNT, testSet);
            db.update(TABLE_ITEM, values, COLUMN_ID + "=? " + "AND " +COLUMN_NAME + "=?", new String[] {i_id, i_name});
        } else {
            testSet = 1;
            values.put(COLUMN_ID, i_id);
            values.put(COLUMN_NAME, i_name);
            values.put(COLUMN_PRICE, i_price);
            values.put(COLUMN_ITEM_COUNT, testSet);
            db.insert(TABLE_ITEM, null, values);
        }
    }

    public void deleteReceiptItem(String i_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "id=?";
        Log.e(LOG, deleteQuery);
        db.delete(TABLE_ITEM, deleteQuery, new String[] {i_id});
    }

    public ArrayList<String> getAllItemNamesOnReceipt(String i_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> itemNames = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ID
                + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {i_id});
        while (c.moveToNext()) {
            String testGet = c.getString(c.getColumnIndex(COLUMN_NAME));
            itemNames.add(testGet);
        }
        return itemNames;
    }

    public ArrayList<String> getAllItemsInTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> items = new ArrayList<>();
        String ret;
        String selectQuery = "SELECT  * FROM " + TABLE_ITEM;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            ret = c.getString(c.getColumnIndex(COLUMN_ID)) +
                    " " + c.getString(c.getColumnIndex(COLUMN_NAME)) +
                    " " + c.getDouble(c.getColumnIndex(COLUMN_PRICE)) +
                    " " + c.getInt(c.getColumnIndex(COLUMN_ITEM_COUNT));
            items.add(ret);
        }
        return items;
    }

    public String getItemID(String i_id, String i_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ID
                + " =? AND " + COLUMN_NAME + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {i_id, i_name});
        if (c != null)
            c.moveToFirst();
        String testGet = c.getString(c.getColumnIndex(COLUMN_ID));
        return testGet;
    }

    public String getItemName(String i_id, String i_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ID
                + " =? AND " + COLUMN_NAME + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {i_id, i_name});
        if (c != null)
            c.moveToFirst();
        String testGet = c.getString(c.getColumnIndex(COLUMN_NAME));
        return testGet;
    }

    public double getItemPrice(String i_id, String i_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ID
                + " =? AND " + COLUMN_NAME + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {i_id, i_name});
        if (c != null)
            c.moveToFirst();
        double testGet = c.getDouble(c.getColumnIndex(COLUMN_PRICE));
        return testGet;
    }

    public int getNumberOfItems(String i_id, String i_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " WHERE " + COLUMN_ID
                + " =? AND " + COLUMN_NAME + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {i_id, i_name});
        if (c != null)
            c.moveToFirst();
        int testGet = c.getInt(c.getColumnIndex(COLUMN_ITEM_COUNT));
        return testGet;
    }

    //End of methods for the Item table
    //============================================================================================

    // Insert method to create a new user
    // Currently, entries in the user table do nothing
    // E.G. db.createUser(UserNameHere, DatabaseNumberHere);
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
        //System.out.println("Retreiving from database user id: " + getUser(testSet));
    }

    // Get method for the ID column of the user table
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

    // Get method for the ID column of the receipt table
    public String getReceipt(String r_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_RECEIPT + " WHERE "
                + COLUMN_ID + " = " + r_id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        String testGet = c.getString(c.getColumnIndex(COLUMN_ID));
        return testGet;
    }

    // Get method that returns a list of all the ID's in the receipt table
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

    // Isert method for the Receipt table
    // E.G. db.insertReceipt(ReceiptIDHere, DateHere);
    public void insertReceipt(String r_id, String r_date){
        System.out.println("Creating a new receipt");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, r_id);
        values.put(COLUMN_DATE, r_date);
        db.insert(TABLE_RECEIPT, null, values);
    }

    public void deleteReceipt(String r_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "id=?";
        Log.e(LOG, deleteQuery);
        db.delete(TABLE_RECEIPT, deleteQuery, new String[] {r_id});
        deleteReceiptItem(r_id);
    }

    // Insert method for the Cat table
    // E.G. db.insertCat(YourCatagoryHere);
    public void insertCat(String c_name){
        System.out.println("Creating a new catagory");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_CAT;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        int testSet = c.getInt(0);
        if (testSet == 0){
            testSet = 1;
        } else {
            testSet = testSet + 1;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, testSet);
        values.put(COLUMN_NAME, c_name);
        db.insert(TABLE_CAT, null, values);
    }

    public int getCatID(int c_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CAT + " WHERE "
                + COLUMN_ID + " = " + c_id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        int testGet = c.getInt(c.getColumnIndex(COLUMN_ID));
        return testGet;
    }

    public String getCatName(int c_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CAT + " WHERE "
                + COLUMN_ID + " = " + c_id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        String testGet = c.getString(c.getColumnIndex(COLUMN_NAME));
        return testGet;
    }

    //Methods for the Store table
    //=========================================================================================
    public void insertStore(String s_name){
        System.out.println("Inserting a new store");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT COUNT(*) as test_column FROM " + TABLE_STORE + " WHERE "
                + COLUMN_STORE_NAME + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {s_name});
        c.moveToFirst();
        int testSet = c.getInt(c.getColumnIndex("test_column"));
        ContentValues values = new ContentValues();
        if (testSet > 0){
            System.out.println("Store already exists");
        } else {
            selectQuery = "SELECT MAX(" + COLUMN_STORE_ID +") as test_column FROM " + TABLE_STORE;
            Log.e(LOG, selectQuery);
            c = db.rawQuery(selectQuery, null);
            c.moveToFirst();
            testSet = c.getInt(c.getColumnIndex("test_column"));
            if (testSet > 0){
                testSet = testSet + 1;
            } else {
                testSet = 1;
            }
            values.put(COLUMN_STORE_ID, testSet);
            values.put(COLUMN_STORE_NAME, s_name);
            db.insert(TABLE_STORE, null, values);
        }
    }

    public String getStoreID(String s_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STORE + " WHERE " + COLUMN_STORE_NAME
                + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {s_name});
        if (c != null)
            c.moveToFirst();
        String testGet = c.getString(c.getColumnIndex(COLUMN_STORE_ID));
        return testGet;
    }

    public String getStoreName(String s_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STORE + " WHERE " + COLUMN_STORE_NAME
                + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {s_name});
        if (c != null)
            c.moveToFirst();
        String testGet = c.getString(c.getColumnIndex(COLUMN_STORE_NAME));
        return testGet;
    }

    public ArrayList<String> getAllStoresInTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> items = new ArrayList<>();
        String ret;
        String selectQuery = "SELECT  * FROM " + TABLE_STORE;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            ret = c.getString(c.getColumnIndex(COLUMN_STORE_ID)) +
                    " " + c.getString(c.getColumnIndex(COLUMN_STORE_NAME));
            items.add(ret);
        }
        return items;
    }
    //End of methods for the Store table
    //==========================================================================================


    public void insertReceiptStore(String r_id, String s_id){
        System.out.println("Inserting a new store bridge");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT COUNT(*) as test_column FROM " + TABLE_RECEIPT_STORE + " WHERE " + COLUMN_R_ID
                + " =? AND " + COLUMN_STORE_ID + " =?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[] {r_id, s_id});
        c.moveToFirst();
        int testSet = c.getInt(c.getColumnIndex("test_column"));
        ContentValues values = new ContentValues();
        if (testSet > 0){
            System.out.println("Bridge already exists");
        } else {
            values.put(COLUMN_R_ID, r_id);
            values.put(COLUMN_STORE_ID, s_id);
            db.insert(TABLE_RECEIPT_STORE, null, values);
        }
    }

    public ArrayList<String> getAllStoresReceiptsInTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> items = new ArrayList<>();
        String ret;
        String selectQuery = "SELECT  * FROM " + TABLE_RECEIPT_STORE;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            ret = c.getString(c.getColumnIndex(COLUMN_R_ID)) +
                    " " + c.getString(c.getColumnIndex(COLUMN_STORE_ID));
            items.add(ret);
        }
        return items;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //Nukes the database and creates a new one from scratch
    //DO NOT USE THIS UNLESS THE TABLES NEED TO BE MODIFIED!!!
    public void danTestUpgrade() {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version");
        SQLiteDatabase db = this.getWritableDatabase();
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