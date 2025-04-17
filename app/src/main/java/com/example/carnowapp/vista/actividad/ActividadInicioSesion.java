package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.carnowapp.utilidad.UtilidadPreferenciasUsuario;
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
    private TextInputLayout tilEmail, tilContrasena;
    private ConstraintLayout clContenedorPrincipal;
    private static final int RC_SIGN_IN = 9001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.inicio_sesion_actividad);

        iniciarPreferenciasUsuario();
        iniciarAnimacion();
        irRegistro();
        mostrarContrasena();
        ocultarTeclado();
        iniciarSesion();
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
        UtilidadAnimacion.animarExpandir(ivLogo);

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
    private void iniciarPreferenciasUsuario() {
        UtilidadPreferenciasUsuario.init(this); // Inicializa las preferencias

        if (UtilidadPreferenciasUsuario.estaLogueado()) {
            // Redirige directamente si ya hay sesión activa
            startActivity(new Intent(this, ActividadMenuPrincipal.class));
            finish();
        }
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

    private void iniciarSesion() {
        btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        etEmail = findViewById(R.id.et_email_is);
        etContrasena = findViewById(R.id.et_contrasena_is);
        tilEmail = findViewById(R.id.til_email_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);

        UtilidadAnimacion.ocultarTecladoAlTocar(this, btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(v ->     {
            if (validarCampos()) {
                String email = etEmail.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();
                FirebaseAutenticacion.iniciarSesionConCorreoYContrasena(this, email, contrasena);
            }
        });
    }


    private boolean validarCampos() {
        tilEmail = findViewById(R.id.til_email_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);
        etEmail = findViewById(R.id.et_email_is);
        etContrasena = findViewById(R.id.et_contrasena_is);

        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        boolean camposValidos = true;

        if (email.isEmpty()) {
            tilEmail.setError("Introduce tu correo");
            camposValidos = false;
        } else {
            tilEmail.setError(null);
        }

        if (contrasena.isEmpty()) {
            tilContrasena.setError("Introduce tu contraseña");
            camposValidos = false;
        } else {
            tilContrasena.setError(null);
        }

        return camposValidos;
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