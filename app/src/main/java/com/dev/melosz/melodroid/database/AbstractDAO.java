package com.dev.melosz.melodroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek.kozina on 9/21/2015.
 * Abstract DAO class containing constructors for the site-wide database and generic database
 * methods which are not defined by an entity DAO class.  This class should be extended for any
 * further implementations of {ENTITY}DAO classes.
 *
 *   Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header
 *                              2. Fixed getAll method and refactor to return list instead of HashMap
 *
 */
public abstract class AbstractDAO {
    // Class Name
    private final String CLASS_NAME = AbstractDAO.class.getSimpleName();

    // Database fields
    public SQLiteDatabase database;
    public DatabaseHelper dbHelper;

    // Activity Context
    public Context APP_CTX;

    /**
     * Assign the Singleton DatabaseHelper for this DAO
     * @param context the Activity Context
     */
    public AbstractDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        APP_CTX = context;
    }

    /**
     * Fetch the readable and writable database with getWritableDatabase().
     * @throws SQLiteException
     */
    public void open() throws SQLiteException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Close the DatabaseHelper stream
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Generic put method to insert an entity in to the database based on table name and
     * Content Values.
     *
     * @param cvs ContentValues which map to DB fields (columns)
     * @param tableName String the table we wish to insert in to
     */
    public void put(ContentValues cvs, String tableName) {

        try {
            // Insert the entity in given tableName if it exists
            database.insert(tableName, null, cvs);
        }
        catch (SQLiteException e){
            // Table does not exist
            Log.e("SQL_ERROR:", e.getMessage());
        }
        getAll(tableName);
    }

    /**
     * Retrieve the list of all rows and columns for a particular table stored in the DB.
     * @param tableName the name of the table we wish to getAll from
     * @return List of column and values
     */
    public List getAll(String tableName) {
        List<String> entries = new ArrayList<>();

        Cursor c = database.query(tableName, null, null, null, null, null, null);
        int i = 0;
        while (c != null && c.moveToNext()) {
            // i represents the row count (pre-increment to start output at 1)
            i++;
            // j represents the column count
            for (int j = 0; j < c.getColumnCount(); j++) {
                String entry = "Table: [" + tableName +
                               "] Row #[" + i +
                               "] Column Name: [" + c.getColumnName(j) +
                               "] Entry: [" + c.getString(j) + "].";
                entries.add(entry);
            }
        }

        // Log each entity
        for(String entry : entries){
            Log.i(CLASS_NAME, entry);
        }

        return entries;
    }

    /**
     * Generic method to check and print out all of the tables that in exist in the app DB
     */
    public void checkTables() {
        List<String> outMessage = new ArrayList<>();
        Cursor c = database.rawQuery("select name FROM sqlite_master WHERE type = 'table'", null);
        if (c.moveToFirst()) {
            int i = 0;
            while (!c.isAfterLast()) {
                outMessage.add("Table #["+ i +"] in our Database: [" + c.getString(0) + "]");
                i++;
                c.moveToNext();
            }
            c.close();
        }

        for(String msg : outMessage){
            Log.i(CLASS_NAME, msg);
        }
    }

    /**
     * Generic SQL method to drop an existing table
     * @param tableName the name of the table to drop
     */
    public void dropTable(String tableName) {
        // Drop the table from the app's database
        try {
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        catch (SQLiteException e) {
            Toast toast = Toast.makeText(APP_CTX, "Failed to drop table ["
                    + tableName + "]. Exception: " + e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();

            Log.e("Failed to drop table [" + tableName + "]", e.getMessage());
        }

        Toast toast = Toast.makeText(APP_CTX, "Dropped Table: " + tableName + "!",
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
