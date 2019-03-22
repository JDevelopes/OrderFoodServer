package com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.orderfoodserver.teknomerkez.orderfoodserver.Common.Common;
import com.orderfoodserver.teknomerkez.orderfoodserver.Interface.ItemClickListener;
import com.orderfoodserver.teknomerkez.orderfoodserver.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView txtMenuName;
    public KenBurnsView menu_image;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);
        txtMenuName = itemView.findViewById(R.id.menu_name);
        menu_image = itemView.findViewById(R.id.menu_image);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(Common.SELECT_THE_ACTİON);
        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        menu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
