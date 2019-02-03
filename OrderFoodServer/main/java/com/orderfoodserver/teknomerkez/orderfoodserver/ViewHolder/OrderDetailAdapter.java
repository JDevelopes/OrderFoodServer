package com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Order;
import com.orderfoodserver.teknomerkez.orderfoodserver.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView product_name,product_price,product_discount,product_quantity;

    public MyViewHolder(View itemView) {
        super(itemView);

        product_name = itemView.findViewById(R.id.product_name);
        product_price = itemView.findViewById(R.id.product_price);
        product_discount = itemView.findViewById(R.id.product_discount);
        product_quantity = itemView.findViewById(R.id.product_quantity);
    }

}
public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_layout,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = myOrders.get(position);
        holder.product_name.setText(String.format("Name : %s", order.getProductName() ));
        holder.product_discount.setText(String.format("Discount : %s", order.getDiscount()+"$" ));
        holder.product_price.setText(String.format("Price : %s", order.getPrice() + "$"));
        holder.product_quantity.setText(String.format("Quantity : %s", order.getQuantity() ));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
