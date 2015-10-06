package com.merchant.aloopy.aloopydatabase;

import android.provider.BaseColumns;

/**
 * Created by imbisibol on 10/6/2015.
 */
public class LoyaltyRewardsContract {

    public String RewardID;
    public String LoyaltyID;
    public String RewardName;
    public Integer PointCost;
    public String RewardImage;
    public String RewardQR;
    public String DateCreated;
    public String DateModified;

    public LoyaltyRewardsContract(){

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + LoyaltyRewardsInformation.TABLE_NAME + " (" +
                    LoyaltyRewardsInformation._ID + " INTEGER PRIMARY KEY," +
                    LoyaltyRewardsInformation.COLUMN_NAME_Reward_ID + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Loyalty_ID + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Reward_Name + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Point_Cost + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Reward_Image + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Reward_QR + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Date_Created + TEXT_TYPE + COMMA_SEP +
                    LoyaltyRewardsInformation.COLUMN_NAME_Date_Modified + TEXT_TYPE +
                    " )";
    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + LoyaltyRewardsInformation.TABLE_NAME;

    public static abstract class LoyaltyRewardsInformation implements BaseColumns {
        public static final String TABLE_NAME = "LoyaltyRewardsInformation";
        public static final String COLUMN_NAME_Reward_ID = "RewardID";
        public static final String COLUMN_NAME_Loyalty_ID = "LoyaltyID";
        public static final String COLUMN_NAME_Reward_Name = "RewardName";
        public static final String COLUMN_NAME_Point_Cost = "PointCost";
        public static final String COLUMN_NAME_Reward_Image = "RewardImage";
        public static final String COLUMN_NAME_Reward_QR = "RewardQR";
        public static final String COLUMN_NAME_Date_Created = "DateCreated";
        public static final String COLUMN_NAME_Date_Modified = "DateModified";
    }
}
