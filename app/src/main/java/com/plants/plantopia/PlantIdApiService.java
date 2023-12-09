package com.plants.plantopia;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


//This interface defines the API endpoint for plant identification. It uses Multipart requests to send the API key and image.


public interface PlantIdApiService {
    @Multipart
    @POST("/identification")
    Call<PlantIdentificationResponse> identifyPlant(
            @Part("api_key") RequestBody apiKey,
            @Part MultipartBody.Part image);
}
