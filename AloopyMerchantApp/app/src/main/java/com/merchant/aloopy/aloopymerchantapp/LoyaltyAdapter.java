package com.merchant.aloopy.aloopymerchantapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.merchant.aloopy.aloopydatabase.MerchantLoyaltyContract;

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
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new LoyaltyItem();
            holder.LoyaltyId = (TextView) row.findViewById(R.id.lblLoyaltyId);
            holder.Title = (TextView) row.findViewById(R.id.lblLoyaltyTitle);
            holder.Volume = (TextView) row.findViewById(R.id.lblVolume);
            holder.CardPrice = (TextView) row.findViewById(R.id.lblCardPrice);
            holder.DateExpiration = (TextView) row.findViewById(R.id.lblExpiryDate);
            holder.LoyaltyCardImage = (ImageView) row.findViewById(R.id.imgCard);
            holder.LoyaltyQRCode = (ImageView) row.findViewById(R.id.imgQRCode);

            row.setTag(holder);
        } else {
            holder = (LoyaltyItem) row.getTag();
        }

        MerchantLoyaltyContract item = data.get(position);
        holder.LoyaltyId.setText(item.LoyaltyId);
        holder.Title.setText(item.Title);
        holder.Volume.setText("AVAILABLE: " + String.valueOf(item.Volume));
        holder.DateExpiration.setText(item.DateExpiration.substring(0, item.DateExpiration.indexOf("T")));
        holder.CardPrice.setText(getContext().getString(R.string.Currency) + item.CardPrice);

        if (item.LoyaltyCardImage != null && item.LoyaltyCardImage != "")
            Common.getImageLoader(null).displayImage(item.LoyaltyCardImage, holder.LoyaltyCardImage);
        if (item.LoyaltyCardQR != null && item.LoyaltyCardQR != "")
            Common.getImageLoader(null).displayImage(item.LoyaltyCardQR, holder.LoyaltyQRCode);


        return row;
    }

    static class LoyaltyItem
    {
        public TextView LoyaltyId;
        public TextView Title;
        public TextView Volume;
        public TextView CardPrice;
        public TextView DateExpiration;
        public ImageView LoyaltyCardImage;
        public ImageView LoyaltyQRCode;

    }
}
