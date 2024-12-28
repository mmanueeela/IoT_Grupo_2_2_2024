package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ModificarPerfil extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        logo = findViewById(R.id.logoEditarMiPerfil);
        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(ModificarPerfil.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);
        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(ModificarPerfil.this, NotificasActivity.class);
            startActivity(intent);
        });
    }
}