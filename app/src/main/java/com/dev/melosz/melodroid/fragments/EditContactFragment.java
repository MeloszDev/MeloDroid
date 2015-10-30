package com.dev.melosz.melodroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.ContactManagementActivity;
import com.dev.melosz.melodroid.classes.Contact;
import com.dev.melosz.melodroid.database.ContactDAO;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

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
        System.out.println("ONCREATEVIEWCALLED");
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_contact, container, false);

        // Button to roll up and close the fragment
        ImageButton closeFragImg = (ImageButton) mView.findViewById(R.id.close_edit_frag);
        closeFragImg.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ContactManagementActivity) getActivity()).hideShowFragment();
            }
        });

        // if the current selected user is the one logged in, hide the password layout
        if (mContact != null) {
            hideShow(R.id.access_password_field, false);
            hideShow(R.id.access_edit_fields, true);
        }
        else
            System.out.println("mContact Is NULL");
        if (mContact.getEmail() != null){
            String[] emails = AppUtil.splitDelimitedString(mContact.getEmail());
        }
        return mView;
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
     * TODO: Validation and helper text
     * @param v View the view that the focusChange occurs on
     * @param hasFocus boolean whether or not the view has focus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case 0:
                    break;
                default :
                    break;
            }
        }
    }


}
