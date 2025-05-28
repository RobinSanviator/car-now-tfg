package com.example.carnowapp.datos.repositorio;

import android.content.Context;
import android.net.Uri;


import com.example.carnowapp.datos.fuenteDeDatos.firebase.VehiculoFuenteDatosFirebase;
import com.example.carnowapp.datos.fuenteDeDatos.local.VehiculoFuenteDeDatosSQLite;
import com.example.carnowapp.modelo.Vehiculo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.List;

public class VehiculoRepositorioImpl implements VehiculoRepositorio {

    private final VehiculoFuenteDatosFirebase firebaseFuente;
    private final VehiculoFuenteDeDatosSQLite sqliteFuente;

    public VehiculoRepositorioImpl(Context contexto) {
        firebaseFuente = new VehiculoFuenteDatosFirebase();
        sqliteFuente = new VehiculoFuenteDeDatosSQLite(contexto);
    }

    @Override
    public Task<List<Vehiculo>> obtenerTodosVehiculos() {
        TaskCompletionSource<List<Vehiculo>> taskSource = new TaskCompletionSource<>();

        firebaseFuente.obtenerTodosVehiculos().addOnSuccessListener(listaFirebase -> {
            for (Vehiculo v : listaFirebase) {
                sqliteFuente.insertarVehiculo(v);
            }
            taskSource.setResult(listaFirebase);
        }).addOnFailureListener(e -> {
            List<Vehiculo> listaLocal = sqliteFuente.obtenerTodosVehiculos();
            taskSource.setResult(listaLocal);
        });

        return taskSource.getTask();
    }

    @Override
    public Task<Void> guardarVehiculo(Vehiculo vehiculo) {
        return firebaseFuente.guardarVehiculo(vehiculo.getVehiculoUID(), vehiculo)
                .addOnSuccessListener(unused -> {
                    sqliteFuente.insertarVehiculo(vehiculo);
                });
    }

    @Override
    public Task<Void> actualizarVehiculo(Vehiculo vehiculo) {
        return firebaseFuente.guardarVehiculo(vehiculo.getVehiculoUID(), vehiculo)
                .addOnSuccessListener(unused -> {
                    sqliteFuente.insertarVehiculo(vehiculo);
                });
    }

    @Override
    public Task<Void> eliminarVehiculo(String vehiculoUID) {
        return firebaseFuente.eliminarVehiculo(vehiculoUID)
                .addOnSuccessListener(unused -> {
                    sqliteFuente.eliminarVehiculo(vehiculoUID);
                });
    }

    @Override
    public Task<String> subirImagenVehiculo(Uri rutaImagen, String vehiculoUID) {
        return firebaseFuente.subirImagenVehiculo(rutaImagen, vehiculoUID);
    }

    @Override
    public Task<List<Vehiculo>> obtenerVehiculosPorCiudad(String ciudad) {
        TaskCompletionSource<List<Vehiculo>> taskSource = new TaskCompletionSource<>();

        firebaseFuente.obtenerVehiculosPorCiudad(ciudad).addOnSuccessListener(listaFirebase -> {
            for (Vehiculo v : listaFirebase) {
                sqliteFuente.insertarVehiculo(v);
            }
            taskSource.setResult(listaFirebase);
        }).addOnFailureListener(e -> {
            List<Vehiculo> listaLocal = sqliteFuente.obtenerVehiculosPorCiudad(ciudad);
            taskSource.setResult(listaLocal);
        });

        return taskSource.getTask();
    }
}