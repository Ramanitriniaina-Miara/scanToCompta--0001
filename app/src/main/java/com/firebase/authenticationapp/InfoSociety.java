package com.firebase.authenticationapp;

//import static com.firebase.authenticationapp.STActivity.my_siret;
//import static com.firebase.authenticationapp.STActivity.uri;

import static com.firebase.authenticationapp.STActivity.choice;
import static com.firebase.authenticationapp.STActivity.key;
import static com.firebase.authenticationapp.STActivity.my_key;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.toolbox.StringRequest;
import com.firebase.authenticationapp.model.*;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.Response.Listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoSociety extends AppCompatActivity {

    private TextView vsiret,vnomUniteLegale,vnomUsageUniteLegale,
            vprenomUsuelUniteLegale,vcategorieEntreprise,
            vlibelleCommuneEtablissement,vdenominationUsuelleEtablissement;
    private ScrollView scrollView;
    private TextView result;
    private ProgressBar loading;
    private CardView container;
    private ListView listView;
    private Button home;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String uri = "https://api.insee.fr/entreprises/sirene/V3/siret/";
    String url = uri + key;
    private boolean valid = false;
    private ArrayAdapter<String> adapter;

    /**
     * ****************WARNING****************
     *
     * YOU NEED TO UPDATE YOU TOKEN EVERY WEEK
     * @param savedInstanceState
     * ***************************************
     **/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_society);

        loading = findViewById(R.id.Loading);
        container = findViewById(R.id.container);
        result = findViewById(R.id.result);
        scrollView = findViewById(R.id.scrollView);
        home = findViewById(R.id.home);
        Button exit = findViewById(R.id.exit);

        vsiret = findViewById(R.id.vsiret);
        vnomUniteLegale = findViewById(R.id.vnomUniteLegale);
        vcategorieEntreprise = findViewById(R.id.vcategorieEntreprise);
        vlibelleCommuneEtablissement = findViewById(R.id.vlibelleCommuneEtablissement);
        vdenominationUsuelleEtablissement = findViewById(R.id.vdenominationUsuelleEtablissement);

        if(choice){
            key = "FR-" + key;
        }else key = "MG-" + key;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                        try {
                            Log.i("reponse", "res" +response);
//                            String s = response.getString("etablissement");
//                            result.setText(s);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonEtablissement = jsonObject.getJSONObject("etablissement");

                            String siret = jsonEtablissement.getString("siret");

                            JSONObject jsonUniteLegale = jsonEtablissement.getJSONObject("uniteLegale");
                            String nomUniteLegale = jsonUniteLegale.getString("nomUniteLegale");
                            String prenomUsuelUniteLegale = jsonUniteLegale.getString("prenomUsuelUniteLegale");
                            String categorieEntreprise = jsonUniteLegale.getString("categorieEntreprise");

                            JSONObject jsonAdresseEtablissement = jsonEtablissement.getJSONObject("adresseEtablissement");
                            String libelleCommuneEtablissement = jsonAdresseEtablissement.getString("libelleCommuneEtablissement");

                            JSONArray jsonPeriodesEtablissement = jsonEtablissement.getJSONArray("periodesEtablissement");
                            JSONObject jsonObjectPeriodesEtablissement = jsonPeriodesEtablissement.getJSONObject(0);
                            String denominationUsuelleEtablissement = jsonObjectPeriodesEtablissement.getString("denominationUsuelleEtablissement");


                            ///////////////////////////////Firestore///////////////////////////////
                            Map<String, Object> details = new HashMap<>();
                            details.put("Fondateur: ", prenomUsuelUniteLegale+" "+nomUniteLegale);
                            details.put("Catégorie Entreprise: ", categorieEntreprise);
                            details.put("Adresse: ", libelleCommuneEtablissement);
                            details.put("NomSociété: ", denominationUsuelleEtablissement);

                            db.document("societe/"+key);
                            db.collection("societe").document(key).set(details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(InfoSociety.this, "SAVED", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(InfoSociety.this, "FAILED", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            //Affichage
                            vsiret.setText("Siret: "+siret);
                            vnomUniteLegale.setText("Fondateur: "+prenomUsuelUniteLegale+" "+nomUniteLegale);
                            vcategorieEntreprise.setText("Catégorie Entreprise: "+categorieEntreprise);
                            vlibelleCommuneEtablissement.setText("Adresse: "+libelleCommuneEtablissement);
                            vdenominationUsuelleEtablissement.setText("Nom Société: "+denominationUsuelleEtablissement);

                            valid = true;

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(InfoSociety.this, "ERROR", Toast.LENGTH_SHORT).show();
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

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid == true){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFromChild(InfoSociety.this);
            }
        });
    }
}