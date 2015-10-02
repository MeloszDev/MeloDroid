package com.dev.melosz.melodroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.database.UserDAO;

import java.util.List;

/**
 * Created by Marek Kozina 09/30/2015
 * Activity for perfoming User operations such as edit, delete, add, clone, etc.
 *
 *   Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header & initial functionality
 *
 */
public class UserManagementActivity extends AppCompatActivity {
    // The container view which has layout change animations turned on.
    private ViewGroup mContainerView;

    // Handler for delaying the populating of rows to show the animation
    private Handler handler = new Handler();

    // DAO database adapter
    private UserDAO uDAO;

    // Application context
    private Context CTX;

    // The list of users to display
    List<AppUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        mContainerView = (ViewGroup) findViewById(R.id.container);

        CTX = this;

        // Open or re-open the DAO & database
        uDAO = new UserDAO(CTX);
        uDAO.open();

        // Pull all users from the database and add them to the dynamic scrollview
        users = uDAO.getAllUsers();

        int delay = 0;
        if(users != null) {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
            for(AppUser user : users) {
                delay += 250;
                final AppUser currUser = user;
                // Delay the rebuilding of the cards so the views can update.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addItem(currUser);
                    }
                }, delay);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user_managment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the HomeScreenActivity.
                NavUtils.navigateUpTo(this, new Intent(this, HomeScreenActivity.class));
                return true;

            case R.id.action_add_user:
                // Hide the "empty" view since there is now at least one item in the list.
                findViewById(android.R.id.empty).setVisibility(View.GONE);

                //TODO: Create EditUserFragment
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(CTX, "Not implemented yet!", duration).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds the rows to the container view and displays the UserName in the row. Clicking X in the
     * row will delete a user and clicking the pencil icon will (in the future) bring up the
     * EditUserFragment to edit the user's profile.
     * @param user AppUser the AppUser for that particular row
     */
    private void addItem(AppUser user) {
        // Instantiate a new "row" view.
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.user_list_item, mContainerView, false);

        // Instantiate the AppUser attached to the listeners of this row
        final AppUser rowUser = user;

        // Set the text in the new row to the user.
        ((TextView) newView.findViewById(android.R.id.text1)).setText(user.getUserName());

        // Will display the EditUserFragment
        newView.findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: Create EditUserFragment
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(CTX, "Not implemented yet!", duration).show();
            }
        });
        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to delete user ["
                        + rowUser.getUserName() + "]? This process cannot be un-done.";

                // Prompt the user to confirm if they want to delete the user
                buildConfirmDialog(CTX, message, rowUser, newView);

            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView.addView(newView, 0);
    }
    /**
     * Builds a confirmation Dialog box with Yes or No as choices
     *
     * @param context the Activity Context
     * @param message String the message to display in the dialog
     */
    private void buildConfirmDialog(final Context context, String message, AppUser user, View v) {
        final AppUser deleteUser = user;
        final View view = v;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(uDAO.deleteUser(deleteUser))
                            Log.i("UserDAO", "Successfully deleted user ["
                                  + deleteUser.getUserName() + "]");

                        mContainerView.removeView(view);
                        // If there are no rows remaining, show the empty view.
                        if (mContainerView.getChildCount() == 0) {
                            findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                        }
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

