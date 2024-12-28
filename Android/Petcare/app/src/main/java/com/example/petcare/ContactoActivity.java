package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.core.view.GravityCompat;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class ContactoActivity extends AppCompatActivity {

    private EditText nombreEditText, correoEditText, problemaEditText;
    private Button enviarFormularioButton;

    private ImageView logo;
    private ImageView notificas;
    private ImageView menuButton;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario); // Este es el layout que creamos

        // Inicializamos las vistas
        drawerLayout = findViewById(R.id.drawerLayoutContacto);  // El ID del DrawerLayout debe coincidir con el XML
        logo = findViewById(R.id.logoContacto);
        notificas = findViewById(R.id.NotificacionesContacto);
        menuButton = findViewById(R.id.imageView16);  // Este es el botón para abrir el menú lateral
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(ContactoActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificaciones
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(ContactoActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

        // Acción para abrir el menú lateral
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menuOptionMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(ContactoActivity.this, MiperfilActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(ContactoActivity.this, AcercaDeActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(ContactoActivity.this, ContactoActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionMisMascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(ContactoActivity.this, MisMascotasActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        menuOptionFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(ContactoActivity.this, FAQActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de la navegación
            }
        });

        // Configuramos el OnClickListener para el botón de enviar formulario
        nombreEditText = findViewById(R.id.Nombre);
        correoEditText = findViewById(R.id.Correo);
        problemaEditText = findViewById(R.id.Problema);
        enviarFormularioButton = findViewById(R.id.EnviarFormulario);

        enviarFormularioButton.setOnClickListener(v -> {
            if (isFormComplete()) {
                Toast.makeText(ContactoActivity.this, "Formulario enviado con éxito!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContactoActivity.this, MisMascotasActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ContactoActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isFormComplete() {
        String nombre = nombreEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String problema = problemaEditText.getText().toString().trim();
        return !nombre.isEmpty() && !correo.isEmpty() && !problema.isEmpty();
    }
}
