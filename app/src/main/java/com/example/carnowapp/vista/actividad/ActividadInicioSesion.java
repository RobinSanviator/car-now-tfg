package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.carnowapp.R;
import com.example.carnowapp.utilidad.UtilidadAnimacion;
import com.example.carnowapp.utilidad.UtilidadDialogo;
import com.example.carnowapp.utilidad.UtilidadNavegacion;
import com.example.carnowapp.utilidad.UtilidadPreferenciasUsuario;
import com.example.carnowapp.utilidad.UtilidadTeclado;
import com.example.carnowapp.utilidad.UtilidadValidacion;
import com.example.carnowapp.vistamodelo.FirebaseAutenticacionVistaModelo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


public class ActividadInicioSesion extends AppCompatActivity {

    private MaterialButton btnEmpezar;
    private ImageView ivLogo;
    private LinearLayout lyAcceso;
    private MaterialButton btnIniciarSesionGoogle,btnIniciarSesion;
    private TextView tvRegistro;
    private TextInputEditText etEmail, etContrasena;
    private TextInputLayout tilEmail, tilContrasena;
    private ConstraintLayout clContenedorPrincipal;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAutenticacionVistaModelo vistaModelo;
    private AlertDialog dialogoDeCarga;



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
        iniciarSesionGoogle();
        configurarGoogleSignIn();

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
            UtilidadNavegacion.configurarBotonAtras(ActividadInicioSesion.this, lyAcceso, null, false);
            if (lyAcceso.getVisibility() != View.VISIBLE) {
                UtilidadAnimacion.animarExpandir(lyAcceso);
            } else {
                UtilidadAnimacion.animarContraer(lyAcceso, ActividadInicioSesion.this);
            }
        });

        lyAcceso.setOnClickListener(v ->{ UtilidadAnimacion.animarContraer(lyAcceso, ActividadInicioSesion.this);});

    }
    private void iniciarPreferenciasUsuario() {
        UtilidadPreferenciasUsuario.init(this);
        //UtilidadPreferenciasUsuario.aplicarIdioma(this);
        if (UtilidadPreferenciasUsuario.estaLogueado()) {
            // Redirige directamente si ya hay sesión activa
            startActivity(new Intent(this, ActividadMenuPrincipal.class));
            finish();
        }
    }

    private void ocultarTeclado(){
        clContenedorPrincipal = findViewById(R.id.cl_contenedor_principal_inicio_sesion);
        UtilidadTeclado.ocultarTecladoAlTocar(this, clContenedorPrincipal);

    }


    /**
     * Muestra la contraseña caundo el sistema detecte texto en el campo de texto de contraseña.
     *
     */
    private void mostrarContrasena(){
        etContrasena = findViewById(R.id.et_contrasena_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);

       UtilidadValidacion.configurarMostrarContrasenaInicio(etContrasena, tilContrasena, this);
    }

    private void iniciarSesion() {
        vistaModelo = new ViewModelProvider(this).get(FirebaseAutenticacionVistaModelo.class);

        btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        etEmail = findViewById(R.id.et_email_is);
        etContrasena = findViewById(R.id.et_contrasena_is);
        tilEmail = findViewById(R.id.til_email_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);

        UtilidadTeclado.ocultarTecladoAlTocar(this, btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(v ->     {
            if (validarCampos()) {
                String email = etEmail.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();
                vistaModelo.iniciarSesionConCorreoYContrasena(email, contrasena);
                observarAutenticacion();
            }
        });
    }

    private void observarAutenticacion() {
        vistaModelo.getUsuarioAutenticado().observe(this, usuario -> {
            if (usuario != null) {
                dialogoDeCarga = UtilidadDialogo.crearDialogoDeCarga(this, R.string.iniciando_sesion);
                dialogoDeCarga.show();

                UtilidadPreferenciasUsuario.guardarEstadoSesion(usuario.getUid());
                startActivity(new Intent(this, ActividadMenuPrincipal.class));
                finish();
            }
        });

        vistaModelo.getCodigoErrorAutenticacion().observe(this, codigo -> {
            int mensajeResId;

            switch (codigo) {
                case USUARIO_NO_REGISTRADO:
                    mensajeResId = R.string.error_usuario_no_registrado;
                    break;
                case CREDENCIALES_INVALIDAS:
                    mensajeResId = R.string.error_credenciales_invalidas;
                    break;
                case CREDENCIALES_NO_VALIDAS:
                    mensajeResId = R.string.error_credenciales_no_validas;
                    break;
                default:
                    mensajeResId = R.string.error_autenticacion_generico;
                    break;
            }

            Snackbar.make(clContenedorPrincipal, getString(mensajeResId), Snackbar.LENGTH_LONG).show();
        });
    }

    public enum CodigoErrorAutenticacion {
        USUARIO_NO_REGISTRADO,
        CREDENCIALES_INVALIDAS,
        CREDENCIALES_NO_VALIDAS,
        ERROR_DESCONOCIDO
    }

    private void iniciarSesionGoogle() {
        // Inicializa el botón de Google SignIn
        btnIniciarSesionGoogle = findViewById(R.id.btn_iniciar_sesion_google);
        vistaModelo = new ViewModelProvider(this).get(FirebaseAutenticacionVistaModelo.class);
        // Verifica que el botón no sea null
        if (btnIniciarSesionGoogle != null) {
            btnIniciarSesionGoogle.setOnClickListener(v -> {
                dialogoDeCarga = UtilidadDialogo.crearDialogoDeCarga(this, R.string.iniciando_sesion);
                dialogoDeCarga.show();
                mostrarCuentaGoogle();
            });
        } else {
            Log.e("ActividadInicioSesion", "btnIniciarSesionGoogle es null");
        }
        observarEventos();
    }

    private void configurarGoogleSignIn() {
        vistaModelo.inicializarGoogleSignIn(this);

    }

    private void mostrarCuentaGoogle() {
        GoogleSignInClient googleSignInClient = vistaModelo.obtenerGoogleSignInClient();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task != null) {
                try {
                    GoogleSignInAccount cuenta = task.getResult(ApiException.class);
                    AuthCredential credential = GoogleAuthProvider.getCredential(cuenta.getIdToken(), null);

                    vistaModelo.iniciarSesionConGoogle(credential, cuenta.getDisplayName(), cuenta.getEmail(), this);
                } catch (ApiException e) {
                    Snackbar.make(clContenedorPrincipal, getString(R.string.error_inicio_sesion), Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(clContenedorPrincipal, getString(R.string.error_inicio_sesion), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void observarEventos() {
        vistaModelo.getLoginExitosoGoogle().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Snackbar.make(clContenedorPrincipal, getString(R.string.inicio_sesion_exitoso), Snackbar.LENGTH_SHORT).show();
                UtilidadNavegacion.redirigirA(ActividadInicioSesion.this, ActividadMenuPrincipal.class);
            }
        });

        vistaModelo.getErrorGoogle().observe(this, error -> {

            if (error != null) {
                Snackbar.make(clContenedorPrincipal, getString(R.string.error_inicio_sesion) + ": " + error, Snackbar.LENGTH_LONG).show();
            }
        });
    }



    private boolean validarCampos() {
        tilEmail = findViewById(R.id.til_email_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);
        etEmail = findViewById(R.id.et_email_is);
        etContrasena = findViewById(R.id.et_contrasena_is);

        boolean emailValido = UtilidadValidacion.validarCampoVacio(tilEmail, etEmail, getString(R.string.error_correo));
        boolean contrasenaValida = UtilidadValidacion.validarCampoVacio(tilContrasena, etContrasena, getString(R.string.error_contrasena));

        return emailValido && contrasenaValida;
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