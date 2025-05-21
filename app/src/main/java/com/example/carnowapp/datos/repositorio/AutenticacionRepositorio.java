package com.example.carnowapp.datos.repositorio;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

public interface AutenticacionRepositorio {

    Task<AuthResult> registrarUsuarioCorreo(String email, String contrasena, String nombre);
    Task<AuthResult> registrarUsuarioGoogle(AuthCredential credential, String nombre, String email);
    Task<AuthResult> iniciarSesionConCorreoYContrasena(String email, String contrasena);
    void inicializarGoogleSignIn(Context context);
    GoogleSignInClient obtenerGoogleSignInClient();
    void cerrarSesionGoogle(Context context, OnCompleteListener<Void> listener);
    void cerrarSesionFirebase();
}