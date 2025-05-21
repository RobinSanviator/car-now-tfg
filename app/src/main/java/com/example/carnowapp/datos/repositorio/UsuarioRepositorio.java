package com.example.carnowapp.datos.repositorio;

import com.example.carnowapp.modelo.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public interface UsuarioRepositorio {
    Task<DataSnapshot> obtenerUsuario(String uid);
    Task<Void> actualizarDatosUsuario(String uid, Usuario usuario);
}
