package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MisMascotasActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuButton;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_mascotas);

        // Obtener referencias
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.imageView16);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);

        logo = findViewById(R.id.logoMisMascotas);
        notificas = findViewById(R.id.notificaMisMascotas);

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(MisMascotasActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(MisMascotasActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

        // Configurar el evento de clic para abrir el menú
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START); // Abrir el menú desde la izquierda
            }
        });

        // Configurar el evento de clic para "MI PERFIL"
        menuOptionMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(MisMascotasActivity.this, MiperfilActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(MisMascotasActivity.this, AcercaDeActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(MisMascotasActivity.this, ContactoActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionMisMascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(MisMascotasActivity.this, MisMascotasActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(MisMascotasActivity.this, FAQActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        // Referencia al botón "btnAñadirMascota"
        Button btnAñadirMascota = findViewById(R.id.btnAñadirMascota);

        // Configurar el click listener para redirigir a AñadirMascotaActivity
        btnAñadirMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a AñadirMascotaActivity
                Intent intent = new Intent(MisMascotasActivity.this, AñadirMascotaActivity.class);
                startActivity(intent);
            }
        });
    }
}
