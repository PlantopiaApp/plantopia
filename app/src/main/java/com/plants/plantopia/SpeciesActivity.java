package com.plants.plantopia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class SpeciesActivity extends AppCompatActivity {

    private String[] allPlantNames = {"Aloe vera", "Amaranth", "Angel trumpet", "Arfaj", "Bamboo", "Bindweed", "Bearberry", "Bittercress",
            "Blackhaw", "Brown Betty", "California buckeye", "Cherry", "Catalina ironwood", "Chigger flower", "Circle Cactus", "Coffee plant ", "Deadnettle", "Drumstick", "Duck retten", "Durian",
            "Earth gall","Elderberry","Elegant lupine","Eytelia","Fellenwort","Felonwort","Flax", "Fumewort",
    "Golden buttons", "Goose tongue", "Green berry nightshade" }; // Replace with your plant names
    private ArrayList<String> displayedPlantNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);
        // Initialize the displayed list with all plant names
        displayedPlantNames = new ArrayList<>(Arrays.asList(allPlantNames));

        ListView listViewSpecies = findViewById(R.id.listViewSpecies);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayedPlantNames);
        listViewSpecies.setAdapter(adapter);

        listViewSpecies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlant = displayedPlantNames.get(position);
                Intent intent = new Intent(SpeciesActivity.this, SpeciesDetails.class);
                intent.putExtra("plantName", selectedPlant);
                startActivity(intent);
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list based on the search query
                displayedPlantNames.clear();
                for (String plantName : allPlantNames) {
                    if (plantName.toLowerCase().contains(newText.toLowerCase())) {
                        displayedPlantNames.add(plantName);
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}