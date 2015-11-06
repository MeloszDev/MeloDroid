package com.dev.melosz.melodroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.classes.Contact;
import com.dev.melosz.melodroid.database.ContactDAO;
import com.dev.melosz.melodroid.database.UserDAO;
import com.dev.melosz.melodroid.fragments.EditContactFragment;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.BackgroundTask;
import com.dev.melosz.melodroid.utils.LogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Marek Kozina 09/30/2015
 * Activity for performing Contact operations such as edit, delete, add, etc.
 *
 */
public class ContactManagementActivity extends FragmentActivity
        implements PopupMenu.OnMenuItemClickListener {

    // Logging controls
    private LogUtil log = new LogUtil();
    private final static String TAG = ContactManagementActivity.class.getSimpleName();
    private final static boolean DEBUG = false;

    public static final String KEY_USERNAME = "userName";

    // The container view which has layout change animations turned on.
    private ViewGroup mContainerView;

    // Handler for delaying the populating of rows to show the animation
    private Handler handler = new Handler();

    // DAO database adapters
    private UserDAO uDAO;
    private ContactDAO cDAO;

    // Application context
    private Context mCTX;

    // The list of contacts to display
    List<Contact> contacts;

    // Stores the current EditFragment
    private EditContactFragment mEditFrag;

    // Holder variables to determine which fragments are populated in the respective container
    private Integer rowClicked;
    private boolean containerVisible;

    private View fragContainer;
    private ViewGroup rowGroup;
    private Contact rowContact;
    private AppUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCTX = this;

        // Open or re-open the DAOs & databases
        uDAO = new UserDAO(mCTX);
        cDAO = new ContactDAO(mCTX);
        uDAO.open();
        cDAO.open();

        if(savedInstanceState != null) {
            String un = savedInstanceState.getString(KEY_USERNAME);
            mUser = uDAO.getUserByName(un);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_management);

        mContainerView = (ViewGroup) findViewById(R.id.contact_container);
        ImageView optionsIV = (ImageView) findViewById(R.id.options_button);

        TextView titleTV = (TextView) findViewById(R.id.title_bar);
        AppUtil.setTitleFont(getAssets(), titleTV);

        ImageButton syncButton = (ImageButton) findViewById(R.id.sync_button);

        containerVisible = true;

        Bundle b = getIntent().getExtras();
        if(b != null && mUser == null) {
            mUser = uDAO.getUserByName(b.getString(KEY_USERNAME));
        }

        if(mUser != null)
            gatherContacts();


        optionsIV.setOnClickListener(
            new ImageView.OnClickListener(){
                public void onClick(View v) {
                    popupMenu(v);
                }
            }
        );

        syncButton.setOnClickListener(
            new ImageButton.OnClickListener() {
                public void onClick(View v) {
                    buildConfirmDialog(mCTX,
                            "Syncing contacts may take a while. Proceed?",
                            null,
                            v);
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_contact_managment, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the HomeScreenActivity.
                NavUtils.navigateUpTo(this, new Intent(this, HomeScreenActivity.class));
                return true;

            case R.id.action_add_contact:
                // Hide the "empty" view since there is now at least one item in the list.
                findViewById(android.R.id.empty).setVisibility(View.GONE);

                Toast.makeText(mCTX, "Not implemented yet!", Toast.LENGTH_SHORT).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the userName if orientation changes
     * @param outState the savedInstanceState when destroying the activity
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(KEY_USERNAME, mUser.getUserName());
    }

    /**
     * Repopulate the fragment if orientation changes
     * @param newConfig Configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        // If the rowContact is not null, then the fragment needs to be repopulated
        if(rowContact != null) {
            // No row was clicked, so set it to null to handle in the hideShowFragment method
            rowClicked = null;
            hideShowFragment();
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Runs a background task to gather all of the User's contacts
     */
    public void gatherContacts(){
        BackgroundTask task =
                new BackgroundTask(mCTX, "Gathering Contacts", "Please wait...");
        task.execute("gatherContacts",
                String.valueOf(mUser.getId()),
                null);
    }
    /**
     * Populates the mContainerView by adding a Contact for each row
     */
    public void populateContacts(List<Contact> contacts){
        this.contacts = contacts;

        if(contacts != null && contacts.size() > 0) {
            // Sort by first name
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact cont1, Contact cont2) {
                    // Since the list populates from the bottom up, sort in reverse
                    return cont2.getFirstName().compareTo(cont1.getFirstName());
                }
            });

            // Hide the empty No Contacts layout
            findViewById(android.R.id.empty).setVisibility(View.GONE);

            // Populate the UI by inserting each Contact
            for (Contact contact : contacts) {
                final Contact currContact = contact;
                // Delay the rebuilding of the contacts so the views can update.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(DEBUG) log.i(TAG, "Adding Contact: [" + currContact.getFirstName() + "]");
                        addItem(currContact);
                    }
                });
            }
        }
    }
    /**
     * Custom menu to override the ActionBar menu
     * @param v View the optionsIV ImageView
     */
    public void popupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu_contact_managment, popup.getMenu());
        popup.show();
    }

    /**
     * Method to access the UserDAO to obtain an Contact by the firstName
     * @param name String the Contact firstName
     * @return the Contact
     */
    public Contact getContact(String name){
        return cDAO.getContactByName(name, mUser.getId());
    }

    /**
     * Adds the rows to the container view and displays the UserName in the row. Clicking X in the
     * row will delete a user and clicking the pencil icon will (in the future) bring up the
     * EditContactFragment to edit the user's profile.
     * @param contact Contact the Contact for that particular row
     */
    private void addItem(Contact contact) {
        // Instantiate a new "row" view.
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.contact_list_item, mContainerView, false);

        // Instantiate the Contact attached to the listeners of this row
        final Contact currContact = contact;

        // Generate new IDs for the fragment containers so they can be targeted separately
        fragContainer = newView.getChildAt(1);
        fragContainer.setId(AppUtil.generateViewId());

        final ImageButton editButton = (ImageButton) newView.findViewById(R.id.edit_button);
        final TextView contactTV = (TextView) newView.findViewById(R.id.contact_text);

        // Set the text in the new row to the user.
        contactTV.setText(contact.getFirstName());

        // Will display the EditContactFragment
        editButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the current row
                rowContact = currContact;
                rowGroup = newView;
                fragContainer = newView.getChildAt(1);
                hideShowFragment();
            }
        });
        contactTV.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the current row
                rowContact = currContact;
                rowGroup = newView;
                fragContainer = newView.getChildAt(1);
                hideShowFragment();
            }
        });
        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to delete Contact ["
                        + currContact.getFirstName() + "]? This process cannot be un-done.";

                // Prompt the user to confirm if they want to delete the user
                buildConfirmDialog(mCTX, message, currContact, newView);

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
     * This method handles the dynamic show/hide of the EditContactFragment
     */
    public void hideShowFragment () {
        // Clear previous indicator borders
        clearAllBackground(mContainerView);

        // Build the EditContactFragment
        EditContactFragment fragment = EditContactFragment.newInstance(rowContact.getFirstName());
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
            rowContact = null;
            rowGroup.setBackgroundResource(0);
        } else {
            // Add the fragment which is the 2nd child of newView ViewGroup as per the xml
            fragTran.add(rowGroup.getChildAt(1).getId(), mEditFrag, rowContact.getFirstName());
            fragTran.commit();
            containerVisible = true;
            rowGroup.setBackgroundResource(R.drawable.edit_contact_border);

            // Scroll the view to the top of the group after the transition animation
            if(rowContact != null) {
                final ScrollView sv = (ScrollView) mContainerView.getParent();
                sv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sv.smoothScrollTo(0, rowGroup.getTop());
                    }
                }, 500);
            }
        }
        rowClicked = fragContainer.getId();
    }

    /**
     * Builds a confirmation Dialog box with Yes or No as choices
     *
     * @param context the Activity Context
     * @param message String the message to display in the dialog
     */
    private void buildConfirmDialog(final Context context, String message, Contact contact, View v) {
        final Contact deleteContact = contact;
        final View view = v;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    cDAO.open();
                    switch (view.getId()) {
                        case R.id.delete_button:
                            if (cDAO.deleteContact(deleteContact)) {
                                if(DEBUG) log.i(TAG, deleteContact.getFirstName() + " Deleted.");

                                Toast.makeText(mCTX,
                                    "Successfully deleted user [" + deleteContact.getFirstName()
                                    + "]", Toast.LENGTH_SHORT).show();
                            }

                            mContainerView.removeView(view);
                            // If there are no rows remaining, show the empty view.
                            if (mContainerView.getChildCount() == 0) {
                                findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                            }
                            cDAO.close();
                            break;

                        case R.id.sync_button:
                            if (DEBUG) log.i(TAG, "Syncing Contacts in BackgroundTask.");
                            BackgroundTask task =
                                    new BackgroundTask(context,
                                                       "Syncing Data",
                                                       "Please wait...");
                            task.execute("syncFromManager",
                                    String.valueOf(mUser.getId()),
                                    null);
                            break;
                        default:
                            break;
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

