package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class FAQActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuButton;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private ImageView logo;
    private ImageView notificas;
    private TextView Contactanos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq); // Este es el layout de FAQActivity

        // Obtener referencias
        drawerLayout = findViewById(R.id.drawerLayout18);
        menuButton = findViewById(R.id.imageView16);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);
        logo = findViewById(R.id.logoFAQ);
        notificas = findViewById(R.id.NotificacionesFAQ);
        Contactanos = findViewById(R.id.textView23);

        // Acción al presionar el TextView
        Contactanos.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad formulario
            Intent intent = new Intent(FAQActivity.this, ContactoActivity.class);
            startActivity(intent);
        });

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad MisMascotasActivity
            Intent intent = new Intent(FAQActivity.this, MiMascotaActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificaciones
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad NotificasActivity
            Intent intent = new Intent(FAQActivity.this, AjustesNotificasActivity.class);
            startActivity(intent);
        });

        // Configurar el evento de clic para abrir el menú
        menuButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START); // Abrir el menú desde la izquierda
        });

        // Configurar las acciones de los botones del menú
        menuOptionMiPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(FAQActivity.this, MiperfilActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú
        });

        menuOptionMisMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(FAQActivity.this, MiMascotaActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú
        });

        menuOptionAcercaDe.setOnClickListener(v -> {
            Intent intent = new Intent(FAQActivity.this, AcercaDeActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú
        });

        menuOptionContacto.setOnClickListener(v -> {
            Intent intent = new Intent(FAQActivity.this, ContactoActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú
        });

        menuOptionFaq.setOnClickListener(v -> {
            // No hace falta hacer nada, ya estamos en la actividad FAQ
            drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú
        });
    }
}
