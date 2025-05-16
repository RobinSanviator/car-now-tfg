package com.example.carnowapp.persistencia.firebasedatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseBaseDeDatosATiempoReal {
    private DatabaseReference databaseReference;

    public FirebaseBaseDeDatosATiempoReal() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://gestorrentacar-f4f48-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

}
