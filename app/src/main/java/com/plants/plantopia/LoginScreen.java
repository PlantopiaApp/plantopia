package com.plants.plantopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    private Button signup;
    private Button buttonLogin;
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

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        muUer=mAuth.getCurrentUser();

        // SignUp button
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupScreen();
            }
        });

        //SignIn button
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        buttonSignInGoogle = findViewById(R.id.buttonSignInGoogle);
        buttonSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, GoogleSignInActivity.class);
                startActivity(intent);
            }
        });
    }

    public void openSignupScreen() {
        Intent intent = new Intent(this, SignUpScreen.class);
        startActivity(intent);
    }

    private void performLogin(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();

        if(!email.matches(emailPattern)){
            editTextEmail.setError("Enter valid Email address!");
        }else if(password.isEmpty() || password.length()<6){
            editTextPassword1.setError("Enter Proper Password!");
        }else{
            progressDialog.setMessage("Please wait while Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
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
        Intent intent = new Intent(this, Location.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}