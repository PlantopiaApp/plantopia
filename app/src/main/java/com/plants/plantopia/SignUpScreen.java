package com.plants.plantopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Collections;

public class SignUpScreen extends AppCompatActivity {

    // Text fields
    EditText editTextUsername, editTextEmail, editTextPassword1, editTextConfirmPassword;

    //Buttons
    Button buttonSignUp;
    Button buttonSignUpFacebook;
    Button buttonSignUpGoogle;

    // Regex to validate email
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    // Google Auth
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;
    FirebaseUser muUer;

    // Facebook Callback manager
    CallbackManager callbackManager;

    private Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        // Set all views to properties
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUpFacebook = (Button) findViewById(R.id.buttonSignUpFacebook);
        buttonSignUpGoogle = (Button) findViewById(R.id.buttonSignUpGoogle);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        // Setting up Signup with facebook
        // Create Callback manager
        callbackManager = CallbackManager.Factory.create();

        // Register facebook callback manager with Facebook callback function
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // If login is success we navigate to next screen
                startActivity(new Intent(SignUpScreen.this, Location.class));
                finish();
            }

            @Override
            public void onCancel() {
                // No need to handle cancel
            }

            @Override
            public void onError(@NonNull FacebookException exception) {
                Toast.makeText(SignUpScreen.this, "Registration Failed! " , Toast.LENGTH_SHORT).show();
            }
        });

        // Set click for facebook signup button.
        buttonSignUpFacebook.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(SignUpScreen.this, Collections.singletonList("public_profile")));

        // Set up Google login
        // Instantiate Google SignIn option, it is used to set the configuration for google login
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Set google SignIn client
        gsc = GoogleSignIn.getClient(this,gso);

        buttonSignUpGoogle.setOnClickListener(v -> performGoogleSignin());

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        muUer = mAuth.getCurrentUser();

        //SignUp Button click
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth();
            }
        });


        // SignIn button
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignInScreen();
            }
        });

    }

    public void performGoogleSignin() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,1000);
    }

    public void openSignInScreen() {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }

    private void PerformAuth(){
        // Retrieve text from the Text Objects
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Validating the text fields
        if (username.isEmpty()) {
            editTextUsername.setError("Enter a username");
        } else if (!email.matches(emailPattern)) {
            editTextEmail.setError("Enter valid Email address!");
        } else if (password.isEmpty() || password.length() < 6) {
            editTextPassword1.setError("Password must contain at least 6 characters!");
        } else if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Password does not match");
        }else{

            // Show registration message
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // User signup using firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Set display name (username)
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates);
                        }

                        // Dismiss registration message
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(SignUpScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpScreen.this, "Registration Failed! " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Code 1000 determines google signup
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Navigate to the Location screen
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(SignUpScreen.this,Location.class);
        startActivity(intent);
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(this, LoginScreen.class);

        // Retrieve the username from the FirebaseUser object
        String username;
        if (muUer != null) {
            username = muUer.getDisplayName();
            intent.putExtra("USERNAME_KEY", username );
        }
        // Add the username as an extra to the Intent
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
