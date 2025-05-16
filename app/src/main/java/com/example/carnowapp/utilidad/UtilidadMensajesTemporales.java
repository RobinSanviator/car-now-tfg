package com.example.carnowapp.utilidad;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class UtilidadMensajesTemporales {

    public static void mostrarMensaje(View vistaRaiz, int idRecursoTexto) {
        Context contexto = vistaRaiz.getContext();
        Snackbar.make(vistaRaiz, contexto.getString(idRecursoTexto), Snackbar.LENGTH_LONG).show();
    }
}
