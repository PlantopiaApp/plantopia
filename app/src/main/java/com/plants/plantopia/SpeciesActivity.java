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

    private String[] allPlantNames = {
            "Aloe vera", "Amaranth", "Angel trumpet", "Arfaj", "Bamboo", "Bearberry", "Bittercress",
            "Blackhaw", "Brown Betty", "California buckeye", "Cherry", "Catalina ironwood", "Chigger flower", "Circle Cactus", "Coffee plant", "Deadnettle", "Drumstick", "Duck retten", "Durian",
            "Earth gall", "Elderberry", "Elegant lupine", "Eytelia", "Fellenwort", "Felonwort", "Flax", "Fumewort",
            "Golden buttons", "Goose tongue", "Green berry nightshade",
            "Honeysuckle", "Hornbeam", "Horseweed", "Hummingbird sage", "Hydrangea", "Ice plant", "Indian pipe", "Iris", "Ivy",
            "Jack in the pulpit", "Jasmine", "Jewel orchid", "Joe-pye weed", "Judas tree", "Juniper",
            "Kangaroo paw", "Katsura tree", "Kentucky bluegrass", "Kinnikinnick", "Knotweed",
            "Lady's mantle", "Lantana", "Lavender", "Lemon balm", "Lily", "Lupine",
            "Maidenhair fern", "Maple", "Marigold", "Mayapple", "Meadow rue", "Milkweed", "Mimosa", "Mint", "Monkey puzzle tree", "Moonflower", "Morning glory", "Moss",
            "Nasturtium", "Nepenthes", "Nerine", "Nigella", "Nutmeg",
            "Oleander", "Oregano", "Oriental poppy", "Ornamental grass", "Osteospermum", "Oxalis",
            "Pansy", "Papyrus", "Passion flower", "Peach tree", "Pearly everlasting", "Pennyroyal", "Peony", "Petunia", "Phlox", "Pine", "Poinsettia", "Pond cypress", "Poppy", "Potentilla", "Primrose",
            "Quaking aspen", "Queen palm", "Queen's wreath",
            "Rain lily", "Rat tail cactus", "Red hot poker", "Rose", "Rosemary", "Rowan",
            "Saffron crocus", "Sage", "Salvia", "Sassafras", "Savory", "Scabiosa", "Sea lavender", "Sedum", "Shasta daisy", "Snapdragon", "Snowdrop", "Sorrel", "Speedwell", "Spinach", "Spicebush", "Squash", "Stargazer lily", "Statice", "Strawberry", "Sunflower", "Sweet pea", "Sweet woodruff", "Sycamore",
            "Tarragon", "Thistle", "Thyme", "Ti plant", "Toad lily", "Torenia", "Trailing arbutus", "Trillium", "Tulip",
            "Umbrella plant", "Upland cress", "Upright juniper",
            "Vanilla orchid", "Vegetable amaranth", "Verbena", "Vervain", "Viburnum", "Vinca", "Viola", "Violet",
            "Wallflower", "Water lily", "Weeping willow", "Wisteria", "Woolly thyme",
            "Xerophyte",
            "Yellowwood", "Yew",
            "Zebra plant", "Zinnia"
    };

    private ArrayList<String> displayedPlantNames;
    private ArrayList<String> filteredPlantNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);

        // Initialize the displayed list with all plant names
        displayedPlantNames = new ArrayList<>(Arrays.asList(allPlantNames));
        filteredPlantNames = new ArrayList<>(displayedPlantNames);

        ListView listViewSpecies = findViewById(R.id.listViewSpecies);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayedPlantNames);
        listViewSpecies.setAdapter(adapter);

        listViewSpecies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlant = filteredPlantNames.get(position);
                Intent intent = new Intent(SpeciesActivity.this, SpeciesDetails2.class);
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
                filteredPlantNames.clear();
                for (String plantName : allPlantNames) {
                    if (plantName.toLowerCase().contains(newText.toLowerCase())) {
                        filteredPlantNames.add(plantName);
                    }
                }
                adapter.clear();
                adapter.addAll(filteredPlantNames);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}
