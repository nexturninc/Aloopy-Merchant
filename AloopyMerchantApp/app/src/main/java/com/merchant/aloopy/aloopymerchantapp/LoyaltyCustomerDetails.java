package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by imbisibol on 10/5/2015.
 */
public class LoyaltyCustomerDetails  extends ActionBarActivity {

    private GetLoyaltyRewardsTask mGetRewardTask = null;
    ProgressBar mProgressBar;
    private IntentIntegrator integrator = null;

    View dvLoyaltyListBody;

    private String UserID;
    private String CustomerLoyaltyID;
    private String responseMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loyalty_customer_details);

        //SHARED PREFERENCES
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        UserID = mSettings.getString(getString(R.string.SHARE_PREF_UserId), null);

        //INTENT
        Intent intent = getIntent();
        CustomerLoyaltyID = intent.getStringExtra(getString(R.string.EXTRA_LoyaltyDetail_Id));

        //CONTROLS
        dvLoyaltyListBody = findViewById(R.id.dvLoyaltyListBody);
        mProgressBar = (ProgressBar)findViewById(R.id.login_progress);

        if(CustomerLoyaltyID == null || CustomerLoyaltyID.isEmpty()) {
            //INITIALIZE SCANNER APP
            integrator = new IntentIntegrator(LoyaltyCustomerDetails.this);
            integrator.addExtra("SCAN_WIDTH", 640);
            integrator.addExtra("SCAN_HEIGHT", 480);
            integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
            //customize the prompt message before scanning
            integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        }

    }

    public class GetLoyaltyRewardsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mCustomerLoyaltyId;

        GetLoyaltyRewardsTask(String userId, String customerLoyaltyId) {

            mUserId = userId;
            mCustomerLoyaltyId = customerLoyaltyId;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean loginSuccess = false;
            String userId = "";
            String userDisplay = "";
            JSONObject jsonResponse = null;

            try {
/*
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userId", mUserId);
                jsonParam.put("customerId", mCustomerId);
                jsonParam.put("loyaltyCardID", mLoyaltyId);

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.PostAPI(jsonParam, "/aloopy/customerloyalty");


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");
                    responseMessage = jsonResponse.getString("responseMessage");

                    if (strSuccess == "true") {

                        JSONArray loyaltyArray = jsonResponse.getJSONArray("loyaltyCards");
                        loginSuccess = true;

                        if(loyaltyArray != null) {

                        }


                        loginSuccess = true;
                    }
                }
*/
            } catch (Exception ex) {

                String abc = ex.getMessage();

            }


            return loginSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetRewardTask = null;
            showProgress(false);

            if (success) {

                if(responseMessage != null && !responseMessage.isEmpty())
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

                //lblStatus.setText("Process has completed successfully!");

            } else {

                if(responseMessage != null && !responseMessage.isEmpty())
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

                //lblStatus.setText("Creation of Customer Loyalty Card has failed! \r\n\r\n" + responseMessage);

            }
        }

        @Override
        protected void onCancelled() {
            mGetRewardTask = null;
            showProgress(false);
        }
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


                    /*lblScannedId.setText(contents);

                    lblGiveCustomerLoyaltyInstructions.setVisibility(View.GONE);
                    btnGiveCustomerLoyalty.setVisibility(View.GONE);

                    btnSaveCustomerLoyalty.setVisibility(View.VISIBLE);*/
                }
                catch(Exception ex){
                    //lblScannedId.setText("Invalid QR Code scanned!");
                }
            } else {
                //lblScannedId.setText("---");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        /*
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
            btnSaveCustomerLoyalty.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    btnSaveCustomerLoyalty.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            btnSaveCustomerLoyalty.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        */
    }
}
