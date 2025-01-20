package com.example.petcare;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarMiMascotaActivity extends AppCompatActivity {

    private EditText nombreMascota, frecuenciaComida, horasComida, cantidadComida;
    private Button botonGuardar;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String mascotaId; // Identificador único de la mascota que vamos a editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_mascota);

        // Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Enlazar vistas
        nombreMascota = findViewById(R.id.nombre);
        frecuenciaComida = findViewById(R.id.frecuencia);
        horasComida = findViewById(R.id.horas);
        cantidadComida = findViewById(R.id.cantidad);
        botonGuardar = findViewById(R.id.yaestaa);

        // Obtener el ID del usuario autenticado
        String userId = firebaseAuth.getCurrentUser().getUid();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar las mascotas del usuario
        cargarMascotasDelUsuario(userId);

        // Configurar acción del botón "¡YA ESTÁ!"
        botonGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void cargarMascotasDelUsuario(String userId) {
        firestore.collection("users").document(userId)
                .collection("pets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Si hay mascotas, obtener la primera (o la que se desee)
                        mascotaId = queryDocumentSnapshots.getDocuments().get(0).getId(); // Ejemplo: toma la primera mascota

                        // Cargar los datos de esa mascota
                        cargarDatosMascota(userId, mascotaId);
                    } else {
                        Toast.makeText(this, "No tienes mascotas registradas.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar mascotas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void cargarDatosMascota(String userId, String mascotaId) {
        firestore.collection("users").document(userId)
                .collection("pets").document(mascotaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String frecuencia = documentSnapshot.getString("frecuencia");
                        String horas = documentSnapshot.getString("hora");
                        String cantidad = documentSnapshot.getString("cantidad");

                        // Rellenar los campos con los datos de la mascota
                        nombreMascota.setText(nombre);
                        frecuenciaComida.setText(frecuencia);
                        horasComida.setText(horas);
                        cantidadComida.setText(cantidad);
                    } else {
                        Toast.makeText(this, "No se encontraron datos para esta mascota.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void guardarCambios() {
        String nombre = nombreMascota.getText().toString().trim();
        String frecuencia = frecuenciaComida.getText().toString().trim();
        String horas = horasComida.getText().toString().trim();
        String cantidad = cantidadComida.getText().toString().trim();

        if (nombre.isEmpty() || frecuencia.isEmpty() || horas.isEmpty() || cantidad.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mascotaActualizada = new HashMap<>();
        mascotaActualizada.put("nombre", nombre);
        mascotaActualizada.put("frecuencia", frecuencia);
        mascotaActualizada.put("hora", horas);
        mascotaActualizada.put("cantidad", cantidad);

        String userId = firebaseAuth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .collection("pets").document(mascotaId)
                .update(mascotaActualizada)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la actividad
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
