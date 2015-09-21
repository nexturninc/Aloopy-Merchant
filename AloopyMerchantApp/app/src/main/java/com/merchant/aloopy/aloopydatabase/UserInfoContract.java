package com.merchant.aloopy.aloopydatabase;

import android.provider.BaseColumns;

/**
 * Created by imbisibol on 9/18/2015.
 */
public class UserInfoContract {

    public UserInfoContract()
    {

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + UserInformation.TABLE_NAME + " (" +
                    UserInformation._ID + " INTEGER PRIMARY KEY," +
                    UserInformation.COLUMN_NAME_User_ID + TEXT_TYPE + COMMA_SEP +
                    UserInformation.COLUMN_NAME_User_Name + TEXT_TYPE + COMMA_SEP +
                    UserInformation.COLUMN_NAME_Display_Name + TEXT_TYPE + COMMA_SEP +
                    UserInformation.COLUMN_NAME_Email + TEXT_TYPE + COMMA_SEP +
                    UserInformation.COLUMN_NAME_Address + TEXT_TYPE + COMMA_SEP +
                    UserInformation.COLUMN_NAME_ContactNo + TEXT_TYPE + COMMA_SEP +
                    UserInformation.COLUMN_NAME_Date_Modified + DATE_TYPE +
                    " )";
    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + UserInformation.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static abstract class UserInformation implements BaseColumns {
        public static final String TABLE_NAME = "UserInformation";
        public static final String COLUMN_NAME_User_ID = "UserId";
        public static final String COLUMN_NAME_User_Name = "UserName";
        public static final String COLUMN_NAME_Display_Name = "DisplayName";
        public static final String COLUMN_NAME_Email = "EmailAddress";
        public static final String COLUMN_NAME_Address = "Address";
        public static final String COLUMN_NAME_ContactNo = "ContactNo";
        public static final String COLUMN_NAME_Date_Modified = "DateModified";


    }
}
