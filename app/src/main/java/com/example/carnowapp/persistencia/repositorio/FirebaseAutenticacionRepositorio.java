package com.example.carnowapp.persistencia.repositorio;


import android.content.Context;
import android.util.Log;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.persistencia.firebasedatabase.FirebaseFuenteDatos;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAutenticacionRepositorio {
    private static final String TAG = "AutenticacionRepo";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private final FirebaseFuenteDatos usuarioFirebase = new FirebaseFuenteDatos();

    public Task<AuthResult> registrarUsuarioCorreo(String email, String contrasena, String nombre) {
        return firebaseAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // Enviar correo de verificación
                        user.sendEmailVerification().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Correo de verificación enviado.");
                            } else {
                                Log.w(TAG, "Error al enviar correo de verificación.", task.getException());
                            }
                        });
                    }
                });
    }

    public Task<AuthResult> registrarUsuarioGoogle(AuthCredential credential, String nombre, String email) {
        return firebaseAuth.signInWithCredential(credential).onSuccessTask(authResult -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (authResult.getAdditionalUserInfo() != null && authResult.getAdditionalUserInfo().isNewUser()) {
                Usuario usuario = new Usuario();
                usuario.setFirebaseUID(user.getUid());
                usuario.setEmail(email);
                usuario.setNombre(nombre);

                return new FirebaseFuenteDatos().guardarUsuario(user.getUid(),usuario).continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(authResult);
                    } else {
                        return Tasks.forException(task.getException());
                    }
                });
            } else {
                return Tasks.forResult(authResult);
            }
        });
    }

    public Task<AuthResult> iniciarSesionConCorreoYContrasena(String email, String contrasena) {
        return firebaseAuth.signInWithEmailAndPassword(email, contrasena);
    }

    public void inicializarGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public GoogleSignInClient obtenerGoogleSignInClient() {
        return googleSignInClient;
    }

    public void cerrarSesionGoogle(Context context, OnCompleteListener<Void> listener) {
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build());

        googleSignInClient.signOut().addOnCompleteListener(listener);
    }

    public void cerrarSesionFirebase() {
        firebaseAuth.signOut();
        Log.d(TAG, "Sesión de Firebase cerrada");
    }
}