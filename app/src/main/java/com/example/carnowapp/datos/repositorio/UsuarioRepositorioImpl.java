package com.example.carnowapp.datos.repositorio;

import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.datos.fuenteDeDatos.FirebaseFuenteDatos;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public class UsuarioRepositorioImpl implements UsuarioRepositorio {
    private final FirebaseFuenteDatos firebaseFuenteDatos;
    //private final SQLiteFuenteDatos sqliteFuenteDatos; // Para futuro uso local

    public UsuarioRepositorioImpl() {
        this.firebaseFuenteDatos =new FirebaseFuenteDatos();
       // this.sqliteFuenteDatos = sqliteFuenteDatos;
    }

    @Override
    public Task<DataSnapshot> obtenerUsuario(String uid) {
        return firebaseFuenteDatos.obtenerUsuario(uid);
    }

    @Override
    public Task<Void> actualizarDatosUsuario(String uid, Usuario usuario) {
        return firebaseFuenteDatos.actualizarDatosUsuario(uid, usuario);
    }
}

