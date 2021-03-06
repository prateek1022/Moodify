package com.cu.moodify;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home_screen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RadioButton happy_rb,sad_rb,angry_rb;
    private LinearLayout happy_ll,sad_ll,angry_ll;
    String mood=mood_types.HAPPY;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        TextView textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        happy_rb=findViewById(R.id.rb_happy);
        sad_rb=findViewById(R.id.rb_sad);
        angry_rb=findViewById(R.id.rb_angry);
        Button play_btn = findViewById(R.id.play_btn);

        happy_ll=findViewById(R.id.happy_ll);
        sad_ll=findViewById(R.id.sad_ll);
        angry_ll=findViewById(R.id.angry_ll);

        happy_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    happy_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d));
                sad_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d2));
                angry_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d2));

                happy_rb.setChecked(true);
                sad_rb.setChecked(false);
                angry_rb.setChecked(false);

                mood=mood_types.HAPPY;

            }
        });

        sad_ll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                happy_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d2));
                sad_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d));
                angry_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d2));

                happy_rb.setChecked(false);
                sad_rb.setChecked(true);
                angry_rb.setChecked(false);

                mood=mood_types.SAD;
            }
        });

        angry_ll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                happy_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d2));
                sad_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d2));
                angry_ll.setBackground(ContextCompat.getDrawable(Home_screen.this,R.drawable.rounded_d));

                happy_rb.setChecked(false);
                sad_rb.setChecked(false);
                angry_rb.setChecked(true);

                mood=mood_types.ANGRY;
            }
        });

        //noinspection ConstantConditions
        textViewUserEmail.setText("Welcome " + user.getDisplayName());


        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home_screen.this, Music_player.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(Home_screen.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
