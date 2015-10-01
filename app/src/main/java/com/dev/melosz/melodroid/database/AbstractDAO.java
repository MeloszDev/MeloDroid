package com.dev.melosz.melodroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marek.kozina on 9/21/2015.
 * Abstract DAO class containing constructors for the site-wide database and generic database
 * methods which are not defined by an entity DAO class.  This class should be extended for any
 * further implementations of {ENTITY}DAO classes.
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
     * @return Map of column and values
     */
    public Map<String, String> getAll(String tableName) {
        Map<String, String> userMap = new HashMap<>();

        Cursor c = database.query(tableName, null, null, null, null, null, null);
        while (c != null && c.moveToNext()) {
            for(int i=0; i < c.getColumnCount(); i++) {
                userMap.put(c.getColumnName(i), c.getString(i));
            }
        }
        // TODO: Remove in production or move to Log.i(foo, bar);
        System.out.println("# of Columns: [" + c.getColumnCount() + "].");
        for(Map.Entry entry : userMap.entrySet()){
            System.out.println("Column Name: [" + entry.getKey() +
                    "] Value: [" + entry.getValue() + "].");
        }
        if(c != null) c.close();

        return userMap;
    }

    /**
     * Generic method to check and print out all of the tables that in exist in the app DB
     */
    public void checkTables() {
        String[] outMessage = new String[]{};
        Cursor c = database.rawQuery("select name FROM sqlite_master WHERE type = 'table'", null);
        if (c.moveToFirst()) {
            int i = 0;
            while (!c.isAfterLast()) {
                outMessage[i] = "Table #["+i+"] in our Database: [" + c.getString(0) + "]\n";
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
