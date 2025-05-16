package com.example.carnowapp.persistencia.repositorio;

import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.persistencia.firebasedatabase.FirebaseFuenteDatos;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public class UsuarioRepositorioImpl implements  UsuarioRepositorio{
    private final FirebaseFuenteDatos firebaseFuenteDatos;
    //private final SQLiteFuenteDatos sqliteFuenteDatos; // Para futuro uso local

    public UsuarioRepositorioImpl(FirebaseFuenteDatos firebaseFuenteDatos/*SQLiteFuenteDatos sqliteFuenteDatos*/) {
        this.firebaseFuenteDatos = firebaseFuenteDatos;
       // this.sqliteFuenteDatos = sqliteFuenteDatos;
    }

    @Override
    public Task<DataSnapshot> obtenerUsuario(String uid) {
        // Por ahora, obtener sólo de Firebase
        return firebaseFuenteDatos.obtenerUsuario(uid);
    }

    @Override
    public Task<Void> actualizarDatosUsuario(String uid, Usuario usuario) {
        // Actualizar en Firebase (y aquí podrías añadir SQLite también en un futuro)
        return firebaseFuenteDatos.actualizarDatosUsuario(uid, usuario);
    }
}

