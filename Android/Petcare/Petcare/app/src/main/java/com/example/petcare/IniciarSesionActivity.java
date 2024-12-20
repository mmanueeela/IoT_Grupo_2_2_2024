package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class IniciarSesionActivity extends AppCompatActivity {
    private EditText correoField, contraseñaField;
    private Button btnIniciarSesion;

    private FirebaseAuth auth; // Instancia de Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_sesion);

        // Inicializar Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Referencias a las vistas en el layout
        correoField = findViewById(R.id.correo);
        contraseñaField = findViewById(R.id.contraseña);
        btnIniciarSesion = findViewById(R.id.login);

        // Acción al presionar "Iniciar Sesión"
        btnIniciarSesion.setOnClickListener(v -> {
            String correo = correoField.getText().toString().trim();
            String contraseña = contraseñaField.getText().toString().trim();

            if (correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(IniciarSesionActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Usar Firebase Authentication para autenticar al usuario
            auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Obtener el ID del usuario autenticado
                                String userId = user.getUid();

                                // Consultar Firestore para obtener el nombre del usuario
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users").document(userId)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String nombre = documentSnapshot.getString("nombre");

                                                // Saludar al usuario por su nombre
                                                Toast.makeText(IniciarSesionActivity.this, "¡Bienvenid@, " + nombre + "!", Toast.LENGTH_SHORT).show();
                                            }

                                            // Redirigir a "MisMascotasActivity"
                                            Intent intent = new Intent(IniciarSesionActivity.this, MisMascotasActivity.class);
                                            startActivity(intent);
                                            finish(); // Finalizar la actividad actual
                                        })
                                        .addOnFailureListener(e -> {
                                            // Si ocurre un error al obtener el nombre, mostrar un mensaje de error
                                            Toast.makeText(IniciarSesionActivity.this, "Error al obtener nombre del usuario", Toast.LENGTH_LONG).show();
                                        });
                            }
                        } else {
                            Toast.makeText(IniciarSesionActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(IniciarSesionActivity.this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
/*
              ..----.._    _
            .' .--.    "-.(O)_
'-.__.-'"'=:|  ,  _)_ \__ . c\'-..  <--- Pelusín
             ''------'---''---'-"⠀⠀⠀⠀⠀⠀
 */