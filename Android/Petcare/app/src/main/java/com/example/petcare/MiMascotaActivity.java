package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MiMascotaActivity extends AppCompatActivity {

    private ImageView logo;
    private ImageView notificas;
    private Button editarMascota;

    private RecyclerView recyclerViewMascota;
    private MascotaAdapter mascotaAdapter;
    private List<Mascota> mascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mimascota); // Este es el layout donde configuramos el TextView

        logo = findViewById(R.id.logoMiMascota);
        notificas = findViewById(R.id.notiMiMascota);
        editarMascota = findViewById(R.id.modificaMiMascota);

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
                                Mascota mascota = document.toObject(Mascota.class);
                                mascotas.add(mascota);
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
            Intent intent = new Intent(MiMascotaActivity.this, NotificasActivity.class);
            startActivity(intent);
        });

        // Acción al presionar editar
        editarMascota.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad
            Intent intent = new Intent(MiMascotaActivity.this, EditarMiMascotaActivity.class);
            startActivity(intent);
        });

    }
}
