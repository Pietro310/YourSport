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
    private boolean disponibile;
    private String tipoTariffa; // Può essere "ORARIO" o "PERSONA"
    private boolean isOperativo = true; // Di base, quando crei un campo, è funzionante

    public Struttura(String id, String nome, String tipologia, List<String> caratteristiche, int capienza, double costoBase, boolean disponibile, String tipoTariffa) {
        this.id = id;
        this.nome = nome;
        this.tipologia = tipologia;
        this.caratteristiche = caratteristiche;
        this.capienza = capienza;
        this.costoBase = costoBase;
        this.disponibile = disponibile;
        this.tipoTariffa = tipoTariffa;
    }

    // Ricerca intelligente (per fa passare i tuoi test senza modificarli)
    public boolean corrisponde(String tipologiaRicercata, List<String> caratteristicheRichieste) {
        // 1. Controlla lo sport (solo se il test l'ha inserito, altrimenti va avanti)
        if (tipologiaRicercata != null && !tipologiaRicercata.trim().isEmpty()) {
            if (!this.tipologia.equalsIgnoreCase(tipologiaRicercata)) {
                return false;
            }
        }
        
        // 2. Controlla le caratteristiche ignorando le maiuscole (es. "Doccia" == "doccia")
        if (caratteristicheRichieste != null && !caratteristicheRichieste.isEmpty()) {
            for (String cRichiesta : caratteristicheRichieste) {
                boolean trovata = false;
                for (String cMia : this.caratteristiche) {
                    if (cMia.equalsIgnoreCase(cRichiesta)) {
                        trovata = true;
                        break;
                    }
                }
                if (!trovata) return false;
            }
        }
        return true;
    }

    public String getDettagli() {
        return "ID: " + id + " | Struttura: " + nome + " | Sport: " + tipologia + 
               " | Capienza: " + capienza + " posti | Tariffa: " + costoBase + "€ (" + tipoTariffa + ")";
    }

    // ==========================================
    // GETTER & SETTER
    // ==========================================

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getTipologia() { return tipologia; }
    public int getCapienza() { return capienza; }
    public double getCostoBase() { return costoBase; }
    public void setCostoBase(double costoBase) { this.costoBase = costoBase; }
    public String getTipoTariffa() { return tipoTariffa; }
    public void setTipoTariffa(String tipoTariffa) { this.tipoTariffa = tipoTariffa; }
    public boolean isDisponibile() { return disponibile; }
    public void setDisponibile(boolean disponibile) { this.disponibile = disponibile; } 
    public List<String> getCaratteristiche() { return caratteristiche; }
    
    public boolean isOperativo() {
        return isOperativo;
    }

    public void setOperativo(boolean isOperativo) {
        this.isOperativo = isOperativo;
    }
    
}

