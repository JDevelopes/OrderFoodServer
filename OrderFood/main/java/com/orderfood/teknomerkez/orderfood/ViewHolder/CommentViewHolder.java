package com.orderfood.teknomerkez.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.orderfood.teknomerkez.orderfood.R;

public class CommentViewHolder extends RecyclerView.ViewHolder{

    public TextView comment_user_name, comment;
    public RatingBar ratingComment;
    public CommentViewHolder(View itemView) {
        super(itemView);
        comment = itemView.findViewById(R.id.comment);
        comment_user_name = itemView.findViewById(R.id.comment_user_name);
        ratingComment = itemView.findViewById(R.id.ratingComment);
    }
}
