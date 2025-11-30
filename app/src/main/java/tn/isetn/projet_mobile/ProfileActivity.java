package tn.isetn.projet_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    EditText txtNom, txtEmail;
    Button btnModifier, btnCreerCommande, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtNom = findViewById(R.id.txtNom);
        txtEmail = findViewById(R.id.txtEmail);

        btnModifier = findViewById(R.id.btnModifier);
        btnCreerCommande = findViewById(R.id.btnCreerCommande);
        btnLogout = findViewById(R.id.btnLogout);

        String emailUser = getIntent().getStringExtra("email");

        if (emailUser == null || emailUser.isEmpty()) {
            Toast.makeText(this, "Erreur : email manquant", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Récupération des infos utilisateur depuis Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(emailUser)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        txtNom.setText(doc.getString("nom"));
                        txtEmail.setText(doc.getString("email"));
                    } else {
                        Toast.makeText(this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );

        // Modifier le nom
        btnModifier.setOnClickListener(v -> {
            String nouveauNom = txtNom.getText().toString().trim();
            if (nouveauNom.isEmpty()) {
                Toast.makeText(this, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(emailUser)
                    .update("nom", nouveauNom)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Profil mis à jour !", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // Créer une commande
        btnCreerCommande.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, CreateOrderActivity.class);
            i.putExtra("email", emailUser);
            startActivity(i);
        });

        // Déconnexion
        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, inscri_con.class);
            startActivity(i);
            finish();
        });
    }
}
