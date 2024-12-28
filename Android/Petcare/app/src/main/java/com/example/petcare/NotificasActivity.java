package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificasActivity extends AppCompatActivity {

    private Button ajustesNotificaciones;
    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificaciones); // Este es el layout donde configuramos el TextView

        ajustesNotificaciones = findViewById(R.id.btnAjustesNoti);
        logo = findViewById(R.id.logoEditarMiPerfil);
        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(NotificasActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(NotificasActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar el boton
        ajustesNotificaciones.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(NotificasActivity.this, AjustesNotificasActivity.class);
            startActivity(intent);
        });



    }
}
