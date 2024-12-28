package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactoActivity extends AppCompatActivity {

    // Declaramos las vistas
    private EditText nombreEditText, correoEditText, problemaEditText;
    private Button enviarFormularioButton;

    private ImageView logo;
    private ImageView notificas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario); // Este es el layout que creamos

        logo = findViewById(R.id.logoContacto);
        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(ContactoActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        notificas = findViewById(R.id.NotificacionesContacto);
        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(ContactoActivity.this, NotificasActivity.class);
            startActivity(intent);
        });


        // Inicializamos las vistas
        nombreEditText = findViewById(R.id.Nombre);
        correoEditText = findViewById(R.id.Correo);
        problemaEditText = findViewById(R.id.Problema);
        enviarFormularioButton = findViewById(R.id.EnviarFormulario);

        // Configuramos el OnClickListener para el botón de enviar formulario
        enviarFormularioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificamos si todos los campos están llenos
                if (isFormComplete()) {
                    // Si todos los campos están llenos, mostramos el toast y redirigimos
                    Toast.makeText(ContactoActivity.this, "Formulario enviado con éxito!", Toast.LENGTH_SHORT).show();

                    // Redirigimos a MisMascotasActivity
                    Intent intent = new Intent(ContactoActivity.this, MisMascotasActivity.class);
                    startActivity(intent);
                    finish(); // Terminamos la actividad actual para que no se quede en el stack de actividades
                } else {
                    // Si algún campo está vacío, mostramos el toast de error
                    Toast.makeText(ContactoActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para verificar si todos los campos están llenos
    private boolean isFormComplete() {
        String nombre = nombreEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String problema = problemaEditText.getText().toString().trim();

        // Verificamos si alguno de los campos está vacío
        return !nombre.isEmpty() && !correo.isEmpty() && !problema.isEmpty();
    }
}
