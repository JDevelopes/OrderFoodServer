package com.orderfood.teknomerkez.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public KenBurnsView food_image;
    public ImageView food_favorite,food_share,quick_cart;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name = itemView.findViewById(R.id.food_name);
        food_image = itemView.findViewById(R.id.food_image);
        food_favorite = itemView.findViewById(R.id.fav);
        food_share = itemView.findViewById(R.id.btnShare);
        quick_cart = itemView.findViewById(R.id.quickCart);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }


}
