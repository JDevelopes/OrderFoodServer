package com.orderfood.teknomerkez.orderfood.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.orderfood.teknomerkez.orderfood.Model.Token;

public class MyFirebaseIDService extends FirebaseInstanceIdService {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();

        if (user != null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("Tokens");
        Token data = new Token(tokenRefreshed, false); // false because this token send from Client app
        reference.child(user.getUid()).setValue(data);
    }
}
