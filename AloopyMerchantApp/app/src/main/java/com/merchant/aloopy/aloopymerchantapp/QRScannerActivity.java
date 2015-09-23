package com.merchant.aloopy.aloopymerchantapp;

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

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
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
    private Camera mCamera = null;
    private CameraCustomView mCameraView = null;
    FrameLayout dvCameraView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        //INTENT
        Intent intent = getIntent();
        QRScanMode = intent.getStringExtra(getString(R.string.EXTRA_QR_Scanner_Mode));

        //CONTROLS
        btnScanQRCode = (Button)findViewById(R.id.btnRefresh);
        dvCameraView = (FrameLayout)findViewById(R.id.dvCameraView);
        try{
            mCamera = Camera.open();
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraCustomView(getBaseContext(), mCamera);//create a SurfaceView to show camera data
            dvCameraView.addView(mCameraView);//add the SurfaceView to the layout
            dvCameraView.setVisibility(View.VISIBLE);
        }

        if(btnScanQRCode != null){
            btnScanQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.takePicture(new Camera.ShutterCallback() {
                                            @Override
                                            public void onShutter() {
                                                String ab = "sdasd";
                                            }
                                        },
                            new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    String ab = "sdasd";
                                }
                            },
                            new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    String ab = "sdasd";
                                }
                            });
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


}
