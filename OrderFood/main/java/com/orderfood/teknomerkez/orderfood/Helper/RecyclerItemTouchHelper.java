package com.orderfood.teknomerkez.orderfood.Helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.orderfood.teknomerkez.orderfood.Interface.RecyclerItemTouchHelperListener;
import com.orderfood.teknomerkez.orderfood.ViewHolder.CartViewHolder;
import com.orderfood.teknomerkez.orderfood.ViewHolder.FavoriteViewHolder;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof CartViewHolder) {
            View foreGroundView = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foreGroundView);
        } else if (viewHolder instanceof FavoriteViewHolder) {
            View favorite_foreground = ((FavoriteViewHolder) viewHolder).view_favorite_foreground;
            getDefaultUIUtil().clearView(favorite_foreground);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof CartViewHolder) {
            View foreGroundView = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof FavoriteViewHolder) {
            View favorite_foreground =((FavoriteViewHolder) viewHolder).view_favorite_foreground;
            getDefaultUIUtil().onDraw(c,recyclerView,favorite_foreground,dX,dY,actionState,isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            if (viewHolder instanceof CartViewHolder){
                View foreGroundView = ((CartViewHolder) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foreGroundView);
            }else if (viewHolder instanceof FavoriteViewHolder){
                View favorite_foreground = ((FavoriteViewHolder) viewHolder).view_favorite_foreground;
                getDefaultUIUtil().onSelected(favorite_foreground);
            }
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       if (viewHolder instanceof CartViewHolder){
           View foreGroundView = ((CartViewHolder) viewHolder).view_foreground;
           getDefaultUIUtil().onDrawOver(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
       }else if (viewHolder instanceof FavoriteViewHolder){
           View favorite_foreground = ((FavoriteViewHolder) viewHolder).view_favorite_foreground;
           getDefaultUIUtil().onDrawOver(c, recyclerView, favorite_foreground, dX, dY, actionState, isCurrentlyActive);
       }
    }
}
