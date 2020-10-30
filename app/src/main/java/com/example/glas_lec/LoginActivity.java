package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private EditText etlecid, etpassword;
    private ProgressBar loading;
    private Button btn_login;
    private TextView link_signup;

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create session manager
        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        //check if user was logged in
        if (sharedPrefManager.getLogin() == true) {
            startActivity(new Intent(LoginActivity.this, Dashboard.class));
            finish();
        } else {

            //initialize variable
            loading = findViewById(R.id.loading);
            etlecid = findViewById(R.id.etlecid);
            etpassword = findViewById(R.id.etpassword);
            link_signup = findViewById(R.id.link_signup);
            btn_login = findViewById(R.id.btn_signin);

            link_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            });

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                }
            });
        }
    }

    private void login() {
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);
        final String lec_id = this.etlecid.getText().toString().trim();
        final String password = this.etpassword.getText().toString().trim();

        if (TextUtils.isEmpty(lec_id)){
            etlecid.setError("Enter your ID");
            etlecid.requestFocus();
            loading.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);
            return;
        }

        if (TextUtils.isEmpty(password)){
            etpassword.setError("Enter password");
            etpassword.requestFocus();
            loading.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);
            return;
        }

        RequestQueue requestQueue =  Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    //if no error response
                    if (error == false){
                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        //store login session
                        sharedPrefManager.setLogin(true);
                        //store username
                        sharedPrefManager.setLecID(lec_id);
                        //store password
                        sharedPrefManager.setPassword(password);
                        //redirect activity to dashboard
                        //redirect activity to dashboard
                        startActivity(new Intent( LoginActivity.this, Dashboard.class));
                        finish();
                    }
                    //if error response
                    if(error==true) {
                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
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
                Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("lec_id", lec_id);
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
