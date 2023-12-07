package com.plants.plantopia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class OfficePlant extends AppCompatActivity {
    ImageView cactus;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_plant);

        cactus = (ImageView) findViewById(R.id.cactus);
        cactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScreen11();
            }
        });
    }

    public void openScreen11() {
        Intent intent = new Intent(this, SpeciesDetails.class);
        startActivity(intent);
    }
}