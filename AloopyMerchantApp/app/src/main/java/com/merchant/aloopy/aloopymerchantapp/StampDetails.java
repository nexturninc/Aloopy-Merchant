package com.merchant.aloopy.aloopymerchantapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantStampInfoContract;
import com.merchant.aloopy.aloopymerchantapp.R;

/**
 * Created by imbisibol on 9/21/2015.
 */
public class StampDetails extends ActionBarActivity {

    String storeStampID = null;

    TextView lblStampLabel;
    TextView lblCustomerStampSets;
    TextView lblCompletedStampSets;
    TextView lblStampScanCustomerQRInstructions;
    ImageView imgQRCode;
    ImageView imgRewardImage;
    ImageView imgMerchantLogo;
    View frmBody;
    View dvStampBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stamp_details);

        //iNTENT
        Intent intent = getIntent();
        storeStampID = intent.getStringExtra(getString(R.string.EXTRA_StampDetail_Id));

        //CONTROLS
        lblStampLabel = (TextView)findViewById(R.id.lblStampLabel);
        lblCustomerStampSets = (TextView)findViewById(R.id.lblCustomerStampSets);
        lblCompletedStampSets = (TextView)findViewById(R.id.lblCompletedStampSets);
        lblStampScanCustomerQRInstructions = (TextView)findViewById(R.id.lblStampScanCustomerQRInstructions);
        imgQRCode = (ImageView)findViewById(R.id.imgQRCode);
        imgRewardImage = (ImageView)findViewById(R.id.imgRewardImage);
        imgMerchantLogo = (ImageView)findViewById(R.id.imgMerchantLogo);
        frmBody = findViewById(R.id.frmBody);
        dvStampBar = findViewById(R.id.dvStampBar);
        Button btnScanCustomerQR = (Button)findViewById(R.id.btnScanCustomerQR);
        btnScanCustomerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.GetInternetConnectivity((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    Intent intent = new Intent(getBaseContext(), QRScannerActivity.class);
                    intent.putExtra(getString(R.string.EXTRA_QR_Scanner_Mode), "Customer");
                    intent.putExtra(getString(R.string.EXTRA_StampDetail_Id), storeStampID);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnBackToList = (Button)findViewById(R.id.btnBackToList);
        btnBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        LoadStampData(storeStampID);
    }

    private void LoadStampData(String id) {

        AloopySQLHelper helper = AloopySQLHelper.getInstance(this);
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
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Customer_Stamp_Count,
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Customer_Stamp_Completed_Count,
        };

        Cursor c = db.query(
                MerchantStampInfoContract.MerchantStampInformation.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Store_StampSet_ID + " = ?",                                // The columns for the WHERE clause
                new String[]{id},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(c.moveToNext()) {

            lblStampLabel.setText(c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Stamp_Title)));
            lblCustomerStampSets.setText(c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Customer_Stamp_Count)));
            lblCompletedStampSets.setText(c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Customer_Stamp_Completed_Count)));
            Common.getImageLoader(null).displayImage(c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_QR_Code)), imgQRCode);
            Common.getImageLoader(null).displayImage(c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Reward_Image)), imgRewardImage);
            Common.getImageLoader(null).displayImage(c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Merchant_Logo_H)), imgMerchantLogo);
            String bgColor = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color));
            if (bgColor != null && bgColor != "")
                frmBody.setBackgroundColor(Integer.parseInt(bgColor, 16) + 0xFF000000);
            String bgColor2 = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Background_Color2));
            if (bgColor2 != null && bgColor2 != "")
                dvStampBar.setBackgroundColor(Integer.parseInt(bgColor2, 16) + 0xFF000000);
            String textColor = c.getString(c.getColumnIndexOrThrow(MerchantStampInfoContract.MerchantStampInformation.COLUMN_NAME_Text_Color));
            if (textColor != null && textColor != "") {
                lblStampLabel.setTextColor(Integer.parseInt(textColor, 16) + 0xFF000000);
                lblStampScanCustomerQRInstructions.setTextColor(Integer.parseInt(textColor, 16) + 0xFF000000);
            }
        }

    }

}
