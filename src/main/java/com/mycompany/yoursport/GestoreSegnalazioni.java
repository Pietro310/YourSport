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

public class GestoreSegnalazioni {

    // Metodo 1: Per l'SSD (mostra l'elenco)
    public List<Segnalazione> mostraSegnalazioni() {
        // Interroga il sistema centrale per farsi dare la lista
        return YourSport.getInstance().getArchivioSegnalazioni(); 
    }

    // Metodo 2: L'operazione di sistema dell'SD
    public boolean aggiornaStatoSegnalazione(String idSegnalazione, String nuovoStato) {
        
        // Messaggio 1.1: Chiede al sistema di trovare la segnalazione
        Segnalazione seg = YourSport.getInstance().getSegnalazione(idSegnalazione);
        
        // Il riquadro [opt] del tuo SD: procediamo solo se l'abbiamo trovata
        if (seg != null) {
            // Messaggio 1.2: Ordina all'oggetto di cambiare stato
            seg.setStato(nuovoStato);
            return true; // Aggiornamento riuscito
        }
        
        return false; // Segnalazione non trovata
    }
}
