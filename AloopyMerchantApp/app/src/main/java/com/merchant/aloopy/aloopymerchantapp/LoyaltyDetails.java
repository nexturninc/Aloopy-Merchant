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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantLoyaltyContract;

import org.w3c.dom.Text;

import java.util.UUID;

/**
 * Created by imbisibol on 10/1/2015.
 */
public class LoyaltyDetails extends ActionBarActivity {

    String loyaltyID;
    TextView lblScannedId;
    TextView lblGiveCustomerLoyaltyInstructions;
    Button btnGiveCustomerLoyalty;
    Button btnSaveCustomerLoyalty;

    private IntentIntegrator integrator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loyalty_details);

        //INTENT
        Intent intent = getIntent();
        loyaltyID = intent.getStringExtra(getString(R.string.EXTRA_LoyaltyDetail_Id));
        //customerID

        //CONTROLS
        lblScannedId = (TextView)findViewById(R.id.lblScannedID);
        lblGiveCustomerLoyaltyInstructions = (TextView)findViewById(R.id.lblGiveCustomerStampInstructions);
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



        LoadLoyaltyData(loyaltyID);
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

                }
                catch(Exception ex){
                    lblScannedId.setText("Invalid QR Code scanned!");
                }
            } else {
                lblScannedId.setText("---");
            }
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

        if(c.moveToNext()) {

        }

    }

    private void SaveCustomerLoyalty(){

    }

}
