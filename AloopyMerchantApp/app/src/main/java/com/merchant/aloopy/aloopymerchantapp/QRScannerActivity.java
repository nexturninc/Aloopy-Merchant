package com.merchant.aloopy.aloopymerchantapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.merchant.aloopy.aloopydatabase.AloopySQLHelper;
import com.merchant.aloopy.aloopydatabase.MerchantInfoContract;
import com.merchant.aloopy.aloopydatabase.UserInfoContract;

import java.util.Locale;

/**
 * Created by imbisibol on 9/23/2015.
 */
public class QRScannerActivity extends ActionBarActivity {

    private String QRScanMode = null;

    private Button btnScanQRCode = null;
    FrameLayout dvCameraView = null;
    private IntentIntegrator integrator = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        //INTENT
        Intent intent = getIntent();
        integrator = new IntentIntegrator(QRScannerActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        //customize the prompt message before scanning
        integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
        QRScanMode = intent.getStringExtra(getString(R.string.EXTRA_QR_Scanner_Mode));

        //CONTROLS
        btnScanQRCode = (Button)findViewById(R.id.btnRefresh);

        if(btnScanQRCode != null){
            btnScanQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_profile){
            //Intent intent = new Intent(getBaseContext(), CustomerProfile.class);
            //startActivity(intent);
        }
        else if (id == R.id.action_logout){
            AloopySQLHelper helper = AloopySQLHelper.getInstance(getBaseContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            db.delete(MerchantInfoContract.MerchantInformation.TABLE_NAME, null, null);
            db.delete(UserInfoContract.UserInformation.TABLE_NAME, null, null);

            SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = mSettings.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); //QR code模式
            startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
            String abc = "dasd";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                Toast.makeText(getBaseContext(), contents, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "An unexpected error has occurred!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
