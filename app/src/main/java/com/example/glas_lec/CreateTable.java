package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CreateTable extends AppCompatActivity {
    private ProgressBar loading;
    private EditText etdatabase, ettablename;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);

        etdatabase = findViewById(R.id.etdatabase);
        ettablename = findViewById(R.id.ettablename);
        submit = findViewById(R.id.submit);
        loading = findViewById(R.id.progressBar);

        //shows the alert dialog before going on
        opendialogue();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitdata();
            }
        });
    }

    private void submitdata() {
        loading.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);
        final String database = this.etdatabase.getText().toString().trim();
        final String table_name = this.ettablename.getText().toString().trim();

        RequestQueue requestQueue = Volley.newRequestQueue(CreateTable.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_CREATETABLE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");

                    if (error == false){
                        Toast.makeText(CreateTable.this,object.getString("message"),Toast.LENGTH_SHORT);
                        startActivity(new Intent(CreateTable.this,TakeCoordinates.class));
                    }
                    //if error response
                    if(error==true) {
                        Toast.makeText(CreateTable.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                loading.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("database", database);
                params.put("table_name", table_name);
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("content-type","application/x-www-form-urlencoded");
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    private void opendialogue() {
        //initilize dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTable.this);
        //set title
        builder.setTitle("Insturctions");
        //set message
        builder.setMessage("Write database name as: Year_1/Year_2  \n Write table name as: unitname_No of lesson e.g SCSB100_Lesson1 ");
        //set positive button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //initialize dialog
        AlertDialog alertDialog = builder.create();
        //show alert dialog
        alertDialog.show();
    }
}