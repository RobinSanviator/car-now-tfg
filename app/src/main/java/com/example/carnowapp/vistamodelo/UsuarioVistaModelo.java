package com.example.carnowapp.vistamodelo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carnowapp.modelo.Usuario;
import com.example.carnowapp.datos.fuenteDeDatos.FirebaseFuenteDatos;
import com.example.carnowapp.datos.repositorio.UsuarioRepositorio;
import com.example.carnowapp.datos.repositorio.UsuarioRepositorioImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsuarioVistaModelo extends ViewModel {
    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeErrorLiveData = new MutableLiveData<>();

    private final UsuarioRepositorio usuarioRepositorio; // Seguimos usando la interfaz en el ViewModel
    private final FirebaseAuth firebaseAuth; // Mantenemos FirebaseAuth aquí para simplificar

    private String uidUsuarioActual; // Para almacenar el UID del usuario autenticado

    public UsuarioVistaModelo() {
        // Creamos la instancia de UsuarioRepositorioImpl directamente aquí,
        // ya que su constructor no requiere FuenteDatosFirebase
        this.usuarioRepositorio = new UsuarioRepositorioImpl();

        // Obtenemos la instancia de FirebaseAuth directamente
        this.firebaseAuth = FirebaseAuth.getInstance();

        // Intentamos obtener el UID del usuario actual al crear el ViewModel
        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();
        if (usuarioActual != null) {
            this.uidUsuarioActual = usuarioActual.getUid();
            // Podríamos llamar a cargarUsuarioActual() aquí si queremos que se cargue
            // el usuario automáticamente al crear el ViewModel
            // cargarUsuarioActual();
        } else {
            // Manejar el caso donde no hay usuario autenticado inicialmente si es necesario
            // mensajeErrorLiveData.postValue("No hay usuario autenticado.");
        }
    }

    // Getters para LiveData
    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public LiveData<Boolean> getCargandoLiveData() {
        return cargandoLiveData;
    }

    public LiveData<String> getMensajeErrorLiveData() {
        return mensajeErrorLiveData;
    }

    // Método para cargar el usuario actual
    public void cargarUsuarioActual() {
        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser(); // Obtenemos el usuario más reciente
        if (usuarioActual == null) {
            mensajeErrorLiveData.postValue("UID de usuario no disponible. Inicie sesión primero.");
            usuarioLiveData.postValue(null); // Limpiar datos de usuario si no hay sesión
            return;
        }

        String uid = usuarioActual.getUid(); // Usamos el UID del usuario actual

        cargandoLiveData.postValue(true); // Indicamos que estamos cargando
        usuarioRepositorio.obtenerUsuario(uid).addOnCompleteListener(task -> {
            cargandoLiveData.postValue(false); // Finalizamos la carga
            if (task.isSuccessful() && task.getResult() != null) {
                Usuario usuario = task.getResult().getValue(Usuario.class);
                if (usuario != null) {
                    usuarioLiveData.postValue(usuario);
                } else {
                    // Esto podría ocurrir si el nodo del usuario existe pero está vacío o mal formado
                    mensajeErrorLiveData.postValue("Error: Datos de usuario inválidos.");
                    usuarioLiveData.postValue(null); // Limpiar datos inválidos
                }
            } else {
                // Manejar el error al obtener datos de Firebase
                mensajeErrorLiveData.postValue("Error al cargar usuario: " + task.getException().getMessage());
                usuarioLiveData.postValue(null); // Limpiar datos en caso de error
            }
        });
    }

    // Método para actualizar el usuario
    public void actualizarUsuario(Usuario usuario) {
        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser(); // Obtenemos el usuario más reciente
        if (usuarioActual == null) {
            mensajeErrorLiveData.postValue("No hay usuario autenticado para actualizar.");
            return;
        }

        String uid = usuarioActual.getUid(); // Usamos el UID del usuario actual

        cargandoLiveData.postValue(true);
        usuarioRepositorio.actualizarDatosUsuario(uid, usuario)
                .addOnSuccessListener(aVoid -> {
                    cargandoLiveData.postValue(false);
                    // Opcional: Actualizar el LiveData localmente después de una actualización exitosa
                    // si los datos locales son confiables y no necesitas recargar de Firebase
                    usuarioLiveData.postValue(usuario);
                    mensajeErrorLiveData.postValue("Datos de usuario actualizados correctamente."); // Mensaje de éxito
                })
                .addOnFailureListener(e -> {
                    cargandoLiveData.postValue(false);
                    mensajeErrorLiveData.postValue("Error al actualizar: " + e.getMessage());
                });
    }
}
