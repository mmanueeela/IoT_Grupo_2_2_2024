package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class ModificarPerfil extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuButton;
    private ImageView logo;
    private ImageView notificas;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private Button btnYaEsta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.imageView16);
        logo = findViewById(R.id.logoEditarMiPerfil);
        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);
        btnYaEsta = findViewById(R.id.YaEsta);

        // Abrir el menú lateral
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Acciones del menú lateral
        menuOptionMiPerfil.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(ModificarPerfil.this, MiperfilActivity.class));
        });

        menuOptionMisMascotas.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(ModificarPerfil.this, MisMascotasActivity.class));
        });

        menuOptionAcercaDe.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(ModificarPerfil.this, AcercaDeActivity.class));
        });

        menuOptionContacto.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(ModificarPerfil.this, ContactoActivity.class));
        });

        menuOptionFaq.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(ModificarPerfil.this, FAQActivity.class));
        });

        // Acción al presionar el logo
        logo.setOnClickListener(v -> {
            startActivity(new Intent(ModificarPerfil.this, MisMascotasActivity.class));
        });

        // Acción al presionar las notificaciones
        notificas.setOnClickListener(v -> {
            startActivity(new Intent(ModificarPerfil.this, NotificasActivity.class));
        });

        // Acción al presionar "¡Ya Está!"
        btnYaEsta.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de perfil o cualquier otra acción que se requiera
            startActivity(new Intent(ModificarPerfil.this, MiperfilActivity.class));
        });
    }
}
