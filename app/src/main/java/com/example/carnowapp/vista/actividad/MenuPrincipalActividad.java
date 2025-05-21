package com.example.carnowapp.vista.actividad;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.carnowapp.R;
import com.example.carnowapp.vista.fragmento.FragmentoAjustes;
import com.example.carnowapp.vista.fragmento.FragmentoCatalogo;
import com.example.carnowapp.vista.fragmento.FragmentoNotification;
import com.example.carnowapp.vista.fragmento.FragmentoReserva;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MenuPrincipalActividad extends AppCompatActivity {

    private FrameLayout flContenedorMenuPrincipal;
    private BottomNavigationView bnvMenuPrincipal;
    private Map<Integer, Fragment> mapaFragmento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.actividad_menu_principal);

        configurarMapaFragmentos();
        configurarListenerNavigation();
        cargarFragmentoPorDefecto(savedInstanceState);

    }

    private void configurarMapaFragmentos() {
        mapaFragmento = new HashMap<>();
        mapaFragmento.put(R.id.opcion_menu_principal, new FragmentoCatalogo());
        mapaFragmento.put(R.id.opcion_ajustes, new FragmentoAjustes());
        mapaFragmento.put(R.id.opcion_reserva, new FragmentoReserva());
        mapaFragmento.put(R.id.opcion_notificaciones, new FragmentoNotification());
    }

    private void configurarListenerNavigation() {
        bnvMenuPrincipal = findViewById(R.id.bnv_menu_principal);
        flContenedorMenuPrincipal = findViewById(R.id.fl_contenedor_menu_principal);

        bnvMenuPrincipal.setOnItemSelectedListener(item -> {
            // Inicializar transacción para cambiar fragmentos
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Obtener el fragmento asociado al item seleccionado desde el mapa
            Fragment fragment = mapaFragmento.get(item.getItemId());
            if (fragment != null) {
                transaction.replace(R.id.fl_contenedor_menu_principal, fragment);
                transaction.commit();
            }

            return true;
        });
    }

    private void cargarFragmentoPorDefecto(Bundle savedInstanceState) {
        bnvMenuPrincipal = findViewById(R.id.bnv_menu_principal);
        flContenedorMenuPrincipal = findViewById(R.id.fl_contenedor_menu_principal);

        // Solo cargar el fragmento por defecto si no hay estado guardado
        if (savedInstanceState == null) {
            // Establecer el ítem seleccionado por defecto
            bnvMenuPrincipal.setSelectedItemId(R.id.opcion_menu_principal);

            // Cargar el fragmento catalogo (vehículos)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_contenedor_menu_principal, new FragmentoCatalogo())
                    .commit();
        }
    }
}