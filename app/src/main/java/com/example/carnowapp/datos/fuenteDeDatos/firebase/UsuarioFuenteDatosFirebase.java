package com.example.carnowapp.datos.fuenteDeDatos.firebase;


import android.net.Uri;

import com.example.carnowapp.modelo.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UsuarioFuenteDatosFirebase {

    private final DatabaseReference usuariosRef;

    public UsuarioFuenteDatosFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuariosRef = database.getReference("usuarios");
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

    public Task<String> subirImagenPerfil(Uri uriImagen) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            return Tasks.forException(new Exception("Usuario no autenticado"));
        }

        String ruta = "imagenes_perfil/" + usuario.getUid() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(ruta);

        return ref.putFile(uriImagen).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Obtener la URL de descarga
            return ref.getDownloadUrl();
        }).continueWithTask(urlTask -> {
            if (!urlTask.isSuccessful()) {
                throw urlTask.getException();
            }
            String urlImagen = urlTask.getResult().toString();
            // Actualizar URL en Firebase Realtime Database
            return usuariosRef.child(usuario.getUid()).child("imagenUrl").setValue(urlImagen)
                    .continueWith(task2 -> {
                        if (!task2.isSuccessful()) {
                            throw task2.getException();
                        }
                        return urlImagen; // devolver la URL para continuar
                    });
        });
    }


}