package com.dev.melosz.melodroid.fragments;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.ContactManagementActivity;
import com.dev.melosz.melodroid.classes.Contact;
import com.dev.melosz.melodroid.database.ContactDAO;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marek Kozina 10/2/2015
 * A fragment class to edit contacts from the ContactManagementActivity
 */
public class EditContactFragment extends Fragment implements View.OnFocusChangeListener{
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = EditContactFragment.class.getSimpleName();
    private static final boolean DEBUG = true;

    // The initialization parameter user
    private static final String CONTACT = "contact";

    // The Contact used in this fragment
    private Contact mContact;

    // The fragments inflated view onCreateView
    private View mView;

    private ContactDAO cDAO;

    private Context mCTX;

    private Handler handler = new Handler();

    // EditText fields
    private EditText mPhoneET;
    private EditText mFirstNameET;
    private EditText mMiddleNameET;
    private EditText mLastNameET;
    private EditText mEmailET;
    private EditText mAddressET;
    private EditText mCityET;
    private EditText mStateET;
    private EditText mZipET;

    // Generic constructor
    public EditContactFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contactParam The Contact FirstName
     * @return A new instance of fragment EditContactFragment.
     */
    public static EditContactFragment newInstance(String contactParam) {
        EditContactFragment fragment = new EditContactFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT, contactParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCTX = getActivity().getApplicationContext();
        cDAO = new ContactDAO(mCTX);
        cDAO.open();

//        setRetainInstance(true);
        if (getArguments() != null) {
            String mEditContact = getArguments().getString(CONTACT);
            if (mEditContact != null && mEditContact.length() != 0) {
                mContact = ((ContactManagementActivity) getActivity()).getContact(mEditContact);
                if(DEBUG) log.i(TAG, "OnCreate EditContactFragment successful. Username: ["
                                  + mEditContact + "]");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_contact, container, false);

        // Button to roll up and close the fragment
        ImageButton closeFragImg = (ImageButton) mView.findViewById(R.id.close_edit_frag);
        closeFragImg.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContact != null)
                    updateContact();
                ((ContactManagementActivity) getActivity()).hideShowFragment();
            }
        });

        // if the current selected user is the one logged in, hide the password layout
        if (mContact != null) {
            hideShow(R.id.access_password_field, false);
            hideShow(R.id.access_edit_fields, true);
        }
        else
            if(DEBUG) log.i(TAG, "No Contact present.");

        if(mContact.getEmail() != null) {
            String[] emails = AppUtil.splitDelimitedString(mContact.getEmail());
        }
        if(mContact.getPhoneNumber() != null) {
            String[] phones = AppUtil.splitDelimitedString(mContact.getPhoneNumber());
        }

        mPhoneET = (EditText) mView.findViewById(R.id.contact_phone);
        mFirstNameET  = (EditText) mView.findViewById(R.id.contact_first_name);
        mMiddleNameET = (EditText) mView.findViewById(R.id.contact_middle_name);
        mLastNameET = (EditText) mView.findViewById(R.id.contact_last_name);
        mEmailET  = (EditText) mView.findViewById(R.id.contact_email);
        mAddressET  = (EditText) mView.findViewById(R.id.contact_street_address);
        mCityET = (EditText) mView.findViewById(R.id.contact_city);
        mStateET  = (EditText) mView.findViewById(R.id.contact_state);
        mZipET  = (EditText) mView.findViewById(R.id.contact_zip);

        mPhoneET.setOnFocusChangeListener(this);
        mFirstNameET.setOnFocusChangeListener(this);
        mMiddleNameET.setOnFocusChangeListener(this);
        mLastNameET.setOnFocusChangeListener(this);
        mEmailET.setOnFocusChangeListener(this);
        mAddressET.setOnFocusChangeListener(this);
        mCityET.setOnFocusChangeListener(this);
        mStateET.setOnFocusChangeListener(this);
        mZipET.setOnFocusChangeListener(this);

        if(mContact != null)
            setETs();

        return mView;
    }

    public void setETs(){
        // First two fields are required and therefore will not be null
        mPhoneET.setText(mContact.getPhoneNumber());
        mFirstNameET.setText(mContact.getFirstName());

        // Optional fields
        if(mContact.getMiddleName() != null)
            mMiddleNameET.setText(mContact.getMiddleName());
        if(mContact.getLastName() != null)
            mLastNameET.setText(mContact.getLastName());
        if(mContact.getEmail() != null)
            mEmailET.setText(mContact.getEmail());
        if(mContact.getAddress() != null)
            mAddressET.setText(mContact.getAddress());
        if(mContact.getCity() != null)
            mCityET.setText(mContact.getCity());
        if(mContact.getState() != null)
            mStateET.setText(mContact.getState());
        if(mContact.getZip() != null)
            mZipET.setText(mContact.getZip());
    }
    /**
     * Hides or shows the appropriate layout
     * @param id int the id of the layout
     * @param show boolean whether or not to show or hide the view
     */
    private void hideShow(int id, boolean show) {
        if (show)
            mView.findViewById(id).setVisibility(View.VISIBLE);
        else
            mView.findViewById(id).setVisibility(View.GONE);
    }


    /**
     * Method to update contact called from multiple methods
     */
    private void updateContact(){
        String message = "";
        try {
            cDAO.updateContact(mContact);
            message = "Updated Contact: [" + mContact.getFirstName() + "].";
        }
        catch (SQLiteException e){
            message = "Unable to update Contact: " + e.getLocalizedMessage();
            if(DEBUG) log.e(TAG, message);
        }
        finally {
            if(DEBUG) log.i(TAG, message);
            Toast.makeText(mCTX, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * TODO: Validation and helper text
     * @param v View the view that the focusChange occurs on
     * @param hasFocus boolean whether or not the view has focus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.contact_email:
                    List<String> emailList = new ArrayList<>();
                    if(mContact.getEmail() != null) {
                        String[] emails = AppUtil.splitDelimitedString(mContact.getEmail());
                        for(String email : emails){
                            emailList.add(email);
                        }
                    }

                    if (emailList != null && emailList.size() > 0){
                        System.out.println("In Email ET: [" + emailList.size() + "] emails total.");
                    }
                    break;
                default :
                    break;
            }
        }
    }

}