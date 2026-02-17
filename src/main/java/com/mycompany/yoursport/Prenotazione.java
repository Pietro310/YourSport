/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author anton
 */


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Prenotazione {
    private Struttura struttura;
    private Sportivo sportivo;

    private String id;
    private LocalDate data;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private int numeroPosti;
    private double costoTotale;
    private String stato;

    public Prenotazione(Struttura struttura, Sportivo sportivo, LocalDate data, 
                        LocalTime oraInizio, LocalTime oraFine, int numeroPosti) {
        this.struttura = struttura;
        this.sportivo = sportivo;
        this.data = data;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.numeroPosti = numeroPosti;
        this.stato = "Bozza"; 
        this.id = "PREN-" + System.currentTimeMillis();
    }

    public void calcolaCostoTotale() {
        double tariffa = struttura.getTariffa();
        long minuti = ChronoUnit.MINUTES.between(oraInizio, oraFine);
        double ore = minuti / 60.0;

        if ("ORARIO".equalsIgnoreCase(struttura.getTipoTariffa())) {
            this.costoTotale = tariffa * ore;
        } else {
            this.costoTotale = tariffa * ore * numeroPosti;
        }
    }

    public void setStato(String nuovoStato) {
        this.stato = nuovoStato;
    }

    // --- GETTERS (Fondamentali per i controlli in YourSport) ---
    public Struttura getStruttura() { return struttura; }
    public LocalDate getData() { return data; }
    public LocalTime getOraInizio() { return oraInizio; }
    public LocalTime getOraFine() { return oraFine; }
    public int getNumeroPosti() { return numeroPosti; }
    public String getStato() { return stato; }

    @Override
    public String toString() {
        // Assicurati che non ci siano eccezioni qui
        return "Prenotazione [" + stato + "] per " + struttura.getNome() + 
               " | Data: " + data + 
               " | Orario: " + oraInizio + "-" + oraFine +
               " | Posti: " + numeroPosti +
               " | Costo Totale: " + String.format("%.2f", costoTotale) + "€";
    }
}