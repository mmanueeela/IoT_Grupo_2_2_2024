package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MiMascotaActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;
    private Button editarMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mimascota); // Este es el layout donde configuramos el TextView

        logo = findViewById(R.id.logoMiMascota);
        notificas = findViewById(R.id.notiMiMascota);
        editarMascota = findViewById(R.id.modificaMiMascota);


        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiMascotaActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiMascotaActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar editar
        editarMascota.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiMascotaActivity.this, EditarMiMascotaActivity.class);
            startActivity(intent);
        });

    }
}
