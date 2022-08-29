package com.firebase.authenticationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.*;
import java.util.regex.Pattern;

//import com.google.firebase.auth.FirebaseAuth;

//import java.awt.font.NumericShaper;

//import java.util.Arrays;
//import java.util.List;


public class MainActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private TextView signup;
    private FirebaseAuth auth;

    private static TextInputLayout textInputEmail;
    private static TextInputLayout textInputUsername;
    private static TextInputLayout textInputPassword;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                            "\\@" +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                            "(" +
                            "\\." +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                            ")+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        password = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
//                validateEmail(txt_email);
                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(MainActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                } else if(txt_password.length() < 6){
                    Toast.makeText( MainActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                        Toast.makeText( MainActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginUser(txt_email, txt_password);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void loginUser(String email, String password){
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>(){
            @Override
           public void onSuccess(AuthResult authResult){
               Toast.makeText(MainActivity.this, "Connecté", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
           }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Utilisateur inexistant ou pas de connexion",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()){
            textInputEmail.setError("Champs vide");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            textInputEmail.setError("Veuillez vérifier votre adresse");
            return false;
        }
        else{
            textInputEmail.setError(null);
            return true;
        }
    }

}