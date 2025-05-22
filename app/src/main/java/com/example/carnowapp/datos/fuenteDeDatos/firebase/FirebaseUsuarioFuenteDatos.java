package com.example.carnowapp.datos.fuenteDeDatos.firebase;


import com.example.carnowapp.modelo.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUsuarioFuenteDatos {

    private final DatabaseReference usuariosRef;

    public FirebaseUsuarioFuenteDatos() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuariosRef = database.getReference("usuarios"); // Nodo usuarios en Firebase
    }


    // Crear o actualizar un usuario (setValue sobreescribe si ya existe)
    public Task<Void> guardarUsuario(String uid, Usuario usuario) {
        return usuariosRef.child(uid).setValue(usuario);
    }

    public Task<Usuario> obtenerUsuario(String uid) {
        return usuariosRef.child(uid).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    return snapshot.getValue(Usuario.class);
                } else {
                    throw new Exception("Usuario no encontrado en Firebase");
                }
            } else {
                throw task.getException();
            }
        });
    }

    // Actualizar un usuario (igual que guardarUsuario, por conveniencia)
    public Task<Void> actualizarUsuario(Usuario usuario) {
        String uid = usuario.getFirebaseUID();
        if (uid == null || uid.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("UID del usuario es nulo o vacío"));
        }
        return usuariosRef.child(uid).setValue(usuario);
    }

    public Task<Void> cambiarContrasena(String nuevaContrasena) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            return usuario.updatePassword(nuevaContrasena);
        } else {
            return Tasks.forException(new Exception("Usuario no autenticado"));
        }
    }

}