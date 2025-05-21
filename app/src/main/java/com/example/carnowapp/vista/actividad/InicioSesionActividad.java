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
import com.example.carnowapp.utilidad.ConstantesAutenticacion;
import com.example.carnowapp.utilidad.AnimacionUtilidad;
import com.example.carnowapp.utilidad.DialogoUtilidad;
import com.example.carnowapp.utilidad.NavegacionUtilidad;
import com.example.carnowapp.utilidad.TecladoUtilidad;
import com.example.carnowapp.utilidad.ValidacionUtilidad;
import com.example.carnowapp.vistamodelo.AjustesVistaModelo;
import com.example.carnowapp.vistamodelo.AutenticacionVistaModelo;
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


public class InicioSesionActividad extends AppCompatActivity {

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
    private AutenticacionVistaModelo autenticacionVistaModelo;
    private AlertDialog dialogoDeCarga;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.inicio_sesion_actividad);


        iniciarAnimacion();
        irRegistro();
        mostrarContrasena();
        ocultarTeclado();
        configuracionVistaModelo();

        observarAutenticacion();

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
        AnimacionUtilidad.animarExpandir(ivLogo);

        // Animar botón empezar con rebote
        AnimacionUtilidad.animarConRebote(btnEmpezar, 500, true);

       // Animar contenedor
        btnEmpezar.setOnClickListener(v -> {
            NavegacionUtilidad.configurarBotonAtras(InicioSesionActividad.this, lyAcceso, null, false);
            if (lyAcceso.getVisibility() != View.VISIBLE) {
                AnimacionUtilidad.animarExpandir(lyAcceso);
            } else {
                AnimacionUtilidad.animarContraer(lyAcceso, InicioSesionActividad.this);
            }
        });

        lyAcceso.setOnClickListener(v ->{ AnimacionUtilidad.animarContraer(lyAcceso, InicioSesionActividad.this);});

    }


    private void ocultarTeclado(){
        clContenedorPrincipal = findViewById(R.id.cl_contenedor_principal_inicio_sesion);
        TecladoUtilidad.ocultarTecladoAlTocar(this, clContenedorPrincipal);

    }


    /**
     * Muestra la contraseña caundo el sistema detecte texto en el campo de texto de contraseña.
     *
     */
    private void mostrarContrasena(){
        etContrasena = findViewById(R.id.et_contrasena_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);

       ValidacionUtilidad.configurarMostrarContrasenaInicio(etContrasena, tilContrasena, this);
    }

    private void configuracionVistaModelo(){
        // Inicializar ViewModel una sola vez
        autenticacionVistaModelo = new ViewModelProvider(this).get(AutenticacionVistaModelo.class);

        // Inicializa GoogleSignInClient en el ViewModel
        autenticacionVistaModelo.inicializarGoogleSignIn(this);
    }

    private void iniciarSesion() {
        btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        etEmail = findViewById(R.id.et_email_is);
        etContrasena = findViewById(R.id.et_contrasena_is);
        tilEmail = findViewById(R.id.til_email_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);
        clContenedorPrincipal = findViewById(R.id.cl_contenedor_principal_inicio_sesion);

        TecladoUtilidad.ocultarTecladoAlTocar(this, btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(v -> {
            if (validarCampos()) {
                String email = etEmail.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();
                mostrarDialogoDeCargaSiActividadActiva();
                autenticacionVistaModelo.iniciarSesionConCorreoYContrasena(email, contrasena);
            }
        });

        // Observa el resultado y errores del inicio con correo
        autenticacionVistaModelo.getUsuarioAutenticado().observe(this, usuario -> {
            if (usuario != null) {
                // Sesión iniciada con éxito
               // PreferenciasUsuarioUtilidad.guardarEstadoSesion(usuario.getUid());
                cerrarDialogoDeCarga();
                startActivity(new Intent(this, MenuPrincipalActividad.class));
                finish();
            }
        });

        autenticacionVistaModelo.getCodigoErrorAutenticacion().observe(this, codigo -> {
            cerrarDialogoDeCarga();
            int mensajeResId;

            switch (codigo) {
                case ConstantesAutenticacion.USUARIO_NO_REGISTRADO:
                    mensajeResId = R.string.error_usuario_no_registrado;
                    break;
                case ConstantesAutenticacion.CREDENCIALES_INVALIDAS:
                    mensajeResId = R.string.error_credenciales_invalidas;
                    break;
                case ConstantesAutenticacion.CREDENCIALES_NO_VALIDAS:
                    mensajeResId = R.string.error_credenciales_no_validas;
                    break;
                default:
                    mensajeResId = R.string.error_autenticacion_generico;
                    break;
            }

            Snackbar.make(clContenedorPrincipal, getString(mensajeResId), Snackbar.LENGTH_LONG).show();
        });
    }

    private void observarAutenticacion() {
        autenticacionVistaModelo.getUsuarioAutenticado().observe(this, usuario -> {
            if (usuario != null) {
                mostrarDialogoDeCargaSiActividadActiva();
                startActivity(new Intent(this, MenuPrincipalActividad.class));
                finish();
            }
        });

        autenticacionVistaModelo.getCodigoErrorAutenticacion().observe(this, codigo -> {
            int mensajeResId;

            switch (codigo) {
                case ConstantesAutenticacion.USUARIO_NO_REGISTRADO:
                    mensajeResId = R.string.error_usuario_no_registrado;
                    break;
                case ConstantesAutenticacion.CREDENCIALES_INVALIDAS:
                    mensajeResId = R.string.error_credenciales_invalidas;
                    break;
                case ConstantesAutenticacion.CREDENCIALES_NO_VALIDAS:
                    mensajeResId = R.string.error_credenciales_no_validas;
                    break;
                default:
                    mensajeResId = R.string.error_autenticacion_generico;
                    break;
            }

            Snackbar.make(clContenedorPrincipal, getString(mensajeResId), Snackbar.LENGTH_LONG).show();
        });
    }

    private void iniciarSesionGoogle() {
        btnIniciarSesionGoogle = findViewById(R.id.btn_iniciar_sesion_google);
        clContenedorPrincipal = findViewById(R.id.cl_contenedor_principal_inicio_sesion);

        if (btnIniciarSesionGoogle != null) {
            btnIniciarSesionGoogle.setOnClickListener(v -> {
                mostrarDialogoDeCargaSiActividadActiva();
                mostrarCuentaGoogle();
            });
        }

        // Observa el resultado y errores del login con Google
        autenticacionVistaModelo.getLoginExitosoGoogle().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                cerrarDialogoDeCarga();
                Snackbar.make(clContenedorPrincipal, getString(R.string.inicio_sesion_exitoso), Snackbar.LENGTH_SHORT).show();
                NavegacionUtilidad.redirigirA(this, MenuPrincipalActividad.class);
                finish();
            }
        });

        autenticacionVistaModelo.getErrorGoogle().observe(this, error -> {
            if (error != null) {
                cerrarDialogoDeCarga();
                Snackbar.make(clContenedorPrincipal, getString(R.string.error_inicio_sesion) + ": " + error, Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private void configurarGoogleSignIn() {
        autenticacionVistaModelo.inicializarGoogleSignIn(this);

    }

    private void mostrarCuentaGoogle() {
        GoogleSignInClient googleSignInClient = autenticacionVistaModelo.obtenerGoogleSignInClient();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount cuenta = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(cuenta.getIdToken(), null);
                // Inicia sesión con Google usando ViewModel (sin pasar Context aquí)
                autenticacionVistaModelo.iniciarSesionGoogle(credential, cuenta.getDisplayName(), cuenta.getEmail());
            } catch (ApiException e) {
                cerrarDialogoDeCarga();
                Snackbar.make(clContenedorPrincipal, getString(R.string.error_inicio_sesion), Snackbar.LENGTH_LONG).show();
            }
        }
    }


    private boolean validarCampos() {
        tilEmail = findViewById(R.id.til_email_is);
        tilContrasena = findViewById(R.id.til_contrasena_is);
        etEmail = findViewById(R.id.et_email_is);
        etContrasena = findViewById(R.id.et_contrasena_is);

        boolean emailValido = ValidacionUtilidad.validarCampoVacio(tilEmail, etEmail, getString(R.string.error_correo));
        boolean contrasenaValida = ValidacionUtilidad.validarCampoVacio(tilContrasena, etContrasena, getString(R.string.error_contrasena));

        return emailValido && contrasenaValida;
    }


    private void mostrarDialogoDeCargaSiActividadActiva() {
        if (!isFinishing() && !isDestroyed()) {
            dialogoDeCarga = DialogoUtilidad.crearDialogoDeCarga(this, R.string.iniciando_sesion);
            dialogoDeCarga.show();
        }
    }

    private void cerrarDialogoDeCarga() {
        if (dialogoDeCarga != null && dialogoDeCarga.isShowing()) {
            dialogoDeCarga.dismiss();
            dialogoDeCarga = null;
        }
    }

    private void irRegistro(){
        tvRegistro = findViewById(R.id.tv_no_cuenta);
        tvRegistro.setOnClickListener(v -> {
            AnimacionUtilidad.animarIzquierdaADerecha(InicioSesionActividad.this, RegistroActividad.class);
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

    @Override
    protected void onDestroy() {
        cerrarDialogoDeCarga();
        super.onDestroy();
    }

}