package com.example.carnowapp.modelo;

public class Vehiculo {
    private int id;
    private String vehiculoUID;     // Para Firebase (UID único)
    private String marca;
    private String modelo;
    private String matricula;
    private String imagen;          // URL o ruta local
    private String estado;          // disponible, reservado, mantenimiento, etc.
    private int plazas;
    private int puertas;
    private String tipoCombustible; // gasolina, diésel, eléctrico, híbrido
    private String transmision;     // manual, automática
    private double precioDia;
    private String ubicacion;


    // Constructor vacío (necesario para Firebase)
    public Vehiculo() {}

    // Constructor completo
    public Vehiculo(int id, String vehiculoUID, String marca, String modelo,
                    String matricula, String imagen, String estado, int plazas,
                    int puertas, String tipoCombustible, String transmision, double precioDia, String ubicacion) {
        this.id = id;
        this.vehiculoUID = vehiculoUID;
        this.marca = marca;
        this.modelo = modelo;
        this.matricula = matricula;
        this.imagen = imagen;
        this.estado = estado;
        this.plazas = plazas;
        this.puertas = puertas;
        this.tipoCombustible = tipoCombustible;
        this.transmision = transmision;
        this.precioDia = precioDia;
        this.ubicacion = ubicacion;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVehiculoUID() { return vehiculoUID; }
    public void setVehiculoUID(String vehiculoUID) { this.vehiculoUID = vehiculoUID; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getPlazas() { return plazas; }
    public void setPlazas(int plazas) { this.plazas = plazas; }

    public int getPuertas() { return puertas; }
    public void setPuertas(int puertas) { this.puertas = puertas; }

    public String getTipoCombustible() { return tipoCombustible; }
    public void setTipoCombustible(String tipoCombustible) { this.tipoCombustible = tipoCombustible; }

    public String getTransmision() { return transmision; }
    public void setTransmision(String transmision) { this.transmision = transmision; }

    public double getPrecioDia() { return precioDia; }
    public void setPrecioDia(double precioDia) { this.precioDia = precioDia; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }


}
