package com.example.carnowapp.vistamodelo;


import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.carnowapp.datos.repositorio.VehiculoRepositorio;
import com.example.carnowapp.datos.repositorio.VehiculoRepositorioImpl;
import com.example.carnowapp.modelo.Vehiculo;

import java.util.List;

public class VehiculoVistaModelo extends AndroidViewModel {


    private final VehiculoRepositorio vehiculoRepositorio;

    private final MutableLiveData<List<Vehiculo>> vehiculosLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();
    private final MutableLiveData<Boolean> error = new MutableLiveData<>();

    private final MutableLiveData<Boolean> vehiculoGuardado = new MutableLiveData<>();
    private final MutableLiveData<String> errorGuardar = new MutableLiveData<>();
    private final MutableLiveData<Boolean> vehiculoActualizado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> vehiculoEliminado = new MutableLiveData<>();
    private final MutableLiveData<String> errorActualizacion = new MutableLiveData<>();
    private final MutableLiveData<String> errorEliminacion = new MutableLiveData<>();

    private final MutableLiveData<String> urlImagenVehiculo = new MutableLiveData<>();
    private final MutableLiveData<String> errorImagenVehiculo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargandoImagenVehiculo = new MutableLiveData<>();
    private final MutableLiveData<List<Vehiculo>> vehiculosPorCiudadLiveData = new MutableLiveData<>();


    public VehiculoVistaModelo(@NonNull Application application) {
        super(application);
        vehiculoRepositorio = new VehiculoRepositorioImpl(application.getApplicationContext());
    }

    public LiveData<List<Vehiculo>> getVehiculosLiveData() {
        return vehiculosLiveData;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<Boolean> getError() {
        return error;
    }

    public LiveData<Boolean> getVehiculoGuardado() {
        return vehiculoGuardado;
    }

    public LiveData<String> getErrorGuardar() {
        return errorGuardar;
    }

    public LiveData<Boolean> getVehiculoActualizado() {
        return vehiculoActualizado;
    }

    public LiveData<Boolean> getVehiculoEliminado() {
        return vehiculoEliminado;
    }

    public LiveData<String> getErrorActualizacion() {
        return errorActualizacion;
    }

    public LiveData<String> getErrorEliminacion() {
        return errorEliminacion;
    }

    public LiveData<String> getUrlImagenVehiculo() {
        return urlImagenVehiculo;
    }

    public LiveData<String> getErrorImagenVehiculo() {
        return errorImagenVehiculo;
    }

    public LiveData<Boolean> getCargandoImagenVehiculo() {
        return cargandoImagenVehiculo;
    }

    public LiveData<List<Vehiculo>> getVehiculosPorCiudadLiveData() {
        return vehiculosPorCiudadLiveData;
    }

    public void cargarVehiculos() {
        cargando.setValue(true);
        error.setValue(false);

        vehiculoRepositorio.obtenerTodosVehiculos()
                .addOnSuccessListener(lista -> {
                    vehiculosLiveData.setValue(lista);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(true);
                    cargando.setValue(false);
                });
    }

    public void guardarVehiculo(Vehiculo vehiculo) {
        cargando.setValue(true);
        error.setValue(false);
        errorGuardar.setValue(null);
        vehiculoGuardado.setValue(false);

        vehiculoRepositorio.guardarVehiculo(vehiculo)
                .addOnSuccessListener(unused -> {
                    vehiculoGuardado.setValue(true);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorGuardar.setValue(e.getMessage());
                    cargando.setValue(false);
                });
    }

    public void actualizarVehiculo(Vehiculo vehiculo) {
        cargando.setValue(true);
        errorActualizacion.setValue(null);
        vehiculoActualizado.setValue(false);

        vehiculoRepositorio.actualizarVehiculo(vehiculo)
                .addOnSuccessListener(unused -> {
                    vehiculoActualizado.setValue(true);
                    cargarVehiculos(); // opcional: recargar la lista
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorActualizacion.setValue(e.getMessage());
                    cargando.setValue(false);
                });
    }

    public void eliminarVehiculo(String vehiculoUID) {
        cargando.setValue(true);
        errorEliminacion.setValue(null);
        vehiculoEliminado.setValue(false);

        vehiculoRepositorio.eliminarVehiculo(vehiculoUID)
                .addOnSuccessListener(unused -> {
                    vehiculoEliminado.setValue(true);
                    cargarVehiculos(); // opcional
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorEliminacion.setValue(e.getMessage());
                    cargando.setValue(false);
                });
    }

    public void subirImagenVehiculo(Uri rutaImagen, String vehiculoUID) {
        cargandoImagenVehiculo.setValue(true);
        vehiculoRepositorio.subirImagenVehiculo(rutaImagen, vehiculoUID)
                .addOnSuccessListener(url -> {
                    urlImagenVehiculo.setValue(url);
                    cargandoImagenVehiculo.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorImagenVehiculo.setValue("Error al subir imagen: " + e.getMessage());
                    cargandoImagenVehiculo.setValue(false);
                });
    }

    public void cargarVehiculosPorCiudad(String ciudad) {
        cargando.setValue(true);
        error.setValue(false);

        vehiculoRepositorio.obtenerVehiculosPorCiudad(ciudad)
                .addOnSuccessListener(lista -> {
                    vehiculosPorCiudadLiveData.setValue(lista);
                    cargando.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(true);
                    cargando.setValue(false);
                });
    }
}