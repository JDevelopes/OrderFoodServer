package com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.orderfoodserver.teknomerkez.orderfoodserver.Common.Common;
import com.orderfoodserver.teknomerkez.orderfoodserver.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener {

    public TextView banner_food_name;
    public KenBurnsView banner_food_image;

    public BannerViewHolder(View itemView) {
        super(itemView);
        banner_food_name = itemView.findViewById(R.id.banner_food_name);
        banner_food_image = itemView.findViewById(R.id.banner_food_image);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(Common.SELECT_THE_ACTİON);
        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        menu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
