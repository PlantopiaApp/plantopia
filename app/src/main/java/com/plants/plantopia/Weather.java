package com.plants.plantopia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

//import cz.msebera.android.httpclient.Header;

public class Weather extends AppCompatActivity {

    // Variables for getting live weather data
    final String APP_ID = "a5ae8222f7f9a568076678d2fd4958b3";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat=6.847006&lon=79.867355&appid=a5ae8222f7f9a568076678d2fd4958b3";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    String Location_Provider = LocationManager.GPS_PROVIDER;

    // Variables for UI elements
    TextView NameofCity, weatherState, Temperature;
    ImageView mweatherIcon;
    LocationManager mLocationManager;
    LocationListener mLocationListner;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_weather);

        // Set up back button
        //imageButton = (ImageButton) findViewById(R.id.back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile();
            }
        });

        // Get references to UI elements
       // weatherState = findViewById(R.id.textView13);
       // Temperature = findViewById(R.id.textView14);
        // mweatherIcon = findViewById(R.id.imageView15);
        // NameofCity = findViewById(R.id.textView15);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }

    // Function to open the profile screen
    public void profile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    // Function to get the current location and make API call for weather data
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                // Get the latitude and longitude of the current location
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                // Set up parameters for API call
               // RequestParams params = new RequestParams();
               // params.put("lat", Latitude);
               //  params.put("lon", Longitude);
               //  params.put("appid", APP_ID);

                // Make the API call to get weather data
               // letsdoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Not used in this app
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Not used in this app
            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Weather.this, "Locationget Succesffully", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                //user denied the permission
            }
    }

   // private void letsdoSomeNetworking(RequestParams params) {
      //  AsyncHttpClient client = new AsyncHttpClient();
       // client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

         //   public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

              //  Toast.makeText(Weather.this, "Data Get Success", Toast.LENGTH_SHORT).show();

               // weatherData weatherD = weatherData.fromJson(response);
               // updateUI(weatherD);
                //super.onSuccess(statusCode, headers, response);


           // @Override
           // public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);

    private void updateUI(weatherData weather) {

        Temperature.setText(weather.getmTemperature());
        NameofCity.setText(weather.getMcity());
        weatherState.setText(weather.getmWeatherType());
        int resourceID = getResources().getIdentifier(weather.getMicon(), "drawable", getPackageName());
        mweatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }
}