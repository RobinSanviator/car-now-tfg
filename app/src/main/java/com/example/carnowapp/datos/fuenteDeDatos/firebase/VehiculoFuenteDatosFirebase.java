package com.example.carnowapp.datos.fuenteDeDatos.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.carnowapp.modelo.Vehiculo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VehiculoFuenteDatosFirebase {

    private final DatabaseReference vehiculosRef;

    public VehiculoFuenteDatosFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        vehiculosRef = database.getReference("vehiculos");
    }

    public Task<Void> guardarVehiculo(String uid, Vehiculo vehiculo) {
        return vehiculosRef.child(uid).setValue(vehiculo);
    }

    public Task<List<Vehiculo>> obtenerTodosVehiculos() {
        TaskCompletionSource<List<Vehiculo>> taskSource = new TaskCompletionSource<>();

        vehiculosRef.get().addOnSuccessListener(snapshot -> {
            List<Vehiculo> lista = new ArrayList<>();
            for (DataSnapshot hijo : snapshot.getChildren()) {
                Vehiculo v = hijo.getValue(Vehiculo.class);
                if (v != null) {
                    lista.add(v);
                }
            }
            taskSource.setResult(lista);
        }).addOnFailureListener(taskSource::setException);

        return taskSource.getTask();
    }

    public Task<Void> eliminarVehiculo(String uid) {
        return vehiculosRef.child(uid).removeValue();
    }

    public Task<String> subirImagenVehiculo(Uri uriImagen, String vehiculoUID) {
        if (vehiculoUID == null || vehiculoUID.isEmpty()) {
            return Tasks.forException(new Exception("UID del vehículo no válido"));
        }

        String ruta = "imagenes_vehiculos/" + vehiculoUID + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(ruta);

        return ref.putFile(uriImagen).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Obtener URL de descarga
            return ref.getDownloadUrl();
        }).continueWithTask(urlTask -> {
            if (!urlTask.isSuccessful()) {
                throw urlTask.getException();
            }
            String urlImagen = urlTask.getResult().toString();
            // Guardar la URL de la imagen en el nodo del vehículo
            return vehiculosRef.child(vehiculoUID).child("imagenUrl").setValue(urlImagen)
                    .continueWith(task2 -> {
                        if (!task2.isSuccessful()) {
                            throw task2.getException();
                        }
                        return urlImagen;
                    });
        });
    }

    public Task<List<Vehiculo>> obtenerVehiculosPorCiudad(String ciudad) {
        TaskCompletionSource<List<Vehiculo>> taskSource = new TaskCompletionSource<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vehiculos");

        ref.orderByChild("ubicacion").equalTo(ciudad)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Vehiculo> lista = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Vehiculo v = ds.getValue(Vehiculo.class);
                            lista.add(v);
                        }
                        taskSource.setResult(lista);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        taskSource.setException(error.toException());
                    }
                });

        return taskSource.getTask();
    }

}
