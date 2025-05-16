package com.example.carnowapp.vistamodelo;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.persistencia.repositorio.FirebaseAutenticacionRepositorio;
import com.example.carnowapp.vista.actividad.ActividadInicioSesion;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAutenticacionVistaModelo extends ViewModel {
    private final FirebaseAutenticacionRepositorio repositorio; // Se inicializará en el constructor o directamente
    private final FirebaseAuth firebaseAuth; // Necesario para getCurrentUser en algunos casos

    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> errorMensajeRegistro = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginExitosoGoogle = new MutableLiveData<>();
    private final MutableLiveData<String> errorGoogle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sesionCerrada = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> usuarioAutenticado = new MutableLiveData<>();
    private final MutableLiveData<ActividadInicioSesion.CodigoErrorAutenticacion> codigoErrorAutenticacion = new MutableLiveData<>();
    private final MutableLiveData<Boolean> correoVerificacionEnviado = new MutableLiveData<>();
    private final MutableLiveData<String> errorCorreoVerificacion = new MutableLiveData<>();
    private MutableLiveData<Boolean> correoVerificado = new MutableLiveData<>();

    // Constructor
    public FirebaseAutenticacionVistaModelo() {
        this.repositorio = new FirebaseAutenticacionRepositorio(); // Instancia directa
        this.firebaseAuth = FirebaseAuth.getInstance(); // Obtener instancia de FirebaseAuth
    }

    // --- Getters para LiveData ---
    public LiveData<Boolean> getRegistroExitoso() {
        return registroExitoso;
    }

    public LiveData<String> getErrorMensajeRegistro() {
        return errorMensajeRegistro;
    }


    public LiveData<Boolean> getLoginExitosoGoogle() {
        return loginExitosoGoogle;
    }

    public LiveData<String> getErrorGoogle() {
        return errorGoogle;
    }

    public LiveData<Boolean> getSesionCerrada() {
        return sesionCerrada;
    }


    public LiveData<FirebaseUser> getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public LiveData<ActividadInicioSesion.CodigoErrorAutenticacion> getCodigoErrorAutenticacion() {return codigoErrorAutenticacion;}

    public LiveData<Boolean> getCorreoVerificacionEnviado() {return correoVerificacionEnviado;}

    public LiveData<String> getErrorCorreoVerificacion() {return errorCorreoVerificacion;}

    public LiveData<Boolean> getCorreoVerificado() {
        return correoVerificado;
    }




    // --- Métodos de Lógica de Autenticación ---

    public void registrarUsuarioCorreo(String email, String contrasena, String nombre, Context contexto) {
        repositorio.registrarUsuarioCorreo(email, contrasena, nombre)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            correoVerificacionEnviado.setValue(true);
                                        } else {
                                            errorCorreoVerificacion.setValue(
                                                    contexto.getString(R.string.error_correo_verificacion)
                                            );
                                            correoVerificacionEnviado.setValue(false);
                                        }
                                    });
                        }
                        registroExitoso.setValue(true);
                    } else {
                        errorMensajeRegistro.setValue(
                                contexto.getString(R.string.error_registro_usuario) +
                                        (task.getException() != null ? task.getException().getMessage() : contexto.getString(R.string.error_desconocido))
                        );
                        registroExitoso.setValue(false);
                    }
                });
    }


    public void verificarCorreo() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Notificamos si el correo ha sido verificado o no
                    correoVerificado.setValue(user.isEmailVerified());
                } else {
                    correoVerificado.setValue(false);
                }
            });
        }
    }



    public void iniciarSesionConGoogle(AuthCredential credencial, String nombre, String email, Context contexto) {
        repositorio.registrarUsuarioGoogle(credencial, nombre, email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginExitosoGoogle.setValue(true);
                    } else {
                        errorGoogle.setValue(task.getException() != null ? task.getException().getMessage() : contexto.getString(R.string.error_desconocido));
                    }
                });
    }

    public void iniciarSesionConCorreoYContrasena(String email, String contrasena) {
        repositorio.iniciarSesionConCorreoYContrasena(email, contrasena)
                .addOnSuccessListener(authResult -> {
                    usuarioAutenticado.setValue(authResult.getUser());
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        codigoErrorAutenticacion.setValue(ActividadInicioSesion.CodigoErrorAutenticacion.USUARIO_NO_REGISTRADO);
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        codigoErrorAutenticacion.setValue(ActividadInicioSesion.CodigoErrorAutenticacion.CREDENCIALES_INVALIDAS);
                    } else if (e instanceof FirebaseAuthException) {
                        codigoErrorAutenticacion.setValue(ActividadInicioSesion.CodigoErrorAutenticacion.CREDENCIALES_NO_VALIDAS);
                    } else {
                        codigoErrorAutenticacion.setValue(ActividadInicioSesion.CodigoErrorAutenticacion.ERROR_DESCONOCIDO);
                    }
                });
    }

    public void inicializarGoogleSignIn(Context contexto) {
        repositorio.inicializarGoogleSignIn(contexto);
    }

    public GoogleSignInClient obtenerGoogleSignInClient() {
        return repositorio.obtenerGoogleSignInClient();
    }


    public void cerrarSesion(Context contexto) {
        repositorio.cerrarSesionFirebase();
        repositorio.cerrarSesionGoogle(contexto, task -> {
            if (task.isSuccessful()) {
                sesionCerrada.setValue(true);
            } else {
                // Puedes agregar un LiveData de error si quieres gestionar fallos
                Log.e("AutenticacionVM", "Error al cerrar sesión Google", task.getException());
            }
        });
    }





}