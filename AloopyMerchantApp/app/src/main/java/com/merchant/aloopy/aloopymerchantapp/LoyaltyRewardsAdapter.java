package com.merchant.aloopy.aloopymerchantapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.merchant.aloopy.aloopydatabase.LoyaltyRewardsContract;


import java.util.ArrayList;

/**
 * Created by imbisibol on 10/6/2015.
 */
public class LoyaltyRewardsAdapter extends ArrayAdapter<LoyaltyRewardsContract> {

    Context context;
    int layoutResourceId;
    ArrayList<LoyaltyRewardsContract> data = new ArrayList<LoyaltyRewardsContract>();

    public LoyaltyRewardsAdapter(Context context, int layoutResourceId,
                                 ArrayList<LoyaltyRewardsContract> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public LoyaltyRewardsContract getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LoyaltyRewardItem holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new LoyaltyRewardItem();
            holder.RewardID = (TextView) row.findViewById(R.id.lblRewardId);
            holder.RewardName = (TextView) row.findViewById(R.id.lblRewardName);
            holder.DateCreated = (TextView) row.findViewById(R.id.lblDateAdded);
            holder.PointCost = (TextView) row.findViewById(R.id.lblPointCost);
            holder.RewardImage = (ImageView) row.findViewById(R.id.imgCard);
            holder.RewardQR = (ImageView) row.findViewById(R.id.imgQRCode);

            row.setTag(holder);
        } else {
            holder = (LoyaltyRewardItem) row.getTag();
        }

        LoyaltyRewardsContract item = data.get(position);
        holder.RewardID.setText(item.RewardID);
        holder.RewardName.setText(item.RewardName);
        holder.DateCreated.setText(item.DateCreated.substring(0, item.DateCreated.indexOf("T")));
        holder.PointCost.setText(String.valueOf(item.PointCost) + "pts");

        if (item.RewardImage != null && item.RewardImage != "")
            Common.getImageLoader(null).displayImage(item.RewardImage, holder.RewardImage);
        if (item.RewardQR != null && item.RewardQR != "")
            Common.getImageLoader(null).displayImage(item.RewardQR, holder.RewardQR);


        return row;
    }

    static class LoyaltyRewardItem {

        public TextView RewardID;
        public TextView RewardName;
        public TextView PointCost;
        public ImageView RewardImage;
        public ImageView RewardQR;
        public TextView DateCreated;

    }

}
