package com.dev.melosz.melodroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.UserManagementActivity;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.utils.LogUtil;

/**
 * Created by Marek Kozina 10/2/2015
 * A fragment class to edit or clone users from the UserManagementActivity
 */
public class EditUserFragment extends Fragment implements View.OnFocusChangeListener{
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = EditUserFragment.class.getSimpleName();
    private static final boolean DEBUG = false;

    // The initialization parameter user
    private static final String USER = "user";

    // The AppUser used in this fragment
    private AppUser mUser;

    // The fragments inflated view onCreateView
    private View mView;

    // Generic constructor
    public EditUserFragment () {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userParam Parameter 1.
     * @return A new instance of fragment EditUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditUserFragment newInstance(String userParam) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putString(USER, userParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mEditUserName = getArguments().getString(USER);
            if (mEditUserName != null && mEditUserName.length() != 0) {
                mUser = ((UserManagementActivity) getActivity()).getAppUser(mEditUserName);
                if(DEBUG) log.i(TAG, "OnCreate EditUserFragment successful. Username: ["
                                  + mEditUserName + "]");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_user, container, false);

        // Button to roll up and close the fragment
        ImageButton closeFragImg = (ImageButton) mView.findViewById(R.id.close_edit_frag);
        closeFragImg.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UserManagementActivity) getActivity()).hideShowFragment();
            }
        });

        // if the current selected user is the one logged in, hide the password layout
        if (mUser.isLogged())
            hideShow(R.id.access_password_field, false);
        else
            hideShow(R.id.access_edit_fields, false);

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
