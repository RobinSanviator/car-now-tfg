package com.example.carnowapp.utilidad;

import android.content.Context;
import android.content.SharedPreferences;

public class UtilidadPreferenciasUsuario {
    private static final String PREF_NAME = "usuario_pref";
    private static final String KEY_USER_LOGGED_IN = "user_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    // Método para inicializar SharedPreferences
    public static void init(Context contexto) {
        if (sharedPreferences == null) {
            sharedPreferences = contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    // Método para guardar el estado de inicio de sesión
    public static void guardarEstadoSesion(boolean estaLogueado, String userId) {
        editor.putBoolean(KEY_USER_LOGGED_IN, estaLogueado);
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    // Método para obtener el estado de inicio de sesión
    public static boolean estaLogueado() {
        return sharedPreferences.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    // Método para obtener el ID del usuario
    public static String obtenerIdUsuario() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Método para eliminar las preferencias cuando se cierra sesión
    public static void cerrarSesion() {
        editor.clear(); // Eliminar todas las preferencias guardadas
        editor.apply();
    }
}
