package com.dev.melosz.melodroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dev.melosz.melodroid.utils.LogUtil;

/**
 * Created by marek.kozina on 8/25/2015.
 * Singleton DatabaseHelper which is called by DAO classes to execute database operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static String METHOD;

    // Declare this DatabaseHelper to insure singleton
    private static DatabaseHelper dbInstance;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 8;

    // The name of the database file on the file system
    private static final String DATABASE_NAME = "TestApp.db";

    // Table Names which will be dropped onUpgrade
    private static final String USER_TABLE = AppUserContract.AppUserEntry.TABLE_NAME;
    private static final String CONTACT_TABLE = ContactContract.ContactEntry.TABLE_NAME;

    // The Create table SQL Strings for each table
    private static final String CREATE_USER_TABLE = AppUserContract.SQL_CREATE;
    private static final String CREATE_CONTACT_TABLE = ContactContract.SQL_CREATE;


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
     * Forces referential integrity by enabling foreign keys
     * @param db the database being accessed
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if(!db.isReadOnly()){
            // Enable Foreign Key Support
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
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
        METHOD = "onCreate()";

        // Create the db to contain the data for AppUser
        if(DEBUG) log.i(TAG, METHOD, "Creating Database: [" + DATABASE_NAME + "].");
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CONTACT_TABLE);
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
        METHOD = "onUpgrade()";

        // Logs the the process of the DB upgrade
        if(DEBUG) log.i(TAG,
                METHOD,
                "Ugrading Database from version [" + oldVersion + "] to [" + newVersion + "]");

        // Drop existing tables. TODO: If this is ever released, must implement data migration
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);

        // Recreate the db
        onCreate(db);
    }
}