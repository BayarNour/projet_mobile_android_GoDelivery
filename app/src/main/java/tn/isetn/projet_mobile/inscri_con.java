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

        // Initialiser Firestore
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

        // Affichage par défaut
        layoutInscription.setVisibility(View.VISIBLE);
        layoutConnexion.setVisibility(View.GONE);

        updateSelectionUI();

        // Switch vers Inscription
        btnGoInscription.setOnClickListener(v -> {
            layoutInscription.setVisibility(View.VISIBLE);
            layoutConnexion.setVisibility(View.GONE);

            btnGoInscription.setBackgroundColor(Color.parseColor("#1B1195"));
            btnGoInscription.setTextColor(Color.WHITE);

            btnGoConnexion.setBackgroundColor(Color.LTGRAY);
            btnGoConnexion.setTextColor(Color.BLACK);
        });

        // Switch vers Connexion
        btnGoConnexion.setOnClickListener(v -> {
            layoutInscription.setVisibility(View.GONE);
            layoutConnexion.setVisibility(View.VISIBLE);

            btnGoConnexion.setBackgroundColor(Color.parseColor("#1B1195"));
            btnGoConnexion.setTextColor(Color.WHITE);

            btnGoInscription.setBackgroundColor(Color.LTGRAY);
            btnGoInscription.setTextColor(Color.BLACK);
        });

        // Sélection du rôle
        btnClient.setOnClickListener(v -> {
            isClient = true;
            updateSelectionUI();
        });

        btnLivreur.setOnClickListener(v -> {
            isClient = false;
            updateSelectionUI();
        });

        // Inscription
        btnInscrire.setOnClickListener(v -> validateForm());

        // Connexion
        btnConnecter.setOnClickListener(v -> loginUser());
    }

    // Met à jour l'UI du rôle Client/Livreur
    private void updateSelectionUI() {
        Drawable selected = ContextCompat.getDrawable(this, R.drawable.selected_tab);
        Drawable unselected = ContextCompat.getDrawable(this, R.drawable.unselected_tab);

        if (isClient) {
            btnClient.setBackground(selected);
            btnClient.setTextColor(Color.WHITE);

            btnLivreur.setBackground(unselected);
            btnLivreur.setTextColor(Color.parseColor("#4B4B4B"));
        } else {
            btnLivreur.setBackground(selected);
            btnLivreur.setTextColor(Color.WHITE);

            btnClient.setBackground(unselected);
            btnClient.setTextColor(Color.parseColor("#4B4B4B"));
        }
    }

    // Validation et inscription
    private void validateForm() {
        String sNom = nom.getText().toString().trim();
        String sEmail = email.getText().toString().trim();
        String sPwd = pwd.getText().toString().trim();
        String sConfirm = confirmPwd.getText().toString().trim();

        // Validations
        if (sNom.isEmpty()) { nom.setError("Veuillez entrer votre nom"); return; }
        if (sNom.length() < 5) { nom.setError("Le nom doit contenir minimum 5 caractères"); return; }

        if (sEmail.isEmpty()) { email.setError("Veuillez entrer un email"); return; }
        if (!sEmail.contains("@")) { email.setError("Email invalide : '@' manquant"); return; }

        String beforeAt = sEmail.substring(0, sEmail.indexOf("@"));
        if (beforeAt.isEmpty()) { email.setError("Avant '@' vous devez écrire quelque chose"); return; }
        if (beforeAt.length() < 5) { email.setError("Avant '@' il faut minimum 5 caractères"); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Format email invalide");
            return;
        }

        if (sPwd.length() < 8) { pwd.setError("Minimum 8 caractères"); return; }
        if (!sPwd.matches(".*[A-Z].*")) { pwd.setError("Une majuscule requise"); return; }
        if (!sPwd.matches(".*[a-z].*")) { pwd.setError("Une minuscule requise"); return; }
        if (!sPwd.matches(".*[0-9].*")) { pwd.setError("Un chiffre requis"); return; }

        if (!sPwd.equals(sConfirm)) {
            confirmPwd.setError("Les mots de passe ne correspondent pas");
            return;
        }

        // Créer l'objet User
        String role = isClient ? "Client" : "Livreur";
        User user = new User(sNom, sEmail, role, sPwd);

        // Enregistrer dans Firestore
        firestore.collection("users")
                .document(sEmail) // ID unique = email
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(inscri_con.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(inscri_con.this, delivery_map.class);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(inscri_con.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Connexion utilisateur
    private void loginUser() {
        String sEmail = inputEmailLogin.getText().toString().trim();
        String sPwd = inputPasswordLogin.getText().toString().trim();

        if (sEmail.isEmpty()) {
            inputEmailLogin.setError("Veuillez entrer un email");
            return;
        }
        if (sPwd.isEmpty()) {
            inputPasswordLogin.setError("Veuillez entrer un mot de passe");
            return;
        }

        firestore.collection("users").document(sEmail).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedPassword = documentSnapshot.getString("password");
                        if (storedPassword != null && storedPassword.equals(sPwd)) {
                            Toast.makeText(inscri_con.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(inscri_con.this, delivery_map.class);
                            startActivity(i);
                            finish();
                        } else {
                            inputPasswordLogin.setError("Mot de passe incorrect");
                        }
                    } else {
                        inputEmailLogin.setError("Utilisateur non trouvé");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(inscri_con.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
