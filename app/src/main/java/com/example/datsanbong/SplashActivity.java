package com.example.datsanbong;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){

            startActivity(
                    new Intent(
                            SplashActivity.this,
                            MainActivity.class
                    )
            );

        }else{

            startActivity(
                    new Intent(
                            SplashActivity.this,
                            LoginActivity.class
                    )
            );

        }

        finish();

    }

}