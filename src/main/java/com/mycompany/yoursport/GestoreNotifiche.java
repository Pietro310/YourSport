/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author david
 */
import java.util.List;
public class GestoreNotifiche {

    // Messaggio 1 dell'SD
    public List<Notifica> mostraNotifiche() {
        YourSport sistema = YourSport.getInstance();
        
        // Messaggio 1.1: Chi è l'utente loggato?
        Sportivo utente = sistema.getCurrentUser();
        
        if (utente != null) {
            // Messaggio 1.2: Dammi le notifiche di questo utente
            return sistema.getNotificheUtente(utente);
        }
        
        return null; // Ritorna null se nessuno è loggato (sicurezza)
    }
}
