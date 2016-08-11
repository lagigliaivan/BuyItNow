package ar.com.bestprice.buyitnow;

import android.accounts.Account;
import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

public class GetUserNameTask extends AsyncTask {
        Activity mActivity;
        String mScope;
        String mEmail;


    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            Account userAccount = new Account(mEmail, "com.google");
            String token = GoogleAuthUtil.getToken(mActivity, userAccount, mScope);
            return token;
        } catch (UserRecoverableAuthException userRecoverableException) {
            userRecoverableException.printStackTrace();

            ((GoogleSignIn)mActivity).handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            fatalException.printStackTrace();
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
        }
        return null;
    }

    public GetUserNameTask(Activity activity, String name, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            String token = fetchToken();
            if (token != null) {
                // **Insert the good stuff here.**
                // Use the token to access the user's Google data.

            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
        }
        return null;
    }
}