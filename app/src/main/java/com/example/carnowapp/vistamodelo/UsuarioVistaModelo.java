package com.example.carnowapp.vistamodelo;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.carnowapp.datos.repositorio.UsuarioRepositorio;
import com.example.carnowapp.datos.repositorio.UsuarioRepositorioImpl;
import com.example.carnowapp.modelo.Usuario;


public class UsuarioVistaModelo extends AndroidViewModel {
    private final UsuarioRepositorio usuarioRepositorio;

    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> contrasenaActualizada = new MutableLiveData<>();
    private final MutableLiveData<String> errorActualizacionContrasena = new MutableLiveData<>();


    public UsuarioVistaModelo(@NonNull Application application) {
        super(application);
        usuarioRepositorio = new UsuarioRepositorioImpl(application.getApplicationContext());
    }

    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }


    public LiveData<Boolean> getContrasenaActualizada() {
        return contrasenaActualizada;
    }

    public LiveData<String> getErrorActualizacionContrasena() {
        return errorActualizacionContrasena;
    }



    // Método para cargar usuario por UID
    public void cargarUsuario(String uid) {
        cargando.setValue(true);
        error.setValue(null);

        usuarioRepositorio.obtenerUsuarioPorUID(uid)
                .addOnSuccessListener(usuario -> {
                    usuarioLiveData.setValue(usuario);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    cargando.setValue(false);
                });
    }

    // Método para actualizar usuario
    public void actualizarUsuario(Usuario usuario) {
        cargando.setValue(true);
        error.setValue(null);

        usuarioRepositorio.actualizarUsuario(usuario)
                .addOnSuccessListener(unused -> {
                    usuarioLiveData.setValue(usuario);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    cargando.setValue(false);
                });
    }

    public void cambiarContrasena(String nuevaContrasena) {
        usuarioRepositorio.cambiarContrasena(nuevaContrasena)
                .addOnSuccessListener(unused -> contrasenaActualizada.setValue(true))
                .addOnFailureListener(e -> errorActualizacionContrasena.setValue(e.getMessage()));
    }
}