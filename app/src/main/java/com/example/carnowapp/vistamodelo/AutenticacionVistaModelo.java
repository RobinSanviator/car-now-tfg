package com.example.carnowapp.vistamodelo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carnowapp.datos.repositorio.AutenticacionRepositorio;
import com.example.carnowapp.datos.repositorio.AutenticacionFirebaseRepositorioImpl;
import com.example.carnowapp.utilidad.ConstantesAutenticacion;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacionVistaModelo extends ViewModel {

    private final AutenticacionRepositorio autenticacionRepositorio;

    private final MutableLiveData<FirebaseUser> usuarioAutenticado = new MutableLiveData<>();
    private final MutableLiveData<Integer> codigoErrorAutenticacion = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginExitosoGoogle = new MutableLiveData<>();
    private final MutableLiveData<String> errorGoogle = new MutableLiveData<>();

    // Nuevos LiveData para registro y verificación
    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> errorMensajeRegistro = new MutableLiveData<>();

    private final MutableLiveData<Boolean> correoVerificado = new MutableLiveData<>();
    private Handler handlerVerificacionCorreo;
    private Runnable runnableVerificacion;


    public AutenticacionVistaModelo() {
        this.autenticacionRepositorio = new AutenticacionFirebaseRepositorioImpl();
    }

    // Getters existentes
    public LiveData<FirebaseUser> getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public LiveData<Integer> getCodigoErrorAutenticacion() {
        return codigoErrorAutenticacion;
    }

    public LiveData<Boolean> getLoginExitosoGoogle() {
        return loginExitosoGoogle;
    }

    public LiveData<String> getErrorGoogle() {
        return errorGoogle;
    }

    // Nuevos getters para registro y verificación
    public LiveData<Boolean> getRegistroExitoso() {
        return registroExitoso;
    }

    public LiveData<String> getErrorMensajeRegistro() {
        return errorMensajeRegistro;
    }

    public LiveData<Boolean> getCorreoVerificado() {
        return correoVerificado;
    }

    // Método para registrar usuario con correo
    public void registrarUsuarioCorreo(String email, String contrasena, String nombre) {
        autenticacionRepositorio.registrarUsuarioCorreo(email, contrasena, nombre)
                .addOnSuccessListener(authResult -> {
                    registroExitoso.postValue(true);
                })
                .addOnFailureListener(e -> {
                    errorMensajeRegistro.postValue(e.getMessage());
                });
    }

    // Verificar si el correo ha sido confirmado (por ejemplo cada 3 segundos)
    public void verificarCorreo() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            correoVerificado.postValue(false);
            return;
        }

        // Si ya tienes un handler, cancela antes para evitar múltiples
        if (handlerVerificacionCorreo != null && runnableVerificacion != null) {
            handlerVerificacionCorreo.removeCallbacks(runnableVerificacion);
        }

        handlerVerificacionCorreo = new Handler(Looper.getMainLooper());

        runnableVerificacion = new Runnable() {
            @Override
            public void run() {
                usuario.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (usuario.isEmailVerified()) {
                            correoVerificado.postValue(true);
                            handlerVerificacionCorreo.removeCallbacks(this); // Detener verificación
                        } else {
                            correoVerificado.postValue(false);
                            handlerVerificacionCorreo.postDelayed(this, 3000); // Verificar de nuevo en 3s
                        }
                    } else {
                        correoVerificado.postValue(false);
                    }
                });
            }
        };

        // Inicia la verificación periódica
        handlerVerificacionCorreo.post(runnableVerificacion);
    }

    public void cancelarVerificacionCorreo() {
        if (handlerVerificacionCorreo != null && runnableVerificacion != null) {
            handlerVerificacionCorreo.removeCallbacks(runnableVerificacion);
        }
    }

    // Métodos existentes para iniciar sesión, Google SignIn, etc...
    public void iniciarSesionConCorreoYContrasena(String email, String contrasena) {
        autenticacionRepositorio.iniciarSesionConCorreoYContrasena(email, contrasena)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                    usuarioAutenticado.setValue(usuario);
                })
                .addOnFailureListener(e -> {
                    String mensaje = e.getMessage() != null ? e.getMessage() : "";
                    if (mensaje.contains("no user record")) {
                        codigoErrorAutenticacion.setValue(ConstantesAutenticacion.USUARIO_NO_REGISTRADO);
                    } else if (mensaje.contains("password") || mensaje.contains("email")) {
                        codigoErrorAutenticacion.setValue(ConstantesAutenticacion.CREDENCIALES_INVALIDAS);
                    } else if (mensaje.contains("network")) {
                        codigoErrorAutenticacion.setValue(ConstantesAutenticacion.ERROR_SERVIDOR);
                    } else {
                        codigoErrorAutenticacion.setValue(ConstantesAutenticacion.ERROR_DESCONOCIDO);
                    }
                });
    }

    public void inicializarGoogleSignIn(Context context) {
        autenticacionRepositorio.inicializarGoogleSignIn(context);
    }

    public GoogleSignInClient obtenerGoogleSignInClient() {
        return autenticacionRepositorio.obtenerGoogleSignInClient();
    }

    public void iniciarSesionGoogle(AuthCredential credential, String nombre, String email) {
        autenticacionRepositorio.registrarUsuarioGoogle(credential, nombre, email)
                .addOnSuccessListener(authResult -> loginExitosoGoogle.setValue(true))
                .addOnFailureListener(e -> {
                    errorGoogle.setValue(e.getMessage());
                    loginExitosoGoogle.setValue(false);
                });
    }

}