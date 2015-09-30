package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantLoyaltyContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by imbisibol on 9/30/2015.
 */
public class LoyaltyList extends Fragment {

    public String UserID;
    public String MerchantId;
    public GridView gridview;

    public ArrayList<MerchantLoyaltyContract> loyaltyData = new ArrayList<MerchantLoyaltyContract>();
    private ProgressBar mProgressBar;
    private View mStampListBody;
    private LoyaltyTask mTask = null;
    public LoyaltyAdapter loyaltyAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.stamp_list, container, false);



        return rootView;
    }

    public static LoyaltyList newInstance() {
        LoyaltyList fragment = new LoyaltyList();

        return fragment;
    }


    public class LoyaltyTask extends AsyncTask<Void, Void, Boolean> {

        private final String MerchantId;

        LoyaltyTask(String merchantId) {
            MerchantId = merchantId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean loginSuccess = false;
            String userId = "";
            String userDisplay = "";
            JSONObject jsonResponse = null;

            try {

                JSONObject jsonParam = new JSONObject();

                Common comm = new Common();
                comm.setAPIURL(getString(R.string.AloopyAPIURL));
                jsonResponse = comm.GetAPI("/aloopy/merchantloyalty/?merchantId=" + MerchantId);


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");

                    if (strSuccess == "true") {

                        JSONArray loyaltyArray = jsonResponse.getJSONArray("loyaltyCards");

                        if(loyaltyArray != null) {

                            loyaltyData = new ArrayList<MerchantLoyaltyContract>();

                            Context cont = getActivity().getBaseContext();

                            // Gets the data repository in write mode
                            AloopySQLHelper helper = AloopySQLHelper.getInstance(getActivity());
                            SQLiteDatabase db = helper.getWritableDatabase();
                            try {
                                db.delete(MerchantLoyaltyContract.MerchantLoyaltyInformation.TABLE_NAME, null, null);
                            }
                            catch (Exception ex)
                            {

                            }

                            for(int ctr=0;ctr<loyaltyArray.length();ctr++) {
                                MerchantLoyaltyContract loyaltyItem = new MerchantLoyaltyContract();
                                loyaltyItem.LoyaltyId = loyaltyArray.getJSONObject(ctr).getString("id");
                                loyaltyItem.Volume = loyaltyArray.getJSONObject(ctr).getInt("volume");
                                loyaltyItem.DateExpiration = loyaltyArray.getJSONObject(ctr).getString("dateExpiration");
                                loyaltyItem.CardPrice = loyaltyArray.getJSONObject(ctr).getString("cardPrice");
                                loyaltyItem.LoyaltyCardImage = loyaltyArray.getJSONObject(ctr).getJSONObject("loyaltyCardImage_x3").getString("filePath");
                                loyaltyItem.LoyaltyCardQR = loyaltyArray.getJSONObject(ctr).getJSONObject("loyaltyCardQRCode_x3").getString("filePath");
                                loyaltyItem.DateCreated = loyaltyArray.getJSONObject(ctr).getString("dateCreated");
                                loyaltyItem.DateModified = loyaltyArray.getJSONObject(ctr).getString("dateModified");


                                loyaltyData.add(loyaltyItem);


                                //SAVE TO DATABASE
                                ContentValues values = new ContentValues();
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID, loyaltyItem.LoyaltyId);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Volume, loyaltyItem.Volume);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Expiration, loyaltyItem.DateExpiration);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Card_Price, loyaltyItem.CardPrice);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_Image, loyaltyItem.LoyaltyCardImage);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_QR, loyaltyItem.LoyaltyCardQR);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Created, loyaltyItem.DateCreated);
                                values.put(MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Date_Modified, loyaltyItem.DateModified);


                                long newRowId;
                                newRowId = db.insert(
                                        MerchantLoyaltyContract.MerchantLoyaltyInformation.TABLE_NAME,
                                        MerchantLoyaltyContract.MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID,
                                        values);
                            }


                            db.close();

                        }


                        loginSuccess = true;
                    }
                }

            } catch (Exception ex) {

                String abc = ex.getMessage();

            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTask = null;
            showProgress(false);

            if (success) {

                loyaltyAdapter = new LoyaltyAdapter(getActivity(), R.layout.loyalty_row, loyaltyData);
                gridview.setAdapter(loyaltyAdapter);

                //finish();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity().getBaseContext());
                dialog.setTitle("Message Alert");
                dialog.setMessage("Failed to retrieve Loyalty Car List!");
                dialog.show();
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
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
            mStampListBody.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mStampListBody.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mStampListBody.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}