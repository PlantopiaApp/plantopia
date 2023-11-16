package com.plants.plantopia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePlants extends AppCompatActivity {

    ImageView cactus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_plants);

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