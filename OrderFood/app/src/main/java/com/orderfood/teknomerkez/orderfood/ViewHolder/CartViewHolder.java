package com.orderfood.teknomerkez.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.orderfood.teknomerkez.orderfood.Common.Common;
import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txt_cartName, txt_price;
    private ItemClickListener itemClickListener;
    public ElegantNumberButton cart_number_button;
    public ImageView cart_image,delete_icon;
    public RelativeLayout view_backround;
    public LinearLayout view_foreground;

    public void setTxt_cartName(TextView txt_cartName) {
        this.txt_cartName = txt_cartName;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cartName = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        cart_number_button = itemView.findViewById(R.id.cart_number_button);
        cart_image = itemView.findViewById(R.id.cart_image);
        view_backround = itemView.findViewById(R.id.view_backround);
        delete_icon = itemView.findViewById(R.id.delete_icon);
        view_foreground = itemView.findViewById(R.id.view_foreground);

        itemView.setOnCreateContextMenuListener(this);
    }


    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
