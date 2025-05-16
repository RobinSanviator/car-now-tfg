package com.example.carnowapp.persistencia.firebasedatabase;

import com.example.carnowapp.modelo.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFuenteDatos {
    private final DatabaseReference usuariosRef;

    public FirebaseFuenteDatos() {
        FirebaseBaseDeDatosATiempoReal firebaseBBDD = new FirebaseBaseDeDatosATiempoReal();
        this.usuariosRef = firebaseBBDD.getDatabaseReference().child("usuarios");
    }

    public Task<Void> guardarUsuario(String uid, Usuario usuario) {
        // Se pasa el uid explícitamente para no depender de FirebaseAuth aquí
        return usuariosRef.child(uid).setValue(usuario);
    }

    public Task<DataSnapshot> obtenerUsuario(String firebaseUID) {
        return usuariosRef.child(firebaseUID).get();
    }

    public Task<Void> actualizarCampoUsuario(String uid, String campo, Object valor) {
        return usuariosRef.child(uid).child(campo).setValue(valor);
    }

    public Task<Void> actualizarUsuario(String uid, Map<String, Object> camposActualizados) {
        return usuariosRef.child(uid).updateChildren(camposActualizados);
    }

    public Task<Void> actualizarDatosUsuario(String uid, Usuario usuario) {
        Map<String, Object> camposActualizados = new HashMap<>();
        camposActualizados.put("nombre", usuario.getNombre());
        camposActualizados.put("email", usuario.getEmail());
        camposActualizados.put("dni", usuario.getDni());
        return usuariosRef.child(uid).updateChildren(camposActualizados);
    }
}