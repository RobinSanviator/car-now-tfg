package com.example.carnowapp.vista.fragmento;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carnowapp.R;
import com.example.carnowapp.persistencia.FirebaseAutenticacion;
import com.example.carnowapp.utilidad.UtilidadDialogo;
import com.example.carnowapp.utilidad.UtilidadPreferenciasUsuario;
import com.example.carnowapp.vista.actividad.ActividadInicioSesion;
import com.google.android.material.button.MaterialButton;


public class FragmentoAjustes extends Fragment {

    private MaterialButton btnCerrarSesion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.ajustes_fragmento, container, false);
        cerrarSesion(vista);
        return vista;
    }

    private void cerrarSesion(View vista){
        btnCerrarSesion = vista.findViewById(R.id.btn_cerrar_sesion);
        Context contexto = vista.getContext();

        btnCerrarSesion.setOnClickListener(view -> {
            UtilidadDialogo.crearDialogoDeCarga(contexto,"Cerrando sesión...");

            FirebaseAutenticacion.cerrarSesion(contexto);
            // Limpiar las preferencias
            UtilidadPreferenciasUsuario.cerrarSesion();

            startActivity(new Intent(getActivity(), ActividadInicioSesion.class));
            getActivity().finish();
        });

    }

}