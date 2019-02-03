package com.orderfood.teknomerkez.orderfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.orderfood.teknomerkez.orderfood.Common.Common;
import com.orderfood.teknomerkez.orderfood.Database.Database;
import com.orderfood.teknomerkez.orderfood.Helper.RecyclerItemTouchHelper;
import com.orderfood.teknomerkez.orderfood.Interface.RecyclerItemTouchHelperListener;
import com.orderfood.teknomerkez.orderfood.Model.Address;
import com.orderfood.teknomerkez.orderfood.Model.DataMessage;
import com.orderfood.teknomerkez.orderfood.Model.MyResponse;
import com.orderfood.teknomerkez.orderfood.Model.Order;
import com.orderfood.teknomerkez.orderfood.Model.Request;
import com.orderfood.teknomerkez.orderfood.Model.Token;
import com.orderfood.teknomerkez.orderfood.Remote.APIService;
import com.orderfood.teknomerkez.orderfood.ViewHolder.CartAdapter;
import com.orderfood.teknomerkez.orderfood.ViewHolder.CartViewHolder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;

    RelativeLayout rootLayout;
    public TextView txtTotalPrice;
    FButton btnPlaceOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    APIService mService;
    Place shippingAddress;
    String address;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //Location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");
        mService = Common.getFCMService();

        //init
        rootLayout = findViewById(R.id.rootLayout);
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        txtTotalPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        // swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);


        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your Cart is empty!!", Toast.LENGTH_SHORT).show();
            }
        });
        if (Common.isConnectedtoInternet(getBaseContext())) {
            loadListFood();
        } else {
            Toast.makeText(this, "PLEASE CHECK YOUR INTETNET CONNECTION", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.order_address_comment, null, false);
        final EditText edtPhone = v.findViewById(R.id.edtPhone);
        final RadioButton edtHomeRadioButton = v.findViewById(R.id.home_address);
        final RadioButton edtWorkRadioButton = v.findViewById(R.id.work_address);

        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Your Address");

        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.d("ERROR", status.getStatusMessage());
            }
        });

        updateYourAddress(edtHomeRadioButton, edtWorkRadioButton, edtAddress);

        final EditText edtComment = v.findViewById(R.id.edtComment);
        edtPhone.setHint("Phone Number");
        alertDialog.setView(v);
        alertDialog.setCancelable(false);
        final EditText shippingAddressText = edtAddress.getView().findViewById(R.id.place_autocomplete_search_input);

        alertDialog.setTitle("One more step!")
                .setMessage("Enter Informations");
        alertDialog.setIcon(R.drawable.add);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Create new Request
                address = shippingAddressText.getText().toString();
                Request request = new Request(
                        edtPhone.getText().toString(),
                        user.getUid(),
                        address,
                        txtTotalPrice.getText().toString(),
                        "0", // status
                        edtComment.getText().toString(),
                        cart
                );
                if (user.getPhoneNumber() != null) {
                    edtPhone.setText(user.getPhoneNumber());
                }

                //submit the firebase
                // gonna use System.CurrentMili to key
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);

                //delete cart
                new Database(getBaseContext()).CleanCart(user.getUid());
                sendNotificationOrder(order_number);
                Toast.makeText(Cart.this, "Thank you, Order Placed", Toast.LENGTH_SHORT).show();
                finish();

                Intent sendPhoneNumber = new Intent(Cart.this, OrderStatus.class);
                sendPhoneNumber.putExtra("phoneNumber", edtPhone.getText().toString());
                startActivity(sendPhoneNumber);

                //remove fragment
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                    }
                }).show();


    }

    private void updateYourAddress(final RadioButton edtHomeRadioButton, final RadioButton edtWorkRadioButton, final PlaceAutocompleteFragment edtAddress) {
        bindAdressRadioButton(edtHomeRadioButton, edtAddress, true);
        bindAdressRadioButton(edtWorkRadioButton, edtAddress, false);
    }

    private void bindAdressRadioButton(final RadioButton addressRadioButton,
                                       final PlaceAutocompleteFragment edtAddress,
                                       final boolean isHome) {
        addressRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance()
                        .getReference("Address")
                        .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Address address = dataSnapshot.getValue(Address.class);

                        if (isChecked) {
                            if (isHome) {
                                try {
                                    if (address.homeAddress == null || address.homeAddress.equals("")) {
                                        return;
                                    }
                                    ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                            .setText(address.homeAddress);
                                } catch (Exception e) {
                                    displayAddressNotFoundToast();
                                }
                            } else {
                                try {
                                    if (address.workAdress == null || address.workAdress.equals("")) {
                                        return;
                                    }

                                    ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                            .setText(address.workAdress);
                                } catch (Exception e) {
                                    displayAddressNotFoundToast();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void displayAddressNotFoundToast() {
        Toast.makeText(Cart.this, "Cannot find a saved address. Please check your saved addresses.",
                Toast.LENGTH_SHORT).show();
    }


    private void sendNotificationOrder(final String order_number) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = reference.orderByChild("isServerToken").equalTo(true); // get all node with isServerToken is true
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);
                    Map<String, String> dataSend = new HashMap<>();
                    dataSend.put("title", "Order Food");
                    dataSend.put("message", "You have new Order " + order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(), dataSend);

                    String test = new Gson().toJson(dataMessage);
                    Log.d("Content", test);

                    mService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            //only run when get result
                            if (response.code() == 200) {
                                if (response.body().succes == 1) {
                                    Toast.makeText(Cart.this, "Thank you, Order Placed", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Cart.this, "Failed!!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("ERROR", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts(user.getUid());
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        int total = 0;

        for (Order order : cart) {
            total += Integer.parseInt(order.getPrice()) * (Integer.parseInt(order.getQuantity()));
            //  txtTotalPrice.setText(fmt.format(total));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        //remove item at List<Order> by position
        cart.remove(position);
        //delete all old data from SQLite
        new Database(this).CleanCart(user.getUid());
        //update new data from List<ORder> to SQLite
        for (Order item : cart) {
            new Database(this).addToCart(item);
        }
        //Refresh
        loadListFood();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int duration, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductID(), user.getUid());

            //calculate again
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(user.getUid());
            for (Order item : orders) {
                total += Integer.parseInt(item.getPrice()) * (Integer.parseInt(item.getQuantity()));
                //  txtTotalPrice.setText(fmt.format(total));
            }
            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));

            // make snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(user.getUid());
                    for (Order item : orders) {
                        total += Integer.parseInt(item.getPrice()) * (Integer.parseInt(item.getQuantity()));
                        //  txtTotalPrice.setText(fmt.format(total));
                    }
                    Locale locale = new Locale("en", "US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
