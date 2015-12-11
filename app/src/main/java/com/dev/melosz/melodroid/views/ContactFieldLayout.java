package com.dev.melosz.melodroid.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Created by marek.kozina on 11/10/2015.
 */
public class ContactFieldLayout extends LinearLayout {

    public EditText editText;
    public ImageView addButton;
    public ImageView saveButton;

    public ContactFieldLayout (Context context, AttributeSet attrs){
        super(context, attrs);
    }
}
