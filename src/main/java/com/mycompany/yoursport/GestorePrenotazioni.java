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
import java.util.List;

public class GestorePrenotazioni {

    // ==========================================================
    // OPERAZIONE DI SISTEMA 1: Mostra le prenotazioni dell'utente
    // ==========================================================
    public List<Prenotazione> mostraMiePrenotazioni(String idSportivo) {
        YourSport ys = YourSport.getInstance();
        
        // 1.1: s = getSportivo(idSportivo)
        Sportivo s = ys.getSportivo(idSportivo);
        
        if (s != null) {
            // 1.2: lista = getElencoPrenotazioni()
            return s.getElencoPrenotazioni();
        }
        return null;
    }

    // ==========================================================
    // OPERAZIONE DI SISTEMA 2: Annulla una prenotazione (Blocco ALT)
    // ==========================================================
    public String annullaPrenotazione(String idPrenotazione) {
        YourSport ys = YourSport.getInstance();
        
        // 1.1: p = getPrenotazione(idPrenotazione)
        Prenotazione p = ys.getPrenotazione(idPrenotazione);
        
        if (p == null) {
            return "errore: prenotazione inesistente";
        }

        // 1.2: data = getData()
        LocalDate dataEvento = p.getData();
        LocalDate oggi = LocalDate.now();

        // Controllo della logica Condizionale (Guardie del Blocco ALT)
        if (dataEvento.isAfter(oggi)) { 
            // [dataEvento > oggi]
            
            // 1.3: setStato("Annullata")
            p.setStato("Annullata");
            
            // Soddisfacimento Post-Condizione: Salviamo lo stato sul database
            GestoreJSON.salvaDati(ys);
            
            // 1.4: prenotazione annullata
            return "prenotazione annullata";
            
        } else {
            // [dataEvento <= oggi]
            
            // 1.5: impossibile annullare
            return "impossibile annullare (la data dell'evento è passata o è oggi)";
        }
    }
}
