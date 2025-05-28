package com.example.carnowapp.datos.repositorio;

import android.content.Context;
import android.net.Uri;

import com.example.carnowapp.datos.fuenteDeDatos.local.UsuarioFuenteDeDatosSQLite;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.datos.fuenteDeDatos.firebase.UsuarioFuenteDatosFirebase;
import com.example.carnowapp.utilidad.RedUtilidad;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioRepositorioImpl implements UsuarioRepositorio {

    private final UsuarioFuenteDatosFirebase firebaseFuenteDatos;
    private final UsuarioFuenteDeDatosSQLite sqliteFuenteDeDatos;
    private final Context contexto;

    public UsuarioRepositorioImpl(Context contexto) {
        this.contexto = contexto.getApplicationContext();
        firebaseFuenteDatos = new UsuarioFuenteDatosFirebase();
        sqliteFuenteDeDatos = new UsuarioFuenteDeDatosSQLite(contexto);
    }

    @Override
    public Task<Usuario> obtenerUsuarioPorUID(String uid) {
        TaskCompletionSource<Usuario> taskSource = new TaskCompletionSource<>();

        // Comprobar si hay conexión a internet
        boolean hayInternet = RedUtilidad.hayConexionInternet(contexto);

        if (hayInternet) {
            // Con conexión → Firebase como fuente principal
            firebaseFuenteDatos.obtenerUsuario(uid)
                    .addOnSuccessListener(usuarioFirebase -> {
                        if (usuarioFirebase != null) {
                            Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
                            if (usuarioLocal == null) {
                                sqliteFuenteDeDatos.insertarUsuario(usuarioFirebase);
                            } else {
                                sqliteFuenteDeDatos.actualizarUsuario(usuarioFirebase);
                            }
                            taskSource.setResult(usuarioFirebase);
                        } else {
                            Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
                            if (usuarioLocal != null) {
                                taskSource.setResult(usuarioLocal);
                            } else {
                                taskSource.setException(new Exception("Usuario no encontrado en Firebase ni en SQLite"));
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
                        if (usuarioLocal != null) {
                            taskSource.setResult(usuarioLocal);
                        } else {
                            taskSource.setException(new Exception("Error Firebase y usuario no encontrado localmente", e));
                        }
                    });

        } else {
            // Sin conexión → solo SQLite
            Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
            if (usuarioLocal != null) {
                taskSource.setResult(usuarioLocal);
            } else {
                taskSource.setException(new Exception("Sin conexión y usuario no encontrado en SQLite"));
            }
        }

        return taskSource.getTask();
    }

    @Override
    public Task<Void> actualizarUsuario(Usuario usuario) {
        TaskCompletionSource<Void> taskSource = new TaskCompletionSource<>();

        // Primero actualizamos en Firebase
        firebaseFuenteDatos.actualizarUsuario(usuario)
                .addOnSuccessListener(unused -> {
                    // Si actualiza Firebase, sincronizamos SQLite
                    boolean actualizadoLocal = sqliteFuenteDeDatos.actualizarUsuario(usuario);
                    if (!actualizadoLocal) {
                        sqliteFuenteDeDatos.insertarUsuario(usuario);
                    }
                    taskSource.setResult(null);
                })
                .addOnFailureListener(e -> {
                    taskSource.setException(e);
                });

        return taskSource.getTask();
    }

    @Override
    public Task<Void> cambiarContrasena(String nuevaContrasena) {
        return firebaseFuenteDatos.cambiarContrasena(nuevaContrasena);
    }

    @Override
    public Task<Uri> subirImagenPerfil(Uri rutaImagen) {
        TaskCompletionSource<Uri> taskSource = new TaskCompletionSource<>();

        firebaseFuenteDatos.subirImagenPerfil(rutaImagen)
                .addOnSuccessListener(urlImagen -> {
                    // urlImagen es String con la URL subida

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    // Actualizamos SQLite con la URL
                    sqliteFuenteDeDatos.actualizarImagenUsuario(uid, urlImagen);

                    // Devolvemos la Uri parseada
                    taskSource.setResult(Uri.parse(urlImagen));
                })
                .addOnFailureListener(taskSource::setException);

        return taskSource.getTask();
    }

}