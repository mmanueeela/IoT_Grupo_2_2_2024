package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class AjustesNotificasActivity extends AppCompatActivity {

    private DrawerLayout drawerLayoutAjustesNotificaciones;
    private ImageView menuButton;
    private Button guardarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajuste_de_notis); // Este es el layout con el DrawerLayout

        drawerLayoutAjustesNotificaciones = findViewById(R.id.drawerLayoutAjustesNotificaciones);
        menuButton = findViewById(R.id.imageView16);
        guardarButton = findViewById(R.id.Modificar);

        // Configurar el evento de clic para abrir el menú lateral
        menuButton.setOnClickListener(v -> drawerLayoutAjustesNotificaciones.openDrawer(GravityCompat.START));

        // Configurar los listeners para las opciones del menú
        findViewById(R.id.menu_option_miperfil).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, MiperfilActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_mismascotas).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, MisMascotasActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_acercade).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, AcercaDeActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_contacto).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, ContactoActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_faq).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, FAQActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        // Acción del botón "Guardar"
        guardarButton.setOnClickListener(v -> {
            // Guardar configuraciones y hacer algo
        });
    }
}
