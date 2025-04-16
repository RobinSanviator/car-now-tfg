package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.carnowapp.R;
import com.example.carnowapp.persistencia.FirebaseAutenticacion;
import com.example.carnowapp.utilidad.UtilidadAnimacion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;



public class ActividadInicioSesion extends AppCompatActivity {

    private MaterialButton btnEmpezar;
    private ImageView ivLogo;
    private LinearLayout lyAcceso;
    private MaterialButton btnIniciarSesionGoogle,btnIniciarSesion;
    private TextView tvRegistro;
    private TextInputEditText etEmail, etContrasena;
    private TextInputLayout tilContrasena;
    private ConstraintLayout clContenedorPrincipal;
    private static final int RC_SIGN_IN = 9001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.inicio_sesion_actividad);


        iniciarAnimacion();
        irRegistro();
        mostrarContrasena();
        ocultarTeclado();
        iniciarSesionConGoogle();


    }

    /**
     * Inicializa las vistas y aplica animaciones al logo, botón de empezar y contenedor de acceso.
     * Configura el comportamiento del botón "Empezar" para expandir o contraer el contenedor.
     */
    private void iniciarAnimacion() {
        ivLogo = findViewById(R.id.iv_logo);
        btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        btnEmpezar = findViewById(R.id.btn_empezar);
        lyAcceso = findViewById(R.id.ly_acceso);


        if (ivLogo == null || btnEmpezar == null || lyAcceso == null) {
            Log.e("ActividadInicioSesion", "Alguna vista necesaria es null.");
            return;
        }

        // Animar logo
        UtilidadAnimacion.animarAumentoTamanyo(ivLogo, 900);

        // Animar botón empezar con rebote
        UtilidadAnimacion.animarConRebote(btnEmpezar, 500, true);

       // Animar contenedor
        btnEmpezar.setOnClickListener(v -> {
            pulsarAtras();
            if (lyAcceso.getVisibility() != View.VISIBLE) {
                UtilidadAnimacion.animarExpandir(lyAcceso);
            } else {
                UtilidadAnimacion.animarContraer(lyAcceso, ActividadInicioSesion.this);
            }
        });

        lyAcceso.setOnClickListener(v ->{ UtilidadAnimacion.animarContraer(lyAcceso, ActividadInicioSesion.this);});

    }

    private void ocultarTeclado(){
        clContenedorPrincipal = findViewById(R.id.cl_contenedor_principal_inicio_sesion);
        UtilidadAnimacion.ocultarTecladoAlTocar(this, clContenedorPrincipal);

    }



    /**
     * Personaliza el comportamiento del botón "Atrás".
     * Si el layout de acceso está visible, lo contrae. Si no, finaliza la actividad.
     */
    private void pulsarAtras() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Si el layout está visible, lo contraemos
                if (lyAcceso.getVisibility() == View.VISIBLE) {
                    UtilidadAnimacion.animarContraer(lyAcceso, ActividadInicioSesion.this);
                } else {
                    // Si no, realizamos el comportamiento estándar de la acción de "Atrás"
                    finish();
                }
            }
        });
    }

    /**
     * Muestra la contraseña caundo el sistema detecte texto en el campo de texto de contraseña.
     *
     */
    private void mostrarContrasena(){
        etContrasena = findViewById(R.id.et_contrasena_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);

        etContrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                } else {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void iniciarSesion(){

    }

    // Método para iniciar sesión con Google
    private void iniciarSesionConGoogle() {
        btnIniciarSesionGoogle = findViewById(R.id.btn_iniciar_sesion_google);
        btnIniciarSesionGoogle.setOnClickListener(v -> {
            FirebaseAutenticacion.iniciarSesionConGoogle(ActividadInicioSesion.this);
        });
    }

    // Manejar el resultado de la autenticación de Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificar que el requestCode sea el que corresponde a Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            FirebaseAutenticacion.manejarResultadoGoogleSignIn(requestCode, resultCode, data, this);
        }
    }




    private void irRegistro(){
        tvRegistro = findViewById(R.id.tv_no_cuenta);
        tvRegistro.setOnClickListener(v -> {
            UtilidadAnimacion.animarIzquierdaADerecha(ActividadInicioSesion.this, ActividadRegistro.class);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean mostrarLayout = getIntent().getBooleanExtra("mostrar_layout", false);
        if (mostrarLayout && lyAcceso!= null) {
            lyAcceso.setVisibility(View.VISIBLE);
        }
    }

}