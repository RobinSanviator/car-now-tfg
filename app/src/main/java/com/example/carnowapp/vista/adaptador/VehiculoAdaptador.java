package com.example.carnowapp.vista.adaptador;

import android.animation.Animator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Vehiculo;
import com.example.carnowapp.utilidad.AnimacionUtilidad;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class VehiculoAdaptador extends RecyclerView.Adapter<VehiculoAdaptador.VehiculoViewHolder> {

    private final List<Vehiculo> listaVehiculos;
    private final OnVehiculoClickListener listener;

    public interface OnVehiculoClickListener {
        void onReservarClick(Vehiculo vehiculo);
        void onDetalleClick(Vehiculo vehiculo);
    }


    public VehiculoAdaptador(List<Vehiculo> listaVehiculos, OnVehiculoClickListener listener) {
        this.listaVehiculos = listaVehiculos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_vehiculo, parent, false);
        return new VehiculoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoViewHolder holder, int position) {
        Vehiculo vehiculo = listaVehiculos.get(position);

        holder.tvModelo.setText(vehiculo.getModelo());
        holder.tvCombustible.setText(vehiculo.getTipoCombustible());
        holder.tvTransmision.setText(vehiculo.getTransmision());
        holder.tvPlazas.setText(String.valueOf(vehiculo.getPlazas()));
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €/día", vehiculo.getPrecioDia()));


        // Cargar imagen desde URL de Firebase Storage (asegúrate de que getUrlImagen() tiene una URL válida)
        Glide.with(holder.itemView.getContext())
                .load(vehiculo.getImagen())
                .placeholder(R.drawable.coche_fondo)
                .into(holder.ivVehiculo);

        holder.itemView.setOnClickListener(v -> {
            AnimacionUtilidad.rotar360Y(holder.itemView, 600, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) { }

                @Override
                public void onAnimationEnd(Animator animator) {
                    // Aquí llama a la función para mostrar detalle del vehículo
                    listener.onDetalleClick(vehiculo);
                }

                @Override
                public void onAnimationCancel(Animator animator) { }

                @Override
                public void onAnimationRepeat(Animator animator) { }
            });
        });
    }

    @Override
    public int getItemCount() {
        return listaVehiculos.size();
    }

    public void actualizarLista(List<Vehiculo> nuevaLista) {
        listaVehiculos.clear();
        listaVehiculos.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    static class VehiculoViewHolder extends RecyclerView.ViewHolder {

        TextView tvModelo, tvCombustible, tvTransmision, tvPlazas, tvPrecio;
        ImageView ivVehiculo;

        public VehiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModelo = itemView.findViewById(R.id.tv_modelo);
            tvCombustible = itemView.findViewById(R.id.tv_combustible);
            tvTransmision = itemView.findViewById(R.id.tv_trasmision);
            tvPlazas = itemView.findViewById(R.id.tv_plazas_coche);
            ivVehiculo = itemView.findViewById(R.id.iv_vehiculo);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
        }
    }


}
