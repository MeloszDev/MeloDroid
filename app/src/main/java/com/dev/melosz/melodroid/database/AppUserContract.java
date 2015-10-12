package com.dev.melosz.melodroid.database;

import android.provider.BaseColumns;

import com.dev.melosz.melodroid.classes.AppUser;

/**
 * Created by marek.kozina on 8/25/2015.
 * This contract class defines and specifies the layout of the AppUser DB schema in a systematic and
 * self documenting way.
 *
 */
public final class AppUserContract {
    /**
     * Default empty Constructor
     */
    public AppUserContract(){
        super();
    }
    /**
     * Inner class that defines the table contents.  Add new Columns here.
     */
    public static abstract class AppUserEntry implements BaseColumns {
        public static final String TABLE_NAME = AppUser.class.getSimpleName();
        public static final String COL_ID = "id";
        public static final String COL_USERNAME = "userName";
        public static final String COL_PASSWORD = "password";
        public static final String COL_FIRSTNAME = "firstName";
        public static final String COL_LASTNAME = "lastName";
        public static final String COL_EMAIL = "email";
        public static final String COL_PHONENUMBER = "phoneNumber";
        public static final String COL_ADDRESS = "address";
        public static final String COL_CITY = "city";
        public static final String COL_STATE = "state";
        public static final String COL_ZIP = "zip";
        public static final String COL_SCORE = "score";
        public static final String COL_LOGGED = "logged";
    }
    /**
     * Build the SQL String that creates the table and columns.
     */
    public static final String SQL_CREATE = "CREATE TABLE " +
            AppUserEntry.TABLE_NAME + " (" +
            AppUserEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AppUserEntry.COL_USERNAME + " TEXT," +
            AppUserEntry.COL_PASSWORD + " TEXT," +
            AppUserEntry.COL_FIRSTNAME + " TEXT," +
            AppUserEntry.COL_LASTNAME + " TEXT," +
            AppUserEntry.COL_EMAIL + " TEXT," +
            AppUserEntry.COL_PHONENUMBER + " TEXT," +
            AppUserEntry.COL_ADDRESS + " TEXT," +
            AppUserEntry.COL_CITY + " TEXT," +
            AppUserEntry.COL_STATE + " TEXT," +
            AppUserEntry.COL_ZIP + " TEXT,"+
            AppUserEntry.COL_SCORE + " INTEGER,"+
            AppUserEntry.COL_LOGGED + " INTEGER );";

    /**
     * Build the SQL String that updates the table and columns.
     */
    public static final String SQL_UPDATE = "ALTER TABLE " +
            AppUserEntry.TABLE_NAME + " ADD COLUMN " +
            AppUserEntry.COL_SCORE + " INTEGER ;";
}
