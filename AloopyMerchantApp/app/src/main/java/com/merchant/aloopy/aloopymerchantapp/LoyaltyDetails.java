package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantLoyaltyContract;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.UUID;

/**
 * Created by imbisibol on 10/1/2015.
 */
public class LoyaltyDetails extends ActionBarActivity {

    private SaveLoyaltyTask mSaveTask = null;

    String responseMessage = "";
    String LoyaltyID;
    String UserID;
    TextView lblScannedId;
    TextView lblGiveCustomerLoyaltyInstructions;
    private TextView lblStatus = null;
    Button btnGiveCustomerLoyalty;
    Button btnSaveCustomerLoyalty;

    ProgressBar mProgressBar;

    private IntentIntegrator integrator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loyalty_details);

        //SHARED PREFERENCES
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        UserID = mSettings.getString(getString(R.string.SHARE_PREF_UserId), null);

        //INTENT
        Intent intent = getIntent();
        LoyaltyID = intent.getStringExtra(getString(R.string.EXTRA_LoyaltyDetail_Id));

        //CONTROLS
        mProgressBar = (ProgressBar)findViewById(R.id.login_progress);
        lblScannedId = (TextView)findViewById(R.id.lblScannedID);
        lblStatus = (TextView)findViewById(R.id.lblStatus);
        lblGiveCustomerLoyaltyInstructions = (TextView)findViewById(R.id.lblGiveCustomerLoyaltyInstructions);
        btnGiveCustomerLoyalty = (Button)findViewById(R.id.btnGiveCustomerLoyalty);
        btnGiveCustomerLoyalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.GetInternetConnectivity((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    //INITIALIZE SCANNER APP

                    integrator = new IntentIntegrator(LoyaltyDetails.this);
                    integrator.addExtra("SCAN_WIDTH", 640);
                    integrator.addExtra("SCAN_HEIGHT", 480);
                    integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
                    //customize the prompt message before scanning
                    integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
                    integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSaveCustomerLoyalty = (Button)findViewById(R.id.btnSaveCustomerLoyalty);
        btnSaveCustomerLoyalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCustomerLoyalty();
            }
        });



        LoadLoyaltyData(LoyaltyID);
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

                    lblGiveCustomerLoyaltyInstructions.setVisibility(View.GONE);
                    btnGiveCustomerLoyalty.setVisibility(View.GONE);

                    btnSaveCustomerLoyalty.setVisibility(View.VISIBLE);
                }
                catch(Exception ex){
                    lblScannedId.setText("Invalid QR Code scanned!");
                }
            } else {
                lblScannedId.setText("---");
            }
        }
    }

    public class SaveLoyaltyTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mLoyaltyId;
        private final String mCustomerId;

        SaveLoyaltyTask(String userId, String loyaltyId, String customerId) {

            mUserId = userId;
            mLoyaltyId = loyaltyId;
            mCustomerId = customerId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean loginSuccess = false;
            String userId = "";
            String userDisplay = "";
            JSONObject jsonResponse = null;

            try {

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

                        AloopySQLHelper helper = AloopySQLHelper.getInstance(getBaseContext());
                        SQLiteDatabase db = helper.getWritableDatabase();

                        if (loyaltyArray != null && loyaltyArray.length() > 0) {

                            ContentValues values = new ContentValues();
                            values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Volume,
                                    loyaltyArray.getJSONObject(0).getInt(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Volume));

                            db.update(MerchantLoyaltyContract.MerchantLoyaltyInformation.TABLE_NAME,
                                    values,
                                    MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID + "=" + loyaltyArray.getJSONObject(0).getString(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID),
                                    null);

                        }


                        db.close();

                    }


                        loginSuccess = true;
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
    }

    //METHOD
    private void LoadLoyaltyData(String id) {

        AloopySQLHelper helper = AloopySQLHelper.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        //GET DATA FROM DB
        String[] projection = {
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Title,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Volume,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Expiration,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Card_Price,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_Image,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_QR,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Created,
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Modified
        };

        Cursor c = db.query(
                MerchantLoyaltyContract.MerchantLoyaltyInformation.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID + " = ?",                                // The columns for the WHERE clause
                new String[]{id},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        //CONTROLS
        TextView lblLoyaltyTitle = (TextView)findViewById(R.id.lblLoyaltyTitle);
        TextView lblVolume = (TextView)findViewById(R.id.lblVolume);
        TextView lblCardPrice = (TextView) findViewById(R.id.lblCardPrice);
        TextView lblExpiryDate = (TextView) findViewById(R.id.lblExpiryDate);
        ImageView imgCard = (ImageView) findViewById(R.id.imgCard);
        ImageView imgQRCode = (ImageView) findViewById(R.id.imgQRCode);

        if(c.moveToNext()) {
            lblLoyaltyTitle.setText(c.getString(c.getColumnIndexOrThrow(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Title)));
            lblVolume.setText("AVAILABLE: " + c.getString(c.getColumnIndexOrThrow(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Volume)));
            lblCardPrice.setText(getString(R.string.Currency) + c.getString(c.getColumnIndexOrThrow(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Card_Price)));
            lblExpiryDate.setText(c.getString(c.getColumnIndexOrThrow(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Expiration)));
            lblExpiryDate.setText(lblExpiryDate.getText().toString().substring(0, lblExpiryDate.getText().toString().indexOf("T")));
            Common.getImageLoader(null).displayImage(c.getString(c.getColumnIndexOrThrow(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_Image)), imgCard);Common.getImageLoader(null).displayImage(c.getString(c.getColumnIndexOrThrow(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_QR)), imgQRCode);

        }

    }
    private void SaveCustomerLoyalty() {
        if (mSaveTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;


        showProgress(true);
        mSaveTask = new SaveLoyaltyTask(UserID, LoyaltyID, lblScannedId.getText().toString());
        mSaveTask.execute((Void) null);

    }

}
