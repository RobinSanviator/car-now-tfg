package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.carnowapp.R;
import com.example.carnowapp.datos.repositorio.PreferenciasAjustesRepositorio;
import com.example.carnowapp.utilidad.AnimacionUtilidad;
import com.example.carnowapp.utilidad.NavegacionUtilidad;
import com.example.carnowapp.vistamodelo.AjustesVistaModelo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ActividadPantallaDeInicio es la actividad inicial de la aplicación que muestra una pantalla de inicio (SplashScreen).
 *
 * Esta actividad es responsable de mostrar la pantalla de inicio durante un breve periodo y luego redirigir a la
 * actividad de inicio de sesión (ActividadInicioSesion).
 */

public class PantallaDeInicioActividad extends AppCompatActivity {

    private AjustesVistaModelo ajustesVistaModelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ajustesVistaModelo = new ViewModelProvider(this).get(AjustesVistaModelo.class);
        ajustesVistaModelo.inicializar(this);

        // Aplica el idioma antes de inflar el layout
        ajustesVistaModelo.aplicarIdioma(this);

        // Establece el layout personalizado con el logo
        setContentView(R.layout.actividad_pantalla_de_inicio);

        // Obtiene la vista del logo
        ImageView logo = findViewById(R.id.logo_inicio);


        // Ejecuta la animación del logo
        AnimacionUtilidad.animarLogoSplash(logo,1700,250);

        // Retrasa el cambio de actividad para dejar ver la animación
        new Handler().postDelayed(() -> {
            FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
            if (usuario != null) {
                // Usuario autenticado, va a pantalla principal
                NavegacionUtilidad.redirigirA(PantallaDeInicioActividad.this, MenuPrincipalActividad.class);
            } else {
                // No hay usuario, va a inicio sesión
                NavegacionUtilidad.redirigirA(PantallaDeInicioActividad.this, InicioSesionActividad.class);
            }

        }, 2000); // Tiempo total del splash (animación + retardo)
    }
}

