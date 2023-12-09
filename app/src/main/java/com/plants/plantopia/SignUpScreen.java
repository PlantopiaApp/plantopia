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

    private final int GOOGLE_SIGN_IN_REQUEST_CODE = 1000;

    // Text fields
    EditText editTextUsername, editTextEmail, editTextPassword1, editTextConfirmPassword;

    //Buttons
    Button buttonSignUp, buttonSignUpFacebook, buttonSignUpGoogle, signIn;

    // Regular expression to validate email
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    // Google Auth
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    // Facebook Callback manager
    CallbackManager callbackManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        // Set all views to properties
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUpFacebook = findViewById(R.id.buttonSignUpFacebook);
        buttonSignUpGoogle = findViewById(R.id.buttonSignUpGoogle);
        signIn = findViewById(R.id.signIn);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        //Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        // Init Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //SignUp Button click
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUsingFireBase();
            }
        });

        // Setting up Signup with facebook
        // Create Callback manager to manage callbacks into Facebook SDK
        callbackManager = CallbackManager.Factory.create();

        // Register facebook callback manager with Facebook callback function
        LoginManager.getInstance().registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // If login is success we navigate to next screen
                navigateToEnableLocationActivity();
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

        // Set on click listener for facebook signup button.
        buttonSignUpFacebook.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(SignUpScreen.this, Collections.singletonList("public_profile")));

        // Set up Google login
        // Instantiate Google SignIn option, it is used to set the configuration for google login
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Get google SignIn client for signup
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //  Set on click listener for google signup
        buttonSignUpGoogle.setOnClickListener(v -> performGoogleSignin());

        // Login screen
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignInScreen();
            }
        });
    }

    private void signupUsingFireBase(){
        // Retrieve text from the Text Objects
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Validating the text fields
        if (username.isEmpty()) {
            editTextUsername.setError( "Enter a username" );
        } else if ( !email.matches(emailPattern) ) {
            editTextEmail.setError("Enter valid Email address!");
        } else if ( password.isEmpty() || password.length() < 6) {
            editTextPassword1.setError( "Password must contain at least 6 characters!" );
        } else if ( !password.equals(confirmPassword) ) {
            editTextConfirmPassword.setError( "Password does not match" );
        } else{
            // Show registration message progress dialog
            progressDialog.setMessage( "Please wait while Registration..." );
            progressDialog.setTitle( "Registration" );
            progressDialog.setCanceledOnTouchOutside( false );
            progressDialog.show();

            // User signup using firebase
            firebaseAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Set display name (username)
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates);
                        }

                        // Dismiss registration progress dialogue message
                        progressDialog.dismiss();
                        sendUserToLoginActivity();
                        Toast.makeText(SignUpScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpScreen.this, "Registration Failed! " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void performGoogleSignin() {
        //create google signin intent from google sdk
        Intent intent = googleSignInClient.getSignInIntent();
        // Start activity and wait for result
        startActivityForResult( intent, GOOGLE_SIGN_IN_REQUEST_CODE );
    }

    /**
     * This is a hook function called when new activity is started for result.
     * Once the result is recieved this method get called and execute the code.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        callbackManager.onActivityResult( requestCode, resultCode, data );
        super.onActivityResult( requestCode, resultCode, data );

        // Here we check the request Code 1000 to determine google signup intent result
        if( requestCode == GOOGLE_SIGN_IN_REQUEST_CODE ){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( data );
            try {
                task.getResult( ApiException.class );
                finish();
                navigateToEnableLocationActivity();
            } catch ( ApiException e ) {
                Toast.makeText( getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(this, LoginScreen.class);

        // Retrieve the username from the FirebaseUser object
        String username;
        if (firebaseUser != null) {
            username = firebaseUser.getDisplayName();
            intent.putExtra("USERNAME_KEY", username );
        }
        // Add the username as an extra to the Intent
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Navigate to the Location screen
    void navigateToEnableLocationActivity(){
        Intent intent = new Intent(SignUpScreen.this, Location.class);
        startActivity( intent );
    }

    public void openSignInScreen() {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }
}
