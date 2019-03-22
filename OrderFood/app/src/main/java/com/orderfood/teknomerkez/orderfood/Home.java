package com.orderfood.teknomerkez.orderfood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orderfood.teknomerkez.orderfood.Common.Common;
import com.orderfood.teknomerkez.orderfood.Database.Database;
import com.orderfood.teknomerkez.orderfood.Interface.ItemClickListener;
import com.orderfood.teknomerkez.orderfood.Model.Address;
import com.orderfood.teknomerkez.orderfood.Model.Banner;
import com.orderfood.teknomerkez.orderfood.Model.Category;
import com.orderfood.teknomerkez.orderfood.Model.Token;
import com.orderfood.teknomerkez.orderfood.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;
    RecyclerView recycler_menu;
    Toolbar toolbar;
    RecyclerView.LayoutManager layoutManager;
    CounterFab fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    private String getCategoryId = "CategoryId";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SwipeRefreshLayout swipeRefreshLayout;
    Place shipToHomeAddress, shipToWorkAddress;
    FirebaseDatabase db;

    //slider
    HashMap<String, String> image_list;
    SliderLayout mslider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        myAdapter();

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.animation_layout);
        recycler_menu.setLayoutAnimation(controller);

        toolbar.setTitle(R.string.Menu);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Cart.class);
                startActivity(intent);
            }
        });

        // /load menu
        loadMenuContent();
        fab.setCount(new Database(this).getCountCart(user.getUid()));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //set Name for users
        if (user != null)
            txtFullName.setText(user.getDisplayName());


        if (Common.isConnectedtoInternet(getBaseContext())) {
            loadMenu();
        } else {
            Toast.makeText(getBaseContext(), "PLEASE CHECK YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
        }
        //Register
        updateToken(FirebaseInstanceId.getInstance().getToken());

        swipeRefreshEvents();
        setupSlider();

    }

    private void setupSlider() {
        mslider = findViewById(R.id.slider);
        image_list = new HashMap<>();
        final DatabaseReference ref_banner = db.getReference("Banner");

        ref_banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Banner banner = postSnapShot.getValue(Banner.class);
                    //gonna concat string name and id like
                    //PİZZA_01 => and gonna use PİZZA show description , 01 for food id to click
                    image_list.put(banner.getName() + "@@@" + banner.getCategoryID() + "@@@" + banner.getFoodId(), banner.getImage());
                }
                for (String key : image_list.keySet()) {
                    String[] keySplit = key.split("@@@");
                    String nameOfFood = keySplit[0];
                    String idOfCategory = keySplit[1];
                    String idOfFood = keySplit[2];


                    //Create Slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this, FoodDetail.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    //add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId", idOfFood);
                    textSliderView.getBundle().putString("CategoryID", idOfCategory);
                    Picasso picasso = Picasso.with(getBaseContext());
                    textSliderView.setPicasso(picasso);
                    mslider.addSlider(textSliderView);

                    //remove event after finish
                    ref_banner.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mslider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mslider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mslider.setCustomAnimation(new DescriptionAnimation());
        mslider.setDuration(3000);

    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private void swipeRefreshEvents() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedtoInternet(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(getBaseContext(), "PLEASE CHECK YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedtoInternet(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(getBaseContext(), "PLEASE CHECK YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateToken(String token) {
        db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("Tokens");
        Token data = new Token(token, false); // false because this token send from Client app
        reference.child(user.getUid()).setValue(data);
    }

    private void loadMenuContent() {
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View viewheaderName = navigationView.getHeaderView(0);
        txtFullName = viewheaderName.findViewById(R.id.txtFullName);
        recycler_menu = findViewById(R.id.recycler_menu);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
    }

    private void loadMenu() {
        //myAdapter();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    private void myAdapter() {
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("Category");
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso p = Picasso.with(getBaseContext());
                p.load(model.image).into(viewHolder.menu_image);


                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                        Intent food_intent = new Intent(Home.this, FoodList.class);
                        //Because categoryId is key , so we just get key of this item
                        food_intent.putExtra(getCategoryId, adapter.getRef(position).getKey());
                        food_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(food_intent);
                    }
                });
            }
        };
      adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        mslider.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(user.getUid()));
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.homeAddress_autocomplete_fragment));
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.workAddress_autocomplete_fragment));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_menu) {
            Intent homepage = new Intent(getBaseContext(), Home.class);
            startActivity(homepage);
            finish();
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
            finish();
        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
            finish();
        } else if (id == R.id.nav_fav) {
            Intent orderIntent = new Intent(Home.this, FavoritesActivity.class);
            startActivity(orderIntent);
            finish();
        } else if (id == R.id.nav_sub) {
          showSettingDialog();
        } else if (id == R.id.nav_sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(Home.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(Home.this, "Sign Out is Completed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (id == R.id.nav_update_address) {
            showHomeandWorkAddress();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Settings");
        LayoutInflater inflater = LayoutInflater.from(this);
        View setting = inflater.inflate(R.layout.setting_layout, null);
        final CheckBox subscribe = setting.findViewById(R.id.sub_news);
        Paper.init(this);
        String isSubscribe = Paper.book().read("sub_new");

        if(isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false")){
            subscribe.setChecked(false);
        }else{
            subscribe.setChecked(true);
        }
        dialog.setView(setting);

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (subscribe.isChecked()){
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new" , "true");
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                    Paper.book().write("sub_new" , "false");
                }
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void showHomeandWorkAddress() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update Address");
        dialog.setMessage("Update Your Work and Home Address");
        LayoutInflater inflater = LayoutInflater.from(this);
            try {
                View layout_address = inflater.inflate(R.layout.home_address_layout, null);
                dialog.setView(layout_address);

                // add ediittexts to alert dialog as a fragment
                PlaceAutocompleteFragment edtHomeAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.homeAddress_autocomplete_fragment);
                PlaceAutocompleteFragment edtWorkAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.workAddress_autocomplete_fragment);
                edtHomeAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
                edtWorkAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

                //edit hint and textsize of fragments
                ((EditText) edtHomeAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Home Address");
                ((EditText) edtHomeAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
                ((EditText) edtWorkAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Work Address");
                ((EditText) edtWorkAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);

                //get address
                edtHomeAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        shipToHomeAddress = place;
                    }

                    @Override
                    public void onError(Status status) {
                        Log.d("ERROR", status.getStatusMessage());
                    }
                });
                edtWorkAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        shipToWorkAddress = place;
                    }

                    @Override
                    public void onError(Status status) {
                        Log.d("ERROR", status.getStatusMessage());
                    }
                });


                dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Address address = new Address();
                        address.setDisplayName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        try {
                            address.setWorkAdress(shipToWorkAddress.getAddress().toString());
                            address.setHomeAddress(shipToHomeAddress.getAddress().toString());
                        } catch (Exception e) {
                            Toast.makeText(Home.this, "Please type both of your addresses if you have", Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference ref = db.getReference("Address");
                        ref.child(userId).setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "Update Successfull !", Toast.LENGTH_SHORT).show();
                            }
                        });
                        getFragmentManager().beginTransaction().remove(getFragmentManager()
                                .findFragmentById(R.id.homeAddress_autocomplete_fragment)).commit();
                        getFragmentManager().beginTransaction().remove(getFragmentManager()
                                .findFragmentById(R.id.workAddress_autocomplete_fragment)).commit();
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getFragmentManager().beginTransaction().remove(getFragmentManager()
                                        .findFragmentById(R.id.homeAddress_autocomplete_fragment)).commit();
                                getFragmentManager().beginTransaction().remove(getFragmentManager()
                                        .findFragmentById(R.id.workAddress_autocomplete_fragment)).commit();
                            }
                        }).show();
            }catch (Exception e) {
                Toast.makeText(this, "You have already updated your addresses", Toast.LENGTH_SHORT).show();
            }
    }
}
