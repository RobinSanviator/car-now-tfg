package com.example.carnowapp.datos.repositorio;

import com.example.carnowapp.modelo.Usuario;
import com.google.android.gms.tasks.Task;

public interface UsuarioRepositorio {
    Task<Usuario> obtenerUsuarioPorUID(String uid);
    Task<Void> actualizarUsuario(Usuario usuario);
    Task<Void> cambiarContrasena(String nuevaContrasena);
}