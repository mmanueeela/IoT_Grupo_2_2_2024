package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CodigoCorrecto extends AppCompatActivity {

    private Button mismascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codigocorrecto); // Asegúrate de tener el layout correspondiente

        mismascotas = findViewById(R.id.sinncodigobotonIRAMISMASCOTAS);

        // Establecer un OnClickListener para el botón
        mismascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir la actividad MisMascotasActivity
                Intent intent = new Intent(CodigoCorrecto.this, AñadirMascotaActivity.class);
                startActivity(intent); // Iniciar la actividad MisMascotasActivity
            }
        });
    }
}
