package com.example.protik.travelsupport;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MenuInflaterClass {

    MenuItem item;
    Context context;

    public MenuInflaterClass(MenuItem menuItem, Context ctx) {
        context = ctx;
        item = menuItem;
    }

    public boolean yo() {

        if (item.getItemId() == R.id.main_logout_id) {
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signOut();
            Toast.makeText(context,"Logged Out..!!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return true;
    }

}
