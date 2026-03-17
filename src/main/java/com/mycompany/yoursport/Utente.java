/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author david
 */
public abstract class Utente {
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String password;

    // Costruttore
    public Utente(String id, String nome, String cognome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    //Getters 
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Metodo 1.1 dell'SD: Lettura dei dati
    public String getDati() {
        return "Nome: " + this.nome + "\n" +
               "Cognome: " + this.cognome + "\n" +
               "Email: " + this.email + "\n" +
               "Password: " + this.password;
    }
    
    
    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
