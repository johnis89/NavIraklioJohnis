package com.example.johnis.naviraklio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.johnis.naviraklio.R;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
    protected void goBackHome(View view){
        Intent backHomeIntent= new Intent(Profile.this, Home.class);
        startActivity(backHomeIntent);
    }
}
