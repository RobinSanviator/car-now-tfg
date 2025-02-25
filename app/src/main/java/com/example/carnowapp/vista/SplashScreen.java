package com.example.carnowapp.vista;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instala el SplashScreen de AndroidX
        androidx.core.splashscreen.SplashScreen splashScreen = androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        //Mantener visible el splash screen visible para esta actividad
        splashScreen.setKeepOnScreenCondition(() -> false);
        //Acceder a la pantalla de introducción
        startActivity(new Intent(this, ActividadIntroduccion.class));
        finish();

    }

}