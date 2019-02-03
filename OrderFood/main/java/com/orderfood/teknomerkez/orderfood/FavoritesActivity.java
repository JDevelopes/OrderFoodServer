package com.orderfood.teknomerkez.orderfood;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orderfood.teknomerkez.orderfood.Database.Database;
import com.orderfood.teknomerkez.orderfood.Helper.RecyclerItemTouchHelper;
import com.orderfood.teknomerkez.orderfood.Interface.RecyclerItemTouchHelperListener;
import com.orderfood.teknomerkez.orderfood.Model.Favorites;
import com.orderfood.teknomerkez.orderfood.ViewHolder.FavoriteAdapter;
import com.orderfood.teknomerkez.orderfood.ViewHolder.FavoriteViewHolder;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recycler_favorite;
    RecyclerView.LayoutManager favorite_layoutmanager;

    FavoriteAdapter adapter;
    RelativeLayout rootLayout_favorite;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rootLayout_favorite = findViewById(R.id.rootLayout_favorite);
        recycler_favorite = findViewById(R.id.recycler_favorites);
        recycler_favorite.setHasFixedSize(true);
        favorite_layoutmanager = new LinearLayoutManager(this);
        recycler_favorite.setLayoutManager(favorite_layoutmanager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_favorite.getContext(),
                R.anim.layout_slide_from_right);
        recycler_favorite.setLayoutAnimation(controller);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recycler_favorite);

        loadFavorites();
    }

    private void loadFavorites() {

        adapter = new FavoriteAdapter(new Database(this).getAllFavorites(user.getUid()),this);
        recycler_favorite.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int duration, int position) {
            if (viewHolder instanceof FavoriteViewHolder){
                String name = ((FavoriteAdapter) recycler_favorite.getAdapter()).getItem(position).getFoodName();
                final Favorites deleteItem = ((FavoriteAdapter) recycler_favorite.getAdapter()).getItem(viewHolder.getAdapterPosition());
                final int deleteIndex = viewHolder.getAdapterPosition();
                adapter.removeItem(viewHolder.getAdapterPosition());
                new Database(getBaseContext()).removeFavorites(deleteItem.getFoodId(),user.getUid());

                Snackbar snackbar = Snackbar.make(rootLayout_favorite, name + " removed from cart!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.restoreItem(deleteItem,deleteIndex);
                        new Database(getBaseContext()).addToFavorites(deleteItem);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FavoritesActivity.this,Home.class);
        startActivity(intent);
        finish();
    }
}
