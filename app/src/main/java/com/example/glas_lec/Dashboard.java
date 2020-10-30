package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    CardView create_table;
    CardView add_students;
    CardView view_table;
    CardView account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        create_table = findViewById(R.id.create_table);
        add_students = findViewById(R.id.add_student);
        view_table = findViewById(R.id.view_table);
        account = findViewById(R.id.account);

        create_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        add_students.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

        view_table.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){

            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Dashboard.this, AccountActivity.class));


            }
        });


    }
}