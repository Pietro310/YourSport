/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author anton
 */

package com.mycompany.yoursport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.UUID;

public class Prenotazione {
    
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
        // Generazione automatica di un ID univoco parziale per la prenotazione
        this.id = "PRN-" + UUID.randomUUID().toString().substring(0, 6);
        this.struttura = struttura;
        this.sportivo = sportivo;
        this.data = data;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.numeroPosti = numeroPosti;
        this.stato = "In attesa"; 
    }

    // =======================================================
    // PREZZI DINAMICI - Information Expert (Usa i dati interni)
    // =======================================================
    public void calcolaCostoTotale() {
        // Chiediamo alla struttura il costo di partenza
        double costoDinamico = struttura.getCostoBase();

        // REGOLE DI BUSINESS PER I PREZZI MAGGIORATI
        
        // 1. Fascia Oraria: Se la prenotazione inizia dopo le 17:59, c'è un sovrapprezzo serale di 5.0€
        if (oraInizio.isAfter(LocalTime.of(17, 59))) {
            costoDinamico += 5.0; 
        }

        // 2. Caratteristiche Extra: Se la struttura ha la caratteristica "Coperto" o "Indoor", costa 3.0€ in più
        if (struttura.getCaratteristiche() != null && 
           (struttura.getCaratteristiche().contains("Coperto") || struttura.getCaratteristiche().contains("Indoor"))) {
            costoDinamico += 3.0;
        }

        // =======================================================
        // CALCOLO FINALE (In base al tipo di tariffazione)
        // =======================================================
        if ("ORARIO".equalsIgnoreCase(struttura.getTipoTariffa())) {
            // Se la tariffa è oraria (es. campo da tennis intero)
            long ore = Duration.between(oraInizio, oraFine).toHours();
            if (ore == 0) ore = 1; // Si paga sempre minimo un'ora
            this.costoTotale = ore * costoDinamico;
        } else {
            // Se la tariffa è a persona (es. ingressi in piscina)
            this.costoTotale = numeroPosti * costoDinamico;
        }
    }

    // ==========================================
    // GETTER & SETTER
    // ==========================================
    public String getId() { return id; }
    public Struttura getStruttura() { return struttura; }
    public Sportivo getSportivo() { return sportivo; }
    public LocalDate getData() { return data; }
    public LocalTime getOraInizio() { return oraInizio; }
    public LocalTime getOraFine() { return oraFine; }
    public int getNumeroPosti() { return numeroPosti; }
    
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    
    public double getCostoTotale() { return costoTotale; }
}