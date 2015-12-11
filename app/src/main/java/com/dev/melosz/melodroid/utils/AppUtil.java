package com.dev.melosz.melodroid.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.drawable.BorderDrawable;
import com.dev.melosz.melodroid.views.BorderFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Created by marek.kozina on 9/8/2015.
 * Helper Utility class for all Activities and Fragments
 *
 */
public class AppUtil {
    // Used for generating viewIDs
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    // The title bar global font
    private static final String TITLE_FONT = "neuropol.ttf";

    // Colors used in the tab indicator
    final static int GREY = Color.parseColor("#C4C4C4");
    final static int BLACK = Color.BLACK;
    final static int APP_COLOR = Color.parseColor("#36454b");

    /**
     * This method will enable/disable a button explicitly when certain conditions are met
     * @param fields EditText fields
     * @param id int the ID of the button in question
     * @param v View the current view
     */
    public static void enableButtonByTextFields(EditText[] fields, int id, View v) {
        Button button = (Button) v.findViewById(id);
        boolean disableButton = false;
        for (EditText et : fields) {
            if (et.getId() == R.id.email_field && checkMinLength(6, et)) {
                disableButton = false;
                break;
            }
            else if(et.getId() != R.id.email_field && checkMinLength(3, et)) {
                disableButton = false;
                break;
            }
            else if((et.getId() == R.id.password_field ||
                     et.getId() == R.id.password_field_reg) && checkMinLength(5, et)) {
                disableButton = false;
                break;
            }
            else {
                disableButton = true;
            }
        }
        button.setEnabled(disableButton);
    }
    /**
     * This method will return a T/F flag depending on the fields id's and length. Used for enabling
     * and disabling the submit button from the RegistrationFragment
     * @param fields EditText fields
     */
    public static boolean enableButtonByTextFields(EditText[] fields) {
        boolean disableButton = false;
        for (EditText et : fields) {
            if (et.getId() == R.id.email_field && checkMinLength(6, et)) {
                disableButton = false;
                break;
            }
            else if(et.getId() != R.id.email_field && checkMinLength(3, et)) {
                disableButton = false;
                break;
            }
            else if((et.getId() == R.id.password_field ||
                     et.getId() == R.id.password_field_reg) && checkMinLength(5, et)) {
                disableButton = false;
                break;
            }
            else {
                disableButton = true;
            }
        }
        return disableButton;
    }
    /**
     * Helper method to check the minimum length required for EditText fields
     * @param min int the minimum required length for the EditText String
     * @param et EditText the field
     * @return boolean whether or not the length is below the minimum
     */
    private static boolean checkMinLength(int min, EditText et){
        return et.getText().toString().length() <= min;
    }
    /**
     * This method will hide the native android keyboard
     * @param act Activity the current Activity
     */
    public static void hideKeyboard(Activity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Draws the border around the selected tab
     *
     * @param layout BorderFrameLayout the tab layout
     * @param bd BorderDrawable the drawable object
     * @param dp int the dp size
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setTabBackground(BorderFrameLayout layout, BorderDrawable bd, int dp) {
        // get the device's SDK
        int sdk = Build.VERSION.SDK_INT;
        // set the border colors for selected appearance
        bd.setRightBorder(dp, GREY);
        bd.setBottomBorder(dp, GREY);
        bd.setTopBorder(dp, BLACK);
        bd.setLeftBorder(dp, BLACK);

        // handle deprecated methods based on the sdk
        if(sdk < Build.VERSION_CODES.JELLY_BEAN){
            layout.setBackgroundDrawable(bd);
        }
        else {
            layout.setBackground(bd);
        }
        // invalidate will refresh the drawables and update the UI
        layout.invalidate();
    }

    /**
     * Draws the border around the selected tab
     *
     * @param layout BorderFrameLayout the border-able layout
     * @param bd BorderDrawable the drawable object
     * @param dp int the dp size
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings({ "deprecation", "unused" })
    public static void setBorderBottom(BorderFrameLayout layout, BorderDrawable bd, int dp) {
        // get the device's SDK
        int sdk = Build.VERSION.SDK_INT;
        // set the border colors for selected appearance
        bd.setBottomBorder(dp, APP_COLOR);

        // handle deprecated methods based on the sdk
        if(sdk < Build.VERSION_CODES.JELLY_BEAN){
            layout.setBackgroundDrawable(bd);
        }
        else {
            layout.setBackground(bd);
        }
        // invalidate will refresh the drawables and update the UI
        layout.invalidate();
    }

    /**
     * Erases the border of the now un-selected tab
     * @param layout BorderFrameLayout the tab to clear the indicator of
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void clearBackground(BorderFrameLayout layout) {
        int sdk = Build.VERSION.SDK_INT;
        // handle deprecated methods based on the sdk
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(null);
        } else {
            layout.setBackground(null);
        }
        // invalidate will refresh the drawables and update the UI
        layout.invalidate();
    }

    /**
     * Sets the font on a particular TextView
     * @param assets AssetManager
     * @param tv TextView the text to apply the font to
     */
    public static void setTitleFont(AssetManager assets, TextView tv) {
        Typeface tf = Typeface.createFromAsset(assets, TITLE_FONT);
        tv.setTypeface(tf);
    }
    /**
     * Generates a unique ID. Used for each dynamically created view
     * @return int the new unique ID
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();

            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.

            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Gets the logged in AppUser stored in the SharedPreferences
     * @param ctx Context the Application Context
     * @return String the AppUser.userName
     */
    public static String getLoggedUser(Context ctx){
        SharedPreferences prefs = getSharedPrefs(ctx);
        return prefs.getString(ctx.getString(R.string.preference_stored_user), null);
    }

    /**
     * Obtain the SharedPreferences for this Context
     * @param ctx Context the Application Context
     * @return The app's SharedPreferences
     */
    public static SharedPreferences getSharedPrefs(Context ctx) {
        return ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
    }

    /**
     * Method to join strings with a chosen delimiter
     * @param list the ArrayList of Strings to delimit
     * @param delimiter String the delimiter desired
     * @return String the joined and delimited String
     */
    public static String joinStrings(List<String> list, String delimiter) {
        StringBuilder sb = new StringBuilder();

        // For the first iteration, the loopingDelimiter will be blank
        String loopingDelimiter = "";

        // Iterate through the list and append the delimiter
        for(String s : list) {
            sb.append(loopingDelimiter);
            sb.append(s);

            // Add the delimiter from here on out
            loopingDelimiter = delimiter;
        }
        return sb.toString();
    }

    /**
     * Splits a comma delimited String sequence into a String[]
     * @param delimited String to split
     * @return the split String[]
     */
    public static String[] splitDelimitedString(String delimited){
        return delimited.split(Pattern.quote(", "));
    }

    /**
     * Converts pixels to dp
     * @param context Context the Application Context
     * @param px int the pixels to convert
     * @return int the converted dp
     */
    @SuppressWarnings("unused")
    public static int dpFromPx(final Context context, int px) {
        return (int) ((px / context.getResources().getDisplayMetrics().density) +.05);
    }

    /**
     * Converts dp to pixels
     * @param context Context the Application Context
     * @param dp int the dp to convert
     * @return int the converted pixels
     */
    @SuppressWarnings("unused")
    public static int pxFromDp(final Context context, int dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) +.05);
    }

    /**
     * Helper method to print an object in pretty JSON format
     * @param obj AppUser the user object to display
     * @return String the JSON formatted output
     */
    public static String prettyPrintObject(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    /**
     * Assigns a random UUID
     * @return String the random UUID
     */
    public static String assignUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static void putPreference(Context ctx, String prefKey, String prefValue){

    }

    /**
     * Helper method to make a somewhat-randomly generated
     * @return List of randomized AppUsers
     */
    @SuppressWarnings("unused")
    public static List<AppUser> makeDummyUserList (){
        List<AppUser> users = new ArrayList<>();
        List<String> names = new ArrayList<>();
        String[] first = new String[]{
                "Melo", "Droid", "Melosz", "Patrick", "Thomas",
                "Greg", "Chris", "User", "Morrison",
                "Kaner", "Boop", "Moop", "Island", "Rando"
        };
        String[] second = new String[]{
                "Zoo", "One", "Miiize", "Champ", "Dude", "XxxX",
                "LMAO", "Bedoop", "Killa", "Hawks", "Town",
                "Pop1", "WoW", "Doge",
        };
        Collections.shuffle(Arrays.asList(first));
        Collections.shuffle(Arrays.asList(second));

        for(int i = 0; i < first.length; i++) {
            String newName = first[i] + second[i] + Math.round(Math.random() * 2016);
            names.add(newName);
        }
        for(String name : names) {
            AppUser newUser = new AppUser();
            newUser.setUserName(name);
            newUser.setPassword("Password");
            newUser.setEmail(name + "@gmail.com");
            newUser.setPhoneNumber(randNum(9000000000L, 1000000000L));
            newUser.setZip(randNum(90000L, 10000L));
            users.add(newUser);
        }
        return users;
    }
    public static String randNum(long dig, long len) {
        String number;
        int num = (int) Math.abs((Math.round(Math.random() * dig) + len));
        number = Integer.toString(num);
        return number;
    }
}