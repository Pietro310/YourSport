/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author anton
 */  


import java.util.ArrayList;
import java.util.List;

public class Sportivo extends Utente {
    
    // LA MAGIA È QUI: 'transient' impedisce a Gson di finire in un loop infinito (StackOverflow)
    private transient List<Prenotazione> elencoPrenotazioni;

    // Costruttore
    public Sportivo(String id, String nome, String cognome, String email, String password) {
        super(id, nome, cognome, email, password);
        this.elencoPrenotazioni = new ArrayList<>();
    }

    // Metodi per gestire le prenotazioni
    public List<Prenotazione> getElencoPrenotazioni() {
        // Poiché Gson salta questa variabile, dobbiamo assicurarci che non sia 'null'
        if (this.elencoPrenotazioni == null) {
            this.elencoPrenotazioni = new ArrayList<>();
        }
        return elencoPrenotazioni;
    }

    public void addPrenotazione(Prenotazione p) {
        if (this.elencoPrenotazioni == null) {
            this.elencoPrenotazioni = new ArrayList<>();
        }
        this.elencoPrenotazioni.add(p);
    }


    
    
}