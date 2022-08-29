package com.firebase.authenticationapp;

import static com.firebase.authenticationapp.MainActivity.validateEmail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.*;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth auth;
    private Button register;
    private TextView signin;
    private EditText username;
    private FirebaseFirestore db;

    private String society="undefined";

    private static String name="Name Undefined";

    private static String txt_email="Email Undefined";

    private static String txt_password="Password Undefined";


    public String getSociety() {
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        RegisterActivity.name = name;
    }

    public static String getTxt_email() {
        return txt_email;
    }

    public static void setTxt_email(String txt_email) {
        RegisterActivity.txt_email = txt_email;
    }

    public static String getTxt_password() {
        return txt_password;
    }

    public static void setTxt_password(String txt_password) {
        RegisterActivity.txt_password = txt_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        signin = (TextView) findViewById(R.id.signin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                name = username.getText().toString();
                txt_email = email.getText().toString();
                txt_password = password.getText().toString();
                HashMap<String, Object> userID= new HashMap<>();
                //validateEmail(txt_email);
                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)|| TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this, "Champ vide", Toast.LENGTH_SHORT).show();
                } else if(txt_password.length() < 6){
                    Toast.makeText( RegisterActivity.this, "mot de passe trop court", Toast.LENGTH_SHORT).show();
                } else{
                    registerUser(txt_email, txt_password);
                    userID.put("name", name);
                    userID.put("email", txt_email);
                    userID.put("society", society);
                    db.collection("userslist").document(txt_email)
                        .set(userID, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText( RegisterActivity.this, "Enregistré", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( RegisterActivity.this, "Erreur pendant l'enregistrement", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void registerUser(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Enregistré", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, STActivity.class));
                } else{
                    Toast.makeText(RegisterActivity.this, "Utilisateur existant ou pas de connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}