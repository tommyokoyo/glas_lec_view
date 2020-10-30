package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountActivity extends AppCompatActivity {

    Button btn_signout;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btn_signout = findViewById(R.id.btn_signout);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initilize dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                //set title
                builder.setTitle("Log out");
                //set message
                builder.setMessage("Are you sure you want to sign out");
                //set positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //set login to false
                        sharedPrefManager.setLogin(false);
                        //set username empty
                        sharedPrefManager.setLecID("");
                        //set password
                        //switch context
                        startActivity(new Intent( AccountActivity.this, LoginActivity.class));
                        //end activity
                        finish();
                    }
                });
                //set negative button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    //cancel dialog
                    dialog.cancel();
                    }
                });

                //initialize dialog
                AlertDialog alertDialog = builder.create();
                //show alert dialog
                alertDialog.show();


            }
        });


    }
}
