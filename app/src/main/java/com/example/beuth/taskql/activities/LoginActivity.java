package com.example.beuth.taskql.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.beuth.taskql.helperClasses.Connection;
import com.example.beuth.taskql.helperClasses.ApplicationParameters;
import com.example.beuth.taskql.helperClasses.Utility;
import com.example.beuth.tasql.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


/**
 * @author Wael Gabsi, Stefan Völkel
 */
public class LoginActivity extends Activity {
    public static final String PREFS_NAME = "LoginPrefs";
    private Connection connection;
    private ApplicationParameters applicationParameters;
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private Button mEmailSignInButton;
    private EditText mPasswordView;
    private EditText mServerView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationParameters = ApplicationParameters.getInstance();
        connection = new Connection(getApplicationContext());
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mServerView = (EditText) findViewById(R.id.serverUrl);

        // set predefined server url
        mServerView.setText("beta.taskql.com");

        // get the shared preferences to know if the user open the app for the first time
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        // check network status
        if (connection.isNetworkAvailable()) {
            if (settings.getString("logged", "").toString().equals("logged")) {
                String username = settings.getString("username", "").toString();
                String password =  settings.getString("password", "").toString();
                String serverUrl = settings.getString("serverUrl", "").toString();

                mAuthTask = new UserLoginTask(username, password, serverUrl);
                mAuthTask.execute((Void) null);
                applicationParameters.setSessionId(settings.getString("SSID", ""));

                Intent intent = new Intent(LoginActivity.this, ProjectActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Utility.displayToast(getApplicationContext(), getString(R.string.no_connection));
        }


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (connection.isNetworkAvailable()) {
            mEmailSignInButton.setEnabled(true);
        } else {
            mEmailSignInButton.setEnabled(false);
            Log.e("Toast", "Toast");
        }
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (connection.isNetworkAvailable()) {
            mEmailSignInButton.setEnabled(true);
        } else {
            mEmailSignInButton.setEnabled(false);
            Utility.displayToast(getApplicationContext(), getString(R.string.no_connection));
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mServerView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String serverUrl = mServerView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(serverUrl)) {
            mServerView.setError(getString(R.string.error_field_required));
            focusView = mServerView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, serverUrl);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return Utility.validate(email);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mServerUrl;

        UserLoginTask(String email, String password, String serverUrl) {
            mEmail = email;
            mPassword = password;
            mServerUrl = serverUrl;
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverResponse = null;
            JSONObject json = new JSONObject();
            try {
                json.put("username", mEmail);
                json.put("password", mPassword);
                serverResponse = connection.doPostRequestWithAdditionalData("https://" + mServerUrl + "/rest/api/1/taskql/login", json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
            //	return text;
        }

        @Override
        protected void onPostExecute(final String results) {
            if (results!=null) {
                JSONObject arr = null;
                String errorCode = null;
                String sessionId = null;
                try {
                    arr = new JSONObject(results);
                    errorCode = arr.getString("errorCode");
                    sessionId = arr.getString("nanomeSessionId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(errorCode.equals("0"))
                {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");
                    applicationParameters.setSessionId(sessionId);
                    editor.putString("SSID", applicationParameters.getSessionId());
                    editor.putString("username", mEmail);
                    editor.putString("password", mPassword);
                    editor.putString("serverUrl", mServerView.getText().toString());
                    editor.commit();
                    applicationParameters.setServerUrl(mServerView.getText().toString());
                    navigateToHomeActivity();
                }
                else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigateToHomeActivity(){
        String username = mEmailView.getText().toString().trim();
        Intent homeIntent = new Intent(getApplicationContext(),ProjectActivity.class);
        homeIntent.putExtra("username", username);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }
}

