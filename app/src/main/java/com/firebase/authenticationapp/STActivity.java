package com.firebase.authenticationapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class STActivity extends AppCompatActivity {

    private RadioButton rb_france;
    private RadioButton rb_madagascar;
    private FrameLayout frameLayout;
    public static EditText my_key;
    public static Button verify, cancel;
    public static String key;
    public static boolean choice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stactivity);

        rb_france = findViewById(R.id.rb_france);
        rb_madagascar = findViewById(R.id.rb_madagascar);
        frameLayout = findViewById(R.id.frame_layout);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        my_key = findViewById(R.id.my_key);
        verify = findViewById(R.id.verify);
        cancel = findViewById(R.id.cancel);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_france) {
                    Fragment fragment = new Choice_french();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.commit();
                    Log.i("radioButton", "radioButton CHECKED");
                    choice =true;

                } else if (checkedId == R.id.rb_madagascar) {
                    Fragment fragment = new Choice_mada();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.commit();
                    choice = false;
                    Log.i("radioButton", "radioButton CHECKED");
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                key = my_key.getText().toString();

                if(key.isEmpty()){
                    Toast.makeText( STActivity.this, "Champs vide", Toast.LENGTH_SHORT).show();
                }
                else if(key.length() != 14){
                    Toast.makeText( STActivity.this, "Numéro non validé", Toast.LENGTH_SHORT).show();
                }
                else {
                    key = my_key.getText().toString();
                    Toast.makeText( STActivity.this, "Numéro validé", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(STActivity.this, InfoSociety.class));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFromChild(STActivity.this);
            }
        });


    }
}