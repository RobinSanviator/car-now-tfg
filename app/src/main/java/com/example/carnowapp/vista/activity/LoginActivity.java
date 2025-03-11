package com.example.carnowapp.vista.activity;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carnowapp.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton botonEmpezar;
    private ImageView ivLogo;
    private LinearLayout linearBotones;
    private MaterialButton botonInicio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.actividad_register);

        iniciarAnimacion();

    }

    private void iniciarAnimacion() {
        ivLogo = findViewById(R.id.iv_logo);
        botonInicio = findViewById(R.id.mb_iniciar_sesion);
        botonEmpezar = findViewById(R.id.mb_empezar);
        linearBotones= findViewById(R.id.linear_acceso);

        if (ivLogo != null) {
            animacionLogo(ivLogo);
        } else {
            Log.e("ActividadIntroduccion", "ivLogo es null");
        }

        if (botonEmpezar != null) {
           animacionRebote(botonEmpezar, true);
        } else {
            Log.e("ActividadIntroduccion", "llBotones es null");
        }

        botonEmpezar.setOnClickListener(v -> animacionExpandirInicio(linearBotones, botonEmpezar));

    }

    private void animacionLogo(View vista) {
        try {
            vista.setVisibility(View.VISIBLE);
            vista.setScaleX(0f);
            vista.setScaleY(0f);
            vista.setAlpha(0f);

            vista.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        } catch (NullPointerException e) {
            Log.e("ActividadIntroduccion", "Error al animar el logo: La vista es null", e);
        } catch (IllegalArgumentException e) {
            Log.e("ActividadIntroduccion", "Error en la animación del logo: Parámetro inválido", e);
        }

    }



    private void animacionRebote(View vista, boolean animacionRebote) {
        vista.setVisibility(View.VISIBLE);
        vista.setScaleX(0f);
        vista.setScaleY(0f);
        vista.setAlpha(0f);

        vista.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(500)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (animacionRebote) {
                        iniciarReboteBoton(vista);
                    }
                })
                .start();
    }


    private void iniciarReboteBoton(View boton) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(boton, "translationY", 0, -15, 0);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }


    private void animacionExpandirInicio(View vista, MaterialButton botonEmpezar) {
        try {
            //Hacer invisible el boton de empezar una vez clicado e iniciar animación
            botonEmpezar.setVisibility((View.GONE));
            vista.setVisibility(View.VISIBLE);
            vista.setTranslationY(200f);
            vista.setAlpha(0f);

            vista.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(600)
                    .start();
        } catch (NullPointerException e) {
            Log.e("ActividadIntroduccion", "Error al animar los botones: La vista es null", e);
        } catch (IllegalArgumentException e) {
            Log.e("ActividadIntroduccion", "Error en la animación de los botones: Parámetro inválido", e);
        }
    }



}