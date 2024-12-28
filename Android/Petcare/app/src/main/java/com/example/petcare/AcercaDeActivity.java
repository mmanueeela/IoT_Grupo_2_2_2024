package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AcercaDeActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;

    private TextView textViewFAQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acerca_de); // Este es el layout donde configuramos el TextView

        logo = findViewById(R.id.logoAcercaDe);
        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(AcercaDeActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        notificas = findViewById(R.id.notiAcercaDe);
        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(AcercaDeActivity.this, NotificasActivity.class);
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
