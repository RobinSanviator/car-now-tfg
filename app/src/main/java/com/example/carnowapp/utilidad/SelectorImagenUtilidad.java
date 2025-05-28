package com.example.carnowapp.utilidad;


import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.carnowapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

public class SelectorImagenUtilidad {
    public interface ImagenSeleccionadaListener {
        void onImagenSeleccionada(Uri uriImagen);
    }

    private final Activity actividad;
    private final Context contexto;

    private final ActivityResultLauncher<String> permisoCamaraLauncher;
    private final ActivityResultLauncher<Intent> lanzadorGaleria;
    private final ActivityResultLauncher<Uri> lanzadorCamara;

    private Uri uriFotoCamara;

    private ImagenSeleccionadaListener listener;

    public SelectorImagenUtilidad(@NonNull Activity activity,
                                  @NonNull ActivityResultLauncher<String> permisoCamaraLauncher,
                                  @NonNull ActivityResultLauncher<Intent> lanzadorGaleria,
                                  @NonNull ActivityResultLauncher<Uri> lanzadorCamara) {
        this.actividad = activity;
        this.contexto = activity.getApplicationContext();
        this.permisoCamaraLauncher = permisoCamaraLauncher;
        this.lanzadorGaleria = lanzadorGaleria;
        this.lanzadorCamara = lanzadorCamara;
    }

    public void setImagenSeleccionadaListener(ImagenSeleccionadaListener listener) {
        this.listener = listener;
    }

    public void mostrarDialogoSeleccionImagen() {
        String[] opciones = {
                actividad.getString(R.string.opcion_tomar_foto),
                actividad.getString(R.string.opcion_elegir_galeria)
        };

        int[] iconos = {
                R.drawable.ic_tomar_foto,    // ícono para "tomar foto"
                R.drawable.ic_imagen_galeria    // ícono para "elegir de galería"
        };
        boolean[] tintarIconos = {true, true};

        AlertDialog dialogo = DialogoUtilidad.crearDialogoSelectorConIconos(
                actividad,
                actividad.getString(R.string.titulo_seleccionar_imagen),
                opciones,
                iconos,
                tintarIconos,
                (dialog, which) -> {
                    if (which == 0) {
                        solicitarPermisoCamara();
                    } else if (which == 1) {
                        abrirGaleria();
                    }
                }
        );

        dialogo.show();
    }

    private void solicitarPermisoCamara() {
        permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
    }


    public void onPermisoCamaraResultado(boolean permitido) {
        if (permitido) {
            abrirCamara();
        } else {
            MensajesUtilidad.mostrarMensaje(actividad.findViewById(android.R.id.content),
                    R.string.permiso_camara_denegada);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        lanzadorGaleria.launch(intent);
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoCamara);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        actividad.grantUriPermission(
                actividad.getPackageName(),
                uriFotoCamara,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
        );
        try {
            File archivoImagen = crearArchivoImagen();
            if (archivoImagen != null) {
                uriFotoCamara = FileProvider.getUriForFile(actividad,
                        actividad.getPackageName() + ".provider", archivoImagen);
                lanzadorCamara.launch(uriFotoCamara);
            }
        } catch (IOException e) {
            e.printStackTrace();
            MensajesUtilidad.mostrarMensaje(actividad.findViewById(android.R.id.content), R.string.error_crear_archivo_imagen);
        }
    }

    private File crearArchivoImagen() throws IOException {
        String nombre = "IMG_" + System.currentTimeMillis();
        File directorio = actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(nombre, ".jpg", directorio);
    }

    // Debes llamar a este método cuando recibas el resultado de cámara
    public void onResultadoCamara(boolean exito) {
        if (exito && uriFotoCamara != null && listener != null) {
            listener.onImagenSeleccionada(uriFotoCamara);
        }
    }

    // Debes llamar a este método cuando recibas el resultado de galería
    public void onResultadoGaleria(Intent data) {
        if (data != null && data.getData() != null && listener != null) {
            listener.onImagenSeleccionada(data.getData());
        }
    }

}
