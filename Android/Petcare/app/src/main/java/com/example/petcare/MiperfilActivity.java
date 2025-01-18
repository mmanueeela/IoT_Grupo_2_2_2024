package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MiperfilActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuButton;
    private ImageView logo;
    private ImageView notificas;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private Button btnModificar;

    private RecyclerView recyclerView;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        recyclerView = findViewById(R.id.recyclerView);
        usuarios = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar el adapter
        usuarioAdapter = new UsuarioAdapter(usuarios);
        recyclerView.setAdapter(usuarioAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Obtener los datos de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            usuarios.clear();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Usuario usuario = document.toObject(Usuario.class);
                                usuarios.add(usuario);
                            }
                            usuarioAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("FirestoreError", "Error al obtener los usuarios", task.getException());
                    }
                });



        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.imageView16);
        logo = findViewById(R.id.logoEditarMiPerfil);
        notificas = findViewById(R.id.NotificacionesEditarMiPerfil);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);
        btnModificar = findViewById(R.id.Modificar);

        // Acciones al presionar los botones del menú lateral
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menuOptionMiPerfil.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mi Perfil
            startActivity(new Intent(MiperfilActivity.this, MiperfilActivity.class));
        });

        menuOptionMisMascotas.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mis Mascotas
            startActivity(new Intent(MiperfilActivity.this, MisMascotasActivity.class));
        });

        menuOptionAcercaDe.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Acerca de
            startActivity(new Intent(MiperfilActivity.this, AcercaDeActivity.class));
        });

        menuOptionContacto.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Contacto
            startActivity(new Intent(MiperfilActivity.this, ContactoActivity.class));
        });

        menuOptionFaq.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad FAQ
            startActivity(new Intent(MiperfilActivity.this, FAQActivity.class));
        });

        // Acción al presionar el logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad Mis Mascotas
            startActivity(new Intent(MiperfilActivity.this, MisMascotasActivity.class));
        });

        // Acción al presionar las notificaciones
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de Notificaciones
            startActivity(new Intent(MiperfilActivity.this, NotificasActivity.class));
        });

        // Acción al presionar el botón Modificar
        btnModificar.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de Modificar Perfil
            startActivity(new Intent(MiperfilActivity.this, ModificarPerfil.class));
        });
    }
}
