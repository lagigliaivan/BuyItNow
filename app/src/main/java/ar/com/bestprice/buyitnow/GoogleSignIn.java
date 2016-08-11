package ar.com.bestprice.buyitnow;

import android.content.*;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by ivan on 07/08/16.
 */
public class GoogleSignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_MAIN_ACTIVITY = 2;

    private GoogleApiClient mGoogleApiClient = null;
    private static final String googleSignInClientId = "771875379-qbdvrqrjdii0gims9upnuqcqrf6753ei.apps.googleusercontent.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(googleSignInClientId)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        setContentView(R.layout.oauth_sign_in);

        com.google.android.gms.common.SignInButton button = (com.google.android.gms.common.SignInButton) findViewById(R.id.google_sign_in_button);
        button.setSize(SignInButton.SIZE_STANDARD);
        button.setScopes(gso.getScopeArray());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUserAccount();
            }
        });
    }

    private void pickUserAccount() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
            case RC_MAIN_ACTIVITY:
                Toast toast = Toast.makeText(this, "Error while authenticating. Please, check user and password", Toast.LENGTH_LONG);
                toast.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ERROR", "On connection error");
    }


    public void handleException(UserRecoverableAuthException exception){
        startActivityForResult(exception.getIntent(), 1);
    }
    private void handleSignInResult(GoogleSignInResult result) {

        Log.d("HANDLING RESULT", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            //Context.getContext().setUserSignInToken(acct.getIdToken());

            Context.getContext().setUserSignInToken(Context.getContext().getSha1());
            //Context.getContext().setUserEmail(acct.getEmail());

            Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, RC_MAIN_ACTIVITY);

        } else {

            Toast toast = Toast.makeText(this, "Error while authenticating. Please, check user and password", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
