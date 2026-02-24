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
    
    // Associazione 0..* verso Prenotazione (riservata solo allo Sportivo)
    private List<Prenotazione> prenotazioni;

    // Costruttore: passa i parametri al padre (Utente) tramite super()
    public Sportivo(String id, String nome, String cognome, String email, String password) {
        super(id, nome, cognome, email, password);
        this.prenotazioni = new ArrayList<>();
    }

    // Metodi per gestire le prenotazioni dello sportivo
    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void addPrenotazione(Prenotazione p) {
        this.prenotazioni.add(p);
    }
}