package com.dev.melosz.melodroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.database.UserDAO;
import com.dev.melosz.melodroid.drawable.BorderDrawable;
import com.dev.melosz.melodroid.fragments.RegistrationFragment;
import com.dev.melosz.melodroid.utils.FragmentUtil;
import com.dev.melosz.melodroid.utils.ViewPagerAdapter;
import com.dev.melosz.melodroid.views.BorderFrameLayout;

/**
 * Created by Marek on 9/28/2015.
 * Main Launcher activity which also performs all DAO calls for the Login and Registration fragments
 *
 *   Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header
 *                              2. Fixed registerNewUser so the logged flag is set and saved
 *
 */
public class MyActivity extends FragmentActivity {
    private final String CLASS_NAME = MyActivity.class.getSimpleName();
    // Activity and Pager declarations
    private ViewPager mViewPager;
    private ViewPagerAdapter viewAdapter;
    private final CharSequence TITLES[] = {"Login", "Signup"};
    private final int NUM_TABS = 2;
    private int DP;
    private Context CTX;

    // Fragment Utility helper class
    private FragmentUtil FUTIL = new FragmentUtil();

    // User DAO adapter for connecting with the databaseHelper
    private UserDAO uDAO;

    // Store user state if logged in/etc.
    private SharedPreferences prefs;

    // Views
    private ImageView ivExitButton;
    private TextView tvLogin;
    private TextView tvSignup;

    /**
     * Default overridden onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        boolean userLoggedIn;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        CTX = this;
        prefs = CTX.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Initialize and open the DB classes
        uDAO = new UserDAO(CTX);
        uDAO.open();

        // See if there is a stored user in the UserPreferences
        String storedUser = prefs.getString(getString(R.string.preference_stored_user), null);
        Log.i(CLASS_NAME, "Stored user is: [" + storedUser + "].");

        // If storedUser isn't null, check to see if the logged flag is set
        userLoggedIn = storedUser != null && checkUserLogged(storedUser);

        // Check if the user is already logged in and present the screen accordingly
        if (!userLoggedIn) {
            initializeActivityViews();
        }
        else {
            openHomeScreenActivity(HomeScreenActivity.class);
        }
    }

    /**
     * Initialization method to set all listeners for UI interaction objects.
     */
    public void initializeActivityViews() {
        // Initialize Clickable Views
        ivExitButton = (ImageView) findViewById(R.id.exit_button);
        tvLogin = (TextView) findViewById(R.id.login_text);
        tvSignup = (TextView) findViewById(R.id.signup_text);

        // Get the screen density and convert the dp into a pixel int
        DP = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        // Instantiate the ViewPager & Adapter
        mViewPager = (ViewPager) findViewById(R.id.fragment_pager);
        viewAdapter = new ViewPagerAdapter(getSupportFragmentManager(), TITLES, NUM_TABS);
        mViewPager.setAdapter(viewAdapter);

        // Set the initial tab selected as the login screen
        mViewPager.setCurrentItem(0);
        setTabSelected(0);

        // Attach page change listener
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                FUTIL.hideKeyboard(MyActivity.this);
                // Draw the indicator box around the selected tab
                setTabSelected(mViewPager.getCurrentItem());
                RegistrationFragment frag = (RegistrationFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.registration_screen_fragment);
                if (position == 0 && frag != null) {
                    frag.clearAllFocus();
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Set the Exit Button Listener and prompt dialog
        ivExitButton.setOnClickListener(
                new ImageView.OnClickListener(){
                    public void onClick(View v) {
                        buildConfirmDialog(CTX, getString(R.string.exit_dialogue));
                    }
                }
        );

        // Set up the tabbable clicks
        tvLogin.setOnClickListener(
                new TextView.OnClickListener(){
                    public void onClick(View v){
                        if (mViewPager.getCurrentItem() == 1){
                            mViewPager.setCurrentItem(0);
                            setTabSelected(0);
                        }
                    }
                }
        );
        tvSignup.setOnClickListener(
                new TextView.OnClickListener(){
                    public void onClick(View v){
                        if (mViewPager.getCurrentItem() == 0){
                            mViewPager.setCurrentItem(1);
                            setTabSelected(1);
                        }
                    }
                }
        );

    }

    /**
     * Draws the indicator border around the selected tab
     * @param tab int the position of the selected tab
     */
    public void setTabSelected(int tab) {
        BorderFrameLayout bLayout1 = (BorderFrameLayout) findViewById(R.id.login_layout_wrapper);
        BorderFrameLayout bLayout2 = (BorderFrameLayout) findViewById(R.id.signup_layout_wrapper);
        switch (tab) {
            case 0:
                FUTIL.clearBackground(bLayout2);
                FUTIL.setTabBackground(bLayout1, new BorderDrawable(), DP);
                break;
            case 1:
                FUTIL.clearBackground(bLayout1);
                FUTIL.setTabBackground(bLayout2, new BorderDrawable(), DP);
                break;
            default:
                break;
        }
    }
    /**
     * Default overridden onCreateOptionsMenu method
     * @param menu Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);

        return true;
    }

    /**
     * Default overridden onOptionsItemSelected method
     * @param item MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will authenticate the user based on the User Name and Password entered
     * @param creds Array of Strings containing the Username and Password
     * @return boolean whether or not the credentials can be authenticated
     */
    public boolean checkCredentials(String[] creds) {
        boolean passed = uDAO.checkCredentials(creds);

        // If the user logs in successfully, set the Logged flag to true and update the user
        if(passed) {
            AppUser user = uDAO.getUserByName(creds[0]);
            user.setLogged(true);
            uDAO.updateUser(user);
        }

        return passed;
    }

    /**
     * This method is used to check if the user has the flag set where they are already logged in.
     * @param user String the UserName from the unET EditText
     * @return boolean whether the user is logged in or not
     */
    public boolean checkUserLogged(String user) {
        boolean check = false;
        if(user != null) {
            AppUser mUser = null;
            try {
                Log.i(CLASS_NAME, "Check if user [" + user + "] is logged in.");
                mUser = uDAO.getUserByName(user);
            }
            catch (Exception e) {
                Log.e(CLASS_NAME, e.getLocalizedMessage());
            }
            if (mUser != null && mUser.isLogged()) {
                Log.i(CLASS_NAME, "User [" + user + "] is already logged in.");
                check = true;
            }
        }
        return check;
    }

    /**
     * Checks the SQLite database if a user exists
     * @param user String the Username String from the unET EditText
     * @return boolean whether or not the user exists
     */
    public boolean checkIfUserExists(String user) {
        return uDAO.doesUserExist(user);
    }

    /**
     * Method to add a new user to the SQLite Database and open the home screen
     * @param user AppUser the new user
     * @return boolean if the user was successfully saved in the db
     */
    public boolean registerNewUser(AppUser user) {
        boolean success;
        try{
            uDAO.saveNewUser(user);
            success = true;
        }
        catch (SQLiteException e){
            Log.e(CLASS_NAME, e.getLocalizedMessage());
            success = false;
        }
        if(success) {
            Log.i(CLASS_NAME,
                    "Updating user [" + user.getUserName() + "] log status to logged in.");
            AppUser newUser = uDAO.getUserByName(user.getUserName());
            newUser.setLogged(true);
            uDAO.updateUser(newUser);
        }
        return success;
    }
    /**
     * Method to redirect to the Home Screen Activity and close the main Login/Signup activity
     * @param clazz Activity the new Activity to start
     */
    public void openHomeScreenActivity(Class clazz) {
        // Start a new Intent to open the Home Screen
        Intent intent = new Intent(this, clazz);
        Bundle args = new Bundle();
        args.putString("Message", getString(R.string.welcome_back_toast));
        intent.putExtras(args);

        startActivity(intent);

        // Finish this activity to remove from the back stack
        finish();
    }

    /**
     * This method allows us to go "back" to the login screen from the registration screen, or close
     * the app all together if no screen is in the BackStack.
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().commit();
        }
        else {
            super.onBackPressed();
            finish();
        }
    }
    /**
     * Builds a confirmation Dialog box with Yes or No as choices
     *
     * @param context the Activity Context
     * @param message String the message to display in the dialog
     */
    private void buildConfirmDialog(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // finish the activity and close the app
                        uDAO.close();
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