package com.dev.melosz.melodroid.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.drawable.BorderDrawable;
import com.dev.melosz.melodroid.views.BorderFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by marek.kozina on 9/8/2015.
 * Helper Utility class for all Activities and Fragments
 *
 *   Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header
 *                              2. Added registration page password clause to enableButton methods
 *
 */
public class FragmentUtil {
    // Used for generating viewIDs
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    // Colors used in the tab indicator
    final int GREY = Color.parseColor("#C4C4C4");
    final int BLACK = Color.BLACK;

    /**
     * This method will enable/disable a button when certain conditions are met
     * @param fields EditText fields
     * @param id int the ID of the button in question
     * @param v View the current view
     */
    public void enableButtonByTextFields(EditText[] fields, int id, View v) {
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
     * This method will enable/disable a button when certain conditions are met
     * @param fields EditText fields
     * @param v View the current view
     */
    public boolean enableButtonByTextFields(EditText[] fields, View v) {
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
    private boolean checkMinLength(int min, EditText et){
        return et.getText().toString().length() <= min;
    }
    /**
     * This method will hide the native android keyboard
     * @param act Activity the current Activity
     */
    public void hideKeyboard(Activity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * TODO: Maybe remove...currently unused
     * @param et EditText the EditText field to check if it's empty
     * @return boolean whether or not the field is empty
     */
    public boolean checkEmpty(EditText et){
        return et.getText() != null && et.getText().toString().length() != 0;
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
    public void setTabBackground(BorderFrameLayout layout, BorderDrawable bd, int dp) {
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
    @SuppressWarnings("deprecation")
    public void setBorderBackground(BorderFrameLayout layout, BorderDrawable bd, int dp) {
        // get the device's SDK
        int sdk = Build.VERSION.SDK_INT;
        // set the border colors for selected appearance
//        bd.setAllBorder(dp, BLACK); TODO

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
    public void clearBackground(BorderFrameLayout layout) {
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
     * Converts pixels to dp
     * @param context Context the Application Context
     * @param px int the pixels to convert
     * @return int the converted dp
     */
    public static int dpFromPx(final Context context, int px) {
        return (int) ((px / context.getResources().getDisplayMetrics().density) +.05);
    }

    /**
     * Converts dp to pixels
     * @param context Context the Application Context
     * @param dp int the dp to convert
     * @return int the converted pixels
     */
    public static int pxFromDp(final Context context, int dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) +.05);
    }

    /**
     * Helper method to print an object in pretty JSON format
     * @param obj AppUser the user object to display
     * @return String the JSON formatted output
     */
    public String prettyPrintObject(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(obj);
        return json;
    }
}