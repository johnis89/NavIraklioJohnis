package com.example.johnis.naviraklio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    protected void goProfile(View view){
        Intent profileIntent=new Intent(Home.this, Profile.class);
        startActivity(profileIntent);
    }
    protected void goCalendar(View view){
        Intent calendarIntent=new Intent(Home.this, Calendar.class);
        startActivity(calendarIntent);
    }
    protected void goMap(View view){
        Intent mapIntent=new Intent(Home.this, Map.class);
        startActivity(mapIntent);
    }
    protected void goLogout(View view){
        Intent logoutIntent=new Intent(Home.this, LoginActivity.class);
        startActivity(logoutIntent);
    }
}
