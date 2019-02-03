package com.orderfoodserver.teknomerkez.orderfoodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orderfoodserver.teknomerkez.orderfoodserver.Common.Common;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Banner;
import com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder.BannerViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class BannerActivity extends AppCompatActivity {

    RecyclerView recycler_banner;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton add_banner;
    RelativeLayout banner_rootlayout;
    FirebaseDatabase database;
    DatabaseReference banners;
    StorageReference storageReference;
    FirebaseStorage storage;
    SwipeRefreshLayout swipeBannerLayout;

    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    //add new Banner
    MaterialEditText edtBannerName, edtBannerFoodId, edtBannerCategoryId;
    FButton btnBannerSelect, btnBannerUpload;

    Banner newBanner;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        // init firebase
        database = FirebaseDatabase.getInstance();
        banners = database.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //init ids
        recycler_banner = findViewById(R.id.banner_food);
        recycler_banner.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_banner.setLayoutManager(layoutManager);
        banner_rootlayout = findViewById(R.id.banner_rootlayout);
        add_banner = findViewById(R.id.banner_fab);
        swipeBannerLayout = findViewById(R.id.swipeBannerLayout);

        add_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBanner();
            }
        });
        loadlistBanners();
        swipeRefreshEvents();
    }

    private void loadlistBanners() {
        FirebaseRecyclerOptions<Banner> allBanner = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banners, Banner.class).build();
        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(allBanner) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.banner_food_name.setText(model.getName());
                Picasso picasso = Picasso.with(getBaseContext());
                picasso.load(model.getImage()).into(holder.banner_food_image);
            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_layout, parent, false);
                return new BannerViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_banner.setAdapter(adapter);
        swipeBannerLayout.setRefreshing(false);
    }

    private void swipeRefreshEvents() {
        swipeBannerLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeBannerLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadlistBanners();
                swipeBannerLayout.setRefreshing(false);
            }
        });
        swipeBannerLayout.post(new Runnable() {
            @Override
            public void run() {
                loadlistBanners();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showAddBanner() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_banner_item = inflater.inflate(R.layout.add_new_banner_layout, null);

        edtBannerName = add_new_banner_item.findViewById(R.id.edtBannerName);
        edtBannerFoodId = add_new_banner_item.findViewById(R.id.edtBannerFoodId);
        edtBannerCategoryId = add_new_banner_item.findViewById(R.id.edtBannerCategoryId);
        btnBannerSelect = add_new_banner_item.findViewById(R.id.btnBannerSelect);
        btnBannerUpload = add_new_banner_item.findViewById(R.id.btnBannerUpload);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Common.ADD_NEW_BANNER);
        //alertDialog.setMessage(Common.FİLL_İNFO);
        alertDialog.setView(add_new_banner_item);
        alertDialog.setIcon(R.drawable.banner_slider);

        btnBannerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnBannerUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newBanner != null) {
                    banners.push().setValue(newBanner);
                }
                loadlistBanners();
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        newBanner = null;
                        loadlistBanners();
                    }
                }).show();
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(BannerActivity.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newBanner = new Banner();
                            newBanner.setName(edtBannerName.getText().toString());
                            newBanner.setFoodId(edtBannerFoodId.getText().toString());
                            newBanner.setCategoryID(edtBannerCategoryId.getText().toString());
                            newBanner.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            btnBannerSelect.setText(Common.IMAGE_SELECTED);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateBannerDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            banners.child(adapter.getRef(item.getOrder()).getKey()).removeValue();
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateBannerDialog(final String key, final Banner item) {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_banner_item = inflater.inflate(R.layout.add_new_banner_layout, null);
        edtBannerName = add_new_banner_item.findViewById(R.id.edtBannerName);
        edtBannerFoodId = add_new_banner_item.findViewById(R.id.edtBannerFoodId);
        edtBannerCategoryId = add_new_banner_item.findViewById(R.id.edtBannerCategoryId);
        btnBannerSelect = add_new_banner_item.findViewById(R.id.btnBannerSelect);
        btnBannerUpload = add_new_banner_item.findViewById(R.id.btnBannerUpload);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Common.EDİT_FOOD);
        alertDialog.setMessage(Common.FİLL_İNFO);
        alertDialog.setView(add_new_banner_item);
        alertDialog.setIcon(R.drawable.cart);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //uploading banner item
                item.setName(edtBannerName.getText().toString());
                item.setFoodId(edtBannerFoodId.getText().toString());
                item.setCategoryID(edtBannerCategoryId.getText().toString());

                //making update
                Map<String, Object> update = new HashMap<>();
                update.put("categoryID", item.getCategoryID());
                update.put("foodId", item.getFoodId());
                update.put("image", item.getImage());
                update.put("name", item.getName());
                banners.child(key).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(banner_rootlayout, Common.BANNER_UPDATED, Snackbar.LENGTH_SHORT).show();
                        loadlistBanners();
                    }
                });
                Snackbar.make(banner_rootlayout, Common.FOOD_UPDATED, Snackbar.LENGTH_SHORT).show();
                loadlistBanners();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadlistBanners();
            }
        });
        alertDialog.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        //getting info of item to the alertdialog
        edtBannerName.setText(item.getName());
        edtBannerFoodId.setText(item.getFoodId());
        edtBannerCategoryId.setText(item.getCategoryID());

        btnBannerUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        btnBannerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void changeImage(final Banner item) {
        if (filePath != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(BannerActivity.this, "Uploaded !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent backtoHome = new Intent(getBaseContext(), Home.class);
        startActivity(backtoHome);
        finish();
    }
}
