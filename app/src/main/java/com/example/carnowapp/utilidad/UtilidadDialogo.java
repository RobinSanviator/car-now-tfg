package com.example.carnowapp.utilidad;

import android.app.Activity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class UtilidadDialogo {

    public static AlertDialog crearDialogoDeCarga(Activity actividad ,String textoTitulo) {
        // Crear ProgressBar programáticamente
        ProgressBar progressBar = new ProgressBar(actividad);
        LinearLayout layout = new LinearLayout(actividad);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(30, 30, 30, 30);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(progressBar);

        // Texto opcional
        TextView mensaje = new TextView(actividad);
        mensaje.setText("Por favor espera...");
        mensaje.setPadding(30, 0, 0, 0);
        mensaje.setTextSize(16);
        layout.addView(mensaje);

        AlertDialog dialogo = new AlertDialog.Builder(actividad)
                .setTitle(textoTitulo)
                .setView(layout)
                .setCancelable(false)
                .create();

        return dialogo;
    }
}
