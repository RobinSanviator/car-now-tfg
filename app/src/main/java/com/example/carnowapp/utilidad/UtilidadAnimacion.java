package com.example.carnowapp.utilidad;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.example.carnowapp.R;

/**
 * Clase utilitaria que proporciona métodos para realizar animaciones comunes en vistas.
 * Las animaciones incluyen aumento de tamaño, rebote, expansión y contracción de vistas.
 */
public class UtilidadAnimacion {

    /**
     * Realiza una animación de aumento de tamaño de la vista con un rebote opcional al final.
     *
     * @param vista           La vista que se va a animar.
     * @param duracion        La duración de la animación en milisegundos.
     * @param animacionRebote Si es verdadero, se inicia la animación de rebote al final de la animación.
     */
    public static void animarConRebote(View vista, long duracion, boolean animacionRebote) {

        vista.setVisibility(View.VISIBLE);
        vista.setScaleX(0f);
        vista.setScaleY(0f);
        vista.setAlpha(0f);

        vista.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(duracion)
                .setStartDelay(500)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (animacionRebote) {
                        iniciarReboteBoton(vista, 800);
                    }
                })
                .start();
    }

    /**
     * Inicia la animación de rebote de un botón, haciéndolo subir y bajar repetidamente.
     *
     * @param boton    La vista del botón a animar.
     * @param duracion La duración de cada ciclo de rebote.
     */
    public static void iniciarReboteBoton(View boton, long duracion) {

        if (boton == null) {
            Log.e("UtilidadAnimacion", "Botón nulo en la animación de rebote");
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(boton, "translationY", 0, -15, 0);
        animator.setDuration(duracion);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }


    /**
     * Realiza una animación para expandir la vista desde una posición desplazada hacia su posición original.
     *
     * @param vista La vista que se va a expandir.
     */
    public static void animarExpandir(View vista) {
        vista.setVisibility(View.VISIBLE);
        vista.setTranslationY(200f);
        vista.setAlpha(0f);

        vista.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(600)
                .start();
    }

    /**
     * Realiza una animación para contraer la vista, moviéndola hacia abajo y haciéndola invisible.
     *
     * @param vista La vista que se va a contraer.
     */
    public static void animarContraer(View vista, Context contexto) {

        // Verificar si el teclado está activo y ocultarlo si es necesario
        InputMethodManager inputMethodManager = (InputMethodManager) contexto.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            // Verifica si el teclado está visible y lo oculta
            View view = ((Activity) contexto).getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        vista.animate()
                .translationY(200f)  // Ajusta el valor para ocultar el layout
                .alpha(0f)
                .setDuration(600)
                .withEndAction(() -> vista.setVisibility(View.GONE))  // Ocultar después de la animación
                .start();
    }

    public static void animarIzquierdaADerecha(Context actividadActual, Class<?> claseDestino) {
        Intent intent = new Intent(actividadActual, claseDestino);
        actividadActual.startActivity(intent);

        if (actividadActual instanceof Activity) {
            ((Activity) actividadActual).overridePendingTransition(
                    R.anim.entrar_desde_derecha, R.anim.salir_hacia_izquierda
            );
        }
    }

    public static void animarDerechaAizquierda(Context actividadActual, Class<?> claseDestino) {
        Intent intent = new Intent(actividadActual, claseDestino);
        actividadActual.startActivity(intent);

        if (actividadActual instanceof Activity) {
            ((Activity) actividadActual).overridePendingTransition(
                    R.anim.entrar_desde_izquierda, R.anim.salir_hacia_derecha
            );
        }
    }


    public static void animarLogoSplash(View logo, long duracionGiro, long duracionSubida) {
        if (logo == null) return;

        logo.setAlpha(0f);
        logo.setRotationY(90f); // Empieza "de lado"
        logo.setTranslationY(0f);

        // Aparece con un giro 3D en eje Y
        logo.animate()
                .rotationY(0f)
                .alpha(1f)
                .setDuration(duracionGiro)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    // Luego sube lentamente y se desvanece
                    logo.animate()
                            .translationY(-logo.getHeight() * 1.5f)
                            .alpha(0f)
                            .setDuration(duracionSubida)
                            .setInterpolator(new AccelerateInterpolator())
                            .start();
                })
                .start();
    }

}

