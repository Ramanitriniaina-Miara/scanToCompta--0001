package com.firebase.authenticationapp;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private static int choice = 0;
    public static int getChoice() {
        return choice;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        CardView download_image = findViewById(R.id.download_image);
        CardView camera = findViewById(R.id.camera);
        Button upload = findViewById(R.id.upload);
        CardView gallery_image = findViewById(R.id.gallery_image);
        CardView exit = findViewById(R.id.exit);
        ImageView cameraImg = findViewById(R.id.cameraImg);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        download_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;
                startActivity(new Intent(HomeActivity.this, ResultActivity.class));
            }
        });
        gallery_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                choice = 3;
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{WRITE_EXTERNAL_STORAGE }, PackageManager.PERMISSION_GRANTED);
                startActivity(new Intent(HomeActivity.this, ResultActivity.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog alertDialog = null;
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Voulez-vous vraiment quitter?");
                builder.setCancelable(true);
                builder.setIcon(R.mipmap.ic_launcher_round);
                AlertDialog finalAlertDialog = alertDialog;
                builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalAlertDialog.cancel();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }



}