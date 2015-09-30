package com.merchant.aloopy.aloopymerchantapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchant.aloopy.aloopydatabase.MerchantLoyaltyContract;
import com.merchant.aloopy.aloopydatabase.MerchantStampInfoContract;

import java.util.ArrayList;

/**
 * Created by imbisibol on 9/30/2015.
 */
public class LoyaltyAdapter extends ArrayAdapter<MerchantLoyaltyContract> {
    Context context;
    int layoutResourceId;
    ArrayList<MerchantLoyaltyContract> data = new ArrayList<MerchantLoyaltyContract>();

    public LoyaltyAdapter(Context context, int layoutResourceId,
                           ArrayList<MerchantLoyaltyContract> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public MerchantLoyaltyContract getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LoyaltyItem holder = null;

        if (row == null) {

        }

        return row;
    }

    static class LoyaltyItem
    {
        public TextView StampCount;
        public TextView StampLabel;
        public TextView StampSetId;
        public TextView StampSetTitle;
        public ImageView StampMerchantLogo;
        public ImageView StampQRCode;
        public RelativeLayout StampBody;

        public ImageView Stamp1;
        public ImageView Stamp2;
        public ImageView Stamp3;
        public ImageView Stamp4;
        public ImageView Stamp5;
        public ImageView Stamp6;
        public ImageView Stamp7;
        public ImageView Stamp8;
        public ImageView Stamp9;
        public ImageView Stamp10;
    }
}
