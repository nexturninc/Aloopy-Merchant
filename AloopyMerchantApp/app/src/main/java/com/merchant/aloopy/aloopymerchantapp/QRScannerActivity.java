package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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
import com.merchant.aloopy.aloopydatabase.UserInfoContract;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by imbisibol on 9/23/2015.
 */
public class QRScannerActivity extends ActionBarActivity {

    private IntentIntegrator integrator = null;
    private String UserId = null;
    private CustomerStampTask mTask = null;
    private CustomerStampSetTask mStampSetTask = null;
    private String ScanType = null;
    private String StoreStampId = null;

    private TextView lblStatus = null;
    private TextView lblScannedId = null;
    private Button btnUpdateCustomerStamp = null;
    private TextView lblGiveCustomerStampInstructions = null;
    private Button btnGiveCustomerStamp = null;
    private View mProgressView;
    private View mControlBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        //SHARED PREFS
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        UserId = mSettings.getString(getString(R.string.SHARE_PREF_UserId), "00000000-0000-0000-0000-000000000000");

        //INTENT
        Intent intent = getIntent();
        ScanType = intent.getStringExtra(getString(R.string.EXTRA_QR_Scanner_Mode));
        StoreStampId = intent.getStringExtra(getString(R.string.EXTRA_StampDetail_Id));

        //CONTROL INITIALIZATION
        mProgressView = findViewById(R.id.login_progress);
        mControlBody = findViewById(R.id.dvStampListBody);
        lblStatus = (TextView)findViewById(R.id.lblStatus);
        lblScannedId = (TextView)findViewById(R.id.lblCustomerStampId);
        TextView lblScannerLabel = (TextView)findViewById(R.id.lblScannerLabel);
        TextView lblStampScannerInstructions = (TextView)findViewById(R.id.lblStampScannerInstructions);
        btnUpdateCustomerStamp = (Button)findViewById(R.id.btnUpdateCustomerStamp);
        btnUpdateCustomerStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StampCustomerStamp(lblScannedId.getText().toString());
            }
        });
        lblGiveCustomerStampInstructions = (TextView)findViewById(R.id.lblGiveCustomerStampInstructions);
        btnGiveCustomerStamp = (Button)findViewById(R.id.btnGiveCustomerStamp);
        btnGiveCustomerStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiveCustomerStampSet(lblScannedId.getText().toString());
            }
        });
        Button btnBackToList = (Button)findViewById(R.id.btnBackToList);
        btnBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lblScannerLabel.setText("Scanned " + ScanType + " ID:");
        if(ScanType.equals("Stamp")) {
            btnUpdateCustomerStamp.setVisibility(View.VISIBLE);
            lblStampScannerInstructions.setVisibility(View.VISIBLE);
        } else if (ScanType.equals("Customer")){
            btnGiveCustomerStamp.setVisibility(View.VISIBLE);
            lblGiveCustomerStampInstructions.setVisibility(View.VISIBLE);
        }


        if(lblScannedId.getText().length() == 0) {
            //INITIALIZE SCANNER APP
            integrator = new IntentIntegrator(QRScannerActivity.this);
            integrator.addExtra("SCAN_WIDTH", 640);
            integrator.addExtra("SCAN_HEIGHT", 480);
            integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
            //customize the prompt message before scanning
            integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        }
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
                    lblScannedId.setText(contents);
                }
                catch(Exception ex){
                    lblScannedId.setText("Invalid QR Code scanned!");
                }
            } else {
                lblScannedId.setText("---");
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
        private final Context mContext;
        private String responseMessage;

        CustomerStampTask(String customerStampId, String userId, Context context) {
            mCustomerStampId = customerStampId;
            mUserId = userId;
            mContext = context;
            responseMessage = "";
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Boolean loginSuccess = false;

            JSONObject jsonResponse = null;
            JSONObject jsonParam = new JSONObject();

            try {

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.PostAPI(jsonParam, "/aloopy/customerstampset/?customerStampSetId=" + mCustomerStampId + "&mercUserId=" + mUserId);


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");
                    responseMessage = jsonResponse.getString("responseMessage");

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

            if(responseMessage != null && !responseMessage.isEmpty())
                Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

            if(success == true){
                lblStatus.setText("Process has completed successfully!");
            }
            else {
                lblStatus.setText("Process has failed!");
            }

            //finish();
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }
    public class CustomerStampSetTask extends AsyncTask<Void, Void, Boolean> {

        private final String mCustomerId;
        private final String mStoreStampSetId;
        private final String mUserId;
        private final Context mContext;
        private String responseMessage;

        CustomerStampSetTask(String customerId, String storeStampSetId, String userId, Context context) {
            mCustomerId = customerId;
            mStoreStampSetId = storeStampSetId;
            mUserId = userId;
            mContext = context;
            responseMessage = "";
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Boolean loginSuccess = false;

            JSONObject jsonResponse = null;
            JSONObject jsonParam = new JSONObject();

            try {

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.PostAPI(jsonParam, "/aloopy/customerstampset/?storeStampId=" + mStoreStampSetId
                        + "&customerId=" + mCustomerId + "&mercUserId=" + mUserId);


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");
                    responseMessage = jsonResponse.getString("responseMessage");

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

            if(responseMessage != null && !responseMessage.isEmpty())
                Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

            if(success == true){
                lblStatus.setText("Process has completed successfully!");
            }
            else {
                lblStatus.setText("Process has failed!\r\n\r\n" + responseMessage);
            }

            //finish();
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

        showProgress(true);
        mTask = new CustomerStampTask(customerStampTask, UserId, getBaseContext());
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

    public void callCustomerStampSetTask(String customerId) {
        if (mStampSetTask != null) {
            return;
        }

        showProgress(true);
        mStampSetTask = new CustomerStampSetTask(customerId, StoreStampId, UserId, getBaseContext());
        mStampSetTask.execute((Void) null);

    }
    private String GiveCustomerStampSet(String customerId)
    {
        String message = "";
        UUID cStampId = null;

        if(Common.GetInternetConnectivity((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {

            try {
                //CHECK GUID FORMAT
                cStampId = UUID.fromString(customerId);

                //ACCESS API
                callCustomerStampSetTask(customerId);

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
