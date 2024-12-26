package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MiperfilActivity extends AppCompatActivity {

    private Button btnModificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil); // Este es el layout donde configuramos el botón Modificar

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
