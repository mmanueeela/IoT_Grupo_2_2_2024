package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class IniciarSesionActivity extends AppCompatActivity {
    private EditText correoField, contraseñaField;
    private Button btnIniciarSesion;

    private FirebaseAuth auth; // Instancia de Firebase Authentication

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_sesion);

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Agregar el ID de cliente web de Firebase
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

// Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

// Configurar el botón de Google Sign-In
        ImageButton googleSignInButton = findViewById(R.id.imageButton3); // El botón de tu diseño
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());


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

                                            // Redirigir a "EscanearActivity"
                                            Intent intent = new Intent(IniciarSesionActivity.this, EscanearActivity.class);
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
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (Exception e) {
                Log.w("GoogleSignIn", "Google sign-in failed", e);
                Toast.makeText(this, "Error en Google Sign-In", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        Toast.makeText(this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();

                        // Redirigir al usuario a EscanearActivity
                        Intent intent = new Intent(IniciarSesionActivity.this, EscanearActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error al autenticar
                        Toast.makeText(this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
/*
              ..----.._    _
            .' .--.    "-.(O)_
'-.__.-'"'=:|  ,  _)_ \__ . c\'-..  <--- Pelusín
             ''------'---''---'-"⠀⠀⠀⠀⠀⠀
 */