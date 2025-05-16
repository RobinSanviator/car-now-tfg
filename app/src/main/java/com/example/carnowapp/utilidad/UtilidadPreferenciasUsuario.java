package com.example.carnowapp.utilidad;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class UtilidadPreferenciasUsuario {
    private static final String PREF_NAME = "preferencias_usuario"; // Nombre de las preferencias
    private static final String KEY_USER_LOGGED_IN = "usuario_logueado"; // Clave para el estado de sesión
    private static final String KEY_USER_ID = "id_usuario"; // Clave para el ID del usuario
    private static final String KEY_USER_LANGUAGE = "idioma_usuario"; // Clave para el idioma
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

    public static void guardarEstadoSesion(String userId) {
        guardarEstadoSesion(true, userId);
    }

    // Método para obtener el estado de inicio de sesión
    public static boolean estaLogueado() {
        return sharedPreferences.getBoolean(KEY_USER_LOGGED_IN, false); // Devuelve 'false' si no está logueado
    }

    // Método para obtener el ID del usuario
    public static String obtenerIdUsuario() {
        return sharedPreferences.getString(KEY_USER_ID, null); // Devuelve 'null' si no hay ID guardado
    }

    // Guardar idioma seleccionado
    public static void guardarIdioma(String idioma) {
        editor.putString(KEY_USER_LANGUAGE, idioma);
        editor.apply();
    }

    // Obtener idioma guardado (por defecto 'es' si no hay)
    public static String obtenerIdioma() {
        return sharedPreferences.getString(KEY_USER_LANGUAGE, "es");
    }

    // Aplicar idioma guardado al contexto (antes de setContentView)
    public static void aplicarIdioma(Context context) {
        init(context); // Asegura que SharedPreferences esté inicializado
        String idioma = obtenerIdioma();
        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }


    // Método para eliminar las preferencias cuando se cierra sesión
    public static void cerrarSesion() {
        editor.clear();
        editor.apply();
    }
}
