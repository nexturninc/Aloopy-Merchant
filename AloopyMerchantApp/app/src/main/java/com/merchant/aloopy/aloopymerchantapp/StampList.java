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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantStampInfoContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by imbisibol on 9/21/2015.
 */
public class StampList extends Fragment {

    public String UserID;
    public String MerchantId;
    public GridView gridview;
    public ArrayList<MerchantStampInfoContract> stampData = new ArrayList<MerchantStampInfoContract>();
    public StampSetAdapter stampSetAdapter;

    private StampSetTask mAuthTask = null;
    private ProgressBar mProgressBar;
    private View mStampListBody;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.stamp_list, container, false);

        mProgressBar = ((ProgressBar)rootView.findViewById(R.id.login_progress));
        mStampListBody = (rootView.findViewById(R.id.dvStampListBody));
        Button btnRefresh = (Button)rootView.findViewById(R.id.btnRefresh);
        Button btnScanQR = (Button)rootView.findViewById(R.id.btnScanCustomerStamp);


        //GET SHARED PREFERENCES
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        UserID = mSettings.getString(getActivity().getString(R.string.SHARE_PREF_UserId), null);
        MerchantId = mSettings.getString(getActivity().getString(R.string.SHARE_PREF_MerchantId), null);

        gridview = (GridView)rootView.findViewById(R.id.gvStampList);

        GetStamps(false);

        //GET DATA
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Toast.makeText(getActivity().getBaseContext(), stampData.get(position).StoreStampSetId, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity().getBaseContext(), StampDetails.class);
                intent.putExtra(getString(R.string.EXTRA_StampDetail_Id), stampData.get(position).StoreStampSetId);
                startActivity(intent);

            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetStamps(true);
            }
        });
        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getBaseContext(), QRScannerActivity.class);
                intent.putExtra(getString(R.string.EXTRA_QR_Scanner_Mode), "Stamp");
                startActivity(intent);

            }
        });


        return rootView;
    }


    public static StampList newInstance() {
        StampList fragment = new StampList();
        return fragment;
    }

    public void GetStamps(boolean forceAPIQuery) {

        if (mAuthTask != null) {
            return;
        }

        showProgress(true);

        AloopySQLHelper helper = AloopySQLHelper.getInstance(this.getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        //GET DATA FROM DB
        String[] projection = {
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stampset_ID,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_ID,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_StampSet_ID,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Title,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Volume,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color2,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Text_Color,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_H,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_V,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Icon,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Reward_Image,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_QR_Code,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Date_Created,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Date_Modified,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Start_Date,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_End_Date,
        };

        Cursor c = db.query(
                MerchantStampInfoContract.MerchantStampInformation.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(forceAPIQuery &&
                !Common.GetInternetConnectivity((ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            showProgress(false);
            Toast.makeText(getActivity().getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
        }
        else {
            if (forceAPIQuery
                    || (Common.GetInternetConnectivity((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)))
                    && c.getCount() == 0) {

                //GET FROM API

                mAuthTask = new StampSetTask(MerchantId);
                mAuthTask.execute((Void) null);

            }
            else //GET FROM DATABASE
            {

                stampData = new ArrayList<MerchantStampInfoContract>();

                while (c.moveToNext()) {

                    MerchantStampInfoContract stampItem = new MerchantStampInfoContract();
                    stampItem.StoreId = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_ID));
                    stampItem.StoreStampSetId = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_StampSet_ID));
                    stampItem.StampTitle = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Title));
                    stampItem.StampVolume = c.getInt(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Volume));
                    stampItem.BackgroundColor = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color));
                    stampItem.BackgroundColor2 = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color2));
                    stampItem.TextColor = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Text_Color));
                    stampItem.MerchantLogoH = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_H));
                    stampItem.MerchantLogoV = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_V));
                    stampItem.StampIcon = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Icon));
                    stampItem.RewardImage = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Reward_Image));
                    stampItem.QRCode = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_QR_Code));
                    stampItem.DateCreated = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Date_Created));
                    stampItem.DateModified = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Date_Modified));
                    stampItem.StartDate = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Start_Date));
                    stampItem.EndDate = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_End_Date));

                    stampData.add(stampItem);
                }

                stampSetAdapter = new StampSetAdapter(getActivity(), R.layout.stamp_set_row, stampData);
                gridview.setAdapter(stampSetAdapter);

                showProgress(false);
            }
        }
    }


    public class StampSetTask extends AsyncTask<Void, Void, Boolean> {

        private final String MerchantId;

        StampSetTask(String merchantId) {
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
                jsonResponse = comm.GetAPI("/aloopy/stamps/?merchantId=" + MerchantId);


                if (jsonResponse != null) {

                    String strSuccess = jsonResponse.getString("success");

                    if (strSuccess == "true") {

                        JSONArray stampSetArray = jsonResponse.getJSONArray("stampSet");

                        if(stampSetArray != null) {

                            stampData = new ArrayList<MerchantStampInfoContract>();

                            Context cont = getActivity().getBaseContext();

                            // Gets the data repository in write mode
                            AloopySQLHelper helper = AloopySQLHelper.getInstance(getActivity());
                            SQLiteDatabase db = helper.getWritableDatabase();
                            try {
                                db.delete(MerchantStampInfoContract.MerchantStampInformation.TABLE_NAME, null, null);
                            }
                            catch (Exception ex)
                            {

                            }

                            for(int ctr=0;ctr<stampSetArray.length();ctr++) {
                                MerchantStampInfoContract stampItem = new MerchantStampInfoContract();
                                stampItem.StoreId = stampSetArray.getJSONObject(ctr).getString("storeID");
                                stampItem.StampSetId = stampSetArray.getJSONObject(ctr).getString("stampSetID");
                                stampItem.StoreStampSetId = stampSetArray.getJSONObject(ctr).getString("storeStampSetID");
                                stampItem.StampTitle = stampSetArray.getJSONObject(ctr).getString("stampName");
                                stampItem.StampVolume = stampSetArray.getJSONObject(ctr).getInt("stampVolume");
                                stampItem.BackgroundColor = stampSetArray.getJSONObject(ctr).getString("backgroundColor");
                                stampItem.BackgroundColor2 = stampSetArray.getJSONObject(ctr).getString("backgroundColor2");
                                stampItem.TextColor = stampSetArray.getJSONObject(ctr).getString("textColor");
                                stampItem.MerchantLogoH = stampSetArray.getJSONObject(ctr).getJSONObject("horizontalMerchantLogo3x").getString("filePath");
                                stampItem.MerchantLogoV = stampSetArray.getJSONObject(ctr).getJSONObject("verticalMerchantLogo3x").getString("filePath");
                                stampItem.StampIcon = stampSetArray.getJSONObject(ctr).getJSONObject("stampIcon3x").getString("filePath");
                                stampItem.RewardImage = stampSetArray.getJSONObject(ctr).getJSONObject("rewardImage3x").getString("filePath");
                                stampItem.QRCode = stampSetArray.getJSONObject(ctr).getJSONObject("qrCodeImage3x").getString("filePath");
                                stampItem.DateCreated = stampSetArray.getJSONObject(ctr).getString("dateModified");
                                stampItem.DateModified = stampSetArray.getJSONObject(ctr).getString("dateModified");
                                stampItem.StartDate = stampSetArray.getJSONObject(ctr).getString("startDate");
                                stampItem.EndDate = stampSetArray.getJSONObject(ctr).getString("endDate");

                                stampData.add(stampItem);


                                //SAVE TO DATABASE
                                ContentValues values = new ContentValues();
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stampset_ID, stampItem.StampSetId);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_ID, stampItem.StoreId);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_StampSet_ID, stampItem.StoreStampSetId);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Title, stampItem.StampTitle);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Volume, stampItem.StampVolume);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color, stampItem.BackgroundColor);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color2, stampItem.BackgroundColor2);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Text_Color, stampItem.TextColor);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_H, stampItem.MerchantLogoH);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_V, stampItem.MerchantLogoV);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Icon, stampItem.StampIcon);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Reward_Image, stampItem.RewardImage);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_QR_Code, stampItem.QRCode);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Date_Created, stampItem.DateCreated);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Date_Modified, stampItem.DateModified);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Start_Date, stampItem.StartDate);
                                values.put(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_End_Date, stampItem.EndDate);


                                long newRowId;
                                newRowId = db.insert(
                                        MerchantStampInfoContract.MerchantStampInformation.TABLE_NAME,
                                        MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_StampSet_ID,
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
            mAuthTask = null;
            showProgress(false);

            if (success) {

                stampSetAdapter = new StampSetAdapter(getActivity(), R.layout.stamp_set_row, stampData);
                gridview.setAdapter(stampSetAdapter);

                //finish();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity().getBaseContext());
                dialog.setTitle("Message Alert");
                dialog.setMessage("Failed tor retrieve Stamp List!");
                dialog.show();
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
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
