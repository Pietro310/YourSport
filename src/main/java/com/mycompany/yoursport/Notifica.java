/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author pietroalberio
 */

import java.time.LocalDate;
import java.util.UUID;

public class Notifica {
    private String id;
    private String messaggio;
    private LocalDate data;
    private Sportivo destinatario; // L'associazione 1 a Sportivo

    // Il costruttore (Messaggio 1.6 dell'SD)
    public Notifica(String messaggio, Sportivo destinatario) {
        this.id = UUID.randomUUID().toString(); // Genera un ID univoco casuale
        this.messaggio = messaggio;
        this.destinatario = destinatario;
        this.data = LocalDate.now(); // Data di oggi
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getMessaggio() { return messaggio; }
    public LocalDate getData() { return data; }
    public Sportivo getDestinatario() { return destinatario; }
}