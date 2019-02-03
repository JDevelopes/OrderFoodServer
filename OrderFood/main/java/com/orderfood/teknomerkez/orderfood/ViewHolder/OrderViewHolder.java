package com.orderfood.teknomerkez.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderID,txtOrderStatus,txtOrderPhone,txtOrderAddress;
    private ItemClickListener itemClikcListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderID = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);
    }

    public void setItemClikcListener(ItemClickListener itemClikcListener) {
        this.itemClikcListener = itemClikcListener;
    }

    @Override
    public void onClick(View view) {
    itemClikcListener.onClick(view,getAdapterPosition(),false);
    }
}
