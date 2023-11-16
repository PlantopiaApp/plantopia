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

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.plants.plantopia.PlantIdentificationResponse;
import com.plants.plantopia.PlantSuggestion;
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

    private void identifyPlantFromUri(Uri imageUri) {
        if (checkStoragePermission()) {
            PlantIdApiService apiService = RetrofitClient.getClient().create(PlantIdApiService.class);

            // Convert the API key to RequestBody
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), "OxAjFfQxUTDGwexU80oQ3TerFIBcr3I8R4UgGAVjHVOpSRnQzy");

            // Convert the imageUri to File
            File imageFile = new File(ImageUtils.getRealPathFromUri(PlantIdentify.this, imageUri));

            // Convert the imageFile to RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);

            // Convert the RequestBody to MultipartBody.Part
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", imageFile.getName(), requestFile);

            // Make the API request
            Call<PlantIdentificationResponse> call = apiService.identifyPlant(apiKey, imagePart);
            call.enqueue(new Callback<PlantIdentificationResponse>() {
                @Override
                public void onResponse(Call<PlantIdentificationResponse> call, Response<PlantIdentificationResponse> response) {
                    // Hide loading indicator (ProgressBar) if added one

                    if (response.isSuccessful()) {
                        String plantName = response.body().getSuggestions().get(0).getPlantName();
                        // Clear previous result and display the new plant name
                        resultTextView.setText("Plant Name: " + plantName);
                    } else {
                        // Log the error response for debugging
                        Log.e("API Error", "Error: " + response.code() + " - " + response.message());

                        // Display a more specific error message or handle the failure gracefully
                        Toast.makeText(PlantIdentify.this, "Failed to identify plant. Please try again onResponse.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PlantIdentificationResponse> call, Throwable t) {
                    Log.e("API Error", "Failed to identify plant. Error: " + t.getMessage());
                    Toast.makeText(PlantIdentify.this, "Failed to identify plant. Please try again.", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            // Request storage permissions
            requestStoragePermissions();
        }

    }
}
