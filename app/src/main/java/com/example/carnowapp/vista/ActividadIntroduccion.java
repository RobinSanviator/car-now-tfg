package com.example.carnowapp.vista;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.example.carnowapp.R;
import com.google.android.material.button.MaterialButton;

public class ActividadIntroduccion extends AppCompatActivity {

    private MaterialButton botonEmpezar;
    private LottieAnimationView vistaAnimacion;
    private ImageView ivLogo;
    private LinearLayout llBotones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.actividad_introduccion);

        iniciarAnimacion();

    }

    private void iniciarAnimacion() {
        ivLogo = findViewById(R.id.iv_logo);
        botonEmpezar = findViewById(R.id.mb_empezar);
        llBotones = findViewById(R.id.linear_acceso);

        if (ivLogo != null) {
            animacionLogo(ivLogo);
        } else {
            Log.e("ActividadIntroduccion", "ivLogo es null.");
        }

        if (llBotones != null) {
            animacionBoton(llBotones);
        } else {
            Log.e("ActividadIntroduccion", "llBotones es null.");
        }

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
                    .setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        } catch (NullPointerException e) {
            Log.e("ActividadIntroduccion", "Error al animar el logo: La vista es null", e);
        } catch (IllegalArgumentException e) {
            Log.e("ActividadIntroduccion", "Error en la animación del logo: Parámetro inválido", e);
        }

    }

    private void animacionBoton(View vista) {
        try {
            vista.setVisibility(View.VISIBLE);
            vista.setTranslationY(200f);
            vista.setAlpha(0f);

            vista.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(1000)
                    .start();
        } catch (NullPointerException e) {
            Log.e("ActividadIntroduccion", "Error al animar los botones: La vista es null", e);
        } catch (IllegalArgumentException e) {
            Log.e("ActividadIntroduccion", "Error en la animación de los botones: Parámetro inválido", e);
        }
    }

}