package com.example.carnowapp.vista.actividad;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.carnowapp.utilidad.UtilidadDialogo;
import com.example.carnowapp.utilidad.UtilidadMensajesTemporales;
import com.example.carnowapp.utilidad.UtilidadValidacion;
import com.example.carnowapp.vistamodelo.FirebaseAutenticacionVistaModelo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carnowapp.R;
import com.example.carnowapp.utilidad.UtilidadAnimacion;
import com.example.carnowapp.utilidad.UtilidadNavegacion;
import com.example.carnowapp.utilidad.UtilidadTeclado;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Method;

public class ActividadRegistro extends AppCompatActivity {

    private FirebaseAutenticacionVistaModelo vistaModelo;
    private ScrollView svContenedor;
    private LinearLayout lyFormularioRegistro;
    private TextInputEditText etConfirmarContrasena, etContrasena, etEmail, etNombre;
    private TextInputLayout til_nombre, tilEmail, tilContrasena, tilConfirmarContrasena;
    private TextView tvIniciarSesion;
    private MaterialButton btnRegistrar;
    private AlertDialog dialogoDeCarga, dialogoVerificacion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registro_actividad);



        mostrarContenidoOcultoPorTeclado();
        mostrarContrasena();
        realizarRegistro();
        irInicioSesionDesdeRegistro();
        pulsarAtrasYVolverInicio();
        ocultarTeclado();

    }

    private void mostrarContenidoOcultoPorTeclado() {
        lyFormularioRegistro = findViewById(R.id.ly_formulario_registro);
        svContenedor = findViewById(R.id.sv_contenedor_registro_principal);

        UtilidadTeclado.ajustarPaddingAlMostrarTeclado(lyFormularioRegistro, svContenedor);

    }

    private void ocultarTeclado(){
       lyFormularioRegistro = findViewById(R.id.ly_formulario_registro);
        btnRegistrar = findViewById(R.id.btn_registrar);
        UtilidadTeclado.ocultarTecladoAlTocar(this, lyFormularioRegistro);
        UtilidadTeclado.ocultarTecladoAlTocar(this, btnRegistrar);


    }

    private void realizarRegistro() {
        vistaModelo = new ViewModelProvider(this).get(FirebaseAutenticacionVistaModelo.class);

        btnRegistrar = findViewById(R.id.btn_registrar);
        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etContrasena = findViewById(R.id.et_contrasena);
        etConfirmarContrasena = findViewById(R.id.et_confirmar_contrasena);

        // Observadores (fuera del listener del botón)
        vistaModelo.getRegistroExitoso().observe(this, exito -> {
            if (dialogoDeCarga != null && dialogoDeCarga.isShowing()) dialogoDeCarga.dismiss();
            if (exito) {
                UtilidadMensajesTemporales.mostrarMensaje(findViewById(android.R.id.content), R.string.registro_exitoso);
                mostrarDialogoVerificacionCorreo();
            }
        });

        vistaModelo.getErrorMensajeRegistro().observe(this, mensaje -> {
            if (dialogoDeCarga != null && dialogoDeCarga.isShowing()) dialogoDeCarga.dismiss();
            if (mensaje != null) {
                UtilidadMensajesTemporales.mostrarMensaje(findViewById(android.R.id.content), R.string.error_registro);
            }
        });

        btnRegistrar.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();

            if (verificarCampos(nombre, email, contrasena, confirmarContrasena)) {
                dialogoDeCarga = UtilidadDialogo.crearDialogoDeCarga(ActividadRegistro.this, R.string.registrando_usuario);
                dialogoDeCarga.show();
                vistaModelo.registrarUsuarioCorreo(email, contrasena, nombre, this);
            }
        });
    }


    private void mostrarDialogoVerificacionCorreo() {
        final AlertDialog dialog = UtilidadDialogo.crearDialogoVerificacionCorreo(ActividadRegistro.this, (dialogInterface, i) -> {
            irInicioSesionDesdeRegistro(); // Acción al pulsar el botón
        });

        dialog.show();

        vistaModelo.getCorreoVerificado().observe(this, verificado -> {
            if (!isFinishing() && !isDestroyed()) {
                if (verificado) {
                    // Ejecutar método 'exito()' del tag
                    Object tag = dialog.getWindow().getDecorView().getTag();
                    if (tag != null) {
                        try {
                            Method metodo = tag.getClass().getDeclaredMethod("exito");
                            metodo.setAccessible(true);
                            metodo.invoke(tag);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    UtilidadMensajesTemporales.mostrarMensaje(findViewById(android.R.id.content), R.string.verifica_correo);
                }
            }
        });

        vistaModelo.verificarCorreo(); // Inicia verificación al mostrar
    }

    @Override
    protected void onResume() {
        super.onResume();
        vistaModelo.verificarCorreo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el diálogo si la actividad se destruye
        if (dialogoDeCarga != null && dialogoDeCarga.isShowing()) {
            dialogoDeCarga.dismiss();
        }
    }


    private boolean verificarCampos(String nombre, String email, String contrasena, String confirmarContrasena) {
        til_nombre = findViewById(R.id.til_nombre);
        tilEmail = findViewById(R.id.til_email);
        tilContrasena = findViewById(R.id.til_contrasena);
        tilConfirmarContrasena = findViewById(R.id.til_confirmar_contrasena);

        TextInputEditText etNombre = findViewById(R.id.et_nombre);
        TextInputEditText etEmail = findViewById(R.id.et_email);
        TextInputEditText etContrasena = findViewById(R.id.et_contrasena);

        boolean nombreValido = UtilidadValidacion.validarCampoVacio(til_nombre, etNombre, getString(R.string.error_nombre));
        boolean emailValido = UtilidadValidacion.validarEmail(tilEmail, etEmail, getString(R.string.error_correo), getString(R.string.error_formato_correo_no_valido));
        boolean contrasenaValida = UtilidadValidacion.validarCampoVacio(tilContrasena, etContrasena, getString(R.string.error_contrasena));
        boolean confirmacionValida = UtilidadValidacion.validarConfirmacionContrasena(tilConfirmarContrasena, contrasena, confirmarContrasena, this);

        return nombreValido && emailValido && contrasenaValida && confirmacionValida;
    }


    private void mostrarContrasena(){
        etContrasena = findViewById(R.id.et_contrasena);
        etConfirmarContrasena = findViewById(R.id.et_confirmar_contrasena);
        tilContrasena = findViewById(R.id.til_contrasena);
        tilConfirmarContrasena = findViewById(R.id.til_confirmar_contrasena);

        UtilidadValidacion.configurarMostrarContrasenaRegistro(
                etContrasena,
                tilContrasena,
                etConfirmarContrasena,
                tilConfirmarContrasena,
                this
        );

    }
    private void irInicioSesionDesdeRegistro() {
        tvIniciarSesion = findViewById(R.id.tv_tiene_cuenta);
        if (tvIniciarSesion != null) {
            tvIniciarSesion.setOnClickListener(v -> {
                Intent intentIrInicio = new Intent(ActividadRegistro.this, ActividadInicioSesion.class);
                intentIrInicio.putExtra("mostrar_layout", true);
                UtilidadAnimacion.animarDerechaAizquierda(ActividadRegistro.this, ActividadInicioSesion.class);
                startActivity(intentIrInicio);
                finish();
            });
        }

    }
    private void pulsarAtrasYVolverInicio() {
        UtilidadNavegacion.configurarBotonAtras(ActividadRegistro.this,  null, ActividadInicioSesion.class, true);
    }
}


