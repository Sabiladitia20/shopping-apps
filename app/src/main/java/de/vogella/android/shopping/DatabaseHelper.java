package de.vogella.android.shopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 1;

    // Table for lists
    private static final String TABLE_LISTS = "lists";
    private static final String COLUMN_LIST_ID = "id";
    private static final String COLUMN_LIST_NAME = "name";

    // Table for items
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_ITEM_PRICE = "price";
    private static final String COLUMN_ITEM_LIST_ID = "list_id"; // Foreign key to `lists`

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create lists table
        String createListsTable = "CREATE TABLE " + TABLE_LISTS + " (" +
                COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIST_NAME + " TEXT NOT NULL)";
        db.execSQL(createListsTable);

        // Create items table
        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                COLUMN_ITEM_PRICE + " REAL NOT NULL, " +
                COLUMN_ITEM_LIST_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ITEM_LIST_ID + ") REFERENCES " + TABLE_LISTS + "(" + COLUMN_LIST_ID + "))";
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }

    // Insert a new list
    public long insertList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_NAME, listName);
        return db.insert(TABLE_LISTS, null, values);
    }

    // Insert a new item
    public long insertItem(String itemName, double itemPrice, long listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_ITEM_PRICE, itemPrice);
        values.put(COLUMN_ITEM_LIST_ID, listId);
        return db.insert(TABLE_ITEMS, null, values);
    }

    // Get all lists
    public Cursor getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LISTS, null);
    }

    // Get all items for a specific list
    public Cursor getItemsForList(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
    }

    // Delete a list and its items
    public void deleteList(long listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First, delete all items associated with the list
        db.delete(TABLE_ITEMS, COLUMN_ITEM_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
        // Then, delete the list itself
        db.delete(TABLE_LISTS, COLUMN_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
    }

    // Update list name
    public void updateList(long listId, String newListName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_NAME, newListName);
        db.update(TABLE_LISTS, values, COLUMN_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
    }

    // Update item details
    public void updateItem(long itemId, String newItemName, double newItemPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, newItemName);
        values.put(COLUMN_ITEM_PRICE, newItemPrice);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }

    // Delete an item
    public void deleteItem(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }
}
