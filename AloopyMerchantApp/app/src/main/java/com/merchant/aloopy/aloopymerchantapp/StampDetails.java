package com.merchant.aloopy.aloopymerchantapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.merchant.aloopy.aloopymerchantapp.R;

/**
 * Created by imbisibol on 9/21/2015.
 */
public class StampDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stamp_details);

        Intent intent = getIntent();
        String stampId = intent.getStringExtra(getString(R.string.EXTRA_StampDetail_Id));

        LoadStampData(stampId);
    }

    private void LoadStampData(String id) {
    }

}
