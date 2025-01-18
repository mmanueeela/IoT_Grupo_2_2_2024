package com.example.petcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {
    private List<Mascota> mascotas;

    public MascotaAdapter(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    @Override
    public MascotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MascotaViewHolder holder, int position) {
        Mascota mascota = mascotas.get(position);
        holder.nombre.setText(mascota.getNombre());
        holder.comida.setText(String.valueOf(mascota.getComida()));
        holder.agua.setText(String.valueOf(mascota.getAgua()));
        holder.hora.setText(mascota.getHora());

        // Cargar la imagen desde la URL usando Glide
        Glide.with(holder.itemView.getContext())
                .load(mascota.getUrlfoto()) // URL de la imagen
                .placeholder(R.drawable.placeholder_image) // Imagen mientras se carga
                .error(R.drawable.error_image) // Imagen si falla
                .into(holder.imageView); // ImageView donde se cargará
    }

    @Override
    public int getItemCount() {
        return mascotas.size();
    }

    public static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, comida, agua, hora;
        public ImageView imageView;

        public MascotaViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textNombre);
            comida = itemView.findViewById(R.id.CantidadComida);
            agua = itemView.findViewById(R.id.CantidadAgua);
            hora = itemView.findViewById(R.id.ProximaComida);
            imageView = itemView.findViewById(R.id.imageFoto);
        }
    }
}
