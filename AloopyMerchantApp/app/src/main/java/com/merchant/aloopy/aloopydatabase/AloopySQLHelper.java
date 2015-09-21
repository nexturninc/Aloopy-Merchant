package com.merchant.aloopy.aloopydatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by imbisibol on 9/18/2015.
 */
public class AloopySQLHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "AloopyMOfflineData.db";

    public static AloopySQLHelper sqlHelperInstance;

    public AloopySQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized AloopySQLHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sqlHelperInstance == null) {
            sqlHelperInstance = new AloopySQLHelper(context.getApplicationContext());
        }
        return sqlHelperInstance;
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(MerchantInfoContract.SQL_CREATE_TABLE);
        db.execSQL(UserInfoContract.SQL_CREATE_TABLE);
        db.execSQL(MerchantStampInfoContract.SQL_CREATE_TABLE);
        //db.execSQL(CustomerCouponContract.SQL_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(MerchantInfoContract.SQL_DELETE_TABLE);
        db.execSQL(UserInfoContract.SQL_DELETE_TABLE);
        db.execSQL(MerchantStampInfoContract.SQL_DELETE_TABLE);
        //db.execSQL(CustomerCouponContract.SQL_DELETE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
