package com.example.carnowapp.vistamodelo;


import android.app.Application;
import android.net.Uri;

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
    private final MutableLiveData<Boolean> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> contrasenaActualizada = new MutableLiveData<>();
    private final MutableLiveData<String> errorActualizacionContrasena = new MutableLiveData<>();
    private final MutableLiveData<Uri> imagenPerfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoImagen = new MutableLiveData<>();
    private final MutableLiveData<String> errorImagen = new MutableLiveData<>();


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

    public LiveData<Boolean> getError() {
        return error;
    }

    public LiveData<Uri> getImagenPerfilLiveData() {
        return imagenPerfilLiveData;
    }

    public LiveData<Boolean> getCargandoImagen() {
        return cargandoImagen;
    }

    public LiveData<String> getErrorImagen() {
        return errorImagen;
    }

    public void cargarUsuario(String uid) {
        cargando.setValue(true);
        error.setValue(false);

        usuarioRepositorio.obtenerUsuarioPorUID(uid)
                .addOnSuccessListener(usuario -> {
                    usuarioLiveData.setValue(usuario);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(true);  // sí hay error
                    cargando.setValue(false);
                });
    }

    // Método para actualizar usuario
    public void actualizarUsuario(Usuario usuario) {
        cargando.setValue(true);
        error.setValue(false);

        usuarioRepositorio.actualizarUsuario(usuario)
                .addOnSuccessListener(unused -> {
                    usuarioLiveData.setValue(usuario);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(true);
                    cargando.setValue(false);
                });
    }

    public void cambiarContrasena(String nuevaContrasena) {
        usuarioRepositorio.cambiarContrasena(nuevaContrasena)
                .addOnSuccessListener(unused -> contrasenaActualizada.setValue(true))
                .addOnFailureListener(e -> errorActualizacionContrasena.setValue(e.getMessage()));
    }

    public void subirImagenPerfil(Uri rutaImagen) {
        cargandoImagen.setValue(true);
        errorImagen.setValue(null);

        usuarioRepositorio.subirImagenPerfil(rutaImagen)
                .addOnSuccessListener(uri -> {
                    imagenPerfilLiveData.setValue(uri);
                    cargandoImagen.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorImagen.setValue(e.getMessage());
                    cargandoImagen.setValue(false);
                });
    }

}