package com.example.carnowapp.vista.actividad;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.carnowapp.R;
import com.example.carnowapp.persistencia.FirebaseAutenticacion;
import com.example.carnowapp.utilidad.UtilidadAnimacion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ActividadRegistro extends AppCompatActivity {

    private ScrollView svContenedor;
    private LinearLayout lyFormularioRegistro;
    private TextInputEditText etConfirmarContrasena, etContrasena, etEmail, etNombre;
    private TextInputLayout til_nombre, tilEmail, tilContrasena, tilConfirmarContrasena;
    private TextView tvIniciarSesion;
    private MaterialButton btnRegistrar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registro_actividad);

        mostrarContenidoOcultoPorTeclado();
        mostrarContrasena();
        realizarRegistro();
        irInicioSesion();
        pulsarAtras();
        ocultarTeclado();

    }

    private void mostrarContenidoOcultoPorTeclado() {
        lyFormularioRegistro = findViewById(R.id.ly_formulario_registro);
        svContenedor = findViewById(R.id.sv_contenedor_registro_principal);

        // Detectar si el teclado se está mostrando
        svContenedor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Rect r = new Rect();
                svContenedor.getWindowVisibleDisplayFrame(r);
                int screenHeight = svContenedor.getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // Si el teclado está visible
                    // Desplazar hacia arriba
                    lyFormularioRegistro.setPadding(0, 0, 0, keypadHeight);
                } else {
                    // Restaurar el padding si el teclado no está visible
                    lyFormularioRegistro.setPadding(0, 0, 0, 0);
                }
                return true;
            }
        });
    }

    private void ocultarTeclado(){
       lyFormularioRegistro = findViewById(R.id.ly_formulario_registro);
        btnRegistrar = findViewById(R.id.btn_registrar);
        UtilidadAnimacion.ocultarTecladoAlTocar(this, lyFormularioRegistro);
        UtilidadAnimacion.ocultarTecladoAlTocar(this, btnRegistrar);


    }


    private void realizarRegistro(){
        btnRegistrar = findViewById(R.id.btn_registrar);
        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etContrasena = findViewById(R.id.et_contrasena);
        etConfirmarContrasena = findViewById(R.id.et_confirmar_contrasena);

        btnRegistrar.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();


            if(verificarCampos(nombre, email, contrasena, confirmarContrasena)){
                FirebaseAutenticacion.registrarUsuario(this, email, contrasena);
            }

        });


    }

    private boolean verificarCampos(String nombre, String email, String contrasena, String confirmarContrasena) {
        til_nombre = findViewById(R.id.til_nombre);
        tilEmail = findViewById(R.id.til_email);
        tilContrasena = findViewById(R.id.til_contrasena);
        tilConfirmarContrasena = findViewById(R.id.til_confirmar_contrasena);

        boolean esValido = true;

        // Limpiar errores anteriores
        til_nombre.setError(null);
        tilEmail.setError(null);
        tilContrasena.setError(null);
        tilConfirmarContrasena.setError(null);

        if (nombre.isEmpty()) {
            til_nombre.setError("Introduce tu nombre");
            esValido = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Introduce tu email");
            esValido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Formato de email no válido");
            esValido = false;
        }

        if (confirmarContrasena.isEmpty()) {
            tilConfirmarContrasena.setError("Confirma tu contraseña");
            esValido = false;
        } else if (!confirmarContrasena.equals(contrasena)) {
            tilConfirmarContrasena.setHelperText("Las contraseñas no coinciden");
            tilConfirmarContrasena.setHelperTextColor(
                    ColorStateList.valueOf(ContextCompat.getColor(ActividadRegistro.this, R.color.color_error))
            );

            esValido = false;

        } else {
            tilConfirmarContrasena.setHelperText(null);
        }

        return esValido;

    }

    private void mostrarContrasena(){
        etContrasena = findViewById(R.id.et_contrasena);
        etConfirmarContrasena = findViewById(R.id.et_confirmar_contrasena);
        tilContrasena = findViewById(R.id.til_contrasena);
        tilConfirmarContrasena = findViewById(R.id.til_confirmar_contrasena);

        etContrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String contrasena = charSequence.toString();

                if (!contrasena.isEmpty()) {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

                    if (contrasena.length() < 6) {
                        tilContrasena.setHelperText("La contraseña debe tener al menos 6 caracteres");
                        tilContrasena.setHelperTextColor(
                                ColorStateList.valueOf(ContextCompat.getColor(ActividadRegistro.this, R.color.color_error))
                        );

                    } else {
                        tilContrasena.setHelperText(null);
                    }


                } else {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        etConfirmarContrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    tilConfirmarContrasena.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                } else {
                    tilConfirmarContrasena.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }
    private void irInicioSesion() {
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
    private void pulsarAtras() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ActividadRegistro.this, ActividadInicioSesion.class);
                intent.putExtra("mostrar_layout", true);
                UtilidadAnimacion.animarDerechaAizquierda(ActividadRegistro.this, ActividadInicioSesion.class);
                startActivity(intent);
                finish();
            }
        });
    }
}


