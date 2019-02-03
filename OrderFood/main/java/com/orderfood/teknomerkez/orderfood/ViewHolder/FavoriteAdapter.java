package com.orderfood.teknomerkez.orderfood.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orderfood.teknomerkez.orderfood.Database.Database;
import com.orderfood.teknomerkez.orderfood.FoodDetail;
import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.Model.Favorites;
import com.orderfood.teknomerkez.orderfood.Model.Order;
import com.orderfood.teknomerkez.orderfood.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private List<Favorites> myFavoriteList;
    private Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public FavoriteAdapter(List<Favorites> myFavoriteList, Context context) {
        this.myFavoriteList = myFavoriteList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorites_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder viewHolder, final int position) {
        viewHolder.favorite_food_name.setText(myFavoriteList.get(position).getFoodName());
        Picasso p = Picasso.with(context);
        p.load(myFavoriteList.get(position).getFoodImage()).into(viewHolder.favorite_food_image);

        viewHolder.favorite_quickCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExist = new Database(context).checkFoodExist(myFavoriteList.get(position).getFoodId(), user.getUid());
                if (isExist) {
                    new Database(context).increaseCart(user.getUid(), myFavoriteList.get(position).getFoodId());
                } else {
                    new Database(context).addToCart(new Order(
                            user.getUid(),
                            myFavoriteList.get(position).getFoodId(),
                            myFavoriteList.get(position).getFoodName(),
                            "1",
                            myFavoriteList.get(position).getFoodPrice(),
                            myFavoriteList.get(position).getFoodDiscount(),
                            myFavoriteList.get(position).getFoodImage()
                    ));
                }
                String message = "Added to Cart: " + myFavoriteList.get(position).getFoodName() + "\n" + "Price: "
                        + myFavoriteList.get(position).getFoodPrice() + "$";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
        final Favorites local = myFavoriteList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(context, "" + local.getFoodName(), Toast.LENGTH_SHORT).show();
                //go food
                String foodID=myFavoriteList.get(position).getFoodId();
                String newFoodId = foodID.substring(2);
                Intent foodDetail = new Intent(context, FoodDetail.class);
                foodDetail.putExtra("FoodId", newFoodId);
                //foodDetail.putExtra("FoodId", String.valueOf(position));
                foodDetail.putExtra("CategoryID", myFavoriteList.get(position).getFoodMenuId());
                foodDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myFavoriteList.size();
    }

    public void removeItem(int position) {
        myFavoriteList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favorites item, int position) {
        myFavoriteList.add(position, item);
        notifyItemInserted(position);
    }

    public Favorites getItem (int position){
        return myFavoriteList.get(position);
    }
}
