package tn.isetn.projet_mobile;

public class User {
    private String nom;
    private String email;
    private String role;
    private String password; // ajouter

    public User() {} // nécessaire pour Firebase

    public User(String nom, String email, String role, String password) {
        this.nom = nom;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
