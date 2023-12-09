package com.plants.plantopia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Screen1 extends AppCompatActivity {

    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);

        btnNext = findViewById(R.id.button8);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOnboardingScreen2();
            }
        });

    }
    public void openOnboardingScreen2(){
        Intent intent = new Intent(this, com.plants.plantopia.Screen2.class);
        startActivity(intent);
    }
}