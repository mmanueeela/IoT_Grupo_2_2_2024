package com.example.petcare;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AñadirMascotaActivity extends AppCompatActivity {
    private EditText nombreField, cantidadField;
    private Spinner frecuenciaSpinner;
    private Button btnGuardar;
    private GridLayout horasLayout;
    private ArrayList<String> horasSeleccionadas = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView17;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // MQTT configuration
    private static final String MQTT_BROKER = "tcp://broker.emqx.io:1883";
    private static final String MQTT_CLIENT_ID = "PetCareAppClient";
    private MqttAsyncClient mqttClient;

    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadirmascota);

        checkStoragePermission();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        imageView17 = findViewById(R.id.imageView17);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("mascotas");

        nombreField = findViewById(R.id.nombre);
        frecuenciaSpinner = findViewById(R.id.frecuencia);
        cantidadField = findViewById(R.id.cantidad);
        btnGuardar = findViewById(R.id.yaestaa);
        horasLayout = findViewById(R.id.gridLayoutHoras);

        imageView17.setOnClickListener(v -> openGallery());

        // Initialize MQTT client
        initializeMqtt();

        frecuenciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String frecuenciaSeleccionadaString = frecuenciaSpinner.getSelectedItem().toString();

                int frecuenciaSeleccionada = 1;
                try {
                    frecuenciaSeleccionada = Integer.parseInt(frecuenciaSeleccionadaString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                horasLayout.removeAllViews();

                for (int i = 0; i < Math.min(frecuenciaSeleccionada, 4); i++) {
                    Button button = new Button(AñadirMascotaActivity.this);
                    button.setText("Seleccionar hora " + (i + 1));
                    horasLayout.addView(button);

                    button.setOnClickListener(v -> {
                        int startingHour = 0;
                        int startingMinute = 0;

                        TimePickerDialog timePicker = new TimePickerDialog(AñadirMascotaActivity.this,
                                (TimePicker view, int hourOfDay, int minute) -> {
                                    String selectedHour = String.format("%02d%02d", hourOfDay, minute); // Formato HHmm
                                    horasSeleccionadas.add(selectedHour);
                                    button.setText(String.format("%02d:%02d", hourOfDay, minute)); // Formato HH:mm
                                    horasSeleccionadas.sort(String::compareTo); // Ordenar de la más temprana a la más tarde
                                }, startingHour, startingMinute, true);
                        timePicker.show();
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

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

                // Crear un nuevo formato para Firebase
                ArrayList<String> horasFirebase = new ArrayList<>();
                for (String hora : horasSeleccionadas) {
                    String formattedHourFirebase = hora.substring(0, 2) + ":" + hora.substring(2, 4); // Convertir HHmm a HH:mm
                    horasFirebase.add(formattedHourFirebase);
                }

                Map<String, Object> mascota = new HashMap<>();
                mascota.put("nombre", nombre);
                mascota.put("frecuencia", horasFirebase.size());
                mascota.put("hora", horasFirebase);
                mascota.put("cantidad", cantidadNum);
                mascota.put("userId", userId);
                mascota.put("petId", petId);

                db.collection("pets")
                        .document(petId)
                        .set(mascota)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AñadirMascotaActivity.this, "Mascota registrada con éxito", Toast.LENGTH_SHORT).show();

                            // Publicar horas en formato JSON al topic "pets/variables/horas"
                            try {
                                JSONObject horasPayload = new JSONObject();
                                horasPayload.put("msg", TextUtils.join(",", horasSeleccionadas)); // Formato HHmm separado por comas
                                publishMqttMessage("pets/variables/horas", horasPayload.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error al construir el mensaje JSON para horas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            // Publicar cantidad en formato JSON al topic "pets/variables/racion"
                            try {
                                JSONObject racionPayload = new JSONObject();
                                racionPayload.put("msg", String.valueOf(cantidadNum));
                                publishMqttMessage("pets/variables/racion", racionPayload.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error al construir el mensaje JSON para ración: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            // Navegar a la siguiente actividad
                            Intent intent = new Intent(AñadirMascotaActivity.this, MiMascotaActivity.class);
                            intent.putExtra("petId", petId); // Puedes pasar el ID de la mascota si es necesario
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AñadirMascotaActivity.this, "Error al registrar la mascota: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            } catch (NumberFormatException e) {
                Toast.makeText(AñadirMascotaActivity.this, "Cantidad debe ser un número válido", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Se requiere permiso para seleccionar una imagen", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeMqtt() {
        try {
            mqttClient = new MqttAsyncClient(MQTT_BROKER, MQTT_CLIENT_ID, new MemoryPersistence());

            mqttClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    runOnUiThread(() ->
                            Toast.makeText(AñadirMascotaActivity.this, "Conectado al broker MQTT", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    runOnUiThread(() ->
                            Toast.makeText(AñadirMascotaActivity.this, "Error al conectar al broker MQTT: " + exception.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al inicializar MQTT: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void publishMqttMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al enviar mensaje MQTT: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AñadirMascotaActivity.this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageReference fileReference = storageReference.child(currentUser.getUid() + "/" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = fileReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this).load(uri).into(imageView17);
                Toast.makeText(AñadirMascotaActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AñadirMascotaActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
