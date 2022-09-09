package com.example.tressmeadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.example.tressmeadminapp.Model.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    Button loginBtn;
    private TextInputEditText emailTxt,passwordTxt;
    private String email,password;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button) findViewById(R.id.login);
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        db = FirebaseDatabase.getInstance().getReference().child("Admin");

        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setClickable(false);
                if(validInfo()) {
                    loginBtn.setClickable(true);
                    pd = new ProgressDialog(Login.this);
                    pd.setMessage("Login....");
                    pd.show();
                    LoginUser();

                }
                else{
                    loginBtn.setClickable(true);
                }

            }
        });
    }

    private void LoginUser() {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            startActivity(new Intent(Login.this,MainScreen.class));
                            finish();
                            pd.dismiss();
                        }
                    }
                });

    }

    public boolean validInfo() {
        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();

        if (email.isEmpty()) {
            emailTxt.setError("Input email!");
            emailTxt.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Please input valid email!");
            emailTxt.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordTxt.setError("Input password!");
            passwordTxt.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordTxt.setError("password must be atleast 6 character!");
            passwordTxt.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(Login.this,MainScreen.class));
            finish();
        }
    }
}