package com.dev.melosz.melodroid.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.dev.melosz.melodroid.fragments.LoginScreen;
import com.dev.melosz.melodroid.fragments.RegistrationFragment;

/**
 * Created by marek.kozina on 9/10/2015.
 * Adapter that handles the UI interaction between the LoginScreen fragment and RegistrationFragment
 */
public class LoginViewPagerAdapter extends FragmentStatePagerAdapter {
    // Logging controls
    private LogUtil log = new LogUtil();
    private final static String TAG = LoginViewPagerAdapter.class.getSimpleName();
    private final static boolean DEBUG = false;

    // Store the number of tabs, this will also be passed when the LoginViewPagerAdapter is created
    private static int NUM_TABS = 2;

    /**
     * Build a Constructor
     * @param fm FragmentManager the Activity's FragmentManager
     */
    public LoginViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Returns the fragment to display for the selected tab/swiped page
     * @param position int the page/tab position
     * @return the Fragment that was swiped/tapped to
     */
    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        if(DEBUG) log.i(TAG, "Getting current Fragment at position: [" + position + "]");
        // if the position is 0, return the Login Screen Fragment
        switch (position){
            // User has tabbed to Sign Up
            case 1:
                frag = new RegistrationFragment();
                break;
            // Default state, or user tabbed back to Login Screen
            case 0:
            default :
                frag = new LoginScreen();
                break;
        }
        return frag;
    }

    /**
     * This method return the Number of tabs for the tabs Strip
     * @return the number of tabs/pages
     */
    @Override
    public int getCount() {
        return NUM_TABS;
    }
}