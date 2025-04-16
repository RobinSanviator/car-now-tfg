package com.example.carnowapp.persistencia;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.example.carnowapp.R;
import com.example.carnowapp.utilidad.UtilidadAnimacion;
import com.example.carnowapp.utilidad.UtilidadDialogo;
import com.example.carnowapp.vista.actividad.ActividadInicioSesion;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.concurrent.Executors;

import android.os.Handler;

import androidx.appcompat.app.AlertDialog;

public class FirebaseAutenticacion {

    private static final String TAG = "FirebaseAutenticacion";
    private static FirebaseAuth firebaseAut = FirebaseAuth.getInstance();
    private static CredentialManager credentialManager;
    private static final int RC_SIGN_IN = 9001;

    public static void registrarUsuario(Activity actividad, String email, String contrasena){
        AlertDialog dialogoDeCarga = UtilidadDialogo.crearDialogoDeCarga(actividad, "Registrando usuario");
        dialogoDeCarga.show();

        firebaseAut.createUserWithEmailAndPassword(email, contrasena).addOnCompleteListener(actividad, task -> {
            dialogoDeCarga.dismiss();
           if (task.isSuccessful()){
               Snackbar.make(actividad.findViewById(android.R.id.content), "Registro completado con éxito", Snackbar.LENGTH_LONG).show();

               new Handler(Looper.getMainLooper()).postDelayed(() -> {
                   Intent intentIrInicio = new Intent(actividad, ActividadInicioSesion.class);
                   intentIrInicio.putExtra("mostrar_layout", true);
                   UtilidadAnimacion.animarDerechaAizquierda(actividad, ActividadInicioSesion.class);
                   startActivity(actividad, intentIrInicio, null);
                   actividad.finish();
               }, 300);

           } else {
               Snackbar.make(actividad.findViewById(android.R.id.content), "Se ha producido un error al realizar el registro", Snackbar.LENGTH_LONG).show();
               Log.e(TAG, "Error al registar usuario: " +task.getException());
           }
        });
    }



    // Método para iniciar sesión con Google
    public static void iniciarSesionConGoogle(Activity actividad) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(actividad.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(actividad, gso);

        // Cerrar sesión si ya hay una cuenta iniciada
        mGoogleSignInClient.signOut().addOnCompleteListener(actividad, task -> {
            // Abre la pantalla de Google Sign-In
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            actividad.startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    // Método para manejar el resultado de la autenticación de Google en onActivityResult
    public static void manejarResultadoGoogleSignIn(int requestCode, int resultCode, Intent data, Activity actividad) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Inicia sesión con Firebase con el ID token de Google
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account, actividad);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Snackbar.make(actividad.findViewById(android.R.id.content), "Error al iniciar sesión con Google", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // Método privado para autenticar con Firebase usando el ID Token de Google
    private static void firebaseAuthWithGoogle(GoogleSignInAccount acct, Activity actividad) {
        AlertDialog dialogoDeCarga = UtilidadDialogo.crearDialogoDeCarga(actividad, "Autenticando con Google");
        dialogoDeCarga.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseAut.signInWithCredential(credential).addOnCompleteListener(actividad, task -> {
            dialogoDeCarga.dismiss();
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAut.getCurrentUser();
                Log.d(TAG, "signInWithCredential:success");
                Snackbar.make(actividad.findViewById(android.R.id.content), "Sesión iniciada con Google", Snackbar.LENGTH_LONG).show();
                redirigirAInicio(actividad);
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Snackbar.make(actividad.findViewById(android.R.id.content), "Error autenticando con Google", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Método para redirigir a la actividad principal después de iniciar sesión
    private static void redirigirAInicio(Activity actividad) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(actividad, ActividadInicioSesion.class);
            actividad.startActivity(intent);
            actividad.finish();
        }, 300);
    }

    public static FirebaseUser obtenerUsuarioActual() {
        return firebaseAut.getCurrentUser();
    }

    public static void cerrarSesion(Context contexto) {
        firebaseAut.signOut();
        if (credentialManager == null) {
            credentialManager = CredentialManager.create(contexto);
        }

        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(Void result) {
                        Log.d(TAG, "Estado de credenciales limpiado");
                    }

                    @Override
                    public void onError(ClearCredentialException e) {
                        Log.e(TAG, "Error limpiando credenciales: " + e.getLocalizedMessage());
                    }
                });
    }

}
