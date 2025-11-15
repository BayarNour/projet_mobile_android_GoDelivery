package tn.isetn.projet_mobile;

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

public class inscri_con extends AppCompatActivity {

    EditText nom, email, pwd, confirmPwd;
    Button btnInscrire, btnClient, btnLivreur;

    // Switch interface
    LinearLayout layoutInscription, layoutConnexion;
    Button btnGoInscription, btnGoConnexion;

    boolean isClient = true; // par défaut Client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscri_con);

        // Champs
        nom = findViewById(R.id.inputNom);
        email = findViewById(R.id.inputEmail);
        pwd = findViewById(R.id.inputPassword);
        confirmPwd = findViewById(R.id.inputConfirmPassword);

        // Boutons rôle
        btnClient = findViewById(R.id.btnClient);
        btnLivreur = findViewById(R.id.btnLivreur);
        btnInscrire = findViewById(R.id.btnInscrire);

        // Layouts inscription / connexion
        layoutInscription = findViewById(R.id.layoutInscription);
        layoutConnexion = findViewById(R.id.layoutConnexion);

        // Boutons switch
        btnGoInscription = findViewById(R.id.btnGoInscription);
        btnGoConnexion = findViewById(R.id.btnGoConnexion);

        // Affichage par défaut
        layoutInscription.setVisibility(View.VISIBLE);
        layoutConnexion.setVisibility(View.GONE);

        // Initial UI update
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

        // Validation
        btnInscrire.setOnClickListener(v -> validateForm());
    }

    // Met à jour UI du rôle
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

    // Validation complète
    void validateForm() {
        String sNom = nom.getText().toString().trim();
        String sEmail = email.getText().toString().trim();
        String sPwd = pwd.getText().toString().trim();
        String sConfirm = confirmPwd.getText().toString().trim();

        // 💠 1 — Nom : min 5 caractères
        if (sNom.isEmpty()) {
            nom.setError("Veuillez entrer votre nom");
            return;
        }
        if (sNom.length() < 5) {
            nom.setError("Le nom doit contenir minimum 5 caractères");
            return;
        }

        // 💠 2 — Email
        if (sEmail.isEmpty()) {
            email.setError("Veuillez entrer un email");
            return;
        }

        // doit contenir '@'
        if (!sEmail.contains("@")) {
            email.setError("Email invalide : '@' manquant");
            return;
        }

        String beforeAt = sEmail.substring(0, sEmail.indexOf("@"));

        // texte avant @ obligatoire
        if (beforeAt.isEmpty()) {
            email.setError("Avant '@' vous devez écrire quelque chose");
            return;
        }

        // minimum 5 caractères avant le @
        if (beforeAt.length() < 5) {
            email.setError("Avant '@' il faut minimum 5 caractères");
            return;
        }

        // Vérification Android REGEX
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Format email invalide");
            return;
        }

        // 💠 3 — Mot de passe fort
        if (sPwd.length() < 8) {
            pwd.setError("Mot de passe : minimum 8 caractères");
            return;
        }
        if (!sPwd.matches(".*[A-Z].*")) {
            pwd.setError("Il faut au moins une lettre majuscule");
            return;
        }
        if (!sPwd.matches(".*[a-z].*")) {
            pwd.setError("Il faut au moins une minuscule");
            return;
        }
        if (!sPwd.matches(".*[0-9].*")) {
            pwd.setError("Il faut au moins un chiffre");
            return;
        }

        // 💠 4 — Confirmation
        if (!sPwd.equals(sConfirm)) {
            confirmPwd.setError("Les mots de passe ne correspondent pas");
            return;
        }

        // ✔ Tout est correct
        String role = isClient ? "Client" : "Livreur";
        Toast.makeText(this, "Compte " + role + " créé avec succès !", Toast.LENGTH_LONG).show();
    }
}
