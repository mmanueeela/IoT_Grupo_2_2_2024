package com.example.petcare;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class EscanearActivity extends AppCompatActivity {
    private Button conCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vincular_dispositivo);

        conCodigo = findViewById(R.id.sinncodigobotonIRAMISMASCOTAS);

        conCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de Perfil
                Intent intent = new Intent(EscanearActivity.this, CodigoNumerosActivity.class);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }

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
                    // Redirigir a la actividad CodigoCorrecto
                    Intent intent = new Intent(this, CodigoCorrecto.class);
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