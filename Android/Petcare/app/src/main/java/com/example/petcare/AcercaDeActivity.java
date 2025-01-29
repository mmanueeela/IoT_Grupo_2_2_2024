package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class AcercaDeActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;

    private TextView textViewFAQ;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private ImageView menuButton;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acerca_de); // Este es el layout donde configuramos el TextView

        drawerLayout = findViewById(R.id.drawerLayout18);
        menuButton = findViewById(R.id.imageView10);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);

        // Acciones al presionar los botones del menú lateral
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menuOptionMiPerfil.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mi Perfil
            startActivity(new Intent(AcercaDeActivity.this, MiperfilActivity.class));
        });

        menuOptionMisMascotas.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mis Mascotas
            startActivity(new Intent(AcercaDeActivity.this, MiMascotaActivity.class));
        });

        menuOptionAcercaDe.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Acerca de
            startActivity(new Intent(AcercaDeActivity.this, AcercaDeActivity.class));
        });

        menuOptionContacto.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Contacto
            startActivity(new Intent(AcercaDeActivity.this, ContactoActivity.class));
        });

        menuOptionFaq.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad FAQ
            startActivity(new Intent(AcercaDeActivity.this, FAQActivity.class));
        });

        logo = findViewById(R.id.logoAcercaDe);
        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(AcercaDeActivity.this, MiMascotaActivity.class);
            startActivity(intent);
        });

        notificas = findViewById(R.id.notiAcercaDe);
        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(AcercaDeActivity.this, AjustesNotificasActivity.class);
            startActivity(intent);
        });

        // Referencia al TextView "Página de FAQ"
        textViewFAQ = findViewById(R.id.textView23);

        // Acción al presionar el TextView
        textViewFAQ.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(AcercaDeActivity.this, FAQActivity.class);
            startActivity(intent);
        });
    }
}
