package com.example.carnowapp.datos.repositorio;

import android.content.Context;

import com.example.carnowapp.datos.fuenteDeDatos.local.SQLiteUsuarioFuenteDeDatos;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.datos.fuenteDeDatos.firebase.FirebaseUsuarioFuenteDatos;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class UsuarioRepositorioImpl implements UsuarioRepositorio {

    private final FirebaseUsuarioFuenteDatos firebaseFuenteDatos;
    private final SQLiteUsuarioFuenteDeDatos sqliteFuenteDeDatos;

    public UsuarioRepositorioImpl(Context context) {
        firebaseFuenteDatos = new FirebaseUsuarioFuenteDatos();
        sqliteFuenteDeDatos = new SQLiteUsuarioFuenteDeDatos(context);
    }

    @Override
    public Task<Usuario> obtenerUsuarioPorUID(String uid) {
        // Devolvemos un Task que intenta primero Firebase, si falla devuelve el usuario local
        TaskCompletionSource<Usuario> taskSource = new TaskCompletionSource<>();

        firebaseFuenteDatos.obtenerUsuario(uid)
                .addOnSuccessListener(usuarioFirebase -> {
                    if (usuarioFirebase != null) {
                        // Si existe en Firebase, guardamos en SQLite (insertar o actualizar)
                        Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
                        if (usuarioLocal == null) {
                            sqliteFuenteDeDatos.insertarUsuario(usuarioFirebase);
                        } else {
                            sqliteFuenteDeDatos.actualizarUsuario(usuarioFirebase);
                        }
                        taskSource.setResult(usuarioFirebase);
                    } else {
                        // No existe en Firebase, devolvemos el local (puede ser null)
                        Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
                        if (usuarioLocal != null) {
                            taskSource.setResult(usuarioLocal);
                        } else {
                            taskSource.setException(new Exception("Usuario no encontrado en Firebase ni en SQLite"));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Si falla Firebase, devolvemos el local o error si no existe
                    Usuario usuarioLocal = sqliteFuenteDeDatos.obtenerUsuarioPorUID(uid);
                    if (usuarioLocal != null) {
                        taskSource.setResult(usuarioLocal);
                    } else {
                        taskSource.setException(new Exception("Error Firebase y usuario no encontrado localmente", e));
                    }
                });

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
}