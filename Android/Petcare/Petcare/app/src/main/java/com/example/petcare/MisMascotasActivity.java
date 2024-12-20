package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MisMascotasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_mascotas);

        // Referencia al botón "btnAñadirMascota"
        Button btnAñadirMascota = findViewById(R.id.btnAñadirMascota);

        // Configurar el click listener
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
