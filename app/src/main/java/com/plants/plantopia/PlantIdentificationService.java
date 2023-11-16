package com.plants.plantopia;

import android.content.Context;
import android.net.Uri;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class PlantIdentificationService {
    private static String base64EncodeFromFile(String fileString) throws Exception {
        File file = new File(fileString);
        String encodedString = ImageUtils.base64EncodeFromFile(file);
        return encodedString;
    }

    public static String identifyPlant(Context context, String apiKey, Uri imageUri) throws Exception {
        // Convert the Android Uri to a file path
        String imagePath = ImageUtils.getRealPathFromUri(context, imageUri);

        // Read image from the local file system and encode
        String[] flowers = new String[]{imagePath};

        JSONObject data = new JSONObject();
        data.put("api_key", apiKey);

        // Add images
        JSONArray images = new JSONArray();
        for (String filename : flowers) {
            String fileData = base64EncodeFromFile(filename);
            images.put(fileData);
        }
        data.put("images", images);

        // Add modifiers
        JSONArray modifiers = new JSONArray()
                .put("crops_fast")
                .put("similar_images");
        data.put("modifiers", modifiers);

        // Add language
        data.put("plant_language", "en");

        // Add plant details
        JSONArray plantDetails = new JSONArray()
                .put("common_names")
                .put("url")
                .put("name_authority")
                .put("wiki_description")
                .put("taxonomy")
                .put("synonyms");
        data.put("plant_details", plantDetails);

        // Make the API request
        String apiUrl = "https://api.plant.id/v2/identify";
        return HttpUtils.sendPostRequest(apiUrl, data);
    }
}
