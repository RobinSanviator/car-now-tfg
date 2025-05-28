package com.example.carnowapp.datos.repositorio;

import android.net.Uri;

import com.example.carnowapp.modelo.Vehiculo;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface VehiculoRepositorio {
    Task<List<Vehiculo>> obtenerTodosVehiculos();
    Task<Void> guardarVehiculo(Vehiculo vehiculo);
    Task<Void> actualizarVehiculo(Vehiculo vehiculo);
    Task<Void> eliminarVehiculo(String vehiculoUID);
    Task<String> subirImagenVehiculo(Uri rutaImagen, String vehiculoUID);
    Task<List<Vehiculo>> obtenerVehiculosPorCiudad(String ciudad);
}