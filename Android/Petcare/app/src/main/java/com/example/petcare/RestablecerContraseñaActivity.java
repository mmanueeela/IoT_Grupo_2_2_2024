package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RestablecerContraseñaActivity extends AppCompatActivity {

    private EditText correoField;
    private Button btnRestablecer;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recuperar_contrasena);

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Referencias a las vistas
        correoField = findViewById(R.id.editTextTextEmailAddress2);
        btnRestablecer = findViewById(R.id.button3);

        // Acción al presionar el botón de restablecer
        btnRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el correo ingresado
                String correo = correoField.getText().toString().trim();

                // Validar que el correo no esté vacío
                if (correo.isEmpty()) {
                    Toast.makeText(RestablecerContraseñaActivity.this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Enviar el enlace de restablecimiento de contraseña
                auth.sendPasswordResetEmail(correo)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Si se envió el correo con éxito
                                Toast.makeText(RestablecerContraseñaActivity.this, "Correo enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();

                                // Redirigir a la actividad CorreoEnviadoActivity
                                Intent intent = new Intent(RestablecerContraseñaActivity.this, CorreoEnviadoActivity.class);
                                startActivity(intent);
                                finish(); // Finaliza la actividad actual
                            } else {
                                // Si ocurrió un error al enviar el correo
                                Toast.makeText(RestablecerContraseñaActivity.this, "Error al enviar el correo: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
