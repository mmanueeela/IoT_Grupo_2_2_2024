package com.example.petcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder> {
    private List<Comida> comidas;

    public ComidaAdapter(List<Comida> comidas) {
        this.comidas = comidas;
    }

    @NonNull
    @Override
    public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comida, parent, false);
        return new ComidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComidaViewHolder holder, int position) {
        Comida comida = comidas.get(position);
        holder.txtComida.setText(String.valueOf(comida.getPorcentaje()));
    }

    @Override
    public int getItemCount() {
        return comidas != null ? comidas.size() : 0;
    }

    public static class ComidaViewHolder extends RecyclerView.ViewHolder {
        TextView txtComida;

        public ComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComida = itemView.findViewById(R.id.txtComidaFirebase);
        }
    }
}
