package com.merchant.aloopy.aloopydatabase;

import android.provider.BaseColumns;

/**
 * Created by imbisibol on 9/30/2015.
 */
public class MerchantLoyaltyContract {

    public String LoyaltyId;
    public String Title;
    public Integer Volume;
    public String DateExpiration;
    public String CardPrice;
    public String LoyaltyCardImage;
    public String LoyaltyCardQR;
    public Integer CustomerCardCount;
    public String DateCreated;
    public String DateModified;

    public MerchantLoyaltyContract(){

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MerchantLoyaltyInformation.TABLE_NAME + " (" +
                    MerchantLoyaltyInformation._ID + " INTEGER PRIMARY KEY," +
                    MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_ID + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Title + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Volume + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Date_Expiration + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Card_Price + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_Image + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Loyalty_Card_QR + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Customer_Card_Count + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Date_Created + TEXT_TYPE + COMMA_SEP +
                    MerchantLoyaltyInformation.COLUMN_NAME_Date_Modified + TEXT_TYPE +
                    " )";
    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MerchantLoyaltyInformation.TABLE_NAME;

    public static abstract class MerchantLoyaltyInformation implements BaseColumns {
        public static final String TABLE_NAME = "MerchantLoyaltyInformation";
        public static final String COLUMN_NAME_Loyalty_ID = "LoyaltyId";
        public static final String COLUMN_NAME_Title = "Title";
        public static final String COLUMN_NAME_Volume = "Volume";
        public static final String COLUMN_NAME_Date_Expiration = "DateExpiration";
        public static final String COLUMN_NAME_Card_Price = "CardPrice";
        public static final String COLUMN_NAME_Loyalty_Card_Image = "LoyaltyCardImage";
        public static final String COLUMN_NAME_Loyalty_Card_QR = "LoyaltyCardQR";
        public static final String COLUMN_NAME_Customer_Card_Count = "CustomerCardCount";
        public static final String COLUMN_NAME_Date_Created = "DateCreated";
        public static final String COLUMN_NAME_Date_Modified = "DateModified";
    }
}
