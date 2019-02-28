package com.example.vchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText displayName,email,password;
    Button createAccount;
    FirebaseAuth auth;
    Toolbar toolbar;
    ProgressBar progressBar;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        displayName = findViewById(R.id.displayNameET);
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        createAccount = findViewById(R.id.signUpButton);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressRegister);

        toolbar = findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setListeners();
    }

    void setListeners(){

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = displayName.getText().toString();
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                if(passwordString.length() < 6){
                    Toast.makeText(RegisterActivity.this,"Password should be at least 6 character long",Toast.LENGTH_SHORT).show();
                }

                if(name.equals("") && emailString.equals("") && passwordString.equals("")){
                    Toast.makeText(RegisterActivity.this,"Please enter the information correctly",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    auth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();

                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                //adding values to a hashMap
                                HashMap<String,String> hashValues = new HashMap<>();
                                hashValues.put("name",displayName.getText().toString());
                                hashValues.put("status","Hey there! I'm using VChat");
                                hashValues.put("image","default");
                                hashValues.put("thumb_image","default");

                                //adding values to the databaseReference
                                databaseReference.setValue(hashValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(RegisterActivity.this,"Some error occurred! Please try again.",Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }
        });

    }
}
