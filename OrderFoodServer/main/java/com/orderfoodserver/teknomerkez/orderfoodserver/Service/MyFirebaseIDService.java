package com.orderfoodserver.teknomerkez.orderfoodserver.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Token;

public class MyFirebaseIDService extends FirebaseInstanceIdService {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (user != null)
            updateToServer(refreshedToken);
    }

    private void updateToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("Tokens");
        Token data = new Token(refreshedToken, true); // true because this token send from Server app
        reference.child(user.getUid()).setValue(data);
    }
}
