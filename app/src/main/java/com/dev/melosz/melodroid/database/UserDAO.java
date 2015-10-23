package com.dev.melosz.melodroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek.kozina on 8/25/2015.
 * Subclass of the AbstractDAO to handle all AppUser related database operations.
 *
 */
public class UserDAO extends AbstractDAO {
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = UserDAO.class.getSimpleName();
    private String METHOD;
    // Set to false to suppress logging
    private static final boolean DEBUG = false;

    // AppUser table name from AppUserContract Class
    private final String USER_TABLE_NAME = AppUserContract.AppUserEntry.TABLE_NAME;

    // Key String constants for AppUser Table
    private final String KEY_ID = AppUserContract.AppUserEntry.COL_ID;
    private final String KEY_USERNAME = AppUserContract.AppUserEntry.COL_USERNAME;
    private final String KEY_PASSWORD = AppUserContract.AppUserEntry.COL_PASSWORD;
    private final String KEY_EMAIL = AppUserContract.AppUserEntry.COL_EMAIL;
    private final String KEY_PHONENUMBER = AppUserContract.AppUserEntry.COL_PHONENUMBER;
    private final String KEY_ZIP = AppUserContract.AppUserEntry.COL_ZIP;
    private final String KEY_SCORE = AppUserContract.AppUserEntry.COL_SCORE;
    private final String KEY_LOGGED = AppUserContract.AppUserEntry.COL_LOGGED;

    /**
     * Assign the Singleton DatabaseHelper for this DAO
     * @param context the Activity Context
     */
    public UserDAO(Context context) {
        super(context);
    }

    /**
     * Method: saveNewUser
     * Description: Makes a new AppUser from the user input and saves it to the DB
     *
     * @param user AppUser the new AppUser to save
     * @return the newly saved user
     */
    public boolean saveNewUser(AppUser user){

        ContentValues cvs = makeUserCVs(user);

        // Call the generic put method which will save a new user based on the cvs
        return super.put(cvs, USER_TABLE_NAME);
    }

    /**
     * Updates an existing user
     *
     * @param user the AppUser to update
     */
    public void updateUser(AppUser user) {
        METHOD = "updateUser()";
        // make cvs for the DB to read/write
        ContentValues cvs = makeUserCVs(user);

        // Attempt to update the user based on id
        try {
            database.update(USER_TABLE_NAME, cvs, "id=" + user.getId(), null);
        }
        catch (SQLiteException e) {
            if(DEBUG) log.e(TAG, METHOD, e.getMessage());
        }
        finally {
            if(DEBUG) log.i(TAG, METHOD, user.getUserName()
                    + " account updated. Fields below: " + AppUtil.prettyPrintObject(user));
        }
    }

    /**
     * Gets an AppUser entity from the db
     *
     * @param name the User Name String
     * @return the retrieved AppUser or null
     */
    public AppUser getUserByName(String name) {
        METHOD = "getUserByName()";

        AppUser setUser = null;
        if(name != null) {
            // Build SQL String
            String sql = "SELECT * FROM " + USER_TABLE_NAME
                    + " WHERE " + KEY_USERNAME + " = '" + name + "';";
            if(DEBUG) log.i(TAG, METHOD, "SQL QUERY: '" + sql + ".");

            Cursor c = database.rawQuery(sql, null);

            // Assign user if name exists
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                // Build existing user by passing the ID
                setUser = makeUserByCursor(c);

                if(DEBUG) log.i(TAG, METHOD, "User [" + setUser.getUserName() +
                        "] Obtained. Fields below:\n." + AppUtil.prettyPrintObject(setUser));

                c.close();
            }
            else if(DEBUG) log.i(TAG,  METHOD, "User [" + name + "] Not found.");
        }
        return setUser;
    }

    /**
     *
     * @param data String array containing the username and password
     * @return boolean if the username and password were a match for an entry in the database
     */
    public boolean checkCredentials(String[] data) {
        METHOD = "checkCredentials()";
        boolean exists = false;
        String sql = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + KEY_USERNAME +
                "=? AND " + KEY_PASSWORD + "=?";

        Cursor c = database.rawQuery(sql, data);

        if (c != null && c.getCount() > 0) {
            if(DEBUG) log.i(TAG,  METHOD, "Matched user [" + data[0]
                    + "] with SQL Query: [" + sql + "].");
            exists = true;
            c.close();
        }

        return exists;
    }
    /**
     *
     * @param user String the username
     * @return boolean whether or not the user exists in the DB already
     */
    public boolean doesUserExist(String user) {
        METHOD = "doesUserExist()";
        boolean exists = (getUserByName(user) != null);
        if(exists && DEBUG) log.i(USER_TABLE_NAME, METHOD, "Found user: [" + user + "].");
        return exists;
    }

    /**
     * Deletes a specified AppUser from the database
     * @param user AppUser the user to be deleted
     * @return boolean whether or not the delete was successful
     */
    public boolean deleteUser(AppUser user) {
        METHOD = "deleteUser()";
        try
        {
            if(DEBUG) log.i(TAG, METHOD, " Attempting to delete user: [" + user.getUserName()
                    + "] from table [" + USER_TABLE_NAME + "].");
            return database.delete(
                    USER_TABLE_NAME,
                    "id=?",
                    new String[]{Integer.toString(user.getId())}) > 0;
        }
        catch(SQLiteException e)
        {
            if(DEBUG) log.e(TAG, METHOD, e.getLocalizedMessage());
            return false;
        }
    }
    /**
     * Method: getAllUsers
     * @return ArrayList of AppUsers
     */
    public List<AppUser> getAllUsers() {
        METHOD = "getAllUsers()";
        List<AppUser> users = new ArrayList<>();
        String sql = "SELECT * FROM " + USER_TABLE_NAME;
        Cursor c = database.rawQuery(sql, null);

        // If the query was successful, execute for each entry
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                // For each entity, make a new AppUser and add it to the users list
                AppUser user = makeUserByCursor(c);
                users.add(user);
                c.moveToNext();
            }
        }
        if(c != null) c.close();

        if(DEBUG){
            for(AppUser u : users) {
                log.i(USER_TABLE_NAME, METHOD, "Found user: " + AppUtil.prettyPrintObject(u));
            }
        }

        return users;
    }

    /**
     * Add the userTable to the schema. To be used only if the table has been explicitly deleted.
     */
    public void addAppUserTable() {
        database.execSQL(AppUserContract.SQL_CREATE);
    }

    /**
     * Builds the necessary ContentValues for saving/updating a user
     * @param user the AppUser
     * @return the ContentValues for a User entity
     */
    private ContentValues makeUserCVs(AppUser user) {
        ContentValues cvs = new ContentValues();
        // KEY_ID is generated so it is not populated as a ContentValue
        cvs.put(KEY_USERNAME, user.getUserName());
        cvs.put(KEY_PASSWORD, user.getPassword());
        cvs.put(KEY_EMAIL, user.getEmail());
        cvs.put(KEY_PHONENUMBER, user.getPhoneNumber());
        cvs.put(KEY_ZIP, user.getZip());
        cvs.put(KEY_SCORE, user.getScore());
        cvs.put(KEY_LOGGED, user.isLogged() ? 1 : 0);
        return cvs;
    }

    /**
     * Builds an existing user
     * @param c the query Cursor
     * @return AppUser the existing AppUser
     */
    private AppUser makeUserByCursor(Cursor c){
        return new AppUser(
                c.getInt(c.getColumnIndex(KEY_ID)),
                c.getString(c.getColumnIndex(KEY_USERNAME)),
                c.getString(c.getColumnIndex(KEY_PASSWORD)),
                c.getString(c.getColumnIndex(KEY_EMAIL)),
                c.getString(c.getColumnIndex(KEY_PHONENUMBER)),
                c.getString(c.getColumnIndex(KEY_ZIP)),
                c.getInt(c.getColumnIndex(KEY_SCORE)),
                c.getInt(c.getColumnIndex(KEY_LOGGED))
        );
    }
}