package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CorreoEnviadoActivity extends AppCompatActivity {

    private Button btnVolverIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.correo_enviado); // Este es el layout donde configuramos el botón

        // Referencia al botón "Volver a Iniciar Sesión"
        btnVolverIniciarSesion = findViewById(R.id.button3);

        // Acción al presionar el botón
        btnVolverIniciarSesion.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de inicio de sesión
            Intent intent = new Intent(CorreoEnviadoActivity.this, IniciarSesionActivity.class);
            startActivity(intent);
            finish(); // Finaliza esta actividad para evitar que el usuario regrese a ella
        });
    }
}
