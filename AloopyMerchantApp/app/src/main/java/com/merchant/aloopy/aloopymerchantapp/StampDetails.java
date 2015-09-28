package com.merchant.aloopy.aloopymerchantapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.merchant.aloopy.aloopymerchantapp.R;

/**
 * Created by imbisibol on 9/21/2015.
 */
public class StampDetails extends ActionBarActivity {

    String storeStampID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stamp_details);

        //iNTENT
        Intent intent = getIntent();
        storeStampID = intent.getStringExtra(getString(R.string.EXTRA_StampDetail_Id));

        //CONTROLS
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
    }

}
