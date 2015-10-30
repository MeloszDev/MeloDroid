package com.dev.melosz.melodroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.HomeScreenActivity;
import com.dev.melosz.melodroid.activities.MyActivity;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.classes.IMEListenerEditText;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

/**
 * Created by Marek Kozina 09/15/2015.
 * Pager-fragment Registration tab with user-info input fields with a changing helper TextView
 *
 */
public class RegistrationFragment extends Fragment implements View.OnFocusChangeListener {
    // Logging controls
    private LogUtil log = new LogUtil();
    private final static String TAG = RegistrationFragment.class.getSimpleName();
    private final static boolean DEBUG = false;

    // Application Context
    private Context mCTX;

    // Apps SharedPreferences
    private SharedPreferences prefs;

    // Handler for runnables
    final Handler handler = new Handler();

    // Fragment interaction views
    private EditText unET;
    private EditText pwET;
    private EditText pw2ET;
    private EditText emailET;
    private EditText zipET;
    private IMEListenerEditText phoneET;
    private Button submitButton;
    private TextView helperTV;

    // Global validation vars
    private boolean VALID = false;
    private EditText[] ET_BUNDLE;

    /**
     * Default constructor
     */
    public RegistrationFragment() {}

    /**
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCTX = getActivity().getApplicationContext();
        prefs = AppUtil.getSharedPrefs(mCTX);
    }

    /**
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        // Instantiate views
        unET = (EditText) view.findViewById(R.id.username_field_reg);
        pwET = (EditText) view.findViewById(R.id.password_field_reg);
        pw2ET = (EditText) view.findViewById(R.id.password_field_reg_reenter);
        emailET = (EditText) view.findViewById(R.id.email_field);
        zipET = (EditText) view.findViewById(R.id.zip_address_field);

        phoneET = (IMEListenerEditText) view.findViewById(R.id.phone_number_field);

        helperTV = (TextView) view.findViewById(R.id.helper_tv);
        TextView clearAllTV = (TextView) view.findViewById(R.id.clear_all_tv);
        clearAllTV.setPaintFlags(clearAllTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submitButton = (Button) view.findViewById(R.id.submit_button);

        // Contain all EditText fields in an array to loop through for text watching
        ET_BUNDLE = new EditText[]{
                unET,
                pwET,
                pw2ET,
                emailET,
                zipET,
                phoneET
        };

        // Initially disable submit button
        setValidity(VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE));

        // Clear default focus'
        clearAllFocus();

        // Assign TextWatchers & OnFocusChangeListeners to all Edit Text fields for enabling the
        // submit button & field validation
        for (EditText et : ET_BUNDLE) {
            et.setOnFocusChangeListener(this);
            final EditText currentET = et;
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    // Do NOT allow spaces
                    String result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        currentET.setText(result);
                        currentET.setSelection(result.length());
                    }

                    VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);

                    // Set button validity explicitly in case it has changed
                    setValidity(VALID);

                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        submitButton.setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUser user = getUserByFields();

                    if(((MyActivity) getActivity()).registerNewUser(user)) {
                        // Add user to preferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.preference_stored_user),
                                         user.getUserName());
                        editor.putInt(getString(R.string.preference_stored_user_id), user.getId());
                        editor.apply();

                        Toast.makeText(mCTX,
                                       "Welcome " + user.getUserName() + "!",
                                       Toast.LENGTH_SHORT).show();
                        ((MyActivity) getActivity())
                                .openHomeScreenActivity(HomeScreenActivity.class);
                    }
                    else
                        Toast.makeText(mCTX,
                                       "Registration Failed. Please try again.",
                                       Toast.LENGTH_SHORT)
                                       .show();
                }
            }
        );
        // Listen for the Done button
        phoneET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE &&
                        phoneET.getText().toString().length() != 0) {
                    validatePhone(phoneET);
                }
                return false;
            }
        });

        // Listen for Back button
        phoneET.setKeyImeChangeListener(new IMEListenerEditText.KeyImeChange() {
            @Override
                public void onKeyIme(int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    System.out.println("BACK BUTTON PRESSED");
                    validatePhone(phoneET);
                }
            }
        });

        // This will format the phone number EditText with special characters
        phoneET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Register the clear all TextView click
        clearAllTV.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v){
                for(EditText et : ET_BUNDLE) {
                    et.getText().clear();
                }
                clearAllFocus();
            }
        });

        return view;
    }

    /**
     * Builds an AppUser from the EditText fields
     * @return user the new AppUser
     */
    private AppUser getUserByFields() {
        AppUser user = new AppUser();

        // replace all special characters from the UI formatting
        String number = phoneET.getText().toString();
        String regex = "\\D";
        number = number.replaceAll(regex, "");

        // Set all of the fields
        user.setUserName(unET.getText().toString());
        user.setPassword(pwET.getText().toString());
        user.setEmail(emailET.getText().toString());
        user.setZip(zipET.getText().toString());
        user.setPhoneNumber(number);

        return user;
    }

    /**
     * Detects the focus change from EditText fields and handles validation based on which field
     * loses focus.
     * @param v View the focused/unfocused view
     * @param hasFocus boolean whether or not the view has focus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()){
                // Username Validation Check
                case R.id.username_field_reg:
                    if (unET.getText().toString().length() != 0 &&
                            ((MyActivity) getActivity()).checkIfUserExists(unET.getText().toString())) {

                        String message = "The username you entered already exists. " +
                                "Please enter a new one.";

                        attachDetachInvalidBorder(unET, false, message);

                        VALID = false;
                    }
                    else {
                        VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                        attachDetachInvalidBorder(unET, true, null);
                    }

                    break;
                // Password Validation Check
                case R.id.password_field_reg:
                    if (pw2ET.getText().toString().length() != 0 &&
                            pwET.getText().toString().length() != 0) {
                        VALID = checkPasswordMatch(pwET, pw2ET);
                    }

                    else {
                        VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                        attachDetachInvalidBorder(pwET, true, null);
                        attachDetachInvalidBorder(pw2ET, true, null);
                    }
                    break;
                // Re-enter Password Validation Check
                case R.id.password_field_reg_reenter:
                    if (pwET.getText().toString().length() != 0 &&
                            pw2ET.getText().toString().length() != 0)
                        VALID = checkPasswordMatch(pwET, pw2ET);
                    else {
                        VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                        attachDetachInvalidBorder(pwET, true, null);
                        attachDetachInvalidBorder(pw2ET, true, null);
                    }
                    break;
                // Email Validation Check
                case R.id.email_field:
                    String emailString = emailET.getText().toString();
                    if(emailString.length() != 0 &&
                            !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                        String message = "Not a valid Email. Please re-enter";
                        attachDetachInvalidBorder(emailET, false, message);
                        VALID = false;
                    }
                    else {
                        VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                        attachDetachInvalidBorder(emailET, true, null);
                    }
                    break;
                // ZipCode Validation Check
                case R.id.zip_address_field:
                    int zipLength = zipET.getText().toString().length();
                    if (zipLength != 0 && zipLength < 5) {
                        String message = "Zip code must be exactly 5 digits. Please re-enter.";
                        attachDetachInvalidBorder(zipET, false, message);
                        VALID = false;
                    }
                    else {
                        VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                        attachDetachInvalidBorder(zipET, true, null);
                    }
                    break;
                // Phone number Validation Check
                case R.id.phone_number_field:
                    if (phoneET.getText().toString().length() != 0)
                        validatePhone(phoneET);
                    else {
                        VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                        attachDetachInvalidBorder(phoneET, true, null);
                    }
                    break;
                default:
                    VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);
                    break;
            }
            // If all fields have been filled out, then set the button Validity
            setValidity(VALID);
        }
        else {
            switch (v.getId()){
                case R.id.username_field_reg:
                    helperTV.setText(R.string.helper_text_username);
                    break;
                case R.id.password_field_reg:
                    helperTV.setText(R.string.helper_text_password);
                    break;
                case R.id.password_field_reg_reenter:
                    helperTV.setText(R.string.helper_text_password2);
                    break;
                case R.id.email_field:
                    helperTV.setText(R.string.helper_text_email);
                    break;
                case R.id.zip_address_field:
                    helperTV.setText(R.string.helper_text_zip);
                    break;
                case R.id.phone_number_field:
                    helperTV.setText(R.string.helper_text_phone);
                    break;
                default:
                    helperTV.setText("");
                    break;
            }
        }
    }

    /**
     * Sets the submitButton enabled or disabled
     * @param flag boolean whether or not the button will be enabled
     */
    public void setValidity(boolean flag){
        submitButton.setEnabled(flag);
    }

    /**
     * Phone number validation method
     * @param et IMEListenerEditText the phone number EditText
     */
    public void validatePhone(IMEListenerEditText et) {
        String number = et.getText().toString();

        // This handles the (xxx)xxx-xxxx phone number which returns false
        if(number.length() == 13) {
            String regex = "\\D";
            number = number.replaceAll(regex, "");
        }
        if(DEBUG) log.i(TAG, "Validating phone number: [" + number + "]");

        String message = "Phone number invalid.  Please re-enter";
        VALID = (PhoneNumberUtils.isGlobalPhoneNumber(number) && number.length() >= 7);
        attachDetachInvalidBorder(phoneET, VALID, message);

        if(VALID)
            VALID = AppUtil.enableButtonByTextFields(ET_BUNDLE);

        setValidity(VALID);
    }

    /**
     * Helper method to check if the passwords match
     * @param et1 EditText the password field
     * @param et2 EditText the re-enter password field
     * @return boolean whether or not the Strings match
     */
    private boolean checkPasswordMatch(EditText et1, EditText et2){
        boolean match = false;

        if(et1.getText().toString().equals(et2.getText().toString())) {
            match = true;
            attachDetachInvalidBorder(pwET, true, null);
            attachDetachInvalidBorder(pw2ET, true, null);
        }
        else {
            String message = "The passwords do not match, please re-enter";
            attachDetachInvalidBorder(pwET, false, message);
            attachDetachInvalidBorder(pw2ET, false, null);
        }
        if (match)
            match = AppUtil.enableButtonByTextFields(ET_BUNDLE);

        return match;
    }

    /**
     * Attach or detach the invalid red border depending on certain conditions
     * @param et EditText the field to set the border
     * @param set boolean whether or not to set normal or red border
     * @param message String the message to display in the Toast
     */
    private void attachDetachInvalidBorder(EditText et, boolean set, String message) {
        if(!set) {
            et.setBackgroundResource(R.drawable.invalid_input);

            // Never set focus on the re-enter password field
            if(et.getId() != R.id.password_field_reg_reenter) {
                Toast.makeText(getActivity().getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG).show();
                setFocusOnET(et);
            }
            else
                et.getText().clear();
        }
        else
            et.setBackgroundResource(R.drawable.bottom_border_selector);
    }

    /**
     * Clears and sets the focus on desired EditText field. Need to request focus in new thread or
     * else the focus will not stay on the desired field
     * @param et EditText the desired field
     */
    private void setFocusOnET(EditText et){
        final EditText currentET = et;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentET.getText().clear();
                currentET.setFocusableInTouchMode(true);
                currentET.setFocusable(true);
                currentET.requestFocus();
            }
        }, 200);
    }

    /**
     * Clears focus on all EditText fields and restore the border state
     */
    public void clearAllFocus(){
        if (ET_BUNDLE != null && helperTV != null) {
            for (EditText et : ET_BUNDLE) {
                et.clearFocus();
                attachDetachInvalidBorder(et, true, null);
            }
        }
    }
}
