package com.example.glas_lec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStructure;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TakeCoordinates extends AppCompatActivity {
    SharedPrefManager sharedPrefManager;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private EditText ettablename;
    private ProgressBar progressBar;
    private TextView textlongitude, textlatitude, textlecid;
    private Spinner database_spin;
    private String year_of_study;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_coordinates);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        ettablename = findViewById(R.id.ettablename);
        textlongitude = findViewById(R.id.longitude);
        textlatitude = findViewById(R.id.latitude);
        textlecid = findViewById(R.id.lecid);
        database_spin = findViewById(R.id.database_spin);
        submit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.progressBar);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakeCoordinates.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        //create array adapter using string array and default spin layout
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.current_year, android.R.layout.simple_spinner_item);
        //specify th layout to use when it appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        database_spin.setAdapter(adapter);

        database_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_of_study = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitdata();
            }
        });

    }

    private void submitdata() {
        progressBar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);

        final String tablename = ettablename.getText().toString().trim();
        final String longitude = textlongitude.getText().toString().trim();
        final String latitude = textlatitude.getText().toString().trim();
        final String name = textlecid.getText().toString().trim();
        final String database = year_of_study;

        if (TextUtils.isEmpty(tablename)){
            ettablename.setError("Enter table Name");
            ettablename.requestFocus();
            progressBar.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_STOREVALUES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    //if no error
                    if (error == false) {
                        Toast.makeText(TakeCoordinates.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TakeCoordinates.this, Dashboard.class));
                        finish();
                    } else if (error == true) {
                        Toast.makeText(TakeCoordinates.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("database", database);
                params.put("table_name", tablename);
                params.put("name", name);
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                return params;
            }


            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("content-type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        double lat;
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(TakeCoordinates.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(TakeCoordinates.this).removeLocationUpdates(this);

                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                    if (latitude > 0){
                        DecimalFormat decimalFormat = new DecimalFormat("#.####");
                        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
                        latitude = new Double(decimalFormat.format(latitude));
                    } else {
                        double result = latitude * 10000;
                        latitude = (Math.ceil(result))/10000;

                    }
                     if (longitude > 0){
                        DecimalFormat decimalFormat = new DecimalFormat("#.####");
                        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
                        longitude = new Double(decimalFormat.format(longitude));
                    }else{
                         double result = longitude * 10000;
                         longitude = (Math.ceil(result))/10000;
                     }

                    textlongitude.setText(String.format(String.valueOf(longitude)));
                    textlatitude.setText(String.format(String.valueOf(latitude)));
                    textlecid.setText(String.format(sharedPrefManager.getLecID()));
                }


            }
        }, Looper.getMainLooper());
    }
}