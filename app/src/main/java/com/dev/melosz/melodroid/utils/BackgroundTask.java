package com.dev.melosz.melodroid.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.ContactManagementActivity;
import com.dev.melosz.melodroid.database.ContactDAO;

/**
 * Created by marek.kozina on 10/21/2015.
 * Background task to perform long database transactions. The paramaters are as follows:
 * String[0] pass String method name
 * String[1] pass String parameter
 * String[2] pass secondary String parameter
 */
public class BackgroundTask extends AsyncTask<String, String, String> {
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = BackgroundTask.class.getSimpleName();
    private String METHOD;
    // Set to false to suppress logging
    private static final boolean DEBUG = false;

    private ProgressDialog mProgressDialog;

    private Context mCTX;

    private ContactDAO cDAO;

    private String mProgressTitle;
    private String mProgressMessage;

    private boolean callback;

    public BackgroundTask(Context context, String title, String message){
        this.mCTX = context;
        this.mProgressTitle = title;
        this.mProgressMessage = message;
        cDAO = new ContactDAO(mCTX);
        callback = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mCTX, R.style.ProgressDialogTheme);
        mProgressDialog.setMessage(mProgressMessage);
        mProgressDialog.setTitle(mProgressTitle);
        mProgressDialog.show();
        Window window = mProgressDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }
    @Override
    protected String doInBackground(String... params) {
        // Switched based on the first String parameter which should be a Method Name
        switch (params[0]) {
            case "syncFromManager":
                if (DEBUG) log.i(TAG, "Outputting Contact Info from " + cDAO.database.toString());
                callback = true;
            case "syncFromHome":
                cDAO.open();
                cDAO.syncContactsWithPhone(Integer.parseInt(params[1]));
                break;
            default:
                break;
        }
        return null;
    }
    @Override
    protected void onPostExecute(String dao) {
        mProgressDialog.dismiss();
        Toast.makeText(mCTX, "Contacts Synched!", Toast.LENGTH_LONG).show();
        if(callback){
            callback = false;
            ContactManagementActivity cmAct = (ContactManagementActivity) mCTX;
            cmAct.populateContacts();
        }
    }
}
