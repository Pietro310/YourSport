/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author anton
 */
import java.util.List;

public class Sportivo {
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String password;

    public Sportivo(String id, String nome, String cognome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getNome() { return nome; }
    public String getId() { return id; }
    // ... altri getter e setter se servono
}