package com.example.carnowapp.vistamodelo;

import android.content.Context;
import android.content.res.Configuration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carnowapp.datos.repositorio.AutenticacionFirebaseRepositorio;
import com.example.carnowapp.datos.repositorio.PreferenciasAjustesRepositorio;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class AjustesVistaModelo extends ViewModel {

    private PreferenciasAjustesRepositorio preferenciasRepositorio;
    private AutenticacionFirebaseRepositorio autenticacionRepositorio;
    private final MutableLiveData<Boolean> sesionCerrada = new MutableLiveData<>();

    public void inicializar(Context context) {
        if (preferenciasRepositorio == null) {
            preferenciasRepositorio = new PreferenciasAjustesRepositorio(context);
        }
        if (autenticacionRepositorio == null) {
            autenticacionRepositorio = new AutenticacionFirebaseRepositorio();
            autenticacionRepositorio.inicializarGoogleSignIn(context);
        }
    }

    public void guardarIdioma(String idioma) {
        preferenciasRepositorio.guardarIdioma(idioma);
    }

    public String obtenerIdioma() {
        return preferenciasRepositorio.obtenerIdioma();
    }

    public void aplicarIdioma(Context contexto) {
        String idioma = obtenerIdioma();
        Locale nuevoLocale = new Locale(idioma);
        Locale.setDefault(nuevoLocale);

        Configuration config = new Configuration();
        config.setLocale(nuevoLocale);

        contexto.getResources().updateConfiguration(config, contexto.getResources().getDisplayMetrics());
    }

    public void marcarSesionActiva() {
        preferenciasRepositorio.marcarSesionActiva();
    }

    public void marcarSesionCerrada() {
        preferenciasRepositorio.cerrarSesion();
    }

    public boolean sesionEstaActiva() {
        return preferenciasRepositorio.obtenerEstadoSesionActiva();
    }

    public LiveData<Boolean> getSesionCerrada() {
        return sesionCerrada;
    }

    public void cerrarSesion(Context context) {
        autenticacionRepositorio.cerrarSesionGoogle(context, task -> {
            if (task.isSuccessful()) {
                autenticacionRepositorio.cerrarSesionFirebase();
                marcarSesionCerrada(); // Guardar en preferencias
                sesionCerrada.postValue(true); // Notificar al observador
            } else {
                // Manejo opcional de error (por ejemplo, log o LiveData de error)
                sesionCerrada.postValue(false);
            }
        });
    }

}