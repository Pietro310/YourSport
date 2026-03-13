/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author david
 */
import java.util.Date;
import java.util.UUID; // Utile per generare un ID univoco automatico

import java.util.Date;
import java.util.UUID;

public class Segnalazione {
    private String id;
    private String descrizione;
    private Date dataInvio;
    private String stato;
    private Sportivo autore;
    private Struttura strutturaCoinvolta;

    // Costruttore
    public Segnalazione(String descrizione, Sportivo autore, Struttura str) {
        this.id = UUID.randomUUID().toString();
        this.descrizione = descrizione;
        this.dataInvio = new Date();
        this.stato = "Aperta";
        this.autore = autore;
        this.strutturaCoinvolta = str;
    }

    // Aggiungi un costruttore vuoto (spesso richiesto dalle librerie JSON per ricreare l'oggetto)
    public Segnalazione() {}
    // getter e setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Date getDataInvio() { return dataInvio; }
    public void setDataInvio(Date dataInvio) { this.dataInvio = dataInvio; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Sportivo getAutore() { return autore; }
    public void setAutore(Sportivo autore) { this.autore = autore; }

    public Struttura getStrutturaCoinvolta() { return strutturaCoinvolta; }
    public void setStrutturaCoinvolta(Struttura strutturaCoinvolta) { this.strutturaCoinvolta = strutturaCoinvolta; }
}