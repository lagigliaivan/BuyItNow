package ar.com.bestprice.buyitnow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

/**
 * Created by ivan on 07/08/16.
 */
public class SingleSignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_MAIN_ACTIVITY = 2;

    private GoogleApiClient mGoogleApiClient = null;
    private static final String googleSignInClientId = "771875379-qbdvrqrjdii0gims9upnuqcqrf6753ei.apps.googleusercontent.com";

    private CallbackManager callbackManager;
    private boolean googleUserLoggedIn = true;
    // private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.single_sign_in);
        configureFacebookSingIn();
        configureGoogleSingIn();
    }

    @Override
    public void onStart() {
        super.onStart();
        silentSingIn();
    }

    private void silentSingIn() {

        if (isFacebookUserLoggedIn()) {
            startMainActivity(AccessToken.getCurrentAccessToken().getToken(), Context.SignInType.FACEBOOK);
        }

        if (isGoogleUserLoggedIn()) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            }
        }
    }

    public boolean isFacebookUserLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void configureFacebookSingIn() {

        LoginButton facebookButton = (LoginButton) findViewById(R.id.facebook_singin_button);
        facebookButton.setReadPermissions("email");


        callbackManager = CallbackManager.Factory.create();

        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                startMainActivity(loginResult.getAccessToken().getToken(), Context.SignInType.FACEBOOK);
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
            Toast.makeText(getApplicationContext(), "Please, verify user/pass", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startMainActivity(String accessToken, Context.SignInType signInType) {

        Context.getContext().setUserSignInToken(accessToken);
        Context.getContext().setUserSignInType(signInType);

        System.out.println("token: " + accessToken + "type: " + signInType);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, RC_MAIN_ACTIVITY);
    }

    private void configureGoogleSingIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(googleSignInClientId)
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        SignInButton googleButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        googleButton.setScopes(gso.getScopeArray());
        googleButton.setOnClickListener(new View.OnClickListener() {
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
                googleUserLoggedIn = true;
                break;
            case RC_MAIN_ACTIVITY:

                if (resultCode == 1001) {
                    if(isFacebookUserLoggedIn()) {
                        LoginManager.getInstance().logOut();
                    } else {
                        googleUserLoggedIn = false;
                    }
                }

                break;

            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ERROR", "On connection error");
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d("HANDLING RESULT", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            googleUserLoggedIn = true;
            startMainActivity(acct.getIdToken(), Context.SignInType.GOOGLE);

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

    public boolean isGoogleUserLoggedIn() {
        return googleUserLoggedIn;
    }
}
