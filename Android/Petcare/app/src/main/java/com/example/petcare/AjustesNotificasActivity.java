package com.example.petcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class AjustesNotificasActivity extends AppCompatActivity {

    private DrawerLayout drawerLayoutAjustesNotificaciones;
    private ImageView menuButton;
    private Button guardarButton;
    private CheckBox checkBox1, checkBox3, checkBox4, checkBox5, checkBox7;

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajuste_de_notis); // Este es el layout con el DrawerLayout

        drawerLayoutAjustesNotificaciones = findViewById(R.id.drawerLayoutAjustesNotificaciones);
        menuButton = findViewById(R.id.imageView16);
        guardarButton = findViewById(R.id.Modificar);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        checkBox7 = findViewById(R.id.checkBox7);

        // Configurar el evento de clic para abrir el menú lateral
        menuButton.setOnClickListener(v -> drawerLayoutAjustesNotificaciones.openDrawer(GravityCompat.START));

        // Configurar los listeners para las opciones del menú
        findViewById(R.id.menu_option_miperfil).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, MiperfilActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        // Listener para el checkBox1
        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si se marca checkBox1, solicitar permiso si no está concedido
                requestNotificationPermission();
            } else {
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);
                checkBox7.setChecked(false);
            }
        });

        // Listener para los otros checkboxes
        configureDependentCheckBox(checkBox3);
        configureDependentCheckBox(checkBox4);
        configureDependentCheckBox(checkBox5);
        configureDependentCheckBox(checkBox7);

        findViewById(R.id.menu_option_mismascotas).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, MisMascotasActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_acercade).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, AcercaDeActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_contacto).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, ContactoActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_option_faq).setOnClickListener(v -> {
            Intent intent = new Intent(AjustesNotificasActivity.this, FAQActivity.class);
            startActivity(intent);
            drawerLayoutAjustesNotificaciones.closeDrawer(GravityCompat.START);
        });

        // Acción del botón "Guardar"
        guardarButton.setOnClickListener(v -> {
            // Guardar configuraciones y hacer algo
        });
    }
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            // Si ya está concedido, mostrar un mensaje y seguir
            Toast.makeText(this, "Permiso para notificaciones concedido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso para notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Si se deniega el permiso, desmarcar checkBox1
                Toast.makeText(this, "Permiso para notificaciones denegado", Toast.LENGTH_SHORT).show();
                checkBox1.setChecked(false); // Desmarcar el checkbox si no se otorga el permiso
            }
        }
    }

    private void configureDependentCheckBox(CheckBox dependentCheckBox) {
        dependentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !checkBox1.isChecked()) {
                Toast.makeText(this, "Debes activar el primer checkbox para habilitar esta opción.", Toast.LENGTH_SHORT).show();
                dependentCheckBox.setChecked(false); // Desmarcar el checkbox automáticamente
            }
        });
    }
}
