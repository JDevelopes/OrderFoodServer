package com.orderfood.teknomerkez.orderfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.orderfood.teknomerkez.orderfood.Common.Common;
import com.orderfood.teknomerkez.orderfood.Database.Database;
import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.Model.Favorites;
import com.orderfood.teknomerkez.orderfood.Model.Food;
import com.orderfood.teknomerkez.orderfood.Model.Order;
import com.orderfood.teknomerkez.orderfood.ViewHolder.FoodViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String categoryID = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    private String getCategoryId = "CategoryId";
    SwipeRefreshLayout swipeFoodRefreshLayout;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //favorites
    Database localDB;

    //Facebook Share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create photo from Bitmap

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        recyclerView = findViewById(R.id.recycler_food);
        swipeFoodRefreshLayout = findViewById(R.id.swipe_food_layout);

        //database
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Food").child(categoryID);
        myAdapter(categoryID);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_slide_from_right);
        recyclerView.setLayoutAnimation(controller);


        // init facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Local DB
        localDB = new Database(this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null)
            categoryID = getIntent().getStringExtra(getCategoryId);      //get intent (first initialize)
        if (!categoryID.isEmpty() && categoryID != null) {
            if (Common.isConnectedtoInternet(getBaseContext())) {
                loadListFood(categoryID);
            } else {
                Toast.makeText(getBaseContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }


        swipeRefreshEvents();
    }

    private void swipeRefreshEvents() {
        swipeFoodRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeFoodRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    categoryID = getIntent().getStringExtra(getCategoryId);      //get intent (first initialize)
                if (!categoryID.isEmpty() && categoryID != null) {
                    if (Common.isConnectedtoInternet(getBaseContext())) {
                        loadListFood(categoryID);
                    } else {
                        Toast.makeText(getBaseContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                swipeFoodRefreshLayout.setRefreshing(false);
            }
        });
        swipeFoodRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null)
                    categoryID = getIntent().getStringExtra(getCategoryId);      //get intent (first initialize)
                if (!categoryID.isEmpty() && categoryID != null) {
                    if (Common.isConnectedtoInternet(getBaseContext())) {
                        loadListFood(categoryID);
                    } else {
                        Toast.makeText(getBaseContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadListFood(String categoryID) {
        myAdapter(categoryID);
        Log.d("TAG", "" + adapter.getItemCount());
        recyclerView.setAdapter(adapter);
        swipeFoodRefreshLayout.setRefreshing(false);
        //animation
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void myAdapter(String categoryID) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Food")
                .child(categoryID);

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(query, Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                viewHolder.food_name.setText(model.getName());
                Log.d("TAG", model.getName());
                Picasso p = Picasso.with(getBaseContext());
                p.load(model.getImage()).into(viewHolder.food_image);

                //add favorites
                addFavorites(viewHolder, position, model);

                //Click to share
                clickToShare(viewHolder, model);

                //quick cart
                quickCartClicks(viewHolder, position, model);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();
                        //go food
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                       //foodDetail.putExtra("FoodId", adapter.getItem(position).getFoodId());
                        //foodDetail.putExtra("FoodId", String.valueOf(position));
                        foodDetail.putExtra("CategoryID", adapter.getItem(position).getMenuId());
                        foodDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(foodDetail);
                    }
                });
            }
        };
        adapter.startListening();
    }

    private void quickCartClicks(@NonNull FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExist = new Database(getBaseContext()).checkFoodExist(model.getMenuId()+adapter.getRef(position).getKey(), user.getUid());
                if (isExist) {
                    new Database(getBaseContext()).increaseCart(user.getUid(), model.getMenuId()+adapter.getRef(position).getKey());
                } else {
                    new Database(getBaseContext()).addToCart(new Order(
                            user.getUid(),
                            model.getMenuId()+adapter.getRef(position).getKey(),
                            model.getName(),
                            "1",
                            model.getPrice(),
                            model.getDiscount(),
                            model.getImage()
                    ));
                }
                String message = "Added to Cart: " + model.getName() + "\n" + "Price: " + model.getPrice() + "$";
                Toast.makeText(FoodList.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickToShare(@NonNull FoodViewHolder viewHolder, @NonNull final Food model) {
        viewHolder.food_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso sharePhoto = Picasso.with(getBaseContext());
                sharePhoto.load(model.getImage()).into(target);
            }
        });
    }

    private void addFavorites(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
        String foodId = model.getMenuId() + adapter.getRef(position).getKey();
        if (localDB.isFavorite(foodId, user.getUid())) {
            viewHolder.food_favorite.setImageResource(R.drawable.favorite);
        }

        //click to change state of favorite
        viewHolder.food_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorites favorites = new Favorites();
                String foodId = model.getMenuId() + adapter.getRef(position).getKey();
                favorites.setFoodId(foodId);
                favorites.setFoodName(model.getName());
                favorites.setFoodDescription(model.getDescription());
                favorites.setFoodDiscount(model.getDiscount());
                favorites.setFoodImage(model.getImage());
                favorites.setFoodMenuId(model.getMenuId());
                favorites.setFoodUserId(user.getUid());
                favorites.setFoodPrice(model.getPrice());

                if (!localDB.isFavorite(foodId, user.getUid())) {
                    localDB.addToFavorites(favorites);
                    viewHolder.food_favorite.setImageResource(R.drawable.favorite);
                    Toast.makeText(FoodList.this, "" + model.getName() + " was added to Favorites",
                            Toast.LENGTH_SHORT).show();
                } else {
                    localDB.removeFavorites(foodId, user.getUid());
                    viewHolder.food_favorite.setImageResource(R.drawable.favorite_empty);
                    Toast.makeText(FoodList.this, "" + model.getName() + " was removed from Favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}
