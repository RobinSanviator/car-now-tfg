package com.example.carnowapp.vista.fragmento;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.carnowapp.R;
import com.example.carnowapp.utilidad.DialogoUtilidad;
import com.example.carnowapp.utilidad.NavegacionUtilidad;
import com.example.carnowapp.vista.actividad.InicioSesionActividad;
import com.example.carnowapp.vistamodelo.AjustesVistaModelo;
import com.example.carnowapp.vistamodelo.AutenticacionVistaModelo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


public class FragmentoAjustes extends Fragment {

    private RelativeLayout rlIdioma, rlEditarPerfil, rlCerrarSesion, rlSalir;
    private AlertDialog dialogoDeCarga;
    private AjustesVistaModelo ajustesVistaModelo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ajustesVistaModelo = new ViewModelProvider(this).get(AjustesVistaModelo.class);
        ajustesVistaModelo.inicializar(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.ajustes_fragmento, container, false);
       inicializarListeners(vista);
        return vista;
    }

    private void inicializarListeners(View vista) {
        rlIdioma = vista.findViewById(R.id.rl_idioma_ajustes);
        rlCerrarSesion = vista.findViewById(R.id.rl_cerrar_sesion_ajustes);
        rlEditarPerfil = vista.findViewById(R.id.rl_editar_perfil_ajustes);
        rlSalir = vista.findViewById(R.id.rl_salir_ajustes);

        rlIdioma.setOnClickListener(v -> {
            mostrarSelectorDeIdioma(vista.getContext());
        });

        rlCerrarSesion.setOnClickListener(vCerrarSesion -> {
            Context contexto = vista.getContext();
            AlertDialog dialogoConfirmacion = DialogoUtilidad.crearDialogoConfirmacion(
                    contexto,
                    contexto.getString(R.string.cerrarSesion),
                    contexto.getString(R.string.cerrar_sesion_pregunta),
                    R.drawable.ic_cerrar_sesion,
                    contexto.getString(R.string.si),
                    (dialog, which) -> cerrarSesion(),
                    contexto.getString(R.string.no),
                    (dialog, which) -> dialog.dismiss()
            );
            dialogoConfirmacion.show();
        });

        rlEditarPerfil.setOnClickListener(vEditarPerfil -> {
            editarPerfil();
        });

        rlSalir.setOnClickListener(vSalir ->{
            Context contexto = vista.getContext();
            AlertDialog dialogoConfirmacion = DialogoUtilidad.crearDialogoConfirmacion(
                    contexto,
                    contexto.getString(R.string.salir),
                    contexto.getString(R.string.salir_pregunta),
                    R.drawable.ic_cerrar_sesion,
                    contexto.getString(R.string.si),
                    (dialog, which) -> salir(contexto),
                    contexto.getString(R.string.no),
                    (dialog, which) -> dialog.dismiss()
            );
            dialogoConfirmacion.show();
        });

    }

    private void cerrarSesion() {
        ajustesVistaModelo.getSesionCerrada().observe(getViewLifecycleOwner(), cerrada -> {
            if (Boolean.TRUE.equals(cerrada)) {
                NavegacionUtilidad.redirigirA(requireActivity(), InicioSesionActividad.class);
            }
        });

        ajustesVistaModelo.cerrarSesion(requireContext());
    }

    private void salir(Context contexto) {
        if (contexto instanceof Activity) {
            ((Activity) contexto).finishAffinity();
        }
    }


    private void editarPerfil(){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            Fragment fragmentoPerfil = new FragmentoPerfil();
            transaction.replace(R.id.fl_contenedor_menu_principal, fragmentoPerfil);
            transaction.addToBackStack(null);
            transaction.commit();

    }

    private void mostrarSelectorDeIdioma(Context contexto) {
        String[] idiomas = {
                contexto.getString(R.string.idioma_espanol),
                contexto.getString(R.string.idioma_ingles)
        };

        int[] iconos = {
                R.drawable.espana,
                R.drawable.uk
        };

        boolean[] tintarIconos = {false, false};

        AlertDialog dialogo = DialogoUtilidad.crearDialogoSelectorConIconos(
                contexto,
                contexto.getString(R.string.titulo_selector_idioma),
                idiomas,
                iconos,
                tintarIconos,
                (dialog, which) -> {
                    String idiomaSeleccionado = idiomas[which];
                    String codigoIdioma = (which == 0) ? "es" : "en";

                    AlertDialog confirmacion = DialogoUtilidad.crearDialogoConfirmacion(
                            contexto,
                            contexto.getString(R.string.cambiar_idioma_titulo),
                            contexto.getString(R.string.cambiar_idioma_pregunta, idiomaSeleccionado),
                            R.drawable.ic_idioma,
                            contexto.getString(R.string.si),
                            (d, w) -> cambiarIdioma(contexto, codigoIdioma),
                            contexto.getString(R.string.no),
                            (d, w) -> d.dismiss()
                    );
                    confirmacion.show();
                }
        );

        dialogo.show();
    }


    private void cambiarIdioma(Context contexto, String codigoIdioma) {
        dialogoDeCarga = DialogoUtilidad.crearDialogoDeCarga(contexto, R.string.cambiar_idioma);
        dialogoDeCarga.show();

        new Handler().postDelayed(() -> {
            ajustesVistaModelo.guardarIdioma(codigoIdioma);
            ajustesVistaModelo.aplicarIdioma(contexto);

            dialogoDeCarga.dismiss();

            Activity actividad = (Activity) contexto;
            Intent intent = actividad.getIntent();

            actividad.finish();
            actividad.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            actividad.startActivity(intent);
            actividad.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }, 1000);
    }

}