package com.firebase.authenticationapp;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.PendingIntent.getActivity;
import static android.graphics.Color.RED;
import static com.firebase.authenticationapp.HomeActivity.getChoice;
import static com.firebase.authenticationapp.Model_ticket.API_URI;
import static com.firebase.authenticationapp.Model_ticket.tab_details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.icu.text.SimpleDateFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.authenticationapp.MainActivity;
import com.firebase.authenticationapp.Model;
import com.firebase.authenticationapp.Model_ticket;
import com.firebase.authenticationapp.PutPDF;
import com.firebase.authenticationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 1;
    private static final int SELECT_PDF = 3;
    private static final int SELECT_GALLERY = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSION_CODE = 1000;

    private static String picturePath, lastDebit="0.00";
    private String path="", str="",selectedPDF="";
    private static String rcp_details="";
    private String currentPhotoPath;
    private String directory = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString();
    private File imageFile;

    private ImageView print_image;
    private Button upload;
    private static TextView textView2,textJson,textView,vsiret,vnomUniteLegale,vnomUsageUniteLegale,
            vprenomUsuelUniteLegale,vcategorieEntreprise,
            vlibelleCommuneEtablissement,vdenominationUsuelleEtablissement,message,result;

    private CardView container;
    private Dialog dialog;

    private static Uri imageUri,pdfUri,jsonName;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("json/");
    private ProgressBar progressBar;
    private Bitmap bitmap = null, bitmapOut = null;
    private DatabaseReference databaseReference;
    public static boolean valid = false;

    private TextRecognizer textRecognizer;

    private static String s_Siret="", denominationUsuelleEtablissement="",codePostalEtablissement="",libelleCommuneEtablissement="";
    private static String tmp = "null";
    private String s1 = "fdsdfsdf\nfdsdf123 123 123 12345\tfds \nfdsdf";

    private ArrayList<String> save = new ArrayList<String>();
    private static ArrayList<String> saveEquals = new ArrayList<String>();
    public static HashMap<String, String> hashMap = new HashMap<String, String>();

    public static Model_ticket md = new Model_ticket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        print_image = findViewById(R.id.print_image);
        upload = findViewById(R.id.upload);
        Button cancel = findViewById(R.id.cancel);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        /***************Initialize Array***************/
                        saveEquals.clear();
        /**********************************************/
        switch (getChoice()){
            //camera
            case 1 :
                try {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(
                                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                        ){
                            String[] permission = {
                                    CAMERA, WRITE_EXTERNAL_STORAGE
                            };
                            requestPermissions(permission, PERMISSION_CODE);
                        }else{
                            dispatchTakePictureIntent();
                        }
                    }else{
                        dispatchTakePictureIntent();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            //download
            case 2 :
                downLoadWithBytes();
                break;
            //gallery
            case 3 :
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_GALLERY);
        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

//                convertToPdf(imageUri);
//                uploadPDF();
                dialog = new Dialog(ResultActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.json_dialog);
                dialog.setCancelable(true); //  Sets whether the dialog is cancelable or not

                Button button = dialog.findViewById(R.id.button);
                Button button2 = dialog.findViewById(R.id.button2);

                result = dialog.findViewById(R.id.result);
                message = dialog.findViewById(R.id.message);
                vsiret = dialog.findViewById(R.id.vsiret);
                vnomUniteLegale = dialog.findViewById(R.id.vnomUniteLegale);
                vcategorieEntreprise = dialog.findViewById(R.id.vcategorieEntreprise);
                vlibelleCommuneEtablissement = dialog.findViewById(R.id.vlibelleCommuneEtablissement);
                vdenominationUsuelleEtablissement = dialog.findViewById(R.id.vdenominationUsuelleEtablissement);
                TextView debit = dialog.findViewById(R.id.debit);


                RequestQueue requestQueue = Volley.newRequestQueue(ResultActivity.this);
                StringRequest jsonObjectRequest = new StringRequest(
                        Request.Method.GET,
                        API_URI + md.getSiret(),
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("reponse", "res" +response);

                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonEtablissement = jsonObject.getJSONObject("etablissement");
                                    JSONObject jsonHeader = jsonObject.getJSONObject("header");

                                    message.setText("Statut : " +jsonHeader.getString("statut"));

                                    String siret = jsonEtablissement.getString("siret");
                                    hashMap.put("siret",siret);

                                    JSONObject jsonUniteLegale = jsonEtablissement.getJSONObject("uniteLegale");

                                        String nomUniteLegale = jsonUniteLegale.getString("nomUniteLegale");
                                        hashMap.put("nomUniteLegale",nomUniteLegale);

                                        String prenomUsuelUniteLegale = jsonUniteLegale.getString("prenomUsuelUniteLegale");
                                        hashMap.put("prenomUsuelUniteLegale",prenomUsuelUniteLegale);

                                        String categorieEntreprise = jsonUniteLegale.getString("categorieEntreprise");
                                        hashMap.put("categorieEntreprise",categorieEntreprise);

                                        String denominationUniteLegale = jsonUniteLegale.getString("denominationUniteLegale");

                                    JSONObject jsonAdresseEtablissement = jsonEtablissement.getJSONObject("adresseEtablissement");

                                        String libelleCommuneEtablissement = jsonAdresseEtablissement.getString("libelleCommuneEtablissement");
                                        hashMap.put("libelleCommuneEtablissement",libelleCommuneEtablissement);

                                        String codePostalEtablissement = jsonAdresseEtablissement.getString("codePostalEtablissement");
                                        hashMap.put("codePostalEtablissement",codePostalEtablissement);

                                    JSONArray jsonPeriodesEtablissement = jsonEtablissement.getJSONArray("periodesEtablissement");

                                        JSONObject jsonObjectPeriodesEtablissement = jsonPeriodesEtablissement.getJSONObject(0);

                                            denominationUsuelleEtablissement = jsonObjectPeriodesEtablissement.getString("denominationUsuelleEtablissement");
                                            if(denominationUsuelleEtablissement == "null"){
                                                denominationUsuelleEtablissement = denominationUniteLegale;
                                            }
                                            hashMap.put("denominationUsuelleEtablissement",denominationUsuelleEtablissement);


                                    /***************************View***************************/
                                    vsiret.setText("Siret: "+siret);
                                    vnomUniteLegale.setText("Fondateur: "+prenomUsuelUniteLegale+" "+nomUniteLegale);
                                    vcategorieEntreprise.setText("Catégorie Entreprise: "+categorieEntreprise);
                                    vlibelleCommuneEtablissement.setText("Adresse: " +codePostalEtablissement +" "
                                            +libelleCommuneEtablissement);
                                    vdenominationUsuelleEtablissement.setText("Nom Société: " + denominationUsuelleEtablissement);
                                    debit.setText("Debit : "+lastDebit);


                                    /***************************CREATE JSON FILE***************************/
                                    createJSONFile(getApplicationContext(), writeJSON(hashMap));
                                    /**
                                     * Don't put this method before those views
                                     */

                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(ResultActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        Log.i("reponse", "Error:" + error);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {

                        Log.i("reponse", "parseNetworkResponse: " + response.statusCode);
                        if (response.data == null || response.data.length == 0) {
                            Log.i("reponse", "parseNetworkResponse:  dataNull");
                            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        } else {
                            return super.parseNetworkResponse(response);
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Accept", "application/json");
                        headers.put("Authorization", "Bearer 039e9af8-b6be-3ef6-b151-b6b6a0197795");
                        return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
                    }
                };
                requestQueue.add(jsonObjectRequest);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        valid = false;
                        dialog.cancel();
                    }

                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        valid = false;
                        uploadToFirebase(jsonName);
                        dialog.cancel();
                    }
                });
                // Show the dialog
                dialog.show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                valid = false;
                onBackPressed();
            }
        });

    }


    /////////////////////////////////////////////CAMERA/////////////////////////////////////////////
    @SuppressLint("QueryPermissionsNeeded")
    public void dispatchTakePictureIntent() throws IOException {
        ActivityCompat.requestPermissions(ResultActivity.this,
                new String[]{CAMERA}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(ResultActivity.this,
                new String[]{MANAGE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(ResultActivity.this,
                new String[]{WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            Log.i("test2", "dispatchTakePictureIntent: " + photoFile.toString());
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.firebase.authenticationapp.fileprovider",
                        photoFile);
                Log.i("test3", "dispatchTakePictureIntent: " + photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                imageUri=photoURI;
                // galleryAddPic();
            }
        }
    }
    private File createImageFile () throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         //suffix
                storageDir      // directory
        );
        currentPhotoPath = image.getAbsolutePath();
        Log.i("test1", "dispatchTakePictureIntent: " + currentPhotoPath);
        return image;
    }
    /////////////////////////////////////////////ON_RESULT/////////////////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //CAMERA
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 700, 800);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            print_image.setImageBitmap(bitmap);
            textRecognition(bitmap);
        }
        if(requestCode == SELECT_GALLERY && resultCode == RESULT_OK && data !=null){
            try {
                imageUri = data.getData();
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 700, 800);
                print_image.setImageBitmap(bitmap);
                textRecognition(bitmap);
                if(requestCode == 1){
                    StorageReference ImageName = storageReference.child("image"+imageUri.getLastPathSegment());
                    ImageName
                            .putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast
                                            .makeText(ResultActivity.this, "Uploaded", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    /////////////////////////////////////////////ONREQUEST PERMISSION/////////////////////////////////////////////
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int grantResults[]) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ResultActivity.this,"Non autorisé", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /////////////////////////////////////////////DOWNLOAD_IMAGE/////////////////////////////////////////////
    private void downLoadWithBytes(){
        StorageReference imageRef = storageReference.child("Images/facture.jpg");
        long MAXBYTES = 1920*1080;
        imageRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                print_image.setImageBitmap(bitmap);
                Toast
                        .makeText(ResultActivity.this,
                                "Image downloaded from Firebase",
                                Toast.LENGTH_SHORT)
                        .show();
                textRecognition(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast
                        .makeText(ResultActivity.this,
                                "Downloading Failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
    ////////////////////////////////////////UPLOAD TO FIREBASE////////////////////////////////////////
    protected void uploadToFirebase(Uri uri){
        StorageReference fileRef = storageReference
                .child(
                        "json/"
                        + denominationUsuelleEtablissement.toString()
                        +"/"
                        +uri.getLastPathSegment()
                );
        Intent intent = new Intent();
        intent.setType("application/json");
        fileRef
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    ///////////////////////////CONVERT TO PDF///////////////////////////
    public void convertToPdf(Uri uri){
        File file = null;
        if(uri != null){
            try {
                bitmap = BitmapFactory
                        .decodeStream(
                                getContentResolver()
                                        .openInputStream(uri));
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 700, 800);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File root = new File(
                    directory, "FOLDER_FOR_PDF"
            );
            if(!root.exists()){
                root.mkdir();
            }
            file = new File(root,
                    "PDF_"
                            + System.currentTimeMillis()
                            + ".pdf");
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument
                    .PageInfo
                    .Builder(bitmap.getWidth(), bitmap.getHeight(), 1)
                    .create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                document.writeTo(fileOutputStream);
                /////////////URI FROM FILE/////////////
                pdfUri = Uri.fromFile(file);
                ///////////////////////////////////////
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast
                    .makeText(ResultActivity.this, "invalide", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    //////////////////////////////////////////UPLOAD PDF///////////////////////////////////////////
    public void uploadPDF(){
        Intent i = new Intent();
        path = directory + "/FOLDER_FOR_PDF/";
        i.setType("application/pdf");
        Toast
                .makeText(ResultActivity.this,
                        "SAVED\n"+path, Toast.LENGTH_SHORT)
                .show();
        StorageReference storagePdf = storageReference
                .child("PDF/"+pdfUri.getLastPathSegment());

        storagePdf
                .putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete()){
                            PutPDF putPDF = new PutPDF(path,pdfUri.toString());
                            databaseReference
                                    .child(databaseReference.push().getKey())
                                    .setValue(putPDF);
                            Toast.makeText(ResultActivity.this,
                                    "PDF Uploaded", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    //////////////////////////////////CONTRAST IMAGE//////////////////////////////////
    public static Bitmap createContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);
        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmOut;
    }
    /////////////////////////////TEXT RECOGNITION/////////////////////////////
    private void textRecognition(Bitmap bitmapIn){
        bitmapIn = createContrast(bitmapIn, 100);
        TextRecognizer textRecognizer = new TextRecognizer
                .Builder(getApplicationContext())
                .build();
        if(!textRecognizer.isOperational()){
            dialog = new Dialog(ResultActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.errors);
            dialog.setCancelable(true); //  Sets whether the dialog is cancelable or not

            Button button = dialog.findViewById(R.id.errorMessage);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }else{
            Frame frame = new Frame
                    .Builder()
                    .setBitmap(bitmapIn)
                    .build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<items.size(); i++){
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n*****\n");
                save.add(myItem.getValue());
            }
            lastDebit = checkNumber(save);
            if(compare(tab_details, sb.toString(), saveEquals)){
                if(!lastDebit.equals("0.00")){
                    valid = true;
                }
            };
            Log.i("saveEquals", saveEquals.toString());
            if(valid) {
                rcp_details = sb.toString();
                textView.setText(rcp_details);
                md.setSiret(checkSiret(sb.toString()));
                textView2.setText("VALIDE\nSiret : "+md.getSiret()+"\nDebit : "+ lastDebit);
            }
            else{
                Dialog errorD = new Dialog(ResultActivity.this);
                errorD.requestWindowFeature(Window.FEATURE_NO_TITLE);
                errorD.setContentView(R.layout.errors);
                errorD.setCancelable(true);
                TextView errorMessage = errorD.findViewById(R.id.errorMessage);
                TextView errorText = errorD.findViewById(R.id.errorText);
                Button ok = errorD.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        valid = false;
                        finish();
                    }
                });
                errorD.show();
            }
            Log.i("testsb", "\n\n--------------\n"+rcp_details+"\n"+saveEquals.toString());
        }
    }
    /////////////////////////////CHECK TEXT/////////////////////////////
    private static boolean checkText(String s1, String s2){
        boolean b = false;
        s1 = s1.toLowerCase();//La phrase
        s2 = s2.toLowerCase();//le mot à chercher
        Pattern pattern = Pattern.compile(s2);
        Matcher matcher = pattern.matcher(s1);
        while(matcher.find()) {
            b = true;
        }
        return b;
    }
    /////////////////////////////CHECK SIRET/////////////////////////////
    private static String checkSiret(String input){
        String output="Indéfini";
        Pattern pattern = Pattern.compile("\\d{14}|\\d{3}\\s\\d{3}\\s\\d{3}\\s\\d{5}");
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()){
            output = matcher.group().replaceAll("\\s","");
        }
        return output;
    }
    /////////////////////////////CHECK DEBIT/////////////////////////////
    private static String checkNumber(ArrayList<String> array){
        float debit1=0,debit2=0;
        Boolean b = false;
        String Sdebit="";
        ArrayList<Float> nb = new ArrayList<Float>();
        HashMap<Float, String> hashMap = new HashMap<>();
        String str1="null", str2="null";

        Pattern pattern1 = Pattern.compile("(total|Total|Tatal|totol|tatai|tatal|tota|Tota).*\\d+\\p{Punct}\\d{2}");
        Matcher matcher1 = null;
        matcher1 = pattern1.matcher(array.toString());
        while(matcher1.find()){
            b=true;
            str1 = matcher1.group().replaceAll("[a-zA-Z]|\\p{Punct}\\d*","");
            debit1 = Float.parseFloat(str1);
        }
        System.out.println("--------------------------");
        Pattern pattern2 = Pattern.compile("\\d+\\p{Punct}\\d{2}");
        Matcher matcher2 = null;
        for(int i=0; i<array.size(); i++){
            matcher2 = pattern2.matcher(array.get(i));
            while(matcher2.find()){
                str2 = matcher2.group().replaceAll("[a-zA-Z]|\\p{Punct}\\d*","");
                System.out.println("group "+i+">"+"  "+matcher2.group());
                nb.add(Float.parseFloat(str2));
                hashMap.put(Float.parseFloat(str2),matcher2.group());
            }
        }


        for(int i=0; i<nb.size(); i++){
            for(int j=0; j<nb.size(); j++){
                if(nb.get(i) > nb.get(j)){
                    debit2 = nb.get(i);
                }else j++;
            }
        }
        for(Map.Entry m : hashMap.entrySet()){
            if(m.getKey().equals(debit2)){
                Sdebit = m.getValue().toString();
            }
        }
        if(b){
            Sdebit = String.valueOf(debit1);
        }

        return Sdebit;
    }

    /////////////////////////////COMPARE TO/////////////////////////////
    private static boolean compare(String[][] tab, String s,ArrayList<String> saveEquals){
        boolean validC = false;
        int i=0, j=0;
        /**
         * tab_details doit être déclaré en dehors de MainActivity
         * Pour que l'appli support le tableau a 2 dimensions
         * Tu n'as qu'a l'importer ensuite
         */
        if(saveEquals.isEmpty()) {
            for (String y[] : tab_details) {
                j = 0;
                for (String x : y) {
                    if (checkText(s, tab[i][j])) {
                        if(saveEquals.isEmpty()){
                            saveEquals.add(tab_details[i][0]);
                        } else saveEquals.add(tab_details[i][0]);
                        break;
                    };
                    j++;
                }
                i++;
            }
        }
        if(saveEquals.size()>2){
            validC = true;
        }
        return validC;
    }

    //****************************WRITE JSON****************************//
    public String writeJSON(HashMap<String,String> hashMap){
        JSONObject json = new JSONObject();
        String jsonString ="";
        try {
            for(Map.Entry m : hashMap.entrySet()){
                json.put(m.getKey().toString(), m.getValue().toString());
            }
            json.put("debit", lastDebit);
            jsonString = json.toString();
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonString;
    }
    //****************************CREATE JSON FILE****************************//
    public void createJSONFile(Context context, String jsonString) throws IOException {

        File rootFolder = new File(directory,"/JSONFile");
        if(!rootFolder.exists()){
            rootFolder.mkdir();
        }
        File jsonFile = new File(
                rootFolder,
                "JSON_"
                        +denominationUsuelleEtablissement
                        +"_"
                        +codePostalEtablissement
                        +"_"
                        +libelleCommuneEtablissement
                        +"@"
                        +System.currentTimeMillis()+".json");
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(jsonString);
        writer.close();
        jsonName = Uri.fromFile(jsonFile);
        //or IOUtils.closeQuietly(writer);
    }

}