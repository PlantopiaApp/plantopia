package com.plants.plantopia;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.DialogInterface;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.app.AlertDialog;

public class Home extends AppCompatActivity {

    private Button btnIdentify;
    Button species;
    private String username;
    private TextView editTextUsername;
    private Dialog dialogg;
    ImageView homePlant, officePlant, outsidePlant;
    private int questionIndex = 0;
    private int yesCount = 0;
    private int noCount = 0;
    private String[] questions = {
            "Question 1/8: Does the plant receive direct sunrise?",
            "Question 2/8: Have you observed any recent changes in the plant's appearance?",
            "Question 3/8: Has the plant been wilting or showing signs of discoloration?",
            "Question 4/8: Does the pot have drainage holes?",
            "Question 5/8: Do you fertilize the plant?",
            "Question 6/8: Have you noticed any pests on the plant?",
            "Question 7/8: Do you remove dead leaves or flowers from the plant?",
            "Question 8/8: Do you water the plant regularly",
            // Add more questions as needed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the username from the Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME_KEY");

        // Find the TextView for username
        editTextUsername = findViewById(R.id.editTextUsername);

        // Set the username to the TextView
        if (username != null) {
            editTextUsername.setText("Hello " + username + ",");
        }

        // Open Button Identify
        btnIdentify = (Button) findViewById(R.id.btnIdentify);
        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlantIdentify();
            }
        });

        // Open Button species
        species = (Button) findViewById(R.id.species);
        species.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlantSpecies();
            }
        });

        Button questionButton = findViewById(R.id.diagnosis);

        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetQuestionnaire();
                showNextQuestion();
            }
        });

        homePlant = (ImageView) findViewById(R.id.homeplant);
        homePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePlant();
            }
        });

        officePlant = (ImageView) findViewById(R.id.imageView5);
        officePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOfficePlant();
            }
        });

        outsidePlant = (ImageView) findViewById(R.id.imageView6);
        outsidePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOutsidePlant();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.dashboard);

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
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    intent.putExtra("USERNAME_KEY", username);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

    }
    public void openPlantSpecies() {
        Intent intent = new Intent(this, com.plants.plantopia.SpeciesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if username is not null, and set it to the TextView
        if (username != null) {
            editTextUsername.setText("Hello " + username + ",");
        }
    }

    public void openPlantIdentify() {
        Intent intent = new Intent(this, com.plants.plantopia.PlantIdentify.class);
        startActivity(intent);
    }

    public void openOfficePlant() {
        Intent intent = new Intent(this, com.plants.plantopia.OfficePlant.class);
        startActivity(intent);
    }

    public void openOutsidePlant() {
        Intent intent = new Intent(this, com.plants.plantopia.OutsidePlants.class);
        startActivity(intent);
    }

    public void openHomePlant() {
        Intent intent = new Intent(this, com.plants.plantopia.HomePlants.class);
        startActivity(intent);
    }

    private void resetQuestionnaire() {
        questionIndex = 0;
        yesCount = 0;
        noCount = 0;
    }

    private void showNextQuestion() {
        if (questionIndex < questions.length) {
            dialogg = new Dialog(this);
            dialogg.setContentView(R.layout.customdialog);
            dialogg.setCancelable(false);

            // Find views inside the custom dialog layout
            ImageView dialogImage = dialogg.findViewById(R.id.dialogImage);
            TextView dialogTitle = dialogg.findViewById(R.id.dialogTitle);
            TextView dialogMessage = dialogg.findViewById(R.id.dialogMessage);
            Button yesButton = dialogg.findViewById(R.id.yesButton);
            Button noButton = dialogg.findViewById(R.id.noButton);
            Button cancelButton = dialogg.findViewById(R.id.cancelButton);

            dialogTitle.setText("Checking In...");
            dialogMessage.setText(questions[questionIndex]);

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    yesCount++;
                    questionIndex++;
                    dialogg.dismiss(); // Dismiss the dialog after a choice is made
                    showNextQuestion();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noCount++;
                    questionIndex++;
                    dialogg.dismiss(); // Dismiss the dialog after a choice is made
                    showNextQuestion();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogg.dismiss(); // Dismiss the dialog when the "Cancel" button is clicked
                }
            });

            dialogg.show();
        } else {
            if (noCount > 4) {
                showSuccessMessage();
            } else if (yesCount >= 4) {
                showFailMessage();
            } else {
                // Handle other cases if needed
            }
        }
    }
    private void showSuccessMessage() {
        // Use the custom layout for the success message dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View successDialogView = getLayoutInflater().inflate(R.layout.customdialog_success, null);
        builder.setView(successDialogView);

        ImageView successDialogImage = successDialogView.findViewById(R.id.successDialogImage);
        TextView successDialogTitle = successDialogView.findViewById(R.id.successDialogTitle);
        TextView successDialogMessage = successDialogView.findViewById(R.id.successDialogMessage);
        Button successDialogOKButton = successDialogView.findViewById(R.id.successDialogOKButton);

        successDialogTitle.setText("Congratulations!");
        successDialogMessage.setText("Your plant is in excellent condition and thriving! It seems you are taking great care of it. Keep up the good work!");

        // Customize the image as needed
        // successDialogImage.setImageResource(R.drawable.your_custom_image);

        builder.setCancelable(false); // Set cancelable property directly on the builder

        AlertDialog successDialog = builder.create();

        successDialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
            }
        });

        successDialog.setCancelable(false);
        successDialog.show();
    }


    private void showFailMessage() {
        // Use the custom layout for the fail message dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View failDialogView = getLayoutInflater().inflate(R.layout.custom_fail_dialog, null);
        builder.setView(failDialogView);

        ImageView failDialogImage = failDialogView.findViewById(R.id.failDialogImage);
        TextView failDialogTitle = failDialogView.findViewById(R.id.failDialogTitle);
        TextView failDialogMessage = failDialogView.findViewById(R.id.failDialogMessage);
        Button failDialogOKButton = failDialogView.findViewById(R.id.failDialogOKButton);

        failDialogTitle.setText("Sorry!");
        failDialogMessage.setText("Your plant is in poor condition. It seems you are not taking great care of it.");

        // failDialogImage.setImageResource(R.drawable.your_custom_image);

        builder.setCancelable(false); // Set cancelable property directly on the builder

        AlertDialog failDialog = builder.create();

        failDialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failDialog.dismiss();
            }
        });

        // Show the dialog using the AlertDialog instance
        failDialog.show();
    }
    }

