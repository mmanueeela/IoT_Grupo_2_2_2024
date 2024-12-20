package com.example.raspby;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.auth.oauth2.GoogleCredentials;

public class FirebaseHandler {
    /*private FirebaseApp firebaseApp;
    private DatabaseReference database;

    public FirebaseHandler(String firebaseConfigPath) throws IOException {
        // Cargar las credenciales de Firebase desde el archivo
        FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);

        // Configurar la aplicación de Firebase
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        // Inicializar la aplicación de Firebase
        firebaseApp = FirebaseApp.initializeApp(options);

        // Obtener la referencia a la base de datos
        database = FirebaseDatabase.getInstance(firebaseApp).getReference();
    }

    public void sendDataAsync(String path, Object data) {
        // Enviar los datos a Firebase en la ruta especificada
        database.child(path).setValueAsync(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Datos enviados a Firebase en " + path + ": " + data);
                    } else {
                        System.out.println("Error al enviar datos a Firebase: " + task.getException());
                    }
                });
    }*/
}
