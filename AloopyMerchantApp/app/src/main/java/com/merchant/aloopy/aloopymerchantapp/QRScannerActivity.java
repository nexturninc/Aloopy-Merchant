package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantInfoContract;
import com.merchant.aloopy.aloopydatabase.MerchantStampInfoContract;
import com.merchant.aloopy.aloopydatabase.UserInfoContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by imbisibol on 9/23/2015.
 */
public class QRScannerActivity extends ActionBarActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private IntentIntegrator integrator = null;
    private String UserId = null;
    private CustomerStampTask mTask = null;
    private String ScanType = null;

    private TextView lblCustomerStampId = null;
    private Button btnUpdateCustomerStamp = null;
    private View mProgressView;
    private View mControlBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        //SHARED PREFS
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        UserId = mSettings.getString(getString(R.string.SHARE_PREF_UserId), "00000000-0000-0000-0000-000000000000");

        //CONTROL INITIALIZATION
        mProgressView = findViewById(R.id.login_progress);
        mControlBody = findViewById(R.id.dvStampListBody);
        lblCustomerStampId = (TextView)findViewById(R.id.lblCustomerStampId);
        TextView lblScannerLabel = (TextView)findViewById(R.id.lblScannerLabel);
        TextView lblStampScannerInstructions = (TextView)findViewById(R.id.lblStampScannerInstructions);
        btnUpdateCustomerStamp = (Button)findViewById(R.id.btnUpdateCustomerStamp);
        btnUpdateCustomerStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StampCustomerStamp(lblCustomerStampId.getText().toString());
            }
        });
        Button btnBackToList = (Button)findViewById(R.id.btnBackToList);
        btnBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //INTENT
        Intent intent = getIntent();
        ScanType = intent.getStringExtra(getString(R.string.EXTRA_QR_Scanner_Mode));
        lblScannerLabel.setText("Scanned " + ScanType + " ID:");
        if(ScanType.equals("Stamp")) {
            btnUpdateCustomerStamp.setVisibility(View.VISIBLE);
            lblStampScannerInstructions.setVisibility(View.VISIBLE);
        }

        //INITIALIZE SCANNER APP
        integrator = new IntentIntegrator(QRScannerActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        //customize the prompt message before scanning
        integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_profile){
            //Intent intent = new Intent(getBaseContext(), CustomerProfile.class);
            //startActivity(intent);
        }
        else if (id == R.id.action_logout){
            AloopySQLHelper helper = AloopySQLHelper.getInstance(getBaseContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            db.delete(MerchantInfoContract.MerchantInformation.TABLE_NAME, null, null);
            db.delete(UserInfoContract.UserInformation.TABLE_NAME, null, null);

            SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = mSettings.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null && contents.length() > 0) {
                UUID cStampId = null;

                try {
                    cStampId = UUID.fromString(contents);
                    lblCustomerStampId.setText(contents);
                }
                catch(Exception ex){
                    lblCustomerStampId.setText("Invalid QR Code scanned!");
                }
            } else {
                lblCustomerStampId.setText("---");
            }
        }
    }


    //TASKS
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mControlBody.setVisibility(show ? View.GONE : View.VISIBLE);
            mControlBody.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mControlBody.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mControlBody.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class CustomerStampTask extends AsyncTask<Void, Void, Boolean> {

        private final String mCustomerStampId;
        private final String mUserId;

        CustomerStampTask(String customerStampId, String userId) {
            mCustomerStampId = customerStampId;
            mUserId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Boolean loginSuccess = false;
            String apiMessage = "";

            JSONObject jsonResponse = null;
            JSONObject jsonParam = new JSONObject();

            try {

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.PostAPI(jsonParam, "/aloopy/customerstampset/?customerStampSetId=" + mCustomerStampId + "&mercUserId=" + mUserId);


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");
                    apiMessage = jsonResponse.getString("responseMessage");
                    Toast.makeText(getBaseContext(), apiMessage, Toast.LENGTH_SHORT).show();

                    if (strSuccess == "true") {
                        loginSuccess = true;
                    }
                }

            }
            catch (Exception ex) {
                String message = ex.getMessage();
            }

            // TODO: register the new account here.
            return loginSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTask = null;
            showProgress(false);

            finish();
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }



    //METHODS
    public void callCustomerStampTask(String customerStampTask) {
        if (mTask != null) {
            return;
        }


        boolean cancel = false;
        View focusView = null;

        showProgress(true);
        mTask = new CustomerStampTask(customerStampTask, UserId);
        mTask.execute((Void) null);

    }
    private String StampCustomerStamp(String customerStampId)
    {
        String message = "";
        UUID cStampId = null;

        if(Common.GetInternetConnectivity((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {

            try {
                //CHECK GUID FORMAT
                cStampId = UUID.fromString(customerStampId);

                //ACCESS API
                callCustomerStampTask(customerStampId);

            }
            catch (IllegalArgumentException argEx) {
                message = getString(R.string.message_InvalidCustomerStampID);
            }
            catch (Exception ex) {
                message = getString(R.string.message_unexpected_error);
            }
        }
        else
        {
            message = getString(R.string.message_Internet_Required);
        }

        return message;
    }
}
