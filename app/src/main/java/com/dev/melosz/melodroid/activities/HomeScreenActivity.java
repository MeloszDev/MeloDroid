package com.dev.melosz.melodroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.database.AppUserContract;
import com.dev.melosz.melodroid.database.DatabaseHelper;
import com.dev.melosz.melodroid.database.UserDAO;

public class HomeScreenActivity extends AppCompatActivity {
    private AppUser mainUser;
    private UserDAO uDAO;
    private DatabaseHelper dh;
    private SQLiteDatabase db;
    private Context CTX;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        CTX = this;

        prefs = CTX.getSharedPreferences(getString(R.string.preference_file_key), CTX.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.preference_stored_user), "Default");
        System.out.println("Stored user is: " + prefs.getString
                (getString(R.string.preference_stored_user), "No User Stored"));

        // TODO: REMOVE WHEN DB CLEANUP IS COMPLETED
        // Initialize the DB for the app
//        dh = dh.getInstance(this);
//        db = dh.getReadableDatabase();

        uDAO = new UserDAO(CTX);
        uDAO.open();

        mainUser = uDAO.getUserByName(currentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnMe;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        System.out.println("ID for the item is : " + id + " Which is: " + item.getTitle());

        // TODO: DRAMATICALLY CHANGE THE MENU OPTIONS
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings :
                System.out.println("So you want to go to settings, huh?");
                uDAO.getAll(AppUserContract.AppUserEntry.TABLE_NAME);
                returnMe = true;
                break;
            case R.id.action_account :
                System.out.println("Building and saving New User");
                //                uDAO.saveNewUser(dh, makeCreds());
                returnMe = true;
                break;
            case R.id.action_memory_game :
                Intent cardIntent = new Intent(this, MemoryGameActivity.class);
                startActivity(cardIntent);
                returnMe = true;
                break;
            case R.id.action_check_tables :
                System.out.println("Checking tables in: " + db.toString());
                uDAO.checkTables();
                returnMe = true;
                break;
            case R.id.action_add_table :
                System.out.println("Adding User Table to: " + db.toString());
                uDAO.addAppUserTable();
                returnMe = true;
                break;
            case R.id.action_drop_table :
                System.out.println("Dropping User Table from: " + db.toString());
                uDAO.dropTable(AppUserContract.AppUserEntry.TABLE_NAME);
                returnMe = true;
                break;
            case R.id.action_logout :
                System.out.println("You are now subscribed to catFacts!");
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
        }
        return returnMe;
    }
    /**
     *
     * @param context
     * @param message
     */
    private void buildConfirmDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Update preferences and the db of the log-out
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.preference_stored_user), null);
                        editor.commit();

                        // Update the isLogged flag for this particular user
                        mainUser.setLogged(false);
                        try {
                            uDAO.updateUser(mainUser);
                        } catch (SQLiteException e) {
                            Log.e("ERROR: ", e.getMessage());
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