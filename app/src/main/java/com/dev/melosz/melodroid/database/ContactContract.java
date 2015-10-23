package com.dev.melosz.melodroid.database;

import android.provider.BaseColumns;

import com.dev.melosz.melodroid.classes.Contact;

/**
 * Created by marek.kozina on 10/19/2015.
 * This contract class defines and specifies the layout of the Contact DB schema in a systematic and
 * self documenting way.
 */
public class ContactContract {
    /**
     * Default empty Constructor
     */
    public ContactContract(){
        super();
    }
    /**
     * Inner class that defines the table contents.  Add new Columns here.
     */
    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = Contact.class.getSimpleName();
        public static final String COL_CONTACT_ID = "contact_id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_FIRSTNAME = "firstName";
        public static final String COL_MIDDLENAME = "middleName";
        public static final String COL_LASTNAME = "lastName";
        public static final String COL_EMAIL = "email";
        public static final String COL_PHONENUMBER = "phoneNumber";
        public static final String COL_ADDRESS = "address";
        public static final String COL_ADDRESS2 = "address2";
        public static final String COL_CITY = "city";
        public static final String COL_STATE = "state";
        public static final String COL_ZIP = "zip";
        public static final String COL_DESCRIPTION = "description";
        public static final String COL_TYPE = "type";
        public static final String COL_UUID = "uuID";
    }
    /**
     * Build the SQL String that creates the table and columns.
     */
    public static final String SQL_CREATE = "CREATE TABLE " +
            ContactEntry.TABLE_NAME + " (" +
            ContactEntry.COL_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ContactEntry.COL_FIRSTNAME + " TEXT NOT NULL," +
            ContactEntry.COL_MIDDLENAME + " TEXT," +
            ContactEntry.COL_LASTNAME + " TEXT," +
            ContactEntry.COL_EMAIL + " TEXT," +
            ContactEntry.COL_PHONENUMBER + " TEXT," +
            ContactEntry.COL_ADDRESS + " TEXT,"+
            ContactEntry.COL_ADDRESS2 + " TEXT,"+
            ContactEntry.COL_CITY + " TEXT,"+
            ContactEntry.COL_STATE + " TEXT,"+
            ContactEntry.COL_ZIP + " TEXT,"+
            ContactEntry.COL_DESCRIPTION + " TEXT,"+
            ContactEntry.COL_TYPE + " TEXT," +
            ContactEntry.COL_UUID + " TEXT UNIQUE," +
            ContactEntry.COL_USER_ID + " INTEGER NOT NULL,"
            + "FOREIGN KEY (" + ContactEntry.COL_USER_ID + ") REFERENCES "
            + AppUserContract.AppUserEntry.TABLE_NAME + "("
            + AppUserContract.AppUserEntry.COL_ID + "));";
}
