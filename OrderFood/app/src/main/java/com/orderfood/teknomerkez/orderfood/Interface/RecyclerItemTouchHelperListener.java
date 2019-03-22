package com.orderfood.teknomerkez.orderfood.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder,int duration, int position);
}
