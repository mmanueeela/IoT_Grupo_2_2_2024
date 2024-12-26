package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AcercaDeActivity extends AppCompatActivity {

    private TextView textViewFAQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acerca_de); // Este es el layout donde configuramos el TextView

        // Referencia al TextView "Página de FAQ"
        textViewFAQ = findViewById(R.id.textView23);

        // Acción al presionar el TextView
        textViewFAQ.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad FAQActivity
            Intent intent = new Intent(AcercaDeActivity.this, FAQActivity.class);
            startActivity(intent);
        });
    }
}
