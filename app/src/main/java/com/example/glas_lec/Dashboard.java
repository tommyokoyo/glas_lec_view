package com.example.glas_lec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    CardView create_table;
    CardView add_students;
    CardView add_location;
    CardView account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        create_table = findViewById(R.id.create_table);
        add_students = findViewById(R.id.add_student);
        add_location = findViewById(R.id.add_location);
        account = findViewById(R.id.account);

        create_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, CreateTable.class));

            }
        });

        add_students.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

        add_location.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                startActivity(new Intent( Dashboard.this, TakeCoordinates.class));

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