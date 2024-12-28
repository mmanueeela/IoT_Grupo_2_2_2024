package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditarMiMascotaActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_mascota); // Este es el layout donde configuramos el TextView

        logo = findViewById(R.id.logoEditarMiPerfil);
        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(EditarMiMascotaActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(EditarMiMascotaActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

    }
}
