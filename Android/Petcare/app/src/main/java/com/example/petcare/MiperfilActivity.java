package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MiperfilActivity extends AppCompatActivity {

    private Button btnModificar;
    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil); // Este es el layout donde configuramos el botón Modificar

        logo = findViewById(R.id.logoEditarMiPerfil);
        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiperfilActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);
        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiperfilActivity.this, NotificasActivity.class);
            startActivity(intent);
        });


        // Referencia al botón "Modificar"
        btnModificar = findViewById(R.id.Modificar);

        // Acción al presionar el botón Modificar
        btnModificar.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad ModificarPerfilActivity
            Intent intent = new Intent(MiperfilActivity.this, ModificarPerfil.class);
            startActivity(intent);
        });
    }
}
