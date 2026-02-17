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
    private String tipoTariffa; // "ORARIO" o "PERSONA"

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

    // Metodo di business logic per il filtro
    public boolean corrisponde(String tipologiaRichiesta, List<String> caratteristicheRichieste) {
        // 1. Controllo Tipologia (se vuota, accetta tutto)
        boolean tipoMatch;
        if (tipologiaRichiesta == null || tipologiaRichiesta.isEmpty()) {
            tipoMatch = true;
        } else {
            tipoMatch = this.tipologia.equalsIgnoreCase(tipologiaRichiesta);
        }
        
        // 2. Controllo Caratteristiche (se vuota, accetta tutto)
        boolean carattMatch;
        if (caratteristicheRichieste == null || caratteristicheRichieste.isEmpty()) {
            carattMatch = true;
        } else {
            carattMatch = this.caratteristiche.containsAll(caratteristicheRichieste);
        }
        
        return tipoMatch && carattMatch && isOperativo;
    }

    // --- GETTERS (Quelli che causavano l'errore se mancanti o vuoti) ---
    
    public String getId() { 
        return id; 
    }

    public String getNome() { 
        return nome; 
    }

    public String getTipologia() { 
        return tipologia; 
    }

    public int getCapienza() { 
        return capienza; 
    }

    public double getTariffa() { 
        return costoBase; 
    }

    public String getTipoTariffa() { 
        return tipoTariffa; 
    }
    
    // Per stampare l'oggetto in modo leggibile (se serve)
    @Override
    public String toString() {
        return nome + " (" + tipologia + ") - Capienza: " + capienza + " - Costo: " + costoBase + "€";
    }
}