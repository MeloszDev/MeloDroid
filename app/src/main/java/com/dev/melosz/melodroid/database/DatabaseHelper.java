package com.dev.melosz.melodroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by marek.kozina on 8/25/2015.
 * Singleton DatabaseHelper which is called by DAO classes to process database operations.
 *
 *    Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Declare this DatabaseHelper to insure singleton
    private static DatabaseHelper dbInstance;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // The name of the database file on the file system
    private static final String DATABASE_NAME = "TestApp.db";

    // Define Table Names which will be built in the onCreate() method here
    // The name of the user table
    private static final String USER_TABLE = AppUserContract.AppUserEntry.TABLE_NAME;

    /**
     * Method used to initialize the DatabaseHelper. This guarantees that only one instance of this
     * DatabaseHelper will exist during the application's lifecycle, therefore preventing memory
     * leaks due to unclosed streams. Always use 'getInstance()' instead of 'new DatabaseHelper()'
     * when initializing the database from activities or fragments.
     *
     * @param ctx the Activity Context
     * @return dbInstance the singleton DatabaseHelper
     */
    public static synchronized DatabaseHelper getInstance(Context ctx) {

        if(dbInstance == null) {
            dbInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return dbInstance;
    }

    /**
     * Default constructor to superclass. Set to private to prevent direct instantiation. See
     * 'getInstance()' method for instantiation reasoning.
     *
     * @param ctx the ApplicationContext (usually Activity)
     */
    private DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the underlying database with the SQL_CREATE queries from the contract classes to
     * create the tables and initialize the data. The onCreate is triggered the first time someone
     * tries to access the database with the getReadableDatabase or getWritableDatabase methods.
     *
     * @param db the database being accessed and that should be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the db to contain the data for AppUser
        db.execSQL(AppUserContract.SQL_CREATE);
    }

    /**
     * This method must be implemented if your application is upgraded and must include the SQL
     * query to upgrade the database from your old to your new schema.
     *
     * @param db the database being upgraded.
     * @param oldVersion the current version of the database before the upgrade.
     * @param newVersion the version of the database after the upgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs the the process of the DB upgrade
        Log.i(DatabaseHelper.class.getSimpleName(), "Ugrading from version [" +
                oldVersion + "] to [" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    // TODO: Attempt to use generics so we don't need to specify type of contract being passed
    public void addTable(SQLiteDatabase db) {
        ContentValues cvs = new ContentValues();
    }
    private static <T> T putTable(SQLiteDatabase db, Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        T ref = clazz.newInstance();
        return ref;
    }
}