package com.cu.moodify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class decider extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);

        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            //that means user is already logged in



            //and open profile activity
            startActivity(new Intent(decider.this, Home_screen.class));
            finish();
        }
        else{
            //user not logged in
            startActivity(new Intent(decider.this, LoginActivity.class));
            finish();
        }
    }
}