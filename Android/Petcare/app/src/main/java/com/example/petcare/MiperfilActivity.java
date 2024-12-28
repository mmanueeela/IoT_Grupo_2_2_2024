package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MiperfilActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuButton;
    private ImageView logo;
    private ImageView notificas;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private Button btnModificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.imageView16);
        logo = findViewById(R.id.logoEditarMiPerfil);
        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);
        btnModificar = findViewById(R.id.Modificar);

        // Acciones al presionar los botones del menú lateral
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menuOptionMiPerfil.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mi Perfil
            startActivity(new Intent(MiperfilActivity.this, MiperfilActivity.class));
        });

        menuOptionMisMascotas.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mis Mascotas
            startActivity(new Intent(MiperfilActivity.this, MisMascotasActivity.class));
        });

        menuOptionAcercaDe.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Acerca de
            startActivity(new Intent(MiperfilActivity.this, AcercaDeActivity.class));
        });

        menuOptionContacto.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Contacto
            startActivity(new Intent(MiperfilActivity.this, ContactoActivity.class));
        });

        menuOptionFaq.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad FAQ
            startActivity(new Intent(MiperfilActivity.this, FAQActivity.class));
        });

        // Acción al presionar el logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad Mis Mascotas
            startActivity(new Intent(MiperfilActivity.this, MisMascotasActivity.class));
        });

        // Acción al presionar las notificaciones
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de Notificaciones
            startActivity(new Intent(MiperfilActivity.this, NotificasActivity.class));
        });

        // Acción al presionar el botón Modificar
        btnModificar.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de Modificar Perfil
            startActivity(new Intent(MiperfilActivity.this, ModificarPerfil.class));
        });
    }
}
