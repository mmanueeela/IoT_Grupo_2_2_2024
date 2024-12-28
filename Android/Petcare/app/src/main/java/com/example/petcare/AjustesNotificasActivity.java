package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AjustesNotificasActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajuste_de_notis); // Este es el layout donde configuramos el TextView

        logo = findViewById(R.id.logoEditarMiPerfil);
        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(AjustesNotificasActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(AjustesNotificasActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

    }
}
