package com.plants.plantopia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;


public class PlantIdentify extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST = 104;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final int GALLERY_PERMISSION_REQUEST = 101;
    private static final int CAMERA_REQUEST = 102;
    private static final int GALLERY_REQUEST = 103;
    private static final String API_KEY = "OxAjFfQxUTDGwexU80oQ3TerFIBcr3I8R4UgGAVjHVOpSRnQzy";

    private ImageView imageView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_identify);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.scan);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Use resource ID values directly in conditions
                if (itemId == R.id.scan) {
                    return true;
                } else if (itemId == R.id.dashboard) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void identifyPlant(View view) {
        if (checkCameraPermission() && checkGalleryPermission()) {
            showImagePickerDialog();
        } else {
            requestPermissions();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST
        );
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkGalleryPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST
        );
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                GALLERY_PERMISSION_REQUEST
        );
    }

    private void requestPermissions() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
        }

        if (!checkGalleryPermission()) {
            requestGalleryPermission();
        }

        if (!checkStoragePermission()) {
            requestStoragePermissions();
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        String[] options = {"Camera", "Gallery"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    openCamera();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case GALLERY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Now, you can proceed with the image picker or other functionality
                    showImagePickerDialog();
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Uri imageUri = getImageUriFromExtras(extras);
                    identifyPlantFromUri(imageUri);
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                identifyPlantFromUri(imageUri);
            }
        }
    }

    private Uri getImageUriFromExtras(Bundle extras) {
        return Uri.parse(extras.get("image_uri").toString());
    }

    private okhttp3.Response makeRequestToEndPoint(JSONObject data) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data.toString());
        Request request = new Request.Builder()
                .url("https://plant.id/api/v3/identification")
                .method("POST", body)
                .addHeader("Api-Key", PlantIdentify.API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            return response;
        } catch ( Exception e ) {
            Toast.makeText(PlantIdentify.this, "Failed to identify plant. Please try again onResponse.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void identifyPlantFromUri(Uri imageUri) {
        try {
            if (checkStoragePermission()) {


                // Convert the imageUri to File
                File imageFile = new File(ImageUtils.getRealPathFromUri(PlantIdentify.this, imageUri));

                // Convert image to base64
                String base64Image = "data:image/jpg;base64," + ImageUtils.base64EncodeFromFile( imageFile );

                //Build body
                JSONObject data = new JSONObject();
                // Add image
                JSONArray images = new JSONArray();
                images.put(base64Image);

                data.put("images", images.toString()); // test values
                data.put("similar_images", true);
                data.put("longitude", 49); // test value
                data.put("latitude", 10); // test values


                // Make the API request
                Response response = this.makeRequestToEndPoint( data );

                if (response != null && response.isSuccessful()) {
                    String plantName = response.body().toString();

//                            .get(0).getPlantName();
                    // Clear previous result and display the new plant name
                    resultTextView.setText("Plant Name: " + plantName);
                } else {
                    // Log the error response for debugging
                    Log.e("API Error", "Error: " + response.code() + " - " + response.message());

                    // Display a more specific error message or handle the failure gracefully
                    Toast.makeText(PlantIdentify.this, "Failed to identify plant. Please try again onResponse.", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Request storage permissions
                requestStoragePermissions();
            }

        } catch (Exception exception) {
            Toast.makeText(this,"Plant Identify Error "+ exception.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }
}
