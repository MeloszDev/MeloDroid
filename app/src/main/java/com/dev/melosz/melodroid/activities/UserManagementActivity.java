package com.dev.melosz.melodroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.database.UserDAO;
import com.dev.melosz.melodroid.fragments.EditUserFragment;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

import java.util.List;

/**
 * Created by Marek Kozina 09/30/2015
 * Activity for performing User operations such as edit, delete, add, clone, etc.
 *
 */
public class UserManagementActivity extends FragmentActivity
        implements PopupMenu.OnMenuItemClickListener {

    // Logging controls
    private LogUtil log = new LogUtil();
    private final static String TAG = UserManagementActivity.class.getSimpleName();
    private final static boolean DEBUG = false;

    // The container view which has layout change animations turned on.
    private ViewGroup mContainerView;

    // Handler for delaying the populating of rows to show the animation
    private Handler handler = new Handler();

    // Base utility class
    AppUtil appUtil = new AppUtil();

    // DAO database adapter
    private UserDAO uDAO;

    // Application context
    private Context CTX;

    // The list of users to display
    List<AppUser> users;

    // Stores the current EditFragment
    private EditUserFragment mEditFrag;

    // Holder variables to determine which fragments are populated in the respective container
    private Integer rowClicked;
    private boolean containerVisible;

    private View fragContainer;
    private ViewGroup rowGroup;
    private AppUser rowUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        mContainerView = (ViewGroup) findViewById(R.id.container);
        ImageView optionsIV = (ImageView) findViewById(R.id.options_button);

        TextView titleTV = (TextView) findViewById(R.id.title_bar);
        appUtil.setTitleFont(getAssets(), titleTV);

        CTX = this;
        containerVisible = true;

        // Open or re-open the DAO & database
        uDAO = new UserDAO(CTX);
        uDAO.open();

        if(DEBUG) {
            log.i(TAG, "Creating Dummy User List");
            // Only need to run this on new install instances
            users = appUtil.makeDummyUserList();
            for(AppUser user : users) {
                uDAO.saveNewUser(user);
                appUtil.prettyPrintObject(user);
            }
        }

        // Pull all users from the database and add them to the dynamic scrollview
        users = uDAO.getAllUsers();

        int delay = 0;
        if(users != null) {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
            for(AppUser user : users) {
                delay += 100;
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

        optionsIV.setOnClickListener(
                new ImageView.OnClickListener(){
                    public void onClick(View v) {
                        popupMenu(v);
                    }
                }
        );
    }

    /**
     * Custom menu to override the ActionBar menu
     * @param v View the optionsIV ImageView
     */
    public void popupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu_user_managment, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user_managment, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the HomeScreenActivity.
                NavUtils.navigateUpTo(this, new Intent(this, HomeScreenActivity.class));
                return true;

            case R.id.action_add_user:
                // Hide the "empty" view since there is now at least one item in the list.
                findViewById(android.R.id.empty).setVisibility(View.GONE);

                Toast.makeText(CTX, "Not implemented yet!", Toast.LENGTH_SHORT).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to access the UserDAO to obtain an AppUser by the UserName
     * @param userName String the AppUser UserName
     * @return the AppUser
     */
    public AppUser getAppUser(String userName){
        return uDAO.getUserByName(userName);
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
        final AppUser currentUser = user;
        // Generate new IDs for the fragment containers so they can be targeted separately
        fragContainer = newView.getChildAt(1);
        fragContainer.setId(appUtil.generateViewId());

        // Set the text in the new row to the user.
        ((TextView) newView.findViewById(android.R.id.text1)).setText(user.getUserName());

        final ImageButton editButton = (ImageButton) newView.findViewById(R.id.edit_button);

        // Will display the EditUserFragment
        editButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the current row
                rowUser = currentUser;
                rowGroup = newView;
                fragContainer = newView.getChildAt(1);
                hideShowFragment();
            }
        });
        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to delete user ["
                        + currentUser.getUserName() + "]? This process cannot be un-done.";

                // Prompt the user to confirm if they want to delete the user
                buildConfirmDialog(CTX, message, currentUser, newView);

            }
        });
        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView.addView(newView, 0);
    }

    /**
     * Clears all of the indication borders
     * @param vg ViewGroup the parent ViewGroup to clear
     */
    public void clearAllBackground(ViewGroup vg){
        for(int i=0; i < vg.getChildCount(); i++){
            View v = vg.getChildAt(i);
            v.setBackgroundResource(0);
        }
    }

    /**
     * This method handles the dynamic show/hide of the EditUserFragment
     */
    public void hideShowFragment () {
        // Clear previous indicator borders
        clearAllBackground(mContainerView);

        // Build the EditUserFragment
        EditUserFragment fragment = EditUserFragment.newInstance(rowUser.getUserName());
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTran = fragMan.beginTransaction();

        // Store the current fragment so we can remove it on separate items
        if (mEditFrag != null) {
            fragTran.remove(mEditFrag);
            mEditFrag = fragment;
        } else {
            mEditFrag = fragment;
        }

        // Checks if the container Layout is visible or not and populates or de-populates the
        // fragment container accordingly
        if (rowClicked != null && rowClicked == fragContainer.getId() && containerVisible) {
            // close the fragment container
            fragTran.commit();
            containerVisible = false;
            rowGroup.setBackgroundResource(0);
        } else {
            // Add the fragment which is the 2nd child of newView ViewGroup as per the xml
            fragTran.add(rowGroup.getChildAt(1).getId(), mEditFrag, rowUser.getUserName());
            fragTran.commit();
            containerVisible = true;
            rowGroup.setBackgroundResource(R.drawable.edit_user_border);
        }
        rowClicked = fragContainer.getId();
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
                        uDAO.open();

                        if(uDAO.deleteUser(deleteUser)) {
                            Toast.makeText(CTX,
                                    "Successfully deleted user [" + deleteUser.getUserName()
                                    + "]", Toast.LENGTH_SHORT).show();
                        }

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

