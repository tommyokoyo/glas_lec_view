package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import static android.widget.AdapterView.*;

public class CreateTable extends AppCompatActivity {
    private ProgressBar loading;
    private Button submit;
    private Spinner database_spin;
    private EditText ettablename;
    private String year_of_study;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);

        ettablename = findViewById(R.id.ettablename);
        database_spin = findViewById(R.id.database_spin);
        submit = findViewById(R.id.submit);
        loading = findViewById(R.id.progressBar);

        alertdialog();

        //create array adapter using string array and default spin layout
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.current_year, android.R.layout.simple_spinner_item);
        //specify th layout to use when it appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        database_spin.setAdapter(adapter);

        database_spin.setOnItemSelectedListener(new OnItemSelectedListener() {
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

    private void alertdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTable.this);
        builder.setTitle("Table Name");
        builder.setMessage("Write Table Name as:courseunit_Lesson\ne.g SCSB100_L1 ");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }


    private void submitdata() {

        loading.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);

        final String database = year_of_study;
        final String table_name = ettablename.getText().toString().trim();

        if (TextUtils.isEmpty(table_name)){
            ettablename.setError("Enter Table Name");
            ettablename.requestFocus();
            loading.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(CreateTable.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_CREATETABLE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");

                    if (error == false){
                        Toast.makeText(CreateTable.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateTable.this,TakeCoordinates.class));
                        finish();
                    }
                    //if error response
                    if(error==true) {
                        Toast.makeText(CreateTable.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
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
                        Toast.makeText(CreateTable.this,obj.getString(""),Toast.LENGTH_SHORT).show();
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


}