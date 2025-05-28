package com.example.carnowapp.vista.fragmento;

import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Vehiculo;
import com.example.carnowapp.utilidad.MensajesUtilidad;
import com.example.carnowapp.utilidad.NavegacionUtilidad;
import com.example.carnowapp.vista.adaptador.CatalogoAdaptador;
import com.example.carnowapp.vista.adaptador.VehiculoAdaptador;
import com.example.carnowapp.vistamodelo.VehiculoVistaModelo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentoCatalogo extends Fragment {

    private AutoCompleteTextView actSelectorUbicacion;
    private FloatingActionButton fabFiltros, fabPerfil;
    private RecyclerView recyclerCatalogo;
    private StepView vista_pasos;

    private VehiculoVistaModelo vehiculoVistaModelo;
    private CatalogoAdaptador catalogoAdaptador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.catalogo_fragmento, container, false);

        inicializarVistas(vista);
        configurarSelectorUbicacion();
        configurarListeners();
        configurarFlujoPasos();
        configurarRecyclerView();
        observarDatos();
        cargarVehiculos();
        return vista;
    }



    private void inicializarVistas(View vista) {
        actSelectorUbicacion = vista.findViewById(R.id.act_selector_ubicacion);
        fabFiltros = vista.findViewById(R.id.fab_filtros);
        fabPerfil = vista.findViewById(R.id.fab_perfil);
        recyclerCatalogo = vista.findViewById(R.id.recycler_vehiculos);
        vista_pasos = vista.findViewById(R.id.vista_pasos);
        vehiculoVistaModelo = new ViewModelProvider(this).get(VehiculoVistaModelo.class);
    }

    private void configurarFlujoPasos(){
        vista_pasos.go(0, true);
    }


    private void configurarSelectorUbicacion() {
        // Cargar el array de ciudades desde strings.xml
        String[] ubicaciones = getResources().getStringArray(R.array.lista_ubicaciones);

        // Crear un ArrayAdapter simple
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ubicaciones);

        // Asignar el adapter al AutoCompleteTextView
        actSelectorUbicacion.setAdapter(adapter);

        // Para que se muestre el dropdown al hacer clic (opcional)
        actSelectorUbicacion.setOnClickListener(v -> actSelectorUbicacion.showDropDown());
    }

    private void configurarListeners() {
        fabFiltros.setOnClickListener(v -> {
            // Aquí se mostrará el diálogo de filtros más adelante
        });

        fabPerfil.setOnClickListener(v -> {
            NavegacionUtilidad.cargarFragmento((AppCompatActivity) requireContext(), new FragmentoPerfil());
        });

        actSelectorUbicacion.setOnItemClickListener((parent, view, position, id) -> {
            String ciudadSeleccionada = (String) parent.getItemAtPosition(position);
            vehiculoVistaModelo.cargarVehiculosPorCiudad(ciudadSeleccionada);
        });
    }

    private void configurarRecyclerView() {

            catalogoAdaptador = new CatalogoAdaptador(new HashMap<>(), new VehiculoAdaptador.OnVehiculoClickListener() {
                @Override
                public void onReservarClick(Vehiculo vehiculo) {
                    // Aquí implementarás la lógica para reservar más adelante
                    MensajesUtilidad.mostrarMensaje(requireView(), R.string.permiso_camara_denegada);
                }

                @Override
                public void onDetalleClick(Vehiculo vehiculo) {
                    // Aquí iría la lógica para mostrar detalle del vehículo
                    MensajesUtilidad.mostrarMensaje(requireView(), R.string.mensaje_verificacion_correo_snackbar);
                }
            });

            recyclerCatalogo.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerCatalogo.setAdapter(catalogoAdaptador);
        }

    private void observarDatos() {
        vehiculoVistaModelo.getVehiculosLiveData().observe(getViewLifecycleOwner(), vehiculos -> {
            Map<String, List<Vehiculo>> agrupadoPorMarca = new HashMap<>();
            for (Vehiculo v : vehiculos) {
                String marca = v.getMarca();
                if (!agrupadoPorMarca.containsKey(marca)) {
                    agrupadoPorMarca.put(marca, new ArrayList<>());
                }
                agrupadoPorMarca.get(marca).add(v);
            }
            catalogoAdaptador.actualizarCatalogo(agrupadoPorMarca);
        });

        vehiculoVistaModelo.getError().observe(getViewLifecycleOwner(), error -> {
            if (Boolean.TRUE.equals(error)) {
                MensajesUtilidad.mostrarMensaje(requireView(), R.string.error_registro);
            }
        });
    }

    private void cargarVehiculos() {
        vehiculoVistaModelo.cargarVehiculos();
    }
}