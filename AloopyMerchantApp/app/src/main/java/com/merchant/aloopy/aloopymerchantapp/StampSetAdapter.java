package com.merchant.aloopy.aloopymerchantapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchant.aloopy.aloopydatabase.MerchantStampInfoContract;

import java.util.ArrayList;

/**
 * Created by imbisibol on 9/21/2015.
 */
public class StampSetAdapter extends ArrayAdapter<MerchantStampInfoContract> {
    Context context;
    int layoutResourceId;
    ArrayList<MerchantStampInfoContract> data = new ArrayList<MerchantStampInfoContract>();

    public StampSetAdapter(Context context, int layoutResourceId,
                           ArrayList<MerchantStampInfoContract> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public MerchantStampInfoContract getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StampItem holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StampItem();
            holder.StampSetId = (TextView) row.findViewById(R.id.txtStampSetId);
            holder.StampSetTitle = (TextView) row.findViewById(R.id.txtStampSetTitle);
            holder.StampMerchantLogo = (ImageView) row.findViewById(R.id.imgMerchantLogo);
            holder.StampBody = (RelativeLayout) row.findViewById(R.id.frmBody);
            holder.StampQRCode = (ImageView) row.findViewById(R.id.imgQRCode);
            holder.Stamp1 = (ImageView) row.findViewById(R.id.imgStamp1);
            holder.Stamp2 = (ImageView) row.findViewById(R.id.imgStamp2);
            holder.Stamp3 = (ImageView) row.findViewById(R.id.imgStamp3);
            holder.Stamp4 = (ImageView) row.findViewById(R.id.imgStamp4);
            holder.Stamp5 = (ImageView) row.findViewById(R.id.imgStamp5);
            holder.Stamp6 = (ImageView) row.findViewById(R.id.imgStamp6);
            holder.Stamp7 = (ImageView) row.findViewById(R.id.imgStamp7);
            holder.Stamp8 = (ImageView) row.findViewById(R.id.imgStamp8);
            holder.Stamp9 = (ImageView) row.findViewById(R.id.imgStamp9);
            holder.Stamp10 = (ImageView) row.findViewById(R.id.imgStamp10);

            row.setTag(holder);
        } else {
            holder = (StampItem) row.getTag();
        }

        MerchantStampInfoContract item = data.get(position);
        holder.StampSetId.setText(item.StoreStampSetId);
        holder.StampSetTitle.setText(item.StampTitle);
        holder.StampSetTitle.setTextColor(Integer.parseInt(item.TextColor, 16) + 0xFF000000);

        if (item.MerchantLogoH != null && item.MerchantLogoH != "")
            Common.getImageLoader(null).displayImage(item.MerchantLogoH, holder.StampMerchantLogo);
        if (item.QRCode != null && item.QRCode != "")
            Common.getImageLoader(null).displayImage(item.QRCode, holder.StampQRCode);
        if (item.BackgroundColor != null && item.BackgroundColor != "")
            holder.StampBody.setBackgroundColor(Integer.parseInt(item.BackgroundColor, 16) + 0xFF000000);

        if (item.StampIcon != null && item.StampIcon != "") {

            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp1);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp2);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp3);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp4);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp5);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp6);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp7);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp8);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp9);
            Common.getImageLoader(null).displayImage(item.StampIcon, holder.Stamp10);
        }

        if(item.StampVolume <= 8) {
            holder.Stamp10.setVisibility(View.GONE);
            holder.Stamp9.setVisibility(View.GONE);
        }
        if(item.StampVolume <= 5) {
            holder.Stamp8.setVisibility(View.GONE);
            holder.Stamp7.setVisibility(View.GONE);
            holder.Stamp6.setVisibility(View.GONE);
        }
        if(item.StampVolume <= 3) {
            holder.Stamp5.setVisibility(View.GONE);
            holder.Stamp4.setVisibility(View.GONE);
        }


        return row;

    }

    static class StampItem
    {
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
