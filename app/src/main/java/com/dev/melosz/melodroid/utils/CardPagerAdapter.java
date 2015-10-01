package com.dev.melosz.melodroid.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dev.melosz.melodroid.fragments.LoginScreen;
import com.dev.melosz.melodroid.fragments.RegistrationFragment;

/**
 * Created by Marek on 9/28/2015.
 */
public class CardPagerAdapter extends FragmentPagerAdapter {
    // Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    CharSequence TITLES[];

    // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private static int NUM_TABS = 2;

    /**
     * Build a Constructor and assign the passed Values to appropriate values in the class
     * @param fm
     * @param mTitles
     * @param mNumbOfTabs
     */
    public CardPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabs) {
        super(fm);
        this.TITLES = mTitles;
        this.NUM_TABS = mNumbOfTabs;
    }

    /**
     * Returns the fragment to display for the selected tab
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        // if the position is 0, return the Login Screen Fragment
        switch (position){
            // User has tabbed to Sign Up
            case 1:
                frag = RegistrationFragment.newInstance(1, "Registration Page");
                break;
            // Default state, or user tabbed back to Login Screen
            case 0:
            default :
                frag = LoginScreen.newInstance(0, "Login Page");
                break;
        }
        return frag;
    }

    /**
     * This method return the titles for the Tabs in the Tab Strip
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    /**
     * This method return the Number of tabs for the tabs Strip
     * @return
     */
    @Override
    public int getCount() {
        return NUM_TABS;
    }
}
