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

    EditText editTextUsername, editTextEmail, editTextPassword1, editTextConfirmPassword;
    Button buttonSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    CallbackManager callbackManager;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button buttonSignUpFacebook;
    Button buttonSignUpGoogle;
    FirebaseAuth mAuth;
    FirebaseUser muUer;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        callbackManager = CallbackManager.Factory.create();

        // Signup with facebook
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(SignUpScreen.this, Location.class));
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


        buttonSignUpGoogle = (Button) findViewById(R.id.buttonSignUpGoogle);
        buttonSignUpGoogle.setOnClickListener(v -> openLocation());

        buttonSignUpFacebook = (Button) findViewById(R.id.buttonSignUpFacebook);
        buttonSignUpFacebook.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(SignUpScreen.this, Collections.singletonList("public_profile")));


        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        muUer=mAuth.getCurrentUser();

        //SignUp Button
        buttonSignUp = findViewById(R.id.buttonSignUp);
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

    public void openLocation() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,1000);
    }

    public void openSignInScreen() {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }

    private void PerformAuth(){
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (username.isEmpty()) {
            editTextUsername.setError("Enter a username");
        } else if (!email.matches(emailPattern)) {
            editTextEmail.setError("Enter valid Email address!");
        } else if (password.isEmpty() || password.length() < 6) {
            editTextPassword1.setError("Password must contain at least 6 characters!");
        } else if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Password does not match");
        }else{
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

                                progressDialog.dismiss();
                                sendUserToNextActivity();
                                Toast.makeText(SignUpScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

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
        Intent intent = new Intent(SignUpScreen.this,Location.class);
        startActivity(intent);
    }

    private void sendUserToNextActivity() {
        // Retrieve the username from the FirebaseUser object
        String username = muUer.getDisplayName();
        Intent intent = new Intent(this, LoginScreen.class);
        // Add the username as an extra to the Intent
        intent.putExtra("USERNAME_KEY", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
