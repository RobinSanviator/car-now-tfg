package com.example.carnowapp.utilidad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.example.carnowapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogoUtilidad {

    public static AlertDialog crearDialogoDeCarga(Context contexto, int idRecursoTexto) {
        ProgressBar progressBar = new ProgressBar(contexto);
        LinearLayout layout = new LinearLayout(contexto);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(30, 30, 30, 30);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(progressBar);


        TextView mensaje = new TextView(contexto);
        mensaje.setText(R.string.espere);
        mensaje.setPadding(30, 0, 0, 0);
        mensaje.setTextSize(16);
        mensaje.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.color_texto_primario)));
        layout.addView(mensaje);

        layout.setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_fondo_variante));


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(contexto)
                .setTitle(idRecursoTexto)
                .setView(layout)
                .setCancelable(false);


        AlertDialog dialogo = builder.create();


        TextView titleView = dialogo.findViewById(android.R.id.title);
        if (titleView != null) {
            titleView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_fondo_variante));
        }

        return builder.create();
    }


    public static AlertDialog crearDialogoConfirmacion(Context contexto,
                                                       String titulo,
                                                       String mensaje,
                                                       int iconoId,
                                                       String textoPositivo,
                                                       DialogInterface.OnClickListener accionPositiva,
                                                       String textoNegativo,
                                                       DialogInterface.OnClickListener accionNegativa) {

        Drawable icono = obtenerIconoTintado(contexto, iconoId);

        return new MaterialAlertDialogBuilder(contexto)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setIcon(icono)
                .setBackground(crearColor(contexto, R.color.color_fondo_variante))
                .setPositiveButton(textoPositivo, accionPositiva)
                .setNegativeButton(textoNegativo, accionNegativa)
                .setNeutralButton(contexto.getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }


    public static AlertDialog crearDialogoVerificacionCorreo(Context contexto, DialogInterface.OnClickListener onClickListener) {
        LinearLayout layout = new LinearLayout(contexto);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_fondo_variante));

        ImageView logo = new ImageView(contexto);
        logo.setImageResource(R.drawable.logo_carnow);
        layout.addView(logo);

        TextView mensaje = new TextView(contexto);
        mensaje.setText(R.string.mensaje_verificacion_correo);
        mensaje.setPadding(0, 20, 0, 20);
        mensaje.setTextSize(16);
        mensaje.setTextColor(ContextCompat.getColor(contexto, R.color.color_texto_primario));
        layout.addView(mensaje);

        ImageView tick = new ImageView(contexto);
        tick.setImageResource(R.drawable.ic_check); // Usa un icono de verificación aquí si lo prefieres
        tick.setVisibility(View.GONE);// Oculto al inicio
        layout.addView(tick);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(contexto)
                .setTitle(R.string.titulo_verificacion_correo)
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton(R.string.inicio_sesion, onClickListener)
                .setBackground(crearColor(contexto, R.color.color_texto_primario));

        AlertDialog dialogo = builder.create();

        dialogo.setOnShowListener(d -> {
            Button boton = dialogo.getButton(AlertDialog.BUTTON_POSITIVE);
            if (boton != null) {
                boton.setVisibility(View.GONE); // Ocultamos al inicio
            }

            // Objeto para manejar cambios desde fuera (como en la Actividad)
            dialogo.getWindow().getDecorView().setTag(new Object() {
                void exito() {
                    mensaje.setText(R.string.mensaje_verificacion_exitosa);
                    logo.setVisibility(View.GONE);
                    tick.setVisibility(View.VISIBLE);
                    if (boton != null) {
                        boton.setVisibility(View.VISIBLE); // Mostramos el botón solo cuando ya esté verificado
                    }
                }
            });
        });

        return dialogo;
    }

    public static Drawable crearColor(Context contexto, int colorResource) {
        return new ColorDrawable(ContextCompat.getColor(contexto, colorResource));
    }

    public static Drawable obtenerIconoTintado(Context contexto, int iconoId) {
        Drawable icono = AppCompatResources.getDrawable(contexto, iconoId);
        if (icono != null) {
            icono.setTint(ContextCompat.getColor(contexto, R.color.color_principal));
        }
        return icono;
    }


    public static AlertDialog crearDialogoSelectorConIconos(Context contexto,
                                                            String titulo,
                                                            String[] opciones,
                                                            int[] iconosOpciones,
                                                            boolean[] tintarIconos,
                                                            DialogInterface.OnClickListener listener) {

        ListAdapter adaptador = new ArrayAdapter<String>(contexto, android.R.layout.select_dialog_item, opciones) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View vista = super.getView(position, convertView, parent);
                TextView texto = (TextView) vista.findViewById(android.R.id.text1);

                texto.setTextColor(ContextCompat.getColor(contexto, R.color.color_texto_primario));
                texto.setTextSize(14); // Tamaño reducido de las opciones

                Drawable icono;
                if (tintarIconos != null && tintarIconos.length > position && tintarIconos[position]) {
                    icono = obtenerIconoTintado(contexto, iconosOpciones[position]);
                } else {
                    icono = AppCompatResources.getDrawable(contexto, iconosOpciones[position]);
                }

                texto.setCompoundDrawablesWithIntrinsicBounds(
                        icono,
                        null, null, null
                );

                texto.setCompoundDrawablePadding(16);
                texto.setPadding(40, 30, 40, 30);
                vista.setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_fondo_variante));
                return vista;
            }
        };

        // Título personalizado con mayor tamaño y color
        TextView tituloPersonalizado = new TextView(contexto);
        tituloPersonalizado.setText(titulo);
        tituloPersonalizado.setPadding(40, 50, 40, 30);
        tituloPersonalizado.setTextColor(ContextCompat.getColor(contexto, R.color.color_texto_primario));
        tituloPersonalizado.setTextSize(18); // Tamaño grande
        tituloPersonalizado.setTypeface(null, Typeface.BOLD);

        AlertDialog dialogo = new MaterialAlertDialogBuilder(contexto)
                .setCustomTitle(tituloPersonalizado)
                .setAdapter(adaptador, listener)
                .setBackground(crearColor(contexto, R.color.color_fondo_variante))
                .setCancelable(true)
                .create();

        return dialogo;
    }
}
