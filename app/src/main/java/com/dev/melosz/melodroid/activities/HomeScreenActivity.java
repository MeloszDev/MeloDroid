package com.dev.melosz.melodroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.database.AppUserContract;
import com.dev.melosz.melodroid.database.UserDAO;
import com.dev.melosz.melodroid.utils.FragmentUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

import java.util.List;

/**
 * Created by Marek Kozina 09/28/2015
 * Main home screen activity where the user lands after logging in or registering successfully.
 * Contains the main menu options and a message to the user for now.  Further functionality will
 * be implemented.
 */
public class HomeScreenActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = HomeScreenActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    // The current logged-in user
    private AppUser mainUser;

    // DAO object for database transactions
    private UserDAO uDAO;

    // The preferences which store the current user
    private SharedPreferences prefs;

    // This Activity Context
    private Context CTX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        CTX = this;

        prefs = CTX.getSharedPreferences
                (getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.preference_stored_user), "Default");
        if(DEBUG) log.i(TAG, "Stored user is: "
                + prefs.getString(getString(R.string.preference_stored_user), "No User Stored"));

        // Open or re-open the DAO & database
        uDAO = new UserDAO(CTX);
        uDAO.open();

        mainUser = uDAO.getUserByName(currentUser);

        ImageView optionsIV = (ImageView) findViewById(R.id.options_button);
        optionsIV.setOnClickListener(
            new ImageView.OnClickListener(){
                public void onClick(View v) {
                    popupMenu(v);
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    /**
     * Custom menu to override the ActionBar menu
     * @param v View the optionsIV ImageView
     */
    public void popupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu_home_screen, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean returnMe = false;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(DEBUG) log.i(TAG, "ID for the item is : " + id + " Which is: " + item.getTitle());

        switch (id) {
            case R.id.action_get_all_users :
                if(DEBUG) log.i(TAG, "Logging all data from: " + uDAO.database.toString());
                uDAO.getAll(AppUserContract.AppUserEntry.TABLE_NAME);
                returnMe = true;
                break;
            case R.id.action_user_management :
                Intent userManagementIntent = new Intent(this, UserManagementActivity.class);
                startActivity(userManagementIntent);
                break;
            case R.id.action_memory_game :
                Intent cardIntent = new Intent(this, MemoryGameActivity.class);
                startActivity(cardIntent);
                returnMe = true;
                break;
            case R.id.action_check_tables :
                if(DEBUG) log.i(TAG, "Checking tables in: " + uDAO.database.toString());
                uDAO.checkTables();
                returnMe = true;
                break;
            case R.id.action_add_table :
                if(DEBUG) log.i(TAG, "Adding User Table to: " + uDAO.database.toString());
                uDAO.addAppUserTable();
                returnMe = true;
                break;
            case R.id.action_drop_table :
                if(DEBUG) log.i(TAG, "Dropping User Table from: " + uDAO.database.toString());
                uDAO.dropTable(AppUserContract.AppUserEntry.TABLE_NAME);
                returnMe = true;
                break;
            case R.id.action_logout :
                buildConfirmDialog(this, getResources().getString(R.string.logoff_dialogue));
                returnMe = true;
                break;
            case R.id.action_draw_gl :
                returnMe = true;
                Intent drawIntent = new Intent(HomeScreenActivity.this, OpenGLES20Activity.class);
                startActivity(drawIntent);
                break;
            default :
                returnMe = super.onOptionsItemSelected(item);
                break;
        }
        return returnMe;
    }
    /**
     *
     * @param context Context the Activity ApplicationContext
     * @param message String the message to present to the user
     */
    private void buildConfirmDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        uDAO = new UserDAO(CTX);
                        uDAO.open();
                        // Update preferences and the db of the log-out
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.preference_stored_user), null);
                        editor.commit();

                        // Update the isLogged flag for this particular user
                        mainUser.setLogged(false);
                        try {
                            uDAO.updateUser(mainUser);
                        } catch (SQLiteException e) {
                            if(DEBUG) log.e(TAG, e.getMessage());
                        }
                        // close the DB
                        uDAO.close();

                        // Re-open the main Activity for the log in screen
                        Intent intent = new Intent(HomeScreenActivity.this, MyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}