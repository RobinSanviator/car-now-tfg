package com.example.carnowapp.vista.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Vehiculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CatalogoAdaptador extends RecyclerView.Adapter<CatalogoAdaptador.CatalogoViewHolder> {

    private final Map<String, List<Vehiculo>> mapaVehiculosPorMarca;
    private final VehiculoAdaptador.OnVehiculoClickListener listener;

    private final List<String> listaMarcas;

    public CatalogoAdaptador(Map<String, List<Vehiculo>> mapaVehiculosPorMarca,
                             VehiculoAdaptador.OnVehiculoClickListener listener) {
        this.mapaVehiculosPorMarca = mapaVehiculosPorMarca;
        this.listener = listener;
        this.listaMarcas = new ArrayList<>(mapaVehiculosPorMarca.keySet());
    }

    @NonNull
    @Override
    public CatalogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_catalogo, parent, false);
        return new CatalogoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogoViewHolder holder, int position) {
        String marca = listaMarcas.get(position);
        List<Vehiculo> vehiculosMarca = mapaVehiculosPorMarca.get(marca);

        holder.tvMarca.setText(marca);

        VehiculoAdaptador vehiculoAdaptador = new VehiculoAdaptador(vehiculosMarca, listener);
        holder.recyclerHorizontal.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerHorizontal.setAdapter(vehiculoAdaptador);
    }

    @Override
    public int getItemCount() {
        return listaMarcas.size();
    }

    public void actualizarCatalogo(Map<String, List<Vehiculo>> nuevoMapa) {
        mapaVehiculosPorMarca.clear();
        mapaVehiculosPorMarca.putAll(nuevoMapa);
        listaMarcas.clear();
        listaMarcas.addAll(nuevoMapa.keySet());
        notifyDataSetChanged();
    }

    static class CatalogoViewHolder extends RecyclerView.ViewHolder {

        TextView tvMarca;
        RecyclerView recyclerHorizontal;

        public CatalogoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarca = itemView.findViewById(R.id.text_marca_titulo);
            recyclerHorizontal = itemView.findViewById(R.id.recycler_coches_horizontal);
        }
    }
}
