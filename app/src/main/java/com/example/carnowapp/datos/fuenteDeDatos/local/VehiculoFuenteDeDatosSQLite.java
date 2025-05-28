package com.example.carnowapp.datos.fuenteDeDatos.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carnowapp.modelo.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class VehiculoFuenteDeDatosSQLite {

    private final BaseDatosLocalSQLite bdLocalSQLite;

    public VehiculoFuenteDeDatosSQLite(Context context) {
        bdLocalSQLite = new BaseDatosLocalSQLite(context);
    }


    public boolean insertarVehiculo(Vehiculo vehiculo) {
        SQLiteDatabase db = bdLocalSQLite.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("vehiculoUID", vehiculo.getVehiculoUID());
        valores.put("marca", vehiculo.getMarca());
        valores.put("modelo", vehiculo.getModelo());
        valores.put("matricula", vehiculo.getMatricula());
        valores.put("imagen", vehiculo.getImagen() != null ? vehiculo.getImagen() : null);
        valores.put("estado", vehiculo.getEstado());
        valores.put("plazas", vehiculo.getPlazas());
        valores.put("puertas", vehiculo.getPuertas());
        valores.put("tipoCombustible", vehiculo.getTipoCombustible());
        valores.put("transmision", vehiculo.getTransmision());
        valores.put("precioDia", vehiculo.getPrecioDia());
        valores.put("ubicacion", vehiculo.getUbicacion());

        long resultado = db.insertWithOnConflict(
                "vehiculos",
                null,
                valores,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        db.close();
        return resultado != -1;
    }


    public List<Vehiculo> obtenerTodosVehiculos() {
        List<Vehiculo> lista = new ArrayList<>();
        SQLiteDatabase db = bdLocalSQLite.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM vehiculos", null);

        if (cursor.moveToFirst()) {
            do {
                Vehiculo v = new Vehiculo();
                v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                v.setVehiculoUID(cursor.getString(cursor.getColumnIndexOrThrow("vehiculoUID")));
                v.setMarca(cursor.getString(cursor.getColumnIndexOrThrow("marca")));
                v.setModelo(cursor.getString(cursor.getColumnIndexOrThrow("modelo")));
                v.setMatricula(cursor.getString(cursor.getColumnIndexOrThrow("matricula")));
                v.setImagen(cursor.getString(cursor.getColumnIndexOrThrow("imagen")));
                v.setEstado(cursor.getString(cursor.getColumnIndexOrThrow("estado")));
                v.setPlazas(cursor.getInt(cursor.getColumnIndexOrThrow("plazas")));
                v.setPuertas(cursor.getInt(cursor.getColumnIndexOrThrow("puertas")));
                v.setTipoCombustible(cursor.getString(cursor.getColumnIndexOrThrow("tipoCombustible")));
                v.setTransmision(cursor.getString(cursor.getColumnIndexOrThrow("transmision")));
                v.setPrecioDia(cursor.getDouble(cursor.getColumnIndexOrThrow("precioDia")));
                v.setUbicacion(cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")));
                lista.add(v);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public boolean eliminarVehiculo(String uid) {
        SQLiteDatabase db = bdLocalSQLite.getWritableDatabase();
        int filas = db.delete("vehiculos", "vehiculoUID = ?", new String[]{uid});
        db.close();
        return filas > 0;
    }

    private Vehiculo VehiculoDesdeCursor(Cursor cursor) {
        Vehiculo v = new Vehiculo();
        v.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        v.setVehiculoUID(cursor.getString(cursor.getColumnIndexOrThrow("vehiculoUID")));
        v.setMarca(cursor.getString(cursor.getColumnIndexOrThrow("marca")));
        v.setModelo(cursor.getString(cursor.getColumnIndexOrThrow("modelo")));
        v.setMatricula(cursor.getString(cursor.getColumnIndexOrThrow("matricula")));
        v.setImagen(cursor.getString(cursor.getColumnIndexOrThrow("imagen")));
        v.setEstado(cursor.getString(cursor.getColumnIndexOrThrow("estado")));
        v.setPlazas(cursor.getInt(cursor.getColumnIndexOrThrow("plazas")));
        v.setPuertas(cursor.getInt(cursor.getColumnIndexOrThrow("puertas")));
        v.setTipoCombustible(cursor.getString(cursor.getColumnIndexOrThrow("tipoCombustible")));
        v.setTransmision(cursor.getString(cursor.getColumnIndexOrThrow("transmision")));
        v.setPrecioDia(cursor.getDouble(cursor.getColumnIndexOrThrow("precioDia")));
        v.setUbicacion(cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")));
        return v;
    }

    public List<Vehiculo> obtenerVehiculosPorCiudad(String ciudad) {
        List<Vehiculo> lista = new ArrayList<>();
        SQLiteDatabase db = bdLocalSQLite.getReadableDatabase();

        Cursor cursor = db.query("vehiculos", null, "ubicacion = ?", new String[]{ciudad}, null, null, null);

        while (cursor.moveToNext()) {
            Vehiculo v = VehiculoDesdeCursor(cursor); // ✔️ Ya no dará error
            lista.add(v);
        }

        cursor.close();
        db.close();

        return lista;
    }

}

