package com.plants.plantopia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStart extends AppCompatActivity {

    private Button button;
    private Button button11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);

        // SignIn button
        button11 = findViewById(R.id.button11);
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScreen11();
            }
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScreen2();
            }
        });
    }

    public void openScreen2() {
        Intent intent = new Intent(this, Screen1.class);
        startActivity(intent);
    }

    public void openScreen11() {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }
}
