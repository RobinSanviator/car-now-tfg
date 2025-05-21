package com.example.carnowapp.utilidad;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

public class TecladoUtilidad {

    public static void ocultarTecladoAlTocar(Context contexto, View vista) {
        vista.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputMethodManager inputMethodManager = (InputMethodManager) contexto.getSystemService(Context.INPUT_METHOD_SERVICE);
                View vistaActividad = ((Activity) contexto).getCurrentFocus();
                if (vistaActividad != null) {
                    inputMethodManager.hideSoftInputFromWindow(vistaActividad.getWindowToken(), 0);
                }
            }
            return false;
        });
    }

    public static void ajustarPaddingAlMostrarTeclado(View layout, View contenedor) {
        contenedor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Rect r = new Rect();
                contenedor.getWindowVisibleDisplayFrame(r);
                int screenHeight = contenedor.getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    layout.setPadding(0, 0, 0, keypadHeight); // Teclado visible
                } else {
                    layout.setPadding(0, 0, 0, 0); // Teclado oculto
                }
                return true;
            }
        });
    }

    /**
     * Oculta el teclado si está visible.
     *
     * @param contexto El contexto actual (puede ser Activity o Fragment).
     * @param vista    Una vista de referencia que tenga un token de ventana.
     */
    public static void ocultarTeclado(Context contexto, View vista) {
        if (contexto != null && vista != null) {
            InputMethodManager inputMethodManager= (InputMethodManager) contexto.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(vista.getWindowToken(), 0);
            }
        }
    }


}
