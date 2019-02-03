package com.orderfood.teknomerkez.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.R;

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public KenBurnsView favorite_food_image;
    public TextView favorite_food_name;
    private ItemClickListener itemClickListener;
    public ImageView favorite_quickCart;
    public RelativeLayout view_favorite_backround,view_favorite_foreground;

    public FavoriteViewHolder(View itemView) {
        super(itemView);

        favorite_food_image = itemView.findViewById(R.id.favorite_food_image);
        favorite_food_name = itemView.findViewById(R.id.favorite_food_name);
        favorite_quickCart = itemView.findViewById(R.id.favorite_quickCart);
        view_favorite_foreground = itemView.findViewById(R.id.view_favorite_foreground);
        view_favorite_backround = itemView.findViewById(R.id.view_favorite_backround);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
