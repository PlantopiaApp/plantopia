package com.plants.plantopia;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // Create an intent to start the main activity
        final Intent getStartIntent = new Intent(MainActivity.this, GetStart.class);
        final Intent homeIntent = new Intent(MainActivity.this, Home.class);

        // Use a handler to delay the start of the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if( currentUser != null) {
                    homeIntent.putExtra("USERNAME_KEY", currentUser.getDisplayName());
                    startActivity(homeIntent);
                } else {
                    startActivity(getStartIntent);
                }
                // Start the main activity
                // Finish the splash activity so that it's not shown when back button is pressed
                finish();
            }
        }, 2000); // Delay for 2000 milliseconds (2 seconds)
    }
}