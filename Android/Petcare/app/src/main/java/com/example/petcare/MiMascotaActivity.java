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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiMascotaActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView logo, notificas, menuButton;
    private Button menuOptionMiPerfil, menuOptionMisMascotas, menuOptionAcercaDe, menuOptionContacto, menuOptionFaq;
    private RecyclerView recyclerViewMascota, recicleComida;
    private MascotaAdapter mascotaAdapter;
    private List<Mascota> mascotas;
    private ComidaAdapter comidaAdapter;
    private List<Comida> comidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mimascota);

        // Configurar Drawer y botones de menú
        setupMenu();

        // Inicializar RecyclerView para Mascotas
        recyclerViewMascota = findViewById(R.id.recyclerViewMascota);
        mascotas = new ArrayList<>();
        mascotaAdapter = new MascotaAdapter(mascotas);
        recyclerViewMascota.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMascota.setAdapter(mascotaAdapter);

        // Inicializar RecyclerView para Comida (si es necesario)
        recicleComida = findViewById(R.id.recicleComida);
        comidas = new ArrayList<>();
        comidaAdapter = new ComidaAdapter(comidas);
        recicleComida.setLayoutManager(new LinearLayoutManager(this));
        recicleComida.setAdapter(comidaAdapter);

        // Cargar datos de Firebase
        loadMascotas();
        loadComida();
    }

    private void setupMenu() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.imageView13);
        menuOptionMiPerfil = findViewById(R.id.menu_option_miperfil);
        menuOptionMisMascotas = findViewById(R.id.menu_option_mismascotas);
        menuOptionAcercaDe = findViewById(R.id.menu_option_acercade);
        menuOptionContacto = findViewById(R.id.menu_option_contacto);
        menuOptionFaq = findViewById(R.id.menu_option_faq);
        logo = findViewById(R.id.logoMiMascota);
        notificas = findViewById(R.id.notiMiMascota);

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        menuOptionMiPerfil.setOnClickListener(v -> openActivity(MiperfilActivity.class));
        menuOptionMisMascotas.setOnClickListener(v -> openActivity(MiMascotaActivity.class));
        menuOptionAcercaDe.setOnClickListener(v -> openActivity(AcercaDeActivity.class));
        menuOptionContacto.setOnClickListener(v -> openActivity(ContactoActivity.class));
        menuOptionFaq.setOnClickListener(v -> openActivity(FAQActivity.class));
        logo.setOnClickListener(v -> openActivity(MisMascotasActivity.class));
        notificas.setOnClickListener(v -> openActivity(AjustesNotificasActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(MiMascotaActivity.this, activityClass));
    }

    private void loadMascotas() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Log.e("FirebaseAuth", "Usuario no autenticado");
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pets")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mascotas.clear();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Mascota mascota = document.toObject(Mascota.class);
                            if (mascota != null) {
                                mascotas.add(mascota);
                            }
                        }
                        mascotaAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "Error al obtener las mascotas", task.getException());
                    }
                });
    }

    private void loadComida() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usersregistros")
                .document("Usuario1")
                .collection("hora")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);

                        // Imprimir todo el contenido para ver si 'registro' está presente
                        Log.d("Firestore", "Documento obtenido: " + lastDocument.getData());

                        if (lastDocument.contains("registro")) {
                            Map<String, Object> registro = (Map<String, Object>) lastDocument.get("registro");
                            if (registro != null && registro.containsKey("percent")) {
                                double percent = (double) registro.get("percent");
                                Log.d("Firestore", "Último porcentaje dentro de 'registro': " + percent);

                                // Crear una lista con el valor de 'percent'
                                List<Comida> comidaList = new ArrayList<>();
                                comidaList.add(new Comida(percent));

                                // Configurar el adaptador para el RecyclerView
                                ComidaAdapter adapter = new ComidaAdapter(comidaList);
                                RecyclerView recyclerView = findViewById(R.id.recicleComida);
                                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                recyclerView.setAdapter(adapter);
                            } else {
                                Log.e("Firestore", "No se encontró 'percent' dentro de 'registro'");
                            }
                        } else {
                            Log.e("Firestore", "El documento no contiene 'registro'");
                        }
                    } else {
                        Log.e("Firestore", "No se encontraron documentos en la colección 'hora'");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos", e));
    }


}
