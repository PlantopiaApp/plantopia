package com.plants.plantopia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlantIdentificationResponse {

    @SerializedName("suggestions")
    private List<PlantSuggestion> suggestions;

    public List<PlantSuggestion> getSuggestions() {
        return suggestions;
    }
}

