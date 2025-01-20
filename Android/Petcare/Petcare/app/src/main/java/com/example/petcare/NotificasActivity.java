package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class NotificasActivity extends AppCompatActivity {

    private DrawerLayout drawerLayoutNotificaciones;
    private ImageView menuButton;
    private Button ajustesNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificaciones);

        drawerLayoutNotificaciones = findViewById(R.id.drawerLayoutNotificaciones);
        menuButton = findViewById(R.id.imageView16);
        ajustesNotificaciones = findViewById(R.id.btnAjustesNoti);

        // Configurar el evento de clic para abrir el menú lateral
        menuButton.setOnClickListener(v -> drawerLayoutNotificaciones.openDrawer(GravityCompat.START));

        // Configurar listeners para las opciones del menú
        findViewById(R.id.menu_option_miperfil).setOnClickListener(v -> {
            Intent intent = new Intent(NotificasActivity.this, MiperfilActivity.class);
            startActivity(intent);
            drawerLayoutNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_mismascotas).setOnClickListener(v -> {
            Intent intent = new Intent(NotificasActivity.this, MisMascotasActivity.class);
            startActivity(intent);
            drawerLayoutNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_acercade).setOnClickListener(v -> {
            Intent intent = new Intent(NotificasActivity.this, AcercaDeActivity.class);
            startActivity(intent);
            drawerLayoutNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_contacto).setOnClickListener(v -> {
            Intent intent = new Intent(NotificasActivity.this, ContactoActivity.class);
            startActivity(intent);
            drawerLayoutNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_faq).setOnClickListener(v -> {
            Intent intent = new Intent(NotificasActivity.this, FAQActivity.class);
            startActivity(intent);
            drawerLayoutNotificaciones.closeDrawer(GravityCompat.START);
        });

        // Configurar el listener para ajustes de notificaciones
        ajustesNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(NotificasActivity.this, AjustesNotificasActivity.class);
            startActivity(intent);
        });
    }
}
