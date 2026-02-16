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

public class Struttura {
    private String id;
    private String nome;
    private String tipologia;
    private List<String> caratteristiche;
    private int capienza;
    private double costoBase;
    private boolean isOperativo;
    private String tipoTariffa;

    public Struttura(String id, String nome, String tipologia, List<String> caratteristiche, 
                     int capienza, double costoBase, boolean isOperativo, String tipoTariffa) {
        this.id = id;
        this.nome = nome;
        this.tipologia = tipologia;
        this.caratteristiche = caratteristiche;
        this.capienza = capienza;
        this.costoBase = costoBase;
        this.isOperativo = isOperativo;
        this.tipoTariffa = tipoTariffa;
    }

public boolean corrisponde(String tipologiaRichiesta, List<String> caratteristicheRichieste) {
    // 1. Controllo Tipologia: Se l'utente lascia vuoto, va bene tutto. Altrimenti deve coincidere.
    boolean tipoMatch;
    if (tipologiaRichiesta == null || tipologiaRichiesta.isEmpty()) {
        tipoMatch = true; // Filtro disattivato, accetta tutto
    } else {
        tipoMatch = this.tipologia.equalsIgnoreCase(tipologiaRichiesta);
    }
    
    // 2. Controllo Caratteristiche: La struttura deve avere TUTTE le caratteristiche richieste
    boolean carattMatch;
    if (caratteristicheRichieste == null || caratteristicheRichieste.isEmpty()) {
        carattMatch = true; // Nessuna caratteristica richiesta
    } else {
        // containsAll verifica se la struttura possiede TUTTI gli elementi della lista richiesta
        // Esempio: Cerco "Doccia". La struttura ha "Doccia, Luci". -> TRUE
        carattMatch = this.caratteristiche.containsAll(caratteristicheRichieste);
    }
    
    // Restituisce true solo se entrambi i controlli passano e la struttura è operativa
    return tipoMatch && carattMatch && isOperativo;
}

    // --- GETTERS (Questi mancavano e causavano errore) ---
    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCapienza() { return capienza; } // <--- ECCOLO!
    public double getTariffa() { return costoBase; }
    public String getTipoTariffa() { return tipoTariffa; }
    
    @Override
    public String toString() {
        return nome + " (" + tipologia + ") - " + costoBase + "€";
    }
}