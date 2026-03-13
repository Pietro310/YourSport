/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author pietroalberio
 */

import java.util.List;

public class GestoreStrutture {

    public boolean sospendiStruttura(String idStruttura) {
        YourSport sistema = YourSport.getInstance();

        // Messaggio 1.1: Trova la struttura
        Struttura str = sistema.getStruttura(idStruttura);

        // RIQUADRO [opt]: [str != null]
        if (str != null) {
            
            // Messaggio 1.2: Spegni la struttura
            str.setOperativo(false);

            // Messaggio 1.3: Recupera prenotazioni da annullare
            List<Prenotazione> listaPrenotazioni = sistema.getPrenotazioniFuture(idStruttura);

            // RIQUADRO [loop]: per ogni pren in listaPrenotazioni
            for (Prenotazione pren : listaPrenotazioni) {
                
                // Messaggio 1.4: Annulla
                pren.setStato("Annullata");

                // Messaggio 1.5: Scopri lo sportivo
                Sportivo utente = pren.getSportivo();

                // Messaggio 1.6: Crea la notifica
                String msg = "AVVISO: La struttura '" + str.getNome() + "' è fuori servizio. La tua prenotazione del " + pren.getData() + " è stata annullata.";
                Notifica n = new Notifica(msg, utente);

                // Messaggio 1.7: Salva nel sistema
                sistema.aggiungiNotifica(n);
            }
            return true; // Sospensione e notifiche completate!
        }
        
        return false; // Struttura non trovata, l'opt non scatta
    }
    
    // Metodo per rimettere in servizio una struttura
    public boolean riattivaStruttura(String idStruttura) {
        YourSport sistema = YourSport.getInstance();
        Struttura str = sistema.getStruttura(idStruttura);

        if (str != null) {
            str.setOperativo(true); // Riaccende l'interruttore
            return true;
        }
        return false;
    }
    
}