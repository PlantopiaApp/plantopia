package com.plants.plantopia;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView humidity, temp, light;
    private Sensor tempSensor, humiditySensor, lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //references to the text views for displaying the sensor readings
        humidity = findViewById(R.id.humidity);
        temp = findViewById(R.id.temperature);
        light = findViewById(R.id.light);

        // Get a reference to the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get references to the light sensor, temperature sensor, and humidity sensor
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Use resource ID values directly in conditions
                if (itemId == R.id.scan) {
                    startActivity(new Intent(getApplicationContext(), PlantIdentify.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.dashboard) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Check if the sensor event is from the temperature sensor
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            // Get the temperature value from the event
            float temperature = event.values[0];
            // Update the text view with the temperature value
            temp.setText(temperature + " °C");
        }

        // Check if the sensor event is from the humidity sensor
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            // Get the humidity value from the event
            float humidityy = event.values[0];
            // Update the text view with the humidity value
            humidity.setText(humidityy + " %");
        }

        // Check if the sensor event is from the light sensor
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            // Get the light intensity value from the event
            float lightIntensity = event.values[0];
            // Update the text view with the light intensity value
            light.setText(lightIntensity + " lux");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listener
        sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener
        sensorManager.unregisterListener(this);
    }
}