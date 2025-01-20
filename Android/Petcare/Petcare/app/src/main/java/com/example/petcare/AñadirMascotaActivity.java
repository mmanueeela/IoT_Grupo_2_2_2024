package com.example.petcare;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AñadirMascotaActivity extends AppCompatActivity {
    private EditText nombreField, cantidadField;
    private Spinner frecuenciaSpinner;
    private Button btnGuardar;
    private GridLayout horasLayout;  // Contenedor dinámico para los botones de hora

    private ArrayList<String> horasSeleccionadas = new ArrayList<>();  // Para almacenar las horas seleccionadas

    private static final int PICK_IMAGE_REQUEST = 1;  // Código de solicitud para elegir una imagen
    private ImageView imageView17;  // ImageView para mostrar la foto seleccionada
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadirmascota);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        imageView17 = findViewById(R.id.imageView17);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("mascotas");  // Referencia a la carpeta "mascotas" en Firebase Storage

        nombreField = findViewById(R.id.nombre);
        frecuenciaSpinner = findViewById(R.id.frecuencia);
        cantidadField = findViewById(R.id.cantidad);
        btnGuardar = findViewById(R.id.yaestaa);
        horasLayout = findViewById(R.id.gridLayoutHoras);  // Contenedor para los botones de hora

        imageView17.setOnClickListener(v -> openGallery());

          // Actualizar el número de botones según la frecuencia seleccionada
        frecuenciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtener la frecuencia seleccionada
                String frecuenciaSeleccionadaString = frecuenciaSpinner.getSelectedItem().toString();

                // Verificar si la frecuencia seleccionada es un número válido
                int frecuenciaSeleccionada = 1;
                try {
                    frecuenciaSeleccionada = Integer.parseInt(frecuenciaSeleccionadaString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                // Limpiar los botones previos si los hubiera
                horasLayout.removeAllViews();

                // Crear los botones según la frecuencia seleccionada (máximo 4)
                for (int i = 0; i < Math.min(frecuenciaSeleccionada, 4); i++) {
                    Button button = new Button(AñadirMascotaActivity.this);
                    button.setText("Seleccionar hora " + (i + 1));
                    horasLayout.addView(button);

                    // Configurar el click listener para abrir el TimePicker
                    final int index = i;
                    button.setOnClickListener(v -> {
                        int startingHour = 0;
                        int startingMinute = 0;

                        // Crear y mostrar el TimePickerDialog
                        TimePickerDialog timePicker = new TimePickerDialog(AñadirMascotaActivity.this,
                                (TimePicker view, int hourOfDay, int minute) -> {
                                    String selectedHour = String.format("%02d:%02d", hourOfDay, minute);
                                    horasSeleccionadas.add(selectedHour); // Añadir la hora seleccionada al array
                                    button.setText(selectedHour); // Actualizar el texto del botón
                                }, startingHour, startingMinute, true);
                        timePicker.show();
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hacer nada si no se selecciona nada
            }
        });

        // Recuperar la URI de la imagen de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PetProfile", MODE_PRIVATE);
        String profileImageUri = sharedPreferences.getString("petImage", null);
        // Verificar si existe una URI guardada y cargarla en el ImageView
        if (profileImageUri != null) {
            ImageView imageView17 = findViewById(R.id.imageView17);
            imageView17.setImageURI(Uri.parse(profileImageUri));
        }
        // Cargar imagen de perfil actual (si existe)
        loadPetImage();

        // Botón guardar
        btnGuardar.setOnClickListener(v -> {
            String nombre = nombreField.getText().toString().trim();
            String cantidad = cantidadField.getText().toString().trim();

            if (TextUtils.isEmpty(nombre) || horasSeleccionadas.isEmpty() || TextUtils.isEmpty(cantidad)) {
                Toast.makeText(AñadirMascotaActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int cantidadNum = Integer.parseInt(cantidad);

                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(AñadirMascotaActivity.this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userId = currentUser.getUid();

                String petId = db.collection("pets").document().getId();

                Map<String, Object> mascota = new HashMap<>();
                mascota.put("nombre", nombre);
                mascota.put("frecuencia", horasSeleccionadas.size());
                mascota.put("hora", horasSeleccionadas);  // Guardar las horas seleccionadas
                mascota.put("cantidad", cantidadNum);
                mascota.put("userId", userId);
                mascota.put("petId", petId);

                db.collection("pets")
                        .document(petId)
                        .set(mascota)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AñadirMascotaActivity.this, "Mascota registrada con éxito", Toast.LENGTH_SHORT).show();


                            finish();  // Esto cerrará la actividad actual (AñadirMascotaActivity)

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AñadirMascotaActivity.this, "Error al registrar la mascota: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });


            } catch (NumberFormatException e) {
                Toast.makeText(AñadirMascotaActivity.this, "Cantidad debe ser un número válido", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(AñadirMascotaActivity.this, MiMascotaActivity.class);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // Solo imágenes
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Método que maneja la respuesta de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();  // Obtener la URI de la imagen seleccionada
            uploadImageToFirebase(imageUri);  // Subir la imagen a Firebase Storage
        }
    }

    // Subir la imagen seleccionada a Firebase Storage
    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AñadirMascotaActivity.this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageReference fileReference = storageReference.child(currentUser.getUid() + "/" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = fileReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obtener la URL de la imagen subida
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Actualizar el ImageView con la nueva imagen
                Glide.with(this).load(uri).into(imageView17);;   // Actualiza el ImageView con la imagen seleccionada

                // Mostrar un mensaje de éxito
                Toast.makeText(AñadirMascotaActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Mostrar mensaje de error si ocurre un problema al subir la imagen
            Toast.makeText(AñadirMascotaActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // Método para actualizar el número de botones según la frecuencia seleccionada
    private void actualizarBotonesHoras(String frecuencia) {
        horasLayout.removeAllViews();  // Limpiar los botones anteriores

        // Obtener el número de veces que la mascota come al día
        int frecuenciaNum;
        try {
            frecuenciaNum = Integer.parseInt(frecuencia);
        } catch (NumberFormatException e) {
            frecuenciaNum = 1; // Por defecto si no es un número válido
        }

        // Limitar a un máximo de 4 botones
        int numBotones = Math.min(frecuenciaNum, 4);

        // Crear botones dinámicamente según la frecuencia
        for (int i = 0; i < numBotones; i++) {
            Button botonHora = new Button(this);
            botonHora.setText("Seleccionar hora " + (i + 1));

            // Mostrar el TimePickerDialog cuando el usuario haga clic en el botón
            botonHora.setOnClickListener(v -> {
                int startingHour = 0;
                int startingMinute = 0;

                TimePickerDialog timePicker = new TimePickerDialog(this,
                        (TimePicker view, int hourOfDay, int minute) -> {
                            String selectedHour = String.format("%02d:%02d", hourOfDay, minute);
                            horasSeleccionadas.add(selectedHour); // Añadir la hora seleccionada al array
                            botonHora.setText(selectedHour); // Actualizar el texto del botón
                        }, startingHour, startingMinute, true);

                timePicker.show();
            });

            horasLayout.addView(botonHora);  // Agregar el botón al layout
        }
    }
    // Método para cargar la imagen de perfil en el ImageView
    private void loadPetImage() {
        firebaseAuth = FirebaseAuth.getInstance(); // Asegúrate de inicializar FirebaseAuth
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String petPictureUrl = documentSnapshot.getString("petPicture");
                            if (petPictureUrl != null) {
                                Glide.with(this).load(petPictureUrl).into(imageView17);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar la imagen de perfil.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
        }
    }
}