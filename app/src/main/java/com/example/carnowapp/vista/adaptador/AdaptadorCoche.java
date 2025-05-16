package com.example.carnowapp.vista.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.carnowapp.R;
import com.example.carnowapp.modelo.Coche;

import java.util.List;

public class AdaptadorCoche extends RecyclerView.Adapter<AdaptadorCoche.ViewHolder> {

    private List<Coche> listaCoches;

    public AdaptadorCoche(List<Coche> listaCoches) {
        this.listaCoches = listaCoches;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_coche, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coche coche = listaCoches.get(position);
        holder.nombre.setText(coche.getModelo());

        // Cargar la imagen del coche en el ImageView
        holder.imagen.setImageResource(coche.getImagenUrl());  // Cargar la imagen del coche desde el recurso drawable
    }

    @Override
    public int getItemCount() {
        return listaCoches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView imagen;  // Añadimos el ImageView para mostrar la imagen

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tv_modelo);  // Asegúrate de que en tu layout `elemento_coche.xml` haya un TextView con este ID
            imagen = itemView.findViewById(R.id.iv_coche);  // Aquí mapeamos el ImageView
        }
    }

}

