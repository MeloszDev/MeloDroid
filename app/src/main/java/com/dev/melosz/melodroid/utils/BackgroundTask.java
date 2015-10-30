package com.dev.melosz.melodroid.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.activities.ContactManagementActivity;
import com.dev.melosz.melodroid.classes.Contact;
import com.dev.melosz.melodroid.database.ContactDAO;

import java.util.List;

/**
 * Created by marek.kozina on 10/21/2015.
 * Background task to perform long database transactions. The paramaters are as follows:
 * String[0] pass String where the BackgroundTask is coming from
 * String[1] pass String parameter
 * String[2] pass secondary String parameter
 */
public class BackgroundTask extends AsyncTask<String, String, String> {
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = BackgroundTask.class.getSimpleName();
    private String METHOD;
    // Set to false to suppress logging
    private static final boolean DEBUG = true;

    // The Progress Dialog for this background task
    private ProgressDialog mProgressDialog;

    // The Activity's Application Context
    private Context mCTX;

    // The ContactDAO ran in the background
    private ContactDAO cDAO;

    // ProgressDialog title and message Strings
    private String mProgressTitle;
    private String mProgressMessage;

    // The callback method to call onPostExecute
    private String callback;

    private List<Contact> contacts;

    /**
     * Constructor for running this AsyncTask BackgroundTask
     * @param context Context the Application Context
     * @param title String the title of the progress dialog message box
     * @param message String the meesage of the progress dialog message box
     */
    public BackgroundTask(Context context, String title, String message){
        this.mCTX = context;
        this.mProgressTitle = title;
        this.mProgressMessage = message;
        cDAO = new ContactDAO(mCTX);
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
        METHOD = "doInBackground()";
        callback = params[0];
        if(DEBUG) {
            int i = 0;
            for(String param : params){
                log.i(TAG, METHOD, "Param[" + i + "]: [" + param + "]");
                i++;
            }
        }
        cDAO.open();
        // Switched based on the first String parameter which should be a Method Name
        switch (callback) {
            case "syncFromManager":
            case "syncFromHome":
                if (DEBUG) log.i(TAG,
                        METHOD,
                        "Syncing contacts from case: [" + callback + "]");
                cDAO.syncContactsWithPhone(Integer.parseInt(params[1]));
                break;
            case "gatherContacts":
                contacts = cDAO.getContactsByUserID(Integer.parseInt(params[1]));
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String arg) {
        METHOD = "onPostExecute()";
        if (DEBUG) log.i(TAG,
                METHOD,
                "Dismissing Dialog with callback method: [" + callback + "]");

        // Dismiss and execute callback if applicable
        mProgressDialog.dismiss();

        // Instantiate activity for callback method calls
        ContactManagementActivity cmAct = (ContactManagementActivity) mCTX;

        // Determine what, if any, callback methods will be executed
        switch (callback) {
            case "syncFromManager":
                cmAct.gatherContacts();
            case "syncFromHome":
                Toast.makeText(mCTX, "Contacts Synced!", Toast.LENGTH_LONG).show();
                break;
            case "gatherContacts":
                cmAct.populateContacts(contacts);
                break;
            default:
                Toast.makeText(mCTX, "An error occurred.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
