package com.orderfoodserver.teknomerkez.orderfoodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orderfoodserver.teknomerkez.orderfoodserver.Common.Common;
import com.orderfoodserver.teknomerkez.orderfoodserver.Interface.ItemClickListener;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Food;
import com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder.FoodViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference foodList;
    StorageReference storageReference;
    FirebaseStorage storage;
    private final String REFERENCE = "Food";
    String categoryID = "", _foodId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FloatingActionButton fab;
    MaterialEditText edtName, edtDescription, edtPrice, edtDiscount,edtFoodId;
    FButton btnSelect, btnUpload;
    Food newFood;
    RelativeLayout rootLayout;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        categoryID = getIntent().getStringExtra(Common.GET_CATEGORY_ID);
        // init firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food/" + categoryID);

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout = findViewById(R.id.rootLayout);

        loadListFood(categoryID);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });
        bindStorageReference();
    }

    // Add new item to list
    private void showAddFoodDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_food_item = inflater.inflate(R.layout.add_new_food_layout, null);
        edtName = add_new_food_item.findViewById(R.id.edtName);
        edtDescription = add_new_food_item.findViewById(R.id.edtDescription);
        edtPrice = add_new_food_item.findViewById(R.id.edtPrice);
        edtDiscount = add_new_food_item.findViewById(R.id.edtDiscount);
        edtFoodId = add_new_food_item.findViewById(R.id.edtFoodId);
        btnUpload = add_new_food_item.findViewById(R.id.btnUpload);
        btnSelect = add_new_food_item.findViewById(R.id.btnSelect);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Common.ADD_NEW_FOOD);
        //alertDialog.setMessage(Common.FİLL_İNFO);
        alertDialog.setView(add_new_food_item);
        alertDialog.setIcon(R.drawable.cart);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //just create new Food
                if (newFood != null) {
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout, Common.NEW_FOOD + newFood.getName() + Common.WAS_ADDED, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        btnUploadClickEvents();
        btnSelectClickEvents();
    }

    private void btnUploadClickEvents() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void bindStorageReference() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void btnSelectClickEvents() {
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newFood = new Food();
                            newFood.setName(edtName.getText().toString());
                            newFood.setDescription(edtDescription.getText().toString());
                            newFood.setPrice(edtPrice.getText().toString());
                            newFood.setDiscount(edtDiscount.getText().toString());
                            newFood.setMenuId(categoryID);
                            newFood.setFoodId(edtFoodId.getText().toString());
                            newFood.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
                    mDialog.setMessage("Uploaded! " + progress + "%");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText(Common.IMAGE_SELECTED);
        }
    }

    private void loadListFood(String categoryID) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Food")
                .child(categoryID);

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(query, Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.food_name.setText(model.getName());
                Log.d("TAG", model.getName());
                Picasso p = Picasso.with(getBaseContext());
                p.load(model.image).into(holder.food_image);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();


                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    //update and delete events
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            foodList.child(adapter.getRef(item.getOrder()).getKey()).removeValue();
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Food item) {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_food_item = inflater.inflate(R.layout.add_new_food_layout, null);
        edtName = add_new_food_item.findViewById(R.id.edtName);
        edtDescription = add_new_food_item.findViewById(R.id.edtDescription);
        edtPrice = add_new_food_item.findViewById(R.id.edtPrice);
        edtDiscount = add_new_food_item.findViewById(R.id.edtDiscount);
        btnUpload = add_new_food_item.findViewById(R.id.btnUpload);
        btnSelect = add_new_food_item.findViewById(R.id.btnSelect);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Common.EDİT_FOOD);
        alertDialog.setMessage(Common.FİLL_İNFO);
        alertDialog.setView(add_new_food_item);
        alertDialog.setIcon(R.drawable.cart);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //uploading food item
                item.setName(edtName.getText().toString());
                foodList.child(key).setValue(item);
                Snackbar.make(rootLayout, Common.FOOD_UPDATED,Snackbar.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        //getting info of item to the alertdialog
        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        btnSelectClickEvents();

    }

    private void changeImage(final Food item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
                    mDialog.setMessage("Uploaded !" + progress + "%");
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backPressed = new Intent(FoodList.this, Home.class);
        startActivity(backPressed);
        finish();
    }

    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(adapter != null)
            adapter.startListening();
    }
}
