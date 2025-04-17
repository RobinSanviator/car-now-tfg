package com.example.carnowapp.utilidad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UtilidadDialogo {

    @SuppressLint("SetTextI18n")
    public static AlertDialog crearDialogoDeCarga(Context contexto, String textoTitulo) {
        ProgressBar progressBar = new ProgressBar(contexto);
        LinearLayout layout = new LinearLayout(contexto);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(30, 30, 30, 30);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(progressBar);


        TextView mensaje = new TextView(contexto);
        mensaje.setText("Por favor espera...");
        mensaje.setPadding(30, 0, 0, 0);
        mensaje.setTextSize(16);
        layout.addView(mensaje);

        // Crear el MaterialAlertDialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(contexto)
                .setTitle(textoTitulo)
                .setView(layout)
                .setCancelable(false); // No permitir que se cierre el diálogo

        return builder.create();
    }

}
