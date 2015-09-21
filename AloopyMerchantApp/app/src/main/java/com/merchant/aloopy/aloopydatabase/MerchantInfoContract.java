package com.merchant.aloopy.aloopydatabase;

import android.provider.BaseColumns;

/**
 * Created by imbisibol on 9/18/2015.
 */
public class MerchantInfoContract {

    public MerchantInfoContract(){

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MerchantInformation.TABLE_NAME + " (" +
                    MerchantInformation._ID + " INTEGER PRIMARY KEY," +
                    MerchantInformation.COLUMN_NAME_Merchant_ID + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Merchant_Name + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Parent_Merchant_ID + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Beacon_UUID + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Merchant_Type + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Merchant_Logo_V + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Merchant_Logo_H + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Address + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Country + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Country_Code + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Telephone_No + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Mobile_No + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Email_Address + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Contact_Person + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Contact_Person_Email + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Contact_Person_Mobile + TEXT_TYPE + COMMA_SEP +
                    MerchantInformation.COLUMN_NAME_Date_Modified + DATE_TYPE +
                    " )";
    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MerchantInformation.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static abstract class MerchantInformation implements BaseColumns {
        public static final String TABLE_NAME = "MerchantInformation";
        public static final String COLUMN_NAME_Merchant_ID = "MerchantId";
        public static final String COLUMN_NAME_Merchant_Name = "MerchantName";
        public static final String COLUMN_NAME_Parent_Merchant_ID = "ParentMerchantID";
        public static final String COLUMN_NAME_Beacon_UUID = "BeaconUUID";
        public static final String COLUMN_NAME_Merchant_Type = "MerchantType";
        public static final String COLUMN_NAME_Merchant_Logo_V = "MerchantLogoV";
        public static final String COLUMN_NAME_Merchant_Logo_H = "MerchantLogoH";
        public static final String COLUMN_NAME_Address = "Address";
        public static final String COLUMN_NAME_Country = "Country";
        public static final String COLUMN_NAME_Country_Code = "CountryCode";
        public static final String COLUMN_NAME_Telephone_No = "TelephoneNo";
        public static final String COLUMN_NAME_Mobile_No = "MobileNo";
        public static final String COLUMN_NAME_Email_Address = "EmailAddress";
        public static final String COLUMN_NAME_Contact_Person = "ContactPerson";
        public static final String COLUMN_NAME_Contact_Person_Email = "ContactPersonEmail";
        public static final String COLUMN_NAME_Contact_Person_Mobile = "ContactPersonMobile";
        public static final String COLUMN_NAME_Date_Modified = "DateModified";


    }
}
