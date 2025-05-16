package com.example.carnowapp.vistamodelo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.persistencia.firebasedatabase.FirebaseFuenteDatos;
import com.example.carnowapp.persistencia.repositorio.UsuarioRepositorio;
import com.example.carnowapp.persistencia.repositorio.UsuarioRepositorioImpl;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioVistaModelo extends ViewModel {

    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeErrorLiveData = new MutableLiveData<>();

    private final UsuarioRepositorio usuarioRepositorio;
    private final FirebaseAuth firebaseAuth;

    public UsuarioVistaModelo() {
        usuarioRepositorio = new UsuarioRepositorioImpl(new FirebaseFuenteDatos());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public LiveData<Boolean> getCargandoLiveData() {
        return cargandoLiveData;
    }

    public LiveData<String> getMensajeErrorLiveData() {
        return mensajeErrorLiveData;
    }

    public void cargarUsuarioActual() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        usuarioRepositorio.obtenerUsuario(uid).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Usuario usuario = task.getResult().getValue(Usuario.class);
                usuarioLiveData.postValue(usuario);
            } else {
                mensajeErrorLiveData.postValue("Error al cargar usuario: " + task.getException());
            }
        });
    }

    public void actualizarUsuario(Usuario usuario) {
        cargandoLiveData.postValue(true);
        String uid = firebaseAuth.getCurrentUser().getUid();
        usuarioRepositorio.actualizarDatosUsuario(uid, usuario)
                .addOnSuccessListener(aVoid -> {
                    cargandoLiveData.postValue(false);
                    usuarioLiveData.postValue(usuario);
                })
                .addOnFailureListener(e -> {
                    cargandoLiveData.postValue(false);
                    mensajeErrorLiveData.postValue("Error al actualizar: " + e.getMessage());
                });
    }
}
