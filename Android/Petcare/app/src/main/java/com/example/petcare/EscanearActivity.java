package com.example.petcare;

import static android.content.ContentValues.TAG;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import okhttp3.ResponseBody;

public class EscanearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vincular_dispositivo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }


        // Encontrar el TextView por su ID
        TextView iniciarSesionTextView = findViewById(R.id.yavinculado);

        // Configurar el listener
        iniciarSesionTextView.setOnClickListener(v -> {
            // Crear un Intent para ir a IniciarSesionActivity
            Intent intent = new Intent(EscanearActivity.this, IniciarSesionActivity.class);
            startActivity(intent); // Iniciar la actividad
        });

        // Botón para iniciar el escaneo
        ImageButton scanButton = findViewById(R.id.qrboton);
        scanButton.setOnClickListener(v -> startQRCodeScanner());

    }
    private void startQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Escanea un código QR");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true); // Opcional: habilita sonido al escanear
        integrator.setCaptureActivity(CaptureActivity.class); // Usa la actividad integrada
        integrator.initiateScan(); // Inicia el escaneo
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrContent = result.getContents();

                // Verificar el contenido del QR
                if (qrContent.equalsIgnoreCase("true")) {
                    // Redirigir a la actividad MisMascotasActivity
                    Intent intent = new Intent(this, MisMascotasActivity.class);
                    startActivity(intent);
                } else {
                    // Mostrar un mensaje de error
                    Toast.makeText(this, "QR equivocado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}