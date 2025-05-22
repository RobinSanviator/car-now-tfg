package com.example.carnowapp.datos.fuenteDeDatos.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosLocalSQLite extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "CarNowBD.db";
    private static final int VERSION_BD = 1;

    // Tabla Usuarios
    private static final String SQL_CREAR_TABLA_USUARIOS =
            "CREATE TABLE usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "firebaseUID TEXT NOT NULL UNIQUE, " +
                    "nombre TEXT, " +
                    "email TEXT, " +
                    "telefono INTEGER, " +
                    "dni TEXT, " +
                    "tarjetaUltimos4 TEXT, " +
                    "tarjetaCaducidad TEXT" +
                    ");";

    // Tabla Vehículos
    private static final String SQL_CREAR_TABLA_VEHICULOS =
            "CREATE TABLE vehiculos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "vehiculoUID TEXT NOT NULL UNIQUE, " +
                    "marca TEXT NOT NULL, " +
                    "modelo TEXT NOT NULL, " +
                    "matricula TEXT UNIQUE NOT NULL, " +
                    "imagen TEXT, " +                        // URL o ruta local
                    "estado TEXT DEFAULT 'disponible', " +   // disponible, reservado, mantenimiento, etc.
                    "plazas INTEGER, " +
                    "puertas INTEGER, " +
                    "tipoCombustible TEXT, " +               // gasolina, diésel, eléctrico, híbrido
                    "transmision TEXT, " +                   // manual, automática
                    "precioDia REAL NOT NULL" +
                    ");";


    // Tabla Reservas
    private static final String SQL_CREAR_TABLA_RESERVAS =
            "CREATE TABLE reservas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "reservaUID TEXT NOT NULL UNIQUE, " +
                    "idUsuario INTEGER NOT NULL, " +
                    "idVehiculo INTEGER NOT NULL, " +
                    "fechaInicio TEXT NOT NULL, " +      // formato ISO 8601
                    "fechaFin TEXT NOT NULL, " +
                    "estado TEXT DEFAULT 'pendiente', " + // pendiente, confirmada, cancelada, finalizada
                    "FOREIGN KEY(idUsuario) REFERENCES usuarios(id), " +
                    "FOREIGN KEY(idVehiculo) REFERENCES vehiculos(id)" +
                    ");";


    // Tabla Notificaciones
    private static final String SQL_CREAR_TABLA_NOTIFICACIONES =
            "CREATE TABLE notificaciones (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "notificacionUID TEXT NOT NULL UNIQUE, " +
                    "idUsuario INTEGER NOT NULL, " +
                    "mensaje TEXT NOT NULL, " +
                    "leida INTEGER DEFAULT 0, " +       // 0 = no leída, 1 = leída
                    "fecha TEXT NOT NULL, " +
                    "FOREIGN KEY(idUsuario) REFERENCES usuarios(id)" +
                    ");";

    // Tabla Pagos
    private static final String SQL_CREAR_TABLA_PAGOS =
            "CREATE TABLE pagos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pagoUID TEXT NOT NULL UNIQUE, " +
                    "idReserva INTEGER NOT NULL, " +
                    "metodoPago TEXT NOT NULL, " +      // tarjeta, paypal, etc.
                    "importe REAL NOT NULL, " +
                    "estado TEXT DEFAULT 'pendiente', " + // pendiente, pagado, fallido
                    "fechaPago TEXT, " +
                    "FOREIGN KEY(idReserva) REFERENCES reservas(id)" +
                    ");";


    public BaseDatosLocalSQLite(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR_TABLA_USUARIOS);
        db.execSQL(SQL_CREAR_TABLA_VEHICULOS);
        db.execSQL(SQL_CREAR_TABLA_RESERVAS);
        db.execSQL(SQL_CREAR_TABLA_NOTIFICACIONES);
        db.execSQL(SQL_CREAR_TABLA_PAGOS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pagos");
        db.execSQL("DROP TABLE IF EXISTS notificaciones");
        db.execSQL("DROP TABLE IF EXISTS reservas");
        db.execSQL("DROP TABLE IF EXISTS vehiculos");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}
