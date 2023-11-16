package com.plants.plantopia;
import com.google.gson.annotations.SerializedName;

public class PlantSuggestion {
    @SerializedName("plant_name")
    private String plantName;

    public String getPlantName() {
        return plantName;
    }
}
