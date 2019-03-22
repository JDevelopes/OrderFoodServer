package com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.orderfoodserver.teknomerkez.orderfoodserver.Interface.ItemClickListener;
import com.orderfoodserver.teknomerkez.orderfoodserver.R;

import info.hoang8f.widget.FButton;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView txtOrderID, txtOrderStatus, txtOrderPhone, txtOrderAddress,txtOrderPaymentMethod;
    public FButton btnEdit, btnRemove, btnDetail, btnDirection;
    private ItemClickListener itemClikcListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderID = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnDetail = itemView.findViewById(R.id.btnDetail);
        btnDirection = itemView.findViewById(R.id.btnDirection);
    }
}

