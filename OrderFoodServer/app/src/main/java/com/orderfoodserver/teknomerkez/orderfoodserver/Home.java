package com.orderfoodserver.teknomerkez.orderfoodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orderfoodserver.teknomerkez.orderfoodserver.Common.Common;
import com.orderfoodserver.teknomerkez.orderfoodserver.Interface.ItemClickListener;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Category;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Token;
import com.orderfoodserver.teknomerkez.orderfoodserver.ViewHolder.MenuViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView textFullName;
    RecyclerView recycler_menu;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseStorage storage;
    FirebaseDatabase database;
    StorageReference storageReference;
    DatabaseReference categories;
    MaterialEditText edtName;
    FButton btnSelect, btnUpload;
    Category newCategory;
    DrawerLayout drawer;
    Uri saveUri;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.MENU_MANAGEMENT);
        setSupportActionBar(toolbar);

        //init firebase
        database = FirebaseDatabase.getInstance();
        categories = database.getReference(Common.CATEGORY);

        recycler_menu = findViewById(R.id.recycler_menu);

        addNewMenuItem();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name For User
        View headerView = navigationView.getHeaderView(0);
        textFullName = headerView.findViewById(R.id.txtFullName);
        if(user != null)
            textFullName.setText(user.getDisplayName());

        loadMenuContent();
        loadMenu();

        //send token
        updateToken(FirebaseInstanceId.getInstance().getToken());
        bindStorageReference();

    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("Tokens");
        Token data = new Token(token, true); // true because this token send from Server app
        reference.child(user.getUid()).setValue(data);
    }

    private void btnSelectClickEvents() {
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
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
            saveUri = data.getData();
            btnSelect.setText(Common.IMAGE_SELECTED);
        }

    }

    private void btnUploadClickEvents() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
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
                    Toast.makeText(Home.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCategory = new Category(edtName.getText().toString(), uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void addNewMenuItem() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu_item = inflater.inflate(R.layout.add_new_menu_layout, null);
        edtName = add_new_menu_item.findViewById(R.id.edtName);
        btnSelect = add_new_menu_item.findViewById(R.id.btnSelect);
        btnUpload = add_new_menu_item.findViewById(R.id.btnUpload);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Common.UPDATE_CATEGORY)
                .setMessage(Common.FİLL_İNFO)
                .setView(add_new_menu_item)
                .setIcon(R.drawable.cart)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //just create new Category
                        if (newCategory != null) {
                            categories.push().setValue(newCategory);
                            Snackbar.make(drawer, Common.NEW_CATEGORY, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        btnUploadClickEvents();
        btnSelectClickEvents();
    }

    private void bindStorageReference() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void loadMenuContent() {
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
    }

    private void loadMenu() {
        Query query = FirebaseDatabase.getInstance()
                .getReference().child(Common.CATEGORY);
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
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso p = Picasso.with(getBaseContext());
                p.load(model.image).into(viewHolder.menu_image);

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        foodList.putExtra(Common.GET_CATEGORY_ID, adapter.getRef(position).getKey());
                        foodList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(foodList);
                        finish();
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            categories.child(adapter.getRef(item.getOrder()).getKey()).removeValue();
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Category item) {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu_item = inflater.inflate(R.layout.add_new_menu_layout, null);
        edtName = add_new_menu_item.findViewById(R.id.edtName);
        btnSelect = add_new_menu_item.findViewById(R.id.btnSelect);
        btnUpload = add_new_menu_item.findViewById(R.id.btnUpload);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Common.UPDATE_CATEGORY)
                .setMessage(Common.FİLL_İNFO)
                .setView(add_new_menu_item)
                .setIcon(R.drawable.cart)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //update information
                        item.setName(edtName.getText().toString());
                        categories.child(key).setValue(item);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        edtName.setText(item.getName());
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        btnSelectClickEvents();

    }

    private void changeImage(final Category item) {
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
                    Toast.makeText(Home.this, "Uploaded !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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
    protected void onResume (){
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent mainMenu = new Intent(getBaseContext(),Home.class);
            startActivity(mainMenu);
            finish();
        } else if (id == R.id.nav_banner) {
            Intent editBanner = new Intent(getBaseContext(),BannerActivity.class);
            startActivity(editBanner);
            finish();

        }else if (id == R.id.nav_sendMessage) {
            Intent intent = new Intent(getBaseContext(), SendMessage.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_orders) {
            Intent orders = new Intent(Home.this, OrderStatus.class);
            startActivity(orders);
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
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
