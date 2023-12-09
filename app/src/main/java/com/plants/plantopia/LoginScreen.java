package com.plants.plantopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.Collections;

public class LoginScreen extends AppCompatActivity {

    private final int GOOGLE_SIGN_IN_REQUEST_CODE = 1000;

    // Buttons
    private Button btnLogin, btnSignup, btnSignInFacebook, btnSignInGoogle;

    // Google Authentication
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;

    // Facebook authentication
    CallbackManager callbackManager;

    // Firebase auth
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String username;
    ProgressDialog progressDialog;
    EditText editTextEmail, editTextPassword1;
    // Regular expression to validate email
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Buttons
        btnSignInFacebook = findViewById(R.id.buttonSignInFacebook);
        btnLogin = findViewById(R.id.buttonLogin);
        btnSignInGoogle = findViewById(R.id.buttonSignInGoogle);
        btnSignInFacebook.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Collections.singletonList("public_profile")));
        btnSignup = findViewById(R.id.signup);

        // Text fields
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);

        progressDialog = new ProgressDialog(this);


        // Retrieve the username from the Intent and assign it to username
        username = getIntent().getStringExtra("USERNAME_KEY");

        // Setup firebase authentication proprties
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });


        // Setting up SignIn with facebook
        // Create Callback manager to manage callbacks into Facebook SDK
        callbackManager = CallbackManager.Factory.create();
        // Register facebook callback manager with Facebook callback function
        LoginManager.getInstance().registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startActivity(new Intent(LoginScreen.this, Location.class));
                finish();
            }
            @Override
            public void onCancel() {
                // No need to handle cancel
            }
            @Override
            public void onError(@NonNull FacebookException exception) {
                // No need to handle error
            }
        });

        // Set up Google login
        // Instantiate Google SignIn option, it is used to set the configuration for google login
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Get google SignIn client for signIn
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        btnSignInGoogle.setOnClickListener(v -> openGoogleSignInIntent());

        // Goto Signup screen
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupScreen();
            }
        });
    }

    private void performLogin(){
        // Retrieve input values from texts
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();

        // Validating the input values
        if( !email.matches( emailPattern )){
            editTextEmail.setError( "Enter a valid Email address!" );
        } else if( password.isEmpty() || password.length() < 6 ){
            editTextPassword1.setError("Enter a Proper Password!");
        } else{
            progressDialog.setMessage("Please wait while Logging in...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Process user sign in using firebase API
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if ( task.isSuccessful() ){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        // Retrieve the username from the FirebaseUser object
                        username = firebaseUser.getDisplayName();
                        openEnableLocationScreen();
                        Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(LoginScreen.this,"Login Failed. "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
    }
    private void openEnableLocationScreen() {
        Intent intent;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            intent = new Intent(this, Location.class);
        } else {
            intent = new Intent(this, Home.class);
        }
        intent.putExtra("USERNAME_KEY", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Email button open to location screen
    public void openGoogleSignInIntent() {
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
        //  if is successful user will be navigated to location activity screen

        if( requestCode == GOOGLE_SIGN_IN_REQUEST_CODE ){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( data );
            try {
                task.getResult( ApiException.class );
                finish();
                openEnableLocationScreen();
            } catch ( ApiException e ) {
                Toast.makeText( getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void openSignupScreen() {
        Intent intent = new Intent( this, SignUpScreen.class );
        startActivity( intent );
    }
}