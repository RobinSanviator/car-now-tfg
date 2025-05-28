package com.example.carnowapp.modelo;

import java.util.Map;

public class ContenedorVehiculos {
    private Map<String, Vehiculo> vehiculos;

    public Map<String, Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(Map<String, Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }
}
