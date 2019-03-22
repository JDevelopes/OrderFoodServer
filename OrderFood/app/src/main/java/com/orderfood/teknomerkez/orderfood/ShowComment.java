package com.orderfood.teknomerkez.orderfood;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.orderfood.teknomerkez.orderfood.Common.Common;
import com.orderfood.teknomerkez.orderfood.Model.Rating;
import com.orderfood.teknomerkez.orderfood.ViewHolder.CommentViewHolder;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    RecyclerView recycler_comment;
    FirebaseRecyclerAdapter<Rating, CommentViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference ratingTBL;
    SwipeRefreshLayout swipeToRefreshList;
    RecyclerView.LayoutManager layoutManager;
    String foodId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/fondamento.xml")
                .setFontAttrId(R.attr.fontPath).build());
        //init
        recycler_comment = findViewById(R.id.showComments);
        swipeToRefreshList = findViewById(R.id.swipeToRefreshList);

        database = FirebaseDatabase.getInstance();
        ratingTBL = database.getReference("Rating");

        layoutManager = new LinearLayoutManager(this);
        recycler_comment.setHasFixedSize(true);
        recycler_comment.setLayoutManager(layoutManager);

        swipeToRefreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.GO_TO_COMMENTS);
                if (!foodId.isEmpty() && foodId != null)
                // commentAdapter();
                {
                    Query query = ratingTBL.orderByChild("foodId").equalTo(foodId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>().setQuery(query, Rating.class).build();
                    adapter = new FirebaseRecyclerAdapter<Rating, CommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Rating model) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            holder.comment.setText(model.getComment());
                            holder.comment_user_name.setText(user.getDisplayName());
                            holder.ratingComment.setRating(Float.parseFloat(model.getRateValue()));
                        }

                        @NonNull
                        @Override
                        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
                            return new CommentViewHolder(view);
                        }
                    };
                    laodComments(foodId);
                }
            }
        });
        //thread to load comment on first touch
        swipeToRefreshList.post(new Runnable() {
            @Override
            public void run() {
                swipeToRefreshList.setRefreshing(true);
                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.GO_TO_COMMENTS);
                if (!foodId.isEmpty() && foodId != null)
                    if (getIntent() != null)
                        foodId = getIntent().getStringExtra(Common.GO_TO_COMMENTS);
                if (!foodId.isEmpty() && foodId != null)
                // commentAdapter();
                {
                    Query query = ratingTBL.orderByChild("foodId").equalTo(foodId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>().setQuery(query, Rating.class).build();
                    adapter = new FirebaseRecyclerAdapter<Rating, CommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Rating model) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            holder.comment.setText(model.getComment());
                            holder.comment_user_name.setText(user.getDisplayName());
                            holder.ratingComment.setRating(Float.parseFloat(model.getRateValue()));
                        }

                        @NonNull
                        @Override
                        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
                            return new CommentViewHolder(view);
                        }
                    };
                    laodComments(foodId);
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void laodComments(String _foodId) {
        adapter.startListening();
        recycler_comment.setAdapter(adapter);
        swipeToRefreshList.setRefreshing(false);
    }
}
