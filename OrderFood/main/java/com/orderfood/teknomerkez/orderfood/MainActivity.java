package com.orderfood.teknomerkez.orderfood;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orderfood.teknomerkez.orderfood.Common.Common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    FButton sign_in_email;
    TextView textSlogan;
    Context context = this;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        init();
        printKeyHash();
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (Common.isConnectedtoInternet(getBaseContext())) {
            bindSignInClickListener();
        } else {
            Toast.makeText(context, "PLEASE CHECK YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
        }
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUI();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.orderfood.teknomerkez.orderfood",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void bindSignInClickListener() {
        sign_in_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(true)
                        .setTheme(R.style.Authantication_Theme)
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
                updateUI();
            } else {
                Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show();
                updateUI();
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

    private void updateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    private void init() {
        sign_in_email = findViewById(R.id.e_mail);
        textSlogan = findViewById(R.id.txtslogan);
    }
}
