package com.merchant.aloopy.aloopydatabase;

import android.provider.BaseColumns;

/**
 * Created by imbisibol on 9/21/2015.
 */
public class MerchantStampInfoContract {

    public String StoreId;
    public String StampSetId;
    public String StoreStampSetId;
    public String StampTitle;
    public int StampVolume;
    public String BackgroundColor;
    public String BackgroundColor2;
    public String TextColor;
    public String MerchantLogoH;
    public String MerchantLogoV;
    public String StampIcon;
    public String RewardImage;
    public String QRCode;
    public String DateCreated;
    public String DateModified;
    public String StartDate;
    public String EndDate;
    public int CustomerStampCount;
    public int CustomerStampCompletedCount;

    public MerchantStampInfoContract()
    {

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MerchantStampInformation.TABLE_NAME + " (" +
                    MerchantStampInformation._ID + " INTEGER PRIMARY KEY," +
                    MerchantStampInformation.COLUMN_NAME_Stampset_ID + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Store_ID + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Store_StampSet_ID + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Stamp_Title + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Stamp_Volume + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Background_Color + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Background_Color2 + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Text_Color + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Merchant_Logo_H + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Merchant_Logo_V + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Stamp_Icon + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Reward_Image + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_QR_Code + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Date_Created + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Date_Modified + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Start_Date + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Customer_Stamp_Count + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_Customer_Stamp_Completed_Count + TEXT_TYPE + COMMA_SEP +
                    MerchantStampInformation.COLUMN_NAME_End_Date + TEXT_TYPE +
                    " )";
    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MerchantStampInformation.TABLE_NAME;

    public static abstract class MerchantStampInformation implements BaseColumns {
        public static final String TABLE_NAME = "MerchantStampInformation";
        public static final String COLUMN_NAME_Stampset_ID = "StampSetID";
        public static final String COLUMN_NAME_Store_ID = "StoreID";
        public static final String COLUMN_NAME_Store_StampSet_ID = "StoreStampSetId";
        public static final String COLUMN_NAME_Stamp_Title = "StampTitle";
        public static final String COLUMN_NAME_Stamp_Volume = "StampVolume";
        public static final String COLUMN_NAME_Background_Color = "BackgroundColor";
        public static final String COLUMN_NAME_Background_Color2 = "BackgroundColor2";
        public static final String COLUMN_NAME_Text_Color = "TextColor";
        public static final String COLUMN_NAME_Merchant_Logo_H = "MerchantLogoH";
        public static final String COLUMN_NAME_Merchant_Logo_V = "MerchantLogoV";
        public static final String COLUMN_NAME_Stamp_Icon = "StampIcon";
        public static final String COLUMN_NAME_Reward_Image = "RewardImage";
        public static final String COLUMN_NAME_QR_Code = "QRCode";
        public static final String COLUMN_NAME_Date_Created = "DateCreated";
        public static final String COLUMN_NAME_Date_Modified = "DateModified";
        public static final String COLUMN_NAME_Start_Date = "StartDate";
        public static final String COLUMN_NAME_End_Date = "EndDate";
        public static final String COLUMN_NAME_Customer_Stamp_Count = "CustomerStampCount";
        public static final String COLUMN_NAME_Customer_Stamp_Completed_Count = "CustomerStampCompletedCount";

    }
}
