package com.example.carnowapp.modelo;

public class Coche {
    private String modelo;
    private int imagenUrl;  // Opcional si quieres mostrar una imagen

    public Coche(String modelo, int imagenUrl) {
        this.modelo = modelo;
        this.imagenUrl = imagenUrl;
    }

    public String getModelo() {
        return modelo;
    }

    public int getImagenUrl() {
        return imagenUrl;
    }
}
