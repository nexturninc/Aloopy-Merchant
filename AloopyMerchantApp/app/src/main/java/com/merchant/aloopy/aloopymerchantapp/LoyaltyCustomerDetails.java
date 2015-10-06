package com.merchant.aloopy.aloopymerchantapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.UUID;

/**
 * Created by imbisibol on 10/5/2015.
 */
public class LoyaltyCustomerDetails  extends ActionBarActivity {

    private IntentIntegrator integrator = null;

    View dvLoyaltyListBody;

    private String UserID;
    private String CustomerLoyaltyID;
    private String responseMessage;

    private TextView lblScannedId;


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
        lblScannedId = (TextView)findViewById(R.id.lblScannedID);
        dvLoyaltyListBody = findViewById(R.id.dvLoyaltyListBody);
        Button btnAwardPoints = (Button)findViewById(R.id.btnAwardPoints);
        btnAwardPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Common.GetInternetConnectivity((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    Toast.makeText(getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getBaseContext(), LoyaltyAwardPoints.class);
                    intent.putExtra(getString(R.string.EXTRA_LoyaltyDetail_Id), CustomerLoyaltyID);
                    startActivity(intent);
                }
            }
        });
        Button btnCollectRewards = (Button)findViewById(R.id.btnCollectRewards);
        btnCollectRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Common.GetInternetConnectivity((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    Toast.makeText(getBaseContext(), getString(R.string.message_Internet_Required), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getBaseContext(), LoyaltyRewards.class);
                    intent.putExtra(getString(R.string.EXTRA_LoyaltyDetail_Id), CustomerLoyaltyID);
                    startActivity(intent);
                }
            }
        });

        //RUN SCANNER
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null && contents.length() > 0) {
                UUID cStampId = null;

                try {
                    cStampId = UUID.fromString(contents);

                    CustomerLoyaltyID = contents;
                    lblScannedId.setText(contents);

                    /*
                    lblGiveCustomerLoyaltyInstructions.setVisibility(View.GONE);
                    btnGiveCustomerLoyalty.setVisibility(View.GONE);

                    btnSaveCustomerLoyalty.setVisibility(View.VISIBLE);*/
                }
                catch(Exception ex){
                    lblScannedId.setText("Invalid QR Code scanned!");
                }
            } else {
                lblScannedId.setText("---");
            }
        }
    }


}
