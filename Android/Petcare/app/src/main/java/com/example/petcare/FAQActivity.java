package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq); // Este es el layout de FAQActivity

        // Obtener referencia al TextView con el ID textView23
        TextView contactoTextView = findViewById(R.id.textView23);

        // Establecer un OnClickListener para redirigir a ContactoActivity
        contactoTextView.setOnClickListener(v -> {
            // Crear un Intent para ir a ContactoActivity
            Intent intent = new Intent(FAQActivity.this, ContactoActivity.class);
            startActivity(intent);  // Iniciar la actividad ContactoActivity
        });
    }
}
