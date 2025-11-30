package tn.isetn.projet_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateOrderActivity extends AppCompatActivity {

    EditText edtNomClient, edtTelClient, edtAdresseRecup, edtAdresseLivraison,
            edtRestaurant, edtDescription, edtArticles, edtPrix;
    Button btnCreateOrder;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        // Bouton retour vers delivery_map
        ImageView btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(CreateOrderActivity.this, delivery_map.class);
            String emailUser = getIntent().getStringExtra("email");
            intent.putExtra("email", emailUser);
            startActivity(intent);
            finish();
        });

        // Récupération des EditText et Button
        edtNomClient = findViewById(R.id.edtNomClient);
        edtTelClient = findViewById(R.id.edtTelClient);
        edtAdresseRecup = findViewById(R.id.edtAdresseRecup);
        edtAdresseLivraison = findViewById(R.id.edtAdresseLivraison);
        edtRestaurant = findViewById(R.id.edtRestaurant);
        edtDescription = findViewById(R.id.edtDescription);
        edtArticles = findViewById(R.id.edtArticles);
        edtPrix = findViewById(R.id.edtPrix);

        btnCreateOrder = findViewById(R.id.btnCreateOrder);

        String emailUser = getIntent().getStringExtra("email");
        btnCreateOrder.setOnClickListener(v -> saveOrder(emailUser));
    }

    private void saveOrder(String emailUser) {
        String nom = edtNomClient.getText().toString().trim();
        String tel = edtTelClient.getText().toString().trim();
        String recup = edtAdresseRecup.getText().toString().trim();
        String livraison = edtAdresseLivraison.getText().toString().trim();
        String resto = edtRestaurant.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();
        String articles = edtArticles.getText().toString().trim();
        String prix = edtPrix.getText().toString().trim();

        // Validation des champs
        if (nom.isEmpty()) { edtNomClient.setError("Nom requis"); edtNomClient.requestFocus(); return; }
        if (tel.isEmpty()) { edtTelClient.setError("Téléphone requis"); edtTelClient.requestFocus(); return; }
        if (!tel.matches("\\d{8,15}")) { edtTelClient.setError("Numéro invalide"); edtTelClient.requestFocus(); return; }
        if (recup.isEmpty()) { edtAdresseRecup.setError("Adresse de récupération requise"); edtAdresseRecup.requestFocus(); return; }
        if (livraison.isEmpty()) { edtAdresseLivraison.setError("Adresse de livraison requise"); edtAdresseLivraison.requestFocus(); return; }
        if (resto.isEmpty()) { edtRestaurant.setError("Nom du restaurant requis"); edtRestaurant.requestFocus(); return; }
        if (articles.isEmpty()) { edtArticles.setError("Articles requis"); edtArticles.requestFocus(); return; }
        if (prix.isEmpty()) { edtPrix.setError("Prix requis"); edtPrix.requestFocus(); return; }

        try {
            Double.parseDouble(prix);
        } catch (NumberFormatException e) {
            edtPrix.setError("Entrez un prix valide");
            edtPrix.requestFocus();
            return;
        }

        Map<String, Object> order = new HashMap<>();
        order.put("emailUser", emailUser);
        order.put("nomClient", nom);
        order.put("telephoneClient", tel);
        order.put("adresseRecuperation", recup);
        order.put("adresseLivraison", livraison);
        order.put("restaurant", resto);
        order.put("description", desc);
        order.put("articles", articles);
        order.put("prixLivraison", prix);
        order.put("etat", "en attente");

        db.collection("commandes")
                .add(order)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Commande créée avec succès !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateOrderActivity.this, delivery_map.class);
                    intent.putExtra("email", emailUser);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
