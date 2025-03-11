package com.example.carnowapp.vista.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen((this));
        super.onCreate(savedInstanceState);

        //Mantener la pantalla activa mientras se muestra el SplashScreen
        splashScreen.setKeepOnScreenCondition(() -> false);
        //Enviar a la actividad de introducción
        startActivity(new Intent(this, LoginActivity.class));
        finish();


    }


}