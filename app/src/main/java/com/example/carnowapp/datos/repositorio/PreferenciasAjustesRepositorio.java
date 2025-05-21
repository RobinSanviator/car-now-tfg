package com.example.carnowapp.datos.repositorio;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenciasAjustesRepositorio {
    private static final String NOMBRE_PREF = "preferencias_ajustes";
    private static final String CLAVE_IDIOMA = "idioma";
    private static final String CLAVE_ESTADO_SESION = "estado_sesion";


    private final SharedPreferences preferencias;

    public PreferenciasAjustesRepositorio(Context context) {
        preferencias = context.getApplicationContext().getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
    }

    public void guardarIdioma(String idioma) {
        preferencias.edit()
                .putString(CLAVE_IDIOMA, idioma)
                .apply();
    }

    public String obtenerIdioma() {
        return preferencias.getString(CLAVE_IDIOMA, "es");
    }


    public void guardarEstadoSesionActiva(boolean estaActivo) {
        preferencias.edit().putBoolean(CLAVE_ESTADO_SESION, estaActivo).apply();
    }

    public boolean obtenerEstadoSesionActiva() {
        return preferencias.getBoolean(CLAVE_ESTADO_SESION, false);
    }

    public void cerrarSesion() {
        guardarEstadoSesionActiva(false);
    }

    public void marcarSesionActiva() {
        guardarEstadoSesionActiva(true);
    }
}
