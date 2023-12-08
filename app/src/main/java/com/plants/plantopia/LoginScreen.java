package com.plants.plantopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.hotspot2.pps.HomeSp;
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

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    CallbackManager callbackManager;

    String username;
    private Button signup;
    private Button buttonLogin;
    Button buttonSignInFacebook;
    Button buttonSignInGoogle;

    EditText editTextEmail, editTextPassword1;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser muUer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Retrieve the username from the Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME_KEY");

        callbackManager = CallbackManager.Factory.create();

        // Signup with facebook
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
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
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);


        buttonSignInGoogle = (Button) findViewById(R.id.buttonSignInGoogle);
        buttonSignInGoogle.setOnClickListener(v -> openLocation());

        buttonSignInFacebook = (Button) findViewById(R.id.buttonSignInFacebook);
        buttonSignInFacebook.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Collections.singletonList("public_profile")));


        // Signup using email and password
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        muUer = mAuth.getCurrentUser();

        // Don't hava an account SignUp button
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupScreen();
            }
        });

        //Login button
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

    }

    //Email button open to location screen
    public void openLocation() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,1000);
    }

    public void openSignupScreen() {
        Intent intent = new Intent(this, SignUpScreen.class);
        startActivity(intent);
    }

    private void performLogin(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();

        if(!email.matches(emailPattern)){
            editTextEmail.setError("Enter a valid Email address!");
        }else if(password.isEmpty() || password.length()<6){
            editTextPassword1.setError("Enter a Proper Password!");
        }else{
            progressDialog.setMessage("Please wait while Logging in...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        muUer = mAuth.getCurrentUser();
                        progressDialog.dismiss();
                        // Retrieve the username from the FirebaseUser object
                        username = muUer.getDisplayName();
                        sendUserToNextActivity();
                        Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginScreen.this,""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
    }
    private void sendUserToNextActivity() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
        Intent intent = new Intent(LoginScreen.this,Location.class);
        startActivity(intent);
    }
}