package com.dev.melosz.melodroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.HomeScreenActivity;
import com.dev.melosz.melodroid.activities.MyActivity;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;


/**
 * Created by marek.kozina on 9/1/2015.
 * Pager-fragment Login Tab with UserName & Password input fields and a forgot password link.

 */
public class LoginScreen extends Fragment {
    // Logging controls
    private LogUtil log = new LogUtil();
    private final static String TAG = LoginScreen.class.getSimpleName();
    private final static boolean DEBUG = false;

    // Application context
    private Context CTX;

    // Preferences for this app
    private SharedPreferences prefs;

    // AppUtil helper class
    private AppUtil appUtil = new AppUtil();

    // View declarations
    private EditText unET;
    private EditText pwET;
    private Button submitButton;
    private TextView forgotPwTV;

    /**
     * Default constructor
     */
    public LoginScreen() {}

    /**
     * Default overridden onCreate method
     * @param savedInstanceState args of the previous state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CTX = getActivity().getApplicationContext();
        prefs = CTX.getSharedPreferences(getString(R.string.preference_file_key),
                                         Context.MODE_PRIVATE);
    }

    /**
     * Default overridden onCreateView method
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_screen, container, false);

        // Initialize and clear focus from the EditText fields
        unET = (EditText) view.findViewById(R.id.username_field);
        pwET = (EditText) view.findViewById(R.id.password_field);
        unET.clearFocus();
        pwET.clearFocus();

        // Initialize Submit button
        submitButton = (Button) view.findViewById(R.id.signin_button);

        // Initialize TextView Forgot Password link
        forgotPwTV = (TextView) view.findViewById(R.id.forgot_password);
        forgotPwTV.setPaintFlags(forgotPwTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // finals to pass in inner class TextWatcher below
        final EditText[] ets = new EditText[]{unET, pwET};
        final int id = R.id.signin_button;
        final View tempView = view;

        // Initial disable of submitButton. If we got to this fragment, the user is not logged in
        appUtil.enableButtonByTextFields(ets, id, tempView);

        // Assign TextWatcher to the Edit Text fields
        for (EditText et : ets) {
            final EditText currentET = et;
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    appUtil.enableButtonByTextFields(ets, id, tempView);

                    // Do NOT allow spaces
                    String result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        currentET.setText(result);
                        currentET.setSelection(result.length());
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        // Set the button click Listeners
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                String un = unET.getText().toString();
                String pw = pwET.getText().toString();
                boolean success = false;
                try {
                    success = ((MyActivity) getActivity()).checkCredentials(new String[]{un, pw});
                } catch (SQLiteException e) {
                    Toast.makeText(CTX, "SQLiteException Run Into: " + e.getMessage(),
                                   duration).show();
                    if(DEBUG) log.e(TAG, e.getMessage());
                }
                if (success) {
                    // Add user to preferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.preference_stored_user), un);
                    editor.apply();

                    Toast.makeText(CTX, "Welcome back, " + un + "!", duration).show();
                    ((MyActivity) getActivity()).openHomeScreenActivity(HomeScreenActivity.class);
                }
                else {
                    Toast toast = Toast.makeText(CTX, getString(R.string.logon_no_match), duration);
                    toast.show();
                    appUtil.hideKeyboard(getActivity());
                    pwET.clearFocus();
                    pwET.getText().clear();
                }
            }
        });

        return view;
    }
}
