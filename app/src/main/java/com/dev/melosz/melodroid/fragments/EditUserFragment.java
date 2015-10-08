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

/**
 * Created by Marek Kozina 10/2/2015
 * A fragment class to edit or clone users from the UserManagementActivity
 */
public class EditUserFragment extends Fragment implements View.OnFocusChangeListener{
    private static final String CLASS_NAME = EditUserFragment.class.getSimpleName();

    // The initialization parameter user
    private static final String USER = "user";

    // The UserName to edit/clone
    private String mEditUserName;

    // The AppUser used in this fragment
    private AppUser mUser;

    //
    private ImageButton closeFragImg;

    public EditUserFragment () {

    }
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
            mEditUserName = getArguments().getString(USER);
            if (mEditUserName != null && mEditUserName.length() != 0) {
                mUser = ((UserManagementActivity) getActivity()).getAppUser(mEditUserName);
                Log.i(CLASS_NAME, "OnCreate EditUserFragment successful. Username: ["
                                  + mEditUserName + "]");
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);
        closeFragImg = (ImageButton) rootView.findViewById(R.id.close_edit_frag);
        closeFragImg.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick (View v){
                ((UserManagementActivity) getActivity()).hideShowFragment();
            }
        });

        rootView.findViewById(R.id.access_edit_fields).setVisibility(View.GONE);

        return rootView;
    }
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
