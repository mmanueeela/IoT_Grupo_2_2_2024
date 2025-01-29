package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    private EditText nombreField, apellidosField, correoField, contraseñaField;
    private Button btnRegistrar;

    private FirebaseAuth auth; // Instancia de Firebase Authentication
    private FirebaseFirestore db; // Instancia de Firebase Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Encontrar el TextView por su ID
        TextView iniciarSesionTextView = findViewById(R.id.IniciarSesion);

        // Configurar el listener
        iniciarSesionTextView.setOnClickListener(v -> {
            // Crear un Intent para ir a IniciarSesionActivity
            Intent intent = new Intent(RegistroActivity.this, IniciarSesionActivity.class);
            startActivity(intent); // Iniciar la actividad
        });

        // Inicializar Firebase Authentication y Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a las vistas
        nombreField = findViewById(R.id.nombre);
        apellidosField = findViewById(R.id.apellidos);
        correoField = findViewById(R.id.correo);
        contraseñaField = findViewById(R.id.contraseña);
        btnRegistrar = findViewById(R.id.register);

        // Acción al presionar el botón de registrar
        btnRegistrar.setOnClickListener(v -> {
            // Obtener los valores de los campos
            String nombre = nombreField.getText().toString().trim();
            String apellidos = apellidosField.getText().toString().trim();
            String correo = correoField.getText().toString().trim();
            String contraseña = contraseñaField.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
                // Si algún campo está vacío, mostrar el Toast
                Toast.makeText(RegistroActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return; // No continuar con el registro
            }

            // Si todos los campos están llenos, proceder con el registro
            auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Obtener el usuario actual después de registrarlo
                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                String userId = user.getUid(); // UID generado por Firebase Authentication

                                // Crear un mapa con los datos del usuario
                                Map<String, Object> usuario = new HashMap<>();
                                usuario.put("nombre", nombre);
                                usuario.put("apellidos", apellidos);
                                usuario.put("correo", correo);
                                usuario.put("userId", userId);

                                // Guardar los datos adicionales en Firestore
                                db.collection("users")
                                        .document(userId) // Usa el UID del usuario como el ID del documento
                                        .set(usuario)
                                        .addOnSuccessListener(aVoid -> {
                                            // Toast para indicar que el usuario fue registrado correctamente
                                            Toast.makeText(RegistroActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();

                                            // Si el registro fue exitoso, redirigir a la actividad EscaneoActivity
                                            Intent intent = new Intent(RegistroActivity.this, EscanearActivity.class);
                                            startActivity(intent); // Iniciar la actividad EscaneoActivity
                                            finish(); // Finalizar la actividad actual
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(RegistroActivity.this, "Error al guardar los datos en Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            }
                        } else {
                            // Si el registro de Firebase falla
                            Toast.makeText(RegistroActivity.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
