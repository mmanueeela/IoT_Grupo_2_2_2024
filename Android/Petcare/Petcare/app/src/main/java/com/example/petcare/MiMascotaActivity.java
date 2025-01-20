package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MiMascotaActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView logo;
    private ImageView notificas;
    private Button menuOptionMiPerfil;
    private Button menuOptionMisMascotas;
    private Button menuOptionAcercaDe;
    private Button menuOptionContacto;
    private Button menuOptionFaq;
    private ImageView menuButton;

    private RecyclerView recyclerViewMascota;
    private MascotaAdapter mascotaAdapter;
    private List<Mascota> mascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mimascota); // Este es el layout donde configuramos el TextView

        logo = findViewById(R.id.logoMiMascota);
        notificas = findViewById(R.id.notiMiMascota);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.imageView13);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);

        // Acciones al presionar los botones del menú lateral
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        menuOptionMiPerfil.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mi Perfil
            startActivity(new Intent(MiMascotaActivity.this, MiperfilActivity.class));
        });

        menuOptionMisMascotas.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Mis Mascotas
            startActivity(new Intent(MiMascotaActivity.this, MiMascotaActivity.class));
        });

        menuOptionAcercaDe.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Acerca de
            startActivity(new Intent(MiMascotaActivity.this, AcercaDeActivity.class));
        });

        menuOptionContacto.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad Contacto
            startActivity(new Intent(MiMascotaActivity.this, ContactoActivity.class));
        });

        menuOptionFaq.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Redirigir a la actividad FAQ
            startActivity(new Intent(MiMascotaActivity.this, FAQActivity.class));
        });

        recyclerViewMascota = findViewById(R.id.recyclerViewMascota);
        mascotas = new ArrayList<>();

        recyclerViewMascota.setLayoutManager(new LinearLayoutManager(this));

        // Configurar el adapter
        mascotaAdapter = new MascotaAdapter(mascotas);
        recyclerViewMascota.setAdapter(mascotaAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Obtener los datos de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pets")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            mascotas.clear();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                // Ahora Firestore devolverá la lista de horas
                                Mascota mascota = document.toObject(Mascota.class);
                                if (mascota != null) {
                                    mascotas.add(mascota);
                                }
                            }
                            mascotaAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("FirestoreError", "Error al obtener los usuarios", task.getException());
                    }
                });

        // Acción al presionar el Logo
        logo.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiMascotaActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar las Notificas
        notificas.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiMascotaActivity.this, AjustesNotificasActivity.class);
            startActivity(intent);
        });


    }
}
