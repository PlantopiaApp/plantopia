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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpScreen extends AppCompatActivity {

    EditText editTextEmail, editTextPassword1, editTextConfirmPassword;
    Button buttonSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser muUer;

    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

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

    public void openSignInScreen() {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }

    private void PerformAuth(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword1.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if(!email.matches(emailPattern)){
            editTextEmail.setError("Enter valid Email address!");
        }else if(password.isEmpty() || password.length()<6){
            editTextPassword1.setError("password must contain at least 6 characters!");
        }else if(!password.equals(confirmPassword)){
            editTextConfirmPassword.setError("Password Not match Both field!");
        }else{
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(SignUpScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(SignUpScreen.this,""+task.getException(), Toast.LENGTH_SHORT).show();
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