package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import com.example.carnowapp.R;
import com.example.carnowapp.utilidad.UtilidadAnimacion;

/**
 * ActividadPantallaDeInicio es la actividad inicial de la aplicación que muestra una pantalla de inicio (SplashScreen).
 *
 * Esta actividad es responsable de mostrar la pantalla de inicio durante un breve periodo y luego redirigir a la
 * actividad de inicio de sesión (ActividadInicioSesion).
 */

public class ActividadPantallaDeInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Establece el layout personalizado con el logo
        setContentView(R.layout.actividad_pantalla_de_inicio);

        // Obtiene la vista del logo
        ImageView logo = findViewById(R.id.logo_inicio);


        // Ejecuta la animación del logo
        UtilidadAnimacion.animarLogoSplash(logo,1700,250);

        // Retrasa el cambio de actividad para dejar ver la animación
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, ActividadInicioSesion.class));
            finish();
        }, 2000); // Tiempo total del splash (animación + retardo)
    }
}

