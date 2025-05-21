package com.example.carnowapp.utilidad;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carnowapp.vista.actividad.InicioSesionActividad;

public class NavegacionUtilidad {


    public static void redirigirA(Activity origen, Class<?> destino) {
        Intent intent = new Intent(origen, destino);
        origen.startActivity(intent);
        origen.finish();
    }



    public static void configurarBotonAtras(AppCompatActivity actividad, @Nullable View vistaOcultable, @Nullable Class<?> destino,
            boolean usarAnimacion) {

        actividad.getOnBackPressedDispatcher().addCallback(
                actividad,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (vistaOcultable != null && vistaOcultable.getVisibility() == View.VISIBLE) {
                            AnimacionUtilidad.animarContraer(vistaOcultable, actividad);
                        } else if (destino != null) {
                            Intent intent = new Intent(actividad, destino);
                            intent.putExtra("mostrar_layout", true);
                            if (usarAnimacion) {
                                AnimacionUtilidad.animarDerechaAizquierda(actividad, destino);
                            }
                            actividad.startActivity(intent);
                            actividad.finish();
                        } else {
                            actividad.finish();
                        }
                    }
                });
    }

    public static void redirigirDeRegistroAInicio(Activity actividad){

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intentIrInicio = new Intent(actividad, InicioSesionActividad.class);
            intentIrInicio.putExtra("mostrar_layout", true);
            AnimacionUtilidad.animarDerechaAizquierda(actividad, InicioSesionActividad.class);
            startActivity(actividad, intentIrInicio, null);
            actividad.finish();
        }, 300);
    }


}
