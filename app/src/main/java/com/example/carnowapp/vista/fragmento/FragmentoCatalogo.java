package com.example.carnowapp.vista.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carnowapp.R;
import com.example.carnowapp.vista.adaptador.CocheAdaptador;
import com.example.carnowapp.modelo.Coche;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragmentoCatalogo extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.catalogo_fragmento, container, false);

        // Configurar RecyclerView para Toyota
        RecyclerView recyclerToyota = vista.findViewById(R.id.recycler_toyota);
        recyclerToyota.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Lista de prueba de coches Toyota
        List<Coche> listaToyota = new ArrayList<>();
        listaToyota.add(new Coche("Toyota Corolla", R.drawable.toyota1));
        listaToyota.add(new Coche("Toyota Camry", R.drawable.toyota2));

        // Configura el adaptador con los datos de prueba para Toyota
        recyclerToyota.setAdapter(new CocheAdaptador(listaToyota));

        // Configurar RecyclerView para BMW
        RecyclerView recyclerBMW = vista.findViewById(R.id.recycler_bmw);
        recyclerBMW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Lista de prueba de coches BMW
        List<Coche> listaBMW = new ArrayList<>();
        listaBMW.add(new Coche("BMW Serie 3", R.drawable.bmw2));
        listaBMW.add(new Coche("BMW X5", R.drawable.bm1));

        // Configura el adaptador con los datos de prueba para BMW
        recyclerBMW.setAdapter(new CocheAdaptador(listaBMW));



        // BOTÓN PERFIL
        FloatingActionButton fabPerfil = vista.findViewById(R.id.fab_perfil);
        fabPerfil.setOnClickListener(v -> {
            // Abrir el FragmentoPerfil
            FragmentoPerfil fragmentoPerfil = new FragmentoPerfil();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_contenedor_menu_principal, fragmentoPerfil) // Asegúrate que tienes este contenedor en tu Activity
                    .addToBackStack(null) // Esto hace que puedas volver atrás con el botón
                    .commit();
        });


        return vista;
    }
}