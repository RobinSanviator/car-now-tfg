package com.example.carnowapp.vista.fragmento;

import static com.example.carnowapp.utilidad.UtilidadTeclado.ocultarTeclado;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.utilidad.UtilidadDialogo;
import com.example.carnowapp.utilidad.UtilidadTeclado;
import com.example.carnowapp.vistamodelo.UsuarioVistaModelo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class FragmentoPerfil extends Fragment {

    private TextInputEditText etNombre, etEmail, etDni;
    private ShapeableImageView ivPerfil;
    private MaterialButton btnGuardarCambios;
    private ScrollView svContenedorPrincipal;
    private ConstraintLayout clContenedorPerfil;
    private String nombreOriginal = "", emailOriginal = "", dniOriginal = "";
    private UsuarioVistaModelo usuarioVistaModelo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.perfil_fragmento, container, false);
        inicializarVistas(vista);
        configurarCamposEditables();
        configurarVistaModelo();
        observarDatosUsuario();
        mostrarContenidoOcultoPorTeclado();
        return vista;
    }

    private void configurarCamposEditables() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Mostrar el botón solo si hay cambios en los campos
                boolean haCambiado = !etNombre.getText().toString().equals(nombreOriginal)
                        || !etEmail.getText().toString().equals(emailOriginal)
                        || !etDni.getText().toString().equals(dniOriginal);
                btnGuardarCambios.setVisibility(haCambiado ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        etNombre.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etDni.addTextChangedListener(watcher);

        // Desactivar la edición y el foco en el campo de correo electrónico
        etEmail.setFocusable(false);
        etEmail.setClickable(true);

        // Configurar el comportamiento al hacer clic en el correo electrónico
        etEmail.setOnClickListener(v -> {
            Snackbar.make(v, getString(R.string.no_modificar_email), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void inicializarVistas(View vista) {
        etNombre = vista.findViewById(R.id.et_nombre);
        etEmail = vista.findViewById(R.id.et_email);
        etDni = vista.findViewById(R.id.et_dni);
        ivPerfil = vista.findViewById(R.id.iv_perfil);
        btnGuardarCambios = vista.findViewById(R.id.btn_guardar_cambios);
        svContenedorPrincipal = vista.findViewById(R.id.sv_contenedor_perfil);
        clContenedorPerfil = vista.findViewById(R.id.cl_contenedor_perfil);
    }

    private void configurarVistaModelo() {
        usuarioVistaModelo = new ViewModelProvider(this).get(UsuarioVistaModelo.class);
        usuarioVistaModelo.cargarUsuarioActual();
    }

    private void observarDatosUsuario() {
        usuarioVistaModelo.getUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                nombreOriginal = usuario.getNombre();
                emailOriginal = usuario.getEmail();
                dniOriginal = usuario.getDni();
                etNombre.setText(nombreOriginal);
                etEmail.setText(emailOriginal);
                etDni.setText(dniOriginal);
            }
        });

        usuarioVistaModelo.getCargandoLiveData().observe(getViewLifecycleOwner(), cargando -> {
            if (cargando) {
                UtilidadDialogo.crearDialogoDeCarga(requireContext(), R.string.actualizando_perfil).show();
            } else {
                // Cerrar diálogo si está abierto (según implementación de UtilidadDialogo)
            }
        });

        usuarioVistaModelo.getMensajeErrorLiveData().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Snackbar.make(requireView(), mensaje, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void guardarCambios() {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre(etNombre.getText().toString().trim());
        usuarioActualizado.setEmail(etEmail.getText().toString().trim());
        usuarioActualizado.setDni(etDni.getText().toString().trim());
        usuarioVistaModelo.actualizarUsuario(usuarioActualizado);
    }

    private void mostrarContenidoOcultoPorTeclado() {
        UtilidadTeclado.ajustarPaddingAlMostrarTeclado(clContenedorPerfil, svContenedorPrincipal);
    }
}
