package com.example.carnowapp.utilidad;

import android.content.Context;
import android.view.View;

import com.example.carnowapp.R;
import com.google.android.material.snackbar.Snackbar;

public class MensajesUtilidad {

    /**
     * Muestra un mensaje en forma de Snackbar usando un recurso de texto.
     */
    public static void mostrarMensaje(View vistaRaiz, int idRecursoTexto) {
        Context contexto = vistaRaiz.getContext();
        Snackbar.make(vistaRaiz, contexto.getString(idRecursoTexto), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Devuelve el mensaje correspondiente según el código de error de autenticación.
     */
    public static String obtenerMensajeError(Context context, int codigoError) {
        switch (codigoError) {
            case ConstantesAutenticacion.CORREO_NO_VERIFICADO:
                return context.getString(R.string.mensaje_verificacion_correo);
            case ConstantesAutenticacion.USUARIO_NO_REGISTRADO:
                return context.getString(R.string.error_usuario_no_registrado);
            case ConstantesAutenticacion.CREDENCIALES_INVALIDAS:
                return context.getString(R.string.error_credenciales_invalidas);
            case ConstantesAutenticacion.ERROR_SERVIDOR:
                return context.getString(R.string.error_servidor);
            case ConstantesAutenticacion.CAMPOS_VACIOS:
                return context.getString(R.string.error_campos_vacios);
            default:
                return context.getString(R.string.error_desconocido);
        }
    }

    /**
     * Muestra un Snackbar con el mensaje correspondiente al código de error.
     */
    public static void mostrarMensajeErrorAutenticacion(View vistaRaiz, int codigoError) {
        Context context = vistaRaiz.getContext();
        String mensaje = obtenerMensajeError(context, codigoError);
        Snackbar.make(vistaRaiz, mensaje, Snackbar.LENGTH_LONG).show();
    }

}
