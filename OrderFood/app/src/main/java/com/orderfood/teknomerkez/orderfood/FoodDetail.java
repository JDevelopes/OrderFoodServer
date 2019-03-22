package com.orderfood.teknomerkez.orderfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
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
import com.orderfood.teknomerkez.orderfood.Common.Common;
import com.orderfood.teknomerkez.orderfood.Database.Database;
import com.orderfood.teknomerkez.orderfood.Model.Food;
import com.orderfood.teknomerkez.orderfood.Model.Order;
import com.orderfood.teknomerkez.orderfood.Model.Rating;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView foodName, foodPrice, foodDescription;
    KenBurnsView foodImage;
    CollapsingToolbarLayout collapse;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    RatingBar ratingBar;
    ElegantNumberButton numberButton;
    String _foodId = "", categoryID = "",ratingId="";
    FirebaseDatabase database;
    DatabaseReference food;
    Food currentFood;
    DatabaseReference ratingTbl;
    FButton showComment;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        init();

        //comment side
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().build());

        categoryID = getIntent().getStringExtra("CategoryID");
        _foodId = getIntent().getStringExtra("FoodId");

        database = FirebaseDatabase.getInstance();
        food = database.getReference("Food/" + categoryID + "/" + _foodId);
        ratingTbl = database.getReference("Rating");

        if(Common.isConnectedtoInternet(getBaseContext())){
            loadFoodDetails(food);
            getRatingFoodId(food);
        }else{
            Toast.makeText(this, "PLEASE CHECK YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
        }

        btnCartEvents();

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToComments = new Intent(FoodDetail.this,ShowComment.class);
                goToComments.putExtra(Common.GO_TO_COMMENTS,_foodId);
                startActivity(goToComments);
                finish();
            }
        });
    }

    private void getRatingFoodId(DatabaseReference food) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(_foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorPrimary)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RationDialogFadingAnim)
                .create(FoodDetail.this)
                .show();
    }


    private void init() {
        showComment = findViewById(R.id.showComment);
        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.rating);
        foodDescription = findViewById(R.id.food_description);
        foodName = findViewById(R.id.food_Name);
        foodPrice = findViewById(R.id.food_price);
        foodImage = findViewById(R.id.img_food);
        collapse = findViewById(R.id.collapsing);

        collapse.setExpandedTitleColor(R.style.ExpandedAppBar);
        collapse.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }

    private void btnCartEvents() {
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        user.getUid(),
                        _foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        String.valueOf(currentFood.getPrice()),
                        String.valueOf(currentFood.getDiscount()),
                        String.valueOf(currentFood.getImage())
                ));
                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFoodDetails(DatabaseReference food) {
        food.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                Picasso p = Picasso.with(getBaseContext());
                p.load(currentFood.getImage()).into(foodImage);

                collapse.setTitle(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());
                foodPrice.setText(currentFood.getPrice());
                foodName.setText(currentFood.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnCart.setCount(new Database(this).getCountCart(user.getUid()));
    }


    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        // get rating and upload to firebase
        final FirebaseUser final_user = FirebaseAuth.getInstance().getCurrentUser();
        final Rating rating = new Rating(final_user.getDisplayName(), _foodId, String.valueOf(value), comments);
        ratingTbl.push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FoodDetail.this, "Thank you for submit rating!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
