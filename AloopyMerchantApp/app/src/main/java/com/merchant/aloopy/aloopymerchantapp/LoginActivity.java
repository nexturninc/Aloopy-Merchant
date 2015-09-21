package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantInfoContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Boolean loginSuccess = false;
            String userId = "";
            String userDisplay = "";
            JSONObject jsonResponse = null;

            try {

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("password", mPassword);
                jsonParam.put("username", mEmail);


                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.PostAPI(jsonParam, "/aloopy/merchantlogin");


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");

                    if (strSuccess == "true") {

                        JSONArray userInfo = jsonResponse.getJSONArray("merchantUserInfo");
                        JSONArray merchantInfo = jsonResponse.getJSONArray("merchantInfo");

                        if(userInfo != null && userInfo.length() > 0
                                && merchantInfo != null && merchantInfo.length() > 0) {

                            userId = userInfo.getJSONObject(0).getString("id");
                            userDisplay = userInfo.getJSONObject(0).getString("name")  +
                                    " (" +
                                    merchantInfo.getJSONObject(0).getString("merchantName") +
                                    ")"
                            ;

                            //SAVE TO SHARED PREFERENCES
                            SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString(getString(R.string.SHARE_PREF_UserId), userId);
                            editor.putString(getString(R.string.SHARE_PREF_UserName), userDisplay);
                            editor.commit();


                            // Gets the data repository in write mode
                            AloopySQLHelper helper = AloopySQLHelper.getInstance(getBaseContext());
                            SQLiteDatabase db = helper.getWritableDatabase();

                            // Create a new map of values, where column names are the keys
                            ContentValues values = new ContentValues();

                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_ID, merchantInfo.getJSONObject(0).getString("id"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_Name, merchantInfo.getJSONObject(0).getString("merchantName"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Parent_Merchant_ID, merchantInfo.getJSONObject(0).getString("id"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Beacon_UUID, merchantInfo.getJSONObject(0).getString("beaconUUID"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_Type, merchantInfo.getJSONObject(0).getString("merchantType"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_Logo_V, merchantInfo.getJSONObject(0).getString("verticalLogoFilepath"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_Logo_H, merchantInfo.getJSONObject(0).getString("horizontalLogoFilepath"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Address, merchantInfo.getJSONObject(0).getString("address"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Country, merchantInfo.getJSONObject(0).getString("country"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Country_Code, merchantInfo.getJSONObject(0).getString("countryCode"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Telephone_No, merchantInfo.getJSONObject(0).getString("telephoneNo"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Mobile_No, merchantInfo.getJSONObject(0).getString("mobileNo"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Email_Address, merchantInfo.getJSONObject(0).getString("contactPersonEmail"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Contact_Person, merchantInfo.getJSONObject(0).getString("contactPerson"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Contact_Person_Email, merchantInfo.getJSONObject(0).getString("contactPersonEmail"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Contact_Person_Mobile, merchantInfo.getJSONObject(0).getString("contactPersonMobile"));
                            values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Date_Modified, merchantInfo.getJSONObject(0).getString("dateModified"));


                            //values.put(MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_ID, merchantInfo.getJSONObject(0).getString("id"));


                            // Insert the new row, returning the primary key value of the new row
                            long newRowId;
                            db.delete(MerchantInfoContract.MerchantInformation.TABLE_NAME, null, null);
                            newRowId = db.insert(
                                    MerchantInfoContract.MerchantInformation.TABLE_NAME,
                                    MerchantInfoContract.MerchantInformation.COLUMN_NAME_Merchant_ID,
                                    values);

                            db.close();

                        }


                        loginSuccess = true;
                    }
                }

            }
            catch (Exception ex) {
                String message = ex.getMessage();
            }

            if(loginSuccess) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }


            // TODO: register the new account here.
            return loginSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            }
            else if (!Common.GetInternetConnectivity((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))){
                mPasswordView.setError("Internet connection is required to log in.");
                mPasswordView.requestFocus();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

