package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView link_signin;
    EditText etlecid, etname, etpassword;
    Button btn_signup;
    ProgressBar loading;

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create session manager
        sharedPrefManager = new SharedPrefManager(getApplicationContext());


        //initialize the views
        etlecid = findViewById(R.id.etlecid);
        etname = findViewById(R.id.etname);
        etpassword = findViewById(R.id.etpassword);
        btn_signup = findViewById(R.id.btn_signup);
        loading = findViewById(R.id.loading);

        link_signin = findViewById(R.id.link_signin);
        link_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( MainActivity.this, LoginActivity.class));
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });

    }

    private void regist() {
        loading.setVisibility(View.VISIBLE);
        btn_signup.setVisibility(View.GONE);

        final String lec_id = this.etlecid.getText().toString().trim();
        final String name = this.etname.getText().toString().trim();
        final String password = this.etpassword.getText().toString().trim();

        if (TextUtils.isEmpty(lec_id)){
            etlecid.setError("Enter your ID");
            etname.requestFocus();
            loading.setVisibility(View.GONE);
            btn_signup.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(name)){
            etname.setError("Enter username");
            etname.requestFocus();
            loading.setVisibility(View.GONE);
            btn_signup.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(password)){
            etpassword.setError("Enter password");
            etpassword.requestFocus();
            loading.setVisibility(View.GONE);
            btn_signup.setVisibility(View.VISIBLE);
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    //if no error response
                    if (error == false){
                        Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        //store login session
                        sharedPrefManager.setLogin(true);
                        //store username
                        sharedPrefManager.setLecID(lec_id);
                        //store password
                        sharedPrefManager.setPassword(password);
                        //redirect activity to dashboard
                        startActivity(new Intent( MainActivity.this, Dashboard.class));
                        finish();
                    }
                    //if error response
                    if(error==true) {
                        Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_signup.setVisibility(View.VISIBLE);
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
                Toast.makeText(MainActivity.this,"Register Error" +error.toString(),Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
                btn_signup.setVisibility(View.VISIBLE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("lec_id", lec_id);
                params.put("name", name);
                params.put("password", password);
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
