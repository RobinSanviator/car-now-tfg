package com.example.carnowapp.datos.fuenteDeDatos.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.carnowapp.modelo.Usuario;


public class SQLiteUsuarioFuenteDeDatos {

    private final BaseDatosLocalSQLite bdLocalSQLite;

    public SQLiteUsuarioFuenteDeDatos(Context context) {
        bdLocalSQLite = new BaseDatosLocalSQLite(context);
    }

    // Insertar usuario - devuelve boolean para éxito
    public boolean insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = bdLocalSQLite.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("firebaseUID", usuario.getFirebaseUID());
        valores.put("nombre", usuario.getNombre());
        valores.put("email", usuario.getEmail());
        if (usuario.getTelefono() != null) {
            valores.put("telefono", usuario.getTelefono());
        } else {
            valores.putNull("telefono");
        }
        valores.put("dni", usuario.getDni());
        valores.put("tarjetaUltimos4", usuario.getTarjetaUltimos4());
        valores.put("tarjetaCaducidad", usuario.getTarjetaCaducidad());

        long resultado = db.insert("usuarios", null, valores);
        db.close();
        return resultado != -1;
    }

    // Obtener usuario por UID - devuelve objeto Usuario o null
    public Usuario obtenerUsuarioPorUID(String uid) {
        SQLiteDatabase db = bdLocalSQLite.getReadableDatabase();
        Usuario usuario = null;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "usuarios",
                    null,
                    "firebaseUID = ?",
                    new String[]{uid},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                Integer telefono = null;
                int telefonoIndex = cursor.getColumnIndex("telefono");
                if (telefonoIndex != -1 && !cursor.isNull(telefonoIndex)) {
                    telefono = cursor.getInt(telefonoIndex);
                }

                usuario = new Usuario(
                        cursor.getString(cursor.getColumnIndexOrThrow("firebaseUID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        telefono,
                        cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tarjetaUltimos4")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tarjetaCaducidad"))
                );
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return usuario;
    }

    // Actualizar usuario - devuelve boolean si actualizó alguna fila
    public boolean actualizarUsuario(Usuario usuario) {
        SQLiteDatabase db = bdLocalSQLite.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", usuario.getNombre());
        valores.put("email", usuario.getEmail());
        if (usuario.getTelefono() != null) {
            valores.put("telefono", usuario.getTelefono());
        } else {
            valores.putNull("telefono");
        }
        valores.put("dni", usuario.getDni());
        valores.put("tarjetaUltimos4", usuario.getTarjetaUltimos4());
        valores.put("tarjetaCaducidad", usuario.getTarjetaCaducidad());

        int filas = db.update("usuarios", valores, "firebaseUID = ?", new String[]{usuario.getFirebaseUID()});
        db.close();
        return filas > 0;
    }



}