package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.LoyaltyRewardsContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by imbisibol on 10/6/2015.
 */
public class LoyaltyRewards  extends ActionBarActivity {

    private GetRewardsTask mGetRewardsTask;
    private SaveLoyaltyPointsTask mSavePointsTask;
    private LoyaltyRewardsAdapter rewardsAdapter;

    private ArrayList<LoyaltyRewardsContract> rewardsData = new ArrayList<>();
    private String UserID;
    private String CustomerLoyaltyID;
    private String MerchantID;
    private String responseMessage;

    private ProgressBar login_progress;
    private View dvRewardsListBody;
    private GridView gvRewardsList;
    private TextView lblProcessStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loyalty_rewards);

        responseMessage = "";

        //SHARED PREFERENCES
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        UserID = mSettings.getString(getString(R.string.SHARE_PREF_UserId), null);
        MerchantID = mSettings.getString(getString(R.string.SHARE_PREF_MerchantId), null);

        //INTENT
        Intent intent = getIntent();
        CustomerLoyaltyID = intent.getStringExtra(getString(R.string.EXTRA_LoyaltyDetail_Id));

        //CONTROLS
        lblProcessStatus = (TextView)findViewById(R.id.lblProcessStatus);
        login_progress = (ProgressBar)findViewById(R.id.login_progress);
        dvRewardsListBody = findViewById(R.id.dvRewardsListBody);
        gvRewardsList = (GridView)findViewById(R.id.gvRewardsList);
        gvRewardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if(!Common.GetInternetConnectivity((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    showProgress(false);
                    Toast.makeText(getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
                }
                else {
                    LoyaltyRewardsContract item = rewardsData.get(position);
                    SaveLoyaltyPoint(item.RewardID, -item.PointCost);
                }
            }
        });

        GetLoyaltyCards();
    }

    public class GetRewardsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mCustomerLoyaltyId;
        private final String mMerchantId;
        private final Context mActivity;

        GetRewardsTask(String customerLoyaltyId, String merchantId, Context activity) {

            mCustomerLoyaltyId = customerLoyaltyId;
            mMerchantId = merchantId;
            mActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean loginSuccess = false;
            JSONObject jsonResponse = null;

            try {

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.GetAPI("/aloopy/loyaltyrewards/?customerLoyaltyId=" + mCustomerLoyaltyId + "&merchantId=" + mMerchantId);


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");
                    responseMessage = jsonResponse.getString("responseMessage");

                    if (strSuccess == "true") {

                        JSONArray rewardsJSON = jsonResponse.getJSONArray("loyaltyRewards");

                        if(rewardsJSON != null) {

                            ArrayList<LoyaltyRewardsContract> rewardsArray = new ArrayList<>();

                            // Gets the data repository in write mode
                            AloopySQLHelper helper = AloopySQLHelper.getInstance(getBaseContext());
                            SQLiteDatabase db = helper.getWritableDatabase();

                            for(int ctr=0;ctr<rewardsJSON.length(); ctr++) {

                                LoyaltyRewardsContract rewardItem = new LoyaltyRewardsContract();
                                rewardItem.RewardID = rewardsJSON.getJSONObject(ctr).getString("id");
                                rewardItem.LoyaltyID = rewardsJSON.getJSONObject(ctr).getString("loyaltyCardID");
                                rewardItem.RewardName = rewardsJSON.getJSONObject(ctr).getString("rewardName");
                                rewardItem.PointCost = rewardsJSON.getJSONObject(ctr).getInt("pointCost");
                                rewardItem.RewardImage = rewardsJSON.getJSONObject(ctr).getJSONObject("rewardImage_x3").getString("filePath");
                                rewardItem.RewardQR = rewardsJSON.getJSONObject(ctr).getJSONObject("qrCodeImage_x3").getString("filePath");
                                rewardItem.DateCreated = rewardsJSON.getJSONObject(ctr).getString("dateCreated");
                                rewardItem.DateModified = rewardsJSON.getJSONObject(ctr).getString("dateModified");
                                rewardsData.add(rewardItem);

                                if(ctr == 0) {
                                    try {
                                        db.delete(LoyaltyRewardsContract.LoyaltyRewardsInformation.TABLE_NAME,
                                                LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Loyalty_ID + "=?", new String[]{rewardItem.LoyaltyID});
                                    } catch (Exception ex) {
                                        String aaa = ex.getMessage();

                                        String abc = "sdas";
                                    }
                                }

                                //SAVE TO DATABASE
                                ContentValues values = new ContentValues();
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Reward_ID, rewardItem.RewardID);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Loyalty_ID, rewardItem.LoyaltyID);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Reward_Name, rewardItem.RewardName);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Point_Cost, rewardItem.PointCost);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Reward_Image, rewardItem.RewardImage);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Reward_QR, rewardItem.RewardQR);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Date_Created, rewardItem.DateCreated);
                                values.put(LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Date_Modified, rewardItem.DateModified);


                                long newRowId;
                                newRowId = db.insert(
                                        LoyaltyRewardsContract.LoyaltyRewardsInformation.TABLE_NAME,
                                        LoyaltyRewardsContract.LoyaltyRewardsInformation.COLUMN_NAME_Reward_ID,
                                        values);
                            }



                            loginSuccess = true;
                        }
                    }
                }

            } catch (Exception ex) {

                String abc = ex.getMessage();

            }


            return loginSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetRewardsTask = null;
            showProgress(false);

            if (success) {

                rewardsAdapter = new LoyaltyRewardsAdapter(mActivity, R.layout.loyalty_rewards_row, rewardsData);
                gvRewardsList.setAdapter(rewardsAdapter);

                //finish();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseContext());
                dialog.setTitle("Message Alert");
                dialog.setMessage("Failed to retrieve Loyalty Reward List!");
                dialog.show();
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mGetRewardsTask = null;
            showProgress(false);
        }
    }
    public class SaveLoyaltyPointsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mCustomerLoyaltyId;
        private final int mPoints;
        private final String mRewardId;

        SaveLoyaltyPointsTask(String userId, String customerLoyaltyId, int Points, String rewardId) {

            mUserId = userId;
            mCustomerLoyaltyId = customerLoyaltyId;
            mPoints = Points;
            mRewardId = rewardId;
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
                jsonParam.put("ReferenceNo", mRewardId);
                jsonParam.put("Amount", mPoints);
                jsonParam.put("LoyaltyCardRewardId", mRewardId);

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
            mSavePointsTask = null;
            showProgress(false);

            if (success) {

                if(responseMessage != null && !responseMessage.isEmpty())
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

                lblProcessStatus.setText("Loyalty Reward has been claimed!");

            } else {

                if(responseMessage != null && !responseMessage.isEmpty())
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();

                lblProcessStatus.setText("Claiming of Loyalty Reward has failed! \r\n\r\n" + responseMessage);

            }
        }

        @Override
        protected void onCancelled() {
            mSavePointsTask = null;
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

            login_progress.setVisibility(show ? View.GONE : View.VISIBLE);
            dvRewardsListBody.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dvRewardsListBody.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            login_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            dvRewardsListBody.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }

    //METHODS
    public void GetLoyaltyCards() {

        if (mGetRewardsTask != null) {
            return;
        }

        showProgress(true);

        mGetRewardsTask = new GetRewardsTask(CustomerLoyaltyID, MerchantID, this);
        mGetRewardsTask.execute((Void) null);

    }
    public void SaveLoyaltyPoint(String rewardId, Integer points) {

        if (mSavePointsTask != null) {
            return;
        }

        showProgress(true);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        mSavePointsTask = new SaveLoyaltyPointsTask(UserID, CustomerLoyaltyID, points, rewardId);
        mSavePointsTask.execute((Void) null);

    }


}
