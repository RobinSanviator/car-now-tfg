package com.example.carnowapp.modelo;

import java.lang.Integer;

public class Usuario {
    private int id;
    private String firebaseUID;
    private String nombre;
    private String email;
    private Integer telefono;
    private String dni;
    private String tarjetaUltimos4;
    private String tarjetaCaducidad;

    public Usuario() {}

    public Usuario(String firebaseUID, String nombre, String email, Integer telefono,
                   String dni, String tarjetaUltimos4, String tarjetaCaducidad) {
        this.firebaseUID = firebaseUID;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.dni = dni;
        this.tarjetaUltimos4 = tarjetaUltimos4;
        this.tarjetaCaducidad = tarjetaCaducidad;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirebaseUID() { return firebaseUID; }
    public void setFirebaseUID(String firebaseUID) { this.firebaseUID = firebaseUID; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getTelefono() { return telefono; }
    public void setTelefono(Integer telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTarjetaUltimos4() { return tarjetaUltimos4; }
    public void setTarjetaUltimos4(String tarjetaUltimos4) { this.tarjetaUltimos4 = tarjetaUltimos4; }

    public String getTarjetaCaducidad() { return tarjetaCaducidad; }
    public void setTarjetaCaducidad(String tarjetaCaducidad) { this.tarjetaCaducidad = tarjetaCaducidad; }
}

