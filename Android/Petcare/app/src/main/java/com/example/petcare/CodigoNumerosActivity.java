package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CodigoNumerosActivity extends AppCompatActivity {

    private EditText editTextCodigo;
    private Button buttonComprobarCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadircodigo); // Ya está en tu diseño XML

        // Inicializar las vistas
        editTextCodigo = findViewById(R.id.codigosensor);
        buttonComprobarCodigo = findViewById(R.id.sinncodigobotonIRAMISMASCOTAS);

        // Configurar el listener del botón
        buttonComprobarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto ingresado en el EditText
                String codigoIngresado = editTextCodigo.getText().toString().trim();

                // Comprobar si el código es "true"
                if ("true".equals(codigoIngresado)) {
                    // Si es "true", redirigir a la actividad CodigoCorrecto
                    Intent intent = new Intent(CodigoNumerosActivity.this, CodigoCorrecto.class);
                    startActivity(intent);
                } else {
                    // Si no es "true", puedes mostrar un mensaje (opcional)
                    editTextCodigo.setError("Código incorrecto. Inténtalo de nuevo.");
                }
            }
        });
    }
}
