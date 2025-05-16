package com.example.carnowapp.vista.fragmento;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.carnowapp.R;
import com.example.carnowapp.utilidad.UtilidadDialogo;
import com.example.carnowapp.utilidad.UtilidadNavegacion;
import com.example.carnowapp.utilidad.UtilidadPreferenciasUsuario;
import com.example.carnowapp.vista.actividad.ActividadInicioSesion;
import com.example.carnowapp.vistamodelo.FirebaseAutenticacionVistaModelo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


public class FragmentoAjustes extends Fragment {

    private RelativeLayout rlIdioma, rlEditarPerfil, rlCerrarSesion, rlSalir;
    private AlertDialog dialogoDeCarga;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            AlertDialog dialogoConfirmacion = UtilidadDialogo.crearDialogoConfirmacion(
                    contexto,
                    contexto.getString(R.string.cerrarSesion),
                    contexto.getString(R.string.cerrar_sesion_pregunta),
                    R.drawable.ic_cerrar_sesion,
                    contexto.getString(R.string.si),
                    (dialog, which) -> cerrarSesion(vista),
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
            AlertDialog dialogoConfirmacion = UtilidadDialogo.crearDialogoConfirmacion(
                    contexto,
                    contexto.getString(R.string.salir),
                    contexto.getString(R.string.salir_pregunta),
                    R.drawable.ic_cerrar_sesion,
                    contexto.getString(R.string.si),
                    (dialog, which) -> salir(vista),
                    contexto.getString(R.string.no),
                    (dialog, which) -> dialog.dismiss()
            );
            dialogoConfirmacion.show();
        });

    }

    private void cerrarSesion(View vista) {
        Context contexto = vista.getContext();

        FirebaseAutenticacionVistaModelo vistaModelo = new ViewModelProvider(requireActivity()).get(FirebaseAutenticacionVistaModelo.class);

        // Observar si se ha cerrado sesión correctamente
        vistaModelo.getSesionCerrada().observe(getViewLifecycleOwner(), cerrada -> {
            if (cerrada != null && cerrada) {
                UtilidadPreferenciasUsuario.cerrarSesion();
                UtilidadNavegacion.redirigirA(getActivity(), ActividadInicioSesion.class);
            }
        });

        // Llamar al cierre de sesión
        vistaModelo.cerrarSesion(contexto);
    }

    private void salir(View vista){
        Context contexto = vista.getContext();
        if (contexto instanceof Activity) {
            Activity activity = (Activity) contexto;
            activity.finishAffinity();
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


        Drawable icono = UtilidadDialogo.obtenerIconoTintado(contexto, R.drawable.ic_idioma);

        new MaterialAlertDialogBuilder(contexto)
                .setTitle(contexto.getString(R.string.titulo_selector_idioma))
                .setIcon(icono)
                .setItems(idiomas, (dialog, which) -> {
                    String idiomaSeleccionado = idiomas[which];
                    String codigoIdioma = (which == 0) ? "es" : "en";

                    UtilidadDialogo.crearDialogoConfirmacion(
                            contexto,
                            contexto.getString(R.string.cambiar_idioma_titulo),
                            contexto.getString(R.string.cambiar_idioma_pregunta, idiomaSeleccionado),
                            R.drawable.ic_idioma,
                            contexto.getString(R.string.si),
                            (d, w) -> {
                                AlertDialog dialogoDeCarga = UtilidadDialogo.crearDialogoDeCarga(contexto, R.string.cambiar_idioma);
                                dialogoDeCarga.show();

                                new Handler().postDelayed(() -> {
                                    actualizarIdioma(contexto, codigoIdioma, idiomaSeleccionado, dialogoDeCarga);
                                }, 1000);
                            },
                            contexto.getString(R.string.no),
                            (d, w) -> d.dismiss()
                    ).show();
                })
                .setBackground(UtilidadDialogo.crearColor(contexto, R.color.color_fondo_variante))
                .setCancelable(true)
                .show();
    }

    private static void actualizarIdioma(Context contexto, String idioma, String idiomaNombre, AlertDialog dialogoDeCarga) {
        UtilidadPreferenciasUsuario.guardarIdioma(idioma);
        UtilidadPreferenciasUsuario.aplicarIdioma(contexto);

        dialogoDeCarga.dismiss();

        Activity actividad = (Activity) contexto;
        Intent intent = actividad.getIntent();

        new Handler().postDelayed(() -> {
            actividad.finish();
            // Transición suave: salida
            actividad.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            actividad.startActivity(intent);

            actividad.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 300);
    }

}