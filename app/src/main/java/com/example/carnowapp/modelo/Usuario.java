package com.example.carnowapp.modelo;

public class Usuario {
    private int id;
    private String firebaseUID;
    private String nombre;
    private String email;
    private String dni;
    private String tarjeta;


    public Usuario() {}


    public Usuario(int id, String firebaseUID, String nombre, String email, String dni, String tarjeta) {
        this.id = id;
        this.firebaseUID = firebaseUID;
        this.nombre = nombre;
        this.email = email;
        this.dni = dni;
        this.tarjeta = tarjeta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

}

