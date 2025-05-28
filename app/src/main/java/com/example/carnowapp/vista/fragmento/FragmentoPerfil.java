package com.example.carnowapp.vista.fragmento;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.utilidad.DialogoUtilidad;
import com.example.carnowapp.utilidad.MensajesUtilidad;
import com.example.carnowapp.utilidad.SelectorImagenUtilidad;
import com.example.carnowapp.utilidad.TecladoUtilidad;
import com.example.carnowapp.utilidad.ValidacionUtilidad;
import com.example.carnowapp.vistamodelo.UsuarioVistaModelo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class FragmentoPerfil extends Fragment {

    private TextInputEditText etNombre, etEmail, etTelefono, etDni, etNuevaContrasena;
    private TextInputLayout tilNuevaContrasena;
    private View lineaDivisoraContrasenArriba, lineaDivisoraContrasenAbajo;
    private MaterialButton btnGuardarCambios,btnCambiarContrasena, btnGuardarContrasena;
    private ScrollView svContenedorPrincipal;
    private ConstraintLayout clContenedorPerfil;
    private String nombreOriginal = "", emailOriginal = "", telefonoOriginal = "", dniOriginal = "";
    private UsuarioVistaModelo usuarioVistaModelo;
    private AlertDialog dialogoCarga;
    private ActivityResultLauncher<String> permisoCamaraLauncher;
    private ActivityResultLauncher<Intent> lanzadorGaleria;
    private ActivityResultLauncher<Uri> lanzadorCamara;
    private Uri imagenSeleccionadaUri;
    private ShapeableImageView ivPerfil;
    private SelectorImagenUtilidad selectorImagenUtilidad;


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
        btnGuardarCambios.setOnClickListener(v -> guardarCambios()  );
        configurarCambioContrasena(vista);

        permisoCamaraLauncher = crearLanzadorPermisoCamara();
        lanzadorGaleria = crearLanzadorGaleria();
        lanzadorCamara = crearLanzadorCamara();

        inicializarSeleccionImagen();

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
                        || !etTelefono.getText().toString().equals(telefonoOriginal)
                        || !etDni.getText().toString().equals(dniOriginal);
                btnGuardarCambios.setVisibility(haCambiado ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        etNombre.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etTelefono.addTextChangedListener(watcher);
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
        etTelefono = vista.findViewById(R.id.et_telefono);
        etDni = vista.findViewById(R.id.et_dni);
        ivPerfil = vista.findViewById(R.id.iv_perfil);
        btnGuardarCambios = vista.findViewById(R.id.btn_guardar_cambios);
        svContenedorPrincipal = vista.findViewById(R.id.sv_contenedor_perfil);
        clContenedorPerfil = vista.findViewById(R.id.cl_contenedor_perfil);
        btnCambiarContrasena = vista.findViewById(R.id.btn_cambiar_contrasena);
        btnGuardarContrasena = vista.findViewById(R.id.btn_guardar_contrasena);
        tilNuevaContrasena = vista.findViewById(R.id.til_nueva_contrasena);
        etNuevaContrasena = vista.findViewById(R.id.et_nueva_contrasena);
        lineaDivisoraContrasenArriba = vista.findViewById(R.id.view_divisor_perfil4);
        lineaDivisoraContrasenAbajo = vista.findViewById(R.id.view_divisor_perfil5);
        ivPerfil = vista.findViewById(R.id.iv_perfil);

    }

    private void configurarVistaModelo() {
        usuarioVistaModelo = new ViewModelProvider(this).get(UsuarioVistaModelo.class);

        // Obtener UID del usuario actual desde FirebaseAuth
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null ?
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid != null && !uid.isEmpty()) {
            usuarioVistaModelo.cargarUsuario(uid);
        } else {
            // Manejar caso en que no haya usuario logueado, si aplica
            Log.e("FragmentoPerfil", "No hay usuario logueado para cargar el perfil.");
            MensajesUtilidad.mostrarMensaje(requireView(), R.string.error_usuario_no_valido);
        }
    }

    private void observarDatosUsuario() {
        usuarioVistaModelo.getUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {

            if (usuario != null) {
                nombreOriginal = usuario.getNombre() != null ? usuario.getNombre() : "";
                emailOriginal = usuario.getEmail() != null ? usuario.getEmail() : "";
                telefonoOriginal = (usuario.getTelefono() != null && usuario.getTelefono() != 0) ? String.valueOf(usuario.getTelefono()) : "";
                dniOriginal = usuario.getDni() != null ? usuario.getDni() : "";

                etNombre.setText(nombreOriginal);
                etEmail.setText(emailOriginal);
                etTelefono.setText(telefonoOriginal);
                etDni.setText(dniOriginal);

                btnGuardarCambios.setVisibility(View.GONE);

                String imagenUrl = usuario.getImagenUrl();
                if (imagenUrl != null && !imagenUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imagenUrl)
                            .placeholder(R.drawable.ic_perfil)
                            .error(R.drawable.ic_perfil)
                            .centerCrop()
                            .into(ivPerfil);
                } else {
                    ivPerfil.setImageResource(R.drawable.ic_perfil);
                }
            }

        });

        usuarioVistaModelo.getCargando().observe(getViewLifecycleOwner(), cargando -> {
            if (!isAdded() || getContext() == null) return;

            if (cargando) {
                if (dialogoCarga == null) {
                    dialogoCarga = DialogoUtilidad.crearDialogoDeCarga(requireContext(), R.string.actualizando_perfil);
                    try {
                        dialogoCarga.show();
                    } catch (WindowManager.BadTokenException e) {
                        dialogoCarga = null;
                    }
                }
            } else {
                if (dialogoCarga != null && dialogoCarga.isShowing()) {
                    dialogoCarga.dismiss();
                    dialogoCarga = null;
                }
            }
        });

        usuarioVistaModelo.getError().observe(getViewLifecycleOwner(), hayError -> {
            if (hayError != null && hayError) {
                MensajesUtilidad.mostrarMensaje(requireView(), R.string.no_hay_conexion_internet);
            }
        });
    }

    private void guardarCambios() {
        boolean dniValido = ValidacionUtilidad.validarDni(
                ((TextInputLayout) etDni.getParent().getParent()), etDni, requireContext()
        );

        boolean telefonoValido = ValidacionUtilidad.validarTelefono(
                ((TextInputLayout) etTelefono.getParent().getParent()), etTelefono, requireContext()
        );

        if (!dniValido || !telefonoValido) return;

        String telefonoStr = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        int telefono = 0;
        try {
            telefono = Integer.parseInt(telefonoStr);
        } catch (NumberFormatException e) {
            etTelefono.setError(getString(R.string.error_telefono_invalido));
            return;
        }

        Usuario usuarioBase = usuarioVistaModelo.getUsuarioLiveData().getValue();
        if (usuarioBase == null || usuarioBase.getFirebaseUID() == null || usuarioBase.getFirebaseUID().isEmpty()) {
            MensajesUtilidad.mostrarMensaje(requireView(), R.string.error_usuario_no_valido);
            return;
        }

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setFirebaseUID(usuarioBase.getFirebaseUID());
        usuarioActualizado.setNombre(etNombre.getText().toString().trim());
        usuarioActualizado.setEmail(etEmail.getText().toString().trim());
        usuarioActualizado.setTelefono(telefono);
        usuarioActualizado.setDni(etDni.getText().toString().trim());

        usuarioVistaModelo.actualizarUsuario(usuarioActualizado);
    }


    private void configurarCambioContrasena(View vista) {
        // Al inicio, ocultamos campos y botón guardar
        tilNuevaContrasena.setVisibility(View.GONE);
        btnGuardarContrasena.setVisibility(View.GONE);

        // Pulsar "Cambiar contraseña" muestra campos y botón guardar, oculta botón cambiar
        btnCambiarContrasena.setOnClickListener(v -> {
            tilNuevaContrasena.setVisibility(View.VISIBLE);
            btnGuardarContrasena.setVisibility(View.VISIBLE);
            lineaDivisoraContrasenArriba.setVisibility(View.VISIBLE);
            lineaDivisoraContrasenAbajo.setVisibility(View.VISIBLE);
            btnCambiarContrasena.setVisibility(View.GONE);

        });

        // Pulsar "Guardar" valida y cambia la contraseña
        btnGuardarContrasena.setOnClickListener(v -> {
            if (!ValidacionUtilidad.validarContrasena(tilNuevaContrasena, etNuevaContrasena, requireContext())) {
                return;
            }
            String nuevaContrasena = etNuevaContrasena.getText().toString().trim();
            usuarioVistaModelo.cambiarContrasena(nuevaContrasena);
        });

        // Observamos resultado éxito
        usuarioVistaModelo.getContrasenaActualizada().observe(getViewLifecycleOwner(), actualizado -> {
            if (actualizado != null && actualizado) {
                MensajesUtilidad.mostrarMensaje(requireView(), R.string.contrasena_actualizada_exito);
                etNuevaContrasena.setText("");

                tilNuevaContrasena.setVisibility(View.GONE);
                btnGuardarContrasena.setVisibility(View.GONE);
                btnCambiarContrasena.setVisibility(View.VISIBLE);
                lineaDivisoraContrasenArriba.setVisibility(View.GONE);
                lineaDivisoraContrasenAbajo.setVisibility(View.GONE);

                TecladoUtilidad.ocultarTeclado(requireContext(), vista);
            }
        });

        // Observamos errores
        usuarioVistaModelo.getErrorActualizacionContrasena().observe(getViewLifecycleOwner(), errorMensaje -> {
            if (errorMensaje != null && !errorMensaje.isEmpty()) {
                Snackbar.make(requireView(), errorMensaje, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private ActivityResultLauncher<String> crearLanzadorPermisoCamara() {
        return registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (selectorImagenUtilidad != null) {
                        selectorImagenUtilidad.onPermisoCamaraResultado(isGranted);
                    }
                });
    }

    private ActivityResultLauncher<Intent> crearLanzadorGaleria() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectorImagenUtilidad.onResultadoGaleria(result.getData());
                    }
                });
    }

    private ActivityResultLauncher<Uri> crearLanzadorCamara() {
        return registerForActivityResult(
                new ActivityResultContracts.TakePicture(), // Correcto para cámara
                result -> {
                    if (result && selectorImagenUtilidad != null) {
                        selectorImagenUtilidad.onResultadoCamara(true);
                    }
                });
    }

    private void inicializarSeleccionImagen() {
        selectorImagenUtilidad = new SelectorImagenUtilidad(
                requireActivity(),
                permisoCamaraLauncher,
                lanzadorGaleria,
                lanzadorCamara
        );

        selectorImagenUtilidad.setImagenSeleccionadaListener(uri -> {
            imagenSeleccionadaUri = uri;
            ivPerfil.setImageURI(uri);
            usuarioVistaModelo.subirImagenPerfil(uri);
        });

        ivPerfil.setOnClickListener(v -> selectorImagenUtilidad.mostrarDialogoSeleccionImagen());
    }


    private void mostrarContenidoOcultoPorTeclado() {
        TecladoUtilidad.ajustarPaddingAlMostrarTeclado(clContenedorPerfil, svContenedorPrincipal);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogoCarga != null && dialogoCarga.isShowing()) {
            dialogoCarga.dismiss();
            dialogoCarga = null;
        }
    }
}
