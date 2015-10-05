package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantLoyaltyContract;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by imbisibol on 10/5/2015.
 */
public class LoyaltyAwardPoints extends ActionBarActivity
{
    private SaveLoyaltyPointsTask mSaveTask = null;

    private String UserID;
    private String CustomerLoyaltyID;
    private String responseMessage;

    private TextView lblScannedID;
    private EditText txtPointAmount;
    private EditText txtTranReferenceNo;
    private EditText txtReferenceAmount;
    private TextView lblStatus;
    private ProgressBar mProgressBar;
    private Button btnAwardPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loyalty_award_points);

        responseMessage = "";

        //SHARED PREFERENCES
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        UserID = mSettings.getString(getString(R.string.SHARE_PREF_UserId), null);

        //INTENT
        Intent intent = getIntent();
        CustomerLoyaltyID = intent.getStringExtra(getString(R.string.EXTRA_LoyaltyDetail_Id));

        //CONTROLS
        lblScannedID = (TextView)findViewById(R.id.lblScannedID);
        lblScannedID.setText(CustomerLoyaltyID);
        txtPointAmount = (EditText)findViewById(R.id.txtPointAmount);
        txtTranReferenceNo = (EditText)findViewById(R.id.txtTranReferenceNo);
        txtReferenceAmount = (EditText)findViewById(R.id.txtReferenceAmount);
        lblStatus = (TextView)findViewById(R.id.lblStatus);
        mProgressBar = (ProgressBar)findViewById(R.id.login_progress);
        btnAwardPoints = (Button)findViewById(R.id.btnAwardPoints);
        btnAwardPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Common.GetInternetConnectivity((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    showProgress(false);
                    Toast.makeText(getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
                }
                else {
                    AwardCustomerPoints();
                }
            }
        });
    }

    public class SaveLoyaltyPointsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mCustomerLoyaltyId;
        private final int mPoints;
        private final String mRefNo;
        private final String mRefAmount;

        SaveLoyaltyPointsTask(String userId, String customerLoyaltyId, int Points, String ReferenceNo, String ReferenceAmount) {

            mUserId = userId;
            mCustomerLoyaltyId = customerLoyaltyId;
            mPoints = Points;
            mRefNo = ReferenceNo;
            mRefAmount = ReferenceAmount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean loginSuccess = false;
            String userId = "";
            String userDisplay = "";
            JSONObject jsonResponse = null;

            try {

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("CreatedBy", mUserId);
                jsonParam.put("CustomerLoyaltyCardID", mCustomerLoyaltyId);
                jsonParam.put("ReferenceNo", mRefNo);
                jsonParam.put("TransactionAmount", mRefAmount);
                jsonParam.put("Amount", mRefAmount);

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.PostAPI(jsonParam, "/aloopy/customerloyaltytransaction");


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");
                    responseMessage = jsonResponse.getString("responseMessage");

                    if (strSuccess == "true") {
                        loginSuccess = true;
                    }
                }

            } catch (Exception ex) {

                String abc = ex.getMessage();

            }


            return loginSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSaveTask = null;
            showProgress(false);

            if (success) {

                if(responseMessage != null && !responseMessage.isEmpty())
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

                lblStatus.setText("Process has completed successfully!");

                txtTranReferenceNo.setText("");
                txtPointAmount.setText("0");
                txtReferenceAmount.setText("0.00");

            } else {

                if(responseMessage != null && !responseMessage.isEmpty())
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

                lblStatus.setText("Creation of Customer Loyalty Card has failed! \r\n\r\n" + responseMessage);

            }
        }

        @Override
        protected void onCancelled() {
            mSaveTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
            btnAwardPoints.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    btnAwardPoints.setVisibility(show ? View.GONE : View.VISIBLE);
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
            btnAwardPoints.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }


    //METHODS
    public void AwardCustomerPoints() {

        if (mSaveTask != null) {
            return;
        }

        if(txtPointAmount.getText().length() == 0)
        {
            txtPointAmount.setError(getString(R.string.txtPointAmount_Required));
        }
        else if(txtTranReferenceNo.getText().length() == 0) {
            txtTranReferenceNo.setError(getString(R.string.txtTranReferenceNo_Required));
        }

        showProgress(true);

        mSaveTask = new SaveLoyaltyPointsTask(UserID, CustomerLoyaltyID, Integer.valueOf(txtPointAmount.getText().toString()), txtTranReferenceNo.getText().toString(), txtReferenceAmount.getText().toString() );
        mSaveTask.execute((Void) null);
    }
}
