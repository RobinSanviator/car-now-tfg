package com.example.carnowapp.datos.repositorio;


import android.content.Context;
import android.util.Log;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.datos.fuenteDeDatos.firebase.UsuarioFuenteDatosFirebase;
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

public class AutenticacionFirebaseRepositorioImpl implements AutenticacionRepositorio {
    private static final String TAG = "AutenticacionRepo";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private final UsuarioFuenteDatosFirebase usuarioFirebase = new UsuarioFuenteDatosFirebase();

    @Override
    public Task<AuthResult> registrarUsuarioCorreo(String email, String contrasena, String nombre) {
        return firebaseAuth.createUserWithEmailAndPassword(email, contrasena)
                .onSuccessTask(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        return Tasks.forException(new Exception("Usuario no creado correctamente"));
                    }

                    Usuario usuario = new Usuario();
                    usuario.setFirebaseUID(user.getUid());
                    usuario.setEmail(email);
                    usuario.setNombre(nombre);
                    usuario.setTelefono(null);
                    usuario.setDni(null);
                    usuario.setTarjetaUltimos4(null);
                    usuario.setTarjetaCaducidad(null);


                    // Guardar usuario en base de datos (delegando a FirebaseFuenteDatos)
                    return usuarioFirebase.guardarUsuario(user.getUid(), usuario)
                            .onSuccessTask(task -> user.sendEmailVerification()
                                    .continueWith(task2 -> authResult));
                });
    }

    @Override
    public Task<AuthResult> registrarUsuarioGoogle(AuthCredential credential, String nombre, String email) {
        return firebaseAuth.signInWithCredential(credential).onSuccessTask(authResult -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (authResult.getAdditionalUserInfo() != null && authResult.getAdditionalUserInfo().isNewUser()) {
                Usuario usuario = new Usuario();
                usuario.setFirebaseUID(user.getUid());
                usuario.setEmail(email);
                usuario.setNombre(nombre);
                usuario.setTelefono(null);
                usuario.setDni(null);
                usuario.setTarjetaUltimos4(null);
                usuario.setTarjetaCaducidad(null);

                return usuarioFirebase.guardarUsuario(user.getUid(), usuario)
                        .onSuccessTask(task -> Tasks.forResult(authResult));
            } else {
                return Tasks.forResult(authResult);
            }
        });
    }


    @Override
    public Task<AuthResult> iniciarSesionConCorreoYContrasena(String email, String contrasena) {
        return firebaseAuth.signInWithEmailAndPassword(email, contrasena);
    }

    @Override
    public void inicializarGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    @Override
    public GoogleSignInClient obtenerGoogleSignInClient() {
        return googleSignInClient;
    }

    @Override
    public void cerrarSesionGoogle(Context context, OnCompleteListener<Void> listener) {
        if (googleSignInClient == null) {
            inicializarGoogleSignIn(context);
        }
        googleSignInClient.signOut().addOnCompleteListener(listener);
    }

    @Override
    public void cerrarSesionFirebase() {
        firebaseAuth.signOut();
        Log.d(TAG, "Sesión de Firebase cerrada");
    }
}