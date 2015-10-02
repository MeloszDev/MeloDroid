package com.dev.melosz.melodroid.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by marek.kozina on 9/22/2015.
 * EditText sublcass which has the capability of listening for back button while focused on an
 * EditText and the softinput keyboard is open.
 *     Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header
 *
 */
public class IMEListenerEditText extends EditText {
    private KeyImeChange keyImeChangeListener;

    public IMEListenerEditText(Context context) {
        super(context);
    }
    public IMEListenerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event){
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}