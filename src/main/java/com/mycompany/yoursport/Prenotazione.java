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
import java.time.Duration;
import java.util.UUID;

public class Prenotazione {
    
    // Il famoso ID mancante!
    private String id;
    
    private Struttura struttura;
    private Sportivo sportivo;
    private LocalDate data;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private int numeroPosti;
    private String stato;
    private double costoTotale;

    public Prenotazione(Struttura struttura, Sportivo sportivo, LocalDate data, LocalTime oraInizio, LocalTime oraFine, int numeroPosti) {
        // Generiamo automaticamente un ID univoco (es. "PRN-7b3f9a")
        this.id = "PRN-" + UUID.randomUUID().toString().substring(0, 6);
        
        this.struttura = struttura;
        this.sportivo = sportivo;
        this.data = data;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.numeroPosti = numeroPosti;
        this.stato = "In attesa"; // Stato di default
    }

    public void calcolaCostoTotale() {
        if ("ORARIO".equalsIgnoreCase(struttura.getTipoTariffa())) {
            // Calcolo costo basato sulle ore
            long ore = Duration.between(oraInizio, oraFine).toHours();
            if (ore == 0) ore = 1; // Minimo 1 ora
            this.costoTotale = ore * struttura.getCostoBase();
        } else {
            // Calcolo costo basato sul numero di persone
            this.costoTotale = numeroPosti * struttura.getCostoBase();
        }
    }

    // ==========================================
    // GETTER & SETTER
    // ==========================================
    
    public String getId() {
        return id;
    }

    public Struttura getStruttura() {
        return struttura;
    }

    public Sportivo getSportivo() {
        return sportivo;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public int getNumeroPosti() {
        return numeroPosti;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public double getCostoTotale() {
        return costoTotale;
    }
}