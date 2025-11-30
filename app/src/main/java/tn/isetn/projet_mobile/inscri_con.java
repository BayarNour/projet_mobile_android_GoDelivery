package tn.isetn.projet_mobile;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class inscri_con extends AppCompatActivity {

    FirebaseFirestore firestore;

    // Champs inscription
    EditText nom, email, pwd, confirmPwd;

    // Champs connexion
    EditText inputEmailLogin, inputPasswordLogin;

    // Boutons
    Button btnInscrire, btnClient, btnLivreur;
    Button btnGoInscription, btnGoConnexion, btnConnecter;

    // Layouts
    LinearLayout layoutInscription, layoutConnexion;

    boolean isClient = true; // par défaut Client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscri_con);

        firestore = FirebaseFirestore.getInstance();

        // Champs Inscription
        nom = findViewById(R.id.inputNom);
        email = findViewById(R.id.inputEmail);
        pwd = findViewById(R.id.inputPassword);
        confirmPwd = findViewById(R.id.inputConfirmPassword);

        // Champs Connexion
        inputEmailLogin = findViewById(R.id.inputEmailLogin);
        inputPasswordLogin = findViewById(R.id.inputPasswordLogin);

        // Boutons
        btnInscrire = findViewById(R.id.btnInscrire);
        btnClient = findViewById(R.id.btnClient);
        btnLivreur = findViewById(R.id.btnLivreur);
        btnGoInscription = findViewById(R.id.btnGoInscription);
        btnGoConnexion = findViewById(R.id.btnGoConnexion);
        btnConnecter = findViewById(R.id.btnConnecter);

        // Layouts
        layoutInscription = findViewById(R.id.layoutInscription);
        layoutConnexion = findViewById(R.id.layoutConnexion);

        layoutInscription.setVisibility(View.VISIBLE);
        layoutConnexion.setVisibility(View.GONE);

        updateSelectionUI();

        // Switch vers Inscription
        btnGoInscription.setOnClickListener(v -> {
            layoutInscription.setVisibility(View.VISIBLE);
            layoutConnexion.setVisibility(View.GONE);
        });

        // Switch vers Connexion
        btnGoConnexion.setOnClickListener(v -> {
            layoutInscription.setVisibility(View.GONE);
            layoutConnexion.setVisibility(View.VISIBLE);
        });

        // Sélection rôle
        btnClient.setOnClickListener(v -> { isClient = true; updateSelectionUI(); });
        btnLivreur.setOnClickListener(v -> { isClient = false; updateSelectionUI(); });

        btnInscrire.setOnClickListener(v -> validateForm());
        btnConnecter.setOnClickListener(v -> loginUser());
    }

    // UI rôle
    private void updateSelectionUI() {
        Drawable selected = ContextCompat.getDrawable(this, R.drawable.selected_tab);
        Drawable unselected = ContextCompat.getDrawable(this, R.drawable.unselected_tab);

        if (isClient) {
            btnClient.setBackground(selected);
            btnClient.setTextColor(Color.WHITE);
            btnLivreur.setBackground(unselected);
            btnLivreur.setTextColor(Color.BLACK);
        } else {
            btnLivreur.setBackground(selected);
            btnLivreur.setTextColor(Color.WHITE);
            btnClient.setBackground(unselected);
            btnClient.setTextColor(Color.BLACK);
        }
    }

    // Validation inscription
    private void validateForm() {
        String sNom = nom.getText().toString().trim();
        String sEmail = email.getText().toString().trim();
        String sPwd = pwd.getText().toString().trim();
        String sConfirm = confirmPwd.getText().toString().trim();

        if (sNom.isEmpty()) { nom.setError("Veuillez entrer votre nom"); return; }
        if (sEmail.isEmpty()) { email.setError("Veuillez entrer un email"); return; }
        if (!sEmail.contains("@") || sEmail.indexOf("@") < 5) { email.setError("Email invalide"); return; }
        if (sPwd.length() < 8) { pwd.setError("Minimum 8 caractères"); return; }
        if (!sPwd.equals(sConfirm)) { confirmPwd.setError("Les mots de passe ne correspondent pas"); return; }

        String role = isClient ? "Client" : "Livreur";

        User user = new User(sNom, sEmail, role, sPwd);

        firestore.collection("users")
                .document(sEmail)
                .set(user)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(inscri_con.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                    // 👉 ENVOYER EMAIL ET ROLE À delivery_map
                    Intent i = new Intent(inscri_con.this, delivery_map.class);
                    i.putExtra("email", sEmail);
                    i.putExtra("role", role);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(inscri_con.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Connexion
    private void loginUser() {
        String sEmail = inputEmailLogin.getText().toString().trim();
        String sPwd = inputPasswordLogin.getText().toString().trim();

        if (sEmail.isEmpty()) { inputEmailLogin.setError("Entrez un email"); return; }
        if (sPwd.isEmpty()) { inputPasswordLogin.setError("Entrez un mot de passe"); return; }

        firestore.collection("users").document(sEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        inputEmailLogin.setError("Utilisateur non trouvé");
                        return;
                    }

                    String storedPassword = documentSnapshot.getString("password");
                    String role = documentSnapshot.getString("role");

                    if (!sPwd.equals(storedPassword)) {
                        inputPasswordLogin.setError("Mot de passe incorrect");
                        return;
                    }

                    Toast.makeText(inscri_con.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                    // 👉 ENVOYER EMAIL + ROLE À delivery_map
                    Intent i = new Intent(inscri_con.this, delivery_map.class);
                    i.putExtra("email", sEmail);
                    i.putExtra("role", role);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(inscri_con.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
