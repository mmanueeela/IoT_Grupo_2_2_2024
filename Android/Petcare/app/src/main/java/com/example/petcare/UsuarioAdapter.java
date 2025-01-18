package com.example.petcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.List;


public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private List<Usuario> usuarios;


    public UsuarioAdapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }


    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }


    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.txtNombre.setText(usuario.getNombre());
        holder.txtApellidos.setText(usuario.getApellidos());
        holder.txtCorreo.setText(usuario.getCorreo());

        // Cargar la imagen desde la URL usando Glide
        Glide.with(holder.itemView.getContext())
                .load(usuario.getUrlfoto()) // URL de la imagen
                .placeholder(R.drawable.placeholder_image) // Imagen mientras se carga
                .error(R.drawable.error_image) // Imagen si falla
                .into(holder.imageView); // ImageView donde se cargará

    }


    @Override
    public int getItemCount() {
        return usuarios.size();
    }


    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtApellidos, txtCorreo;
        public ImageView imageView;



        public UsuarioViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.verdaderoNombre);
            txtApellidos = itemView.findViewById(R.id.verdaderoApellido);
            txtCorreo = itemView.findViewById(R.id.txtVerdaderoCorreo);
            imageView = itemView.findViewById(R.id.imageView8);

        }
    }
}

