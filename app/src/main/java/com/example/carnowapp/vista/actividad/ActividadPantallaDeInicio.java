package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * ActividadPantallaDeInicio es la actividad inicial de la aplicación que muestra una pantalla de inicio (SplashScreen).
 *
 * Esta actividad es responsable de mostrar la pantalla de inicio durante un breve periodo y luego redirigir a la
 * actividad de inicio de sesión (ActividadInicioSesion).
 */

 public class ActividadPantallaDeInicio extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen((this));
        super.onCreate(savedInstanceState);

        //Mantener la pantalla activa mientras se muestra el SplashScreen
        splashScreen.setKeepOnScreenCondition(() -> false);
        //Enviar a la actividad de introducción
        startActivity(new Intent(this, ActividadInicioSesion.class));
        finish();


    }


}