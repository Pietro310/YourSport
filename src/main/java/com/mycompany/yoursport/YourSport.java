/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.yoursport;

/**
 *
 * @author pietroalberio
 */
 // Sostituisci con il tuo package se diverso


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YourSport {

    private static YourSport instance;
    private List<Struttura> catalogoStrutture;
    private List<Prenotazione> archivioPrenotazioni;
    private List<Sportivo> elencoSportivi;
    private Prenotazione prenotazioneCorrente;
    private Sportivo currentUser;

    private YourSport() {
        this.catalogoStrutture = new ArrayList<>();
        this.archivioPrenotazioni = new ArrayList<>();
        this.elencoSportivi = new ArrayList<>();
        inizializzaDatiTest();
    }

    public static YourSport getInstance() {
        if (instance == null) instance = new YourSport();
        return instance;
    }

    public void login(String idSportivo) {
        for (Sportivo s : elencoSportivi) {
            if (s.getId().equalsIgnoreCase(idSportivo)) {
                this.currentUser = s;
                return;
            }
        }
        System.out.println("Utente non trovato.");
    }

    // --- PASSO 4 UC2: MOSTRA DISPONIBILITÀ ---
    // Abbiamo rimosso i LocalTime da qui. 
    // Questo metodo ora filtra solo per Tipologia e Caratteristiche.
    // La struttura appare sempre se esiste, il controllo orario si fa dopo.
    public List<Struttura> cercaStruttura(String tipologia, List<String> caratteristiche, LocalDate data) {
        System.out.println("--- Ricerca Strutture compatibili per il " + data + " ---");
        List<Struttura> risultato = new ArrayList<>();

        for (Struttura s : catalogoStrutture) {
            // Verifica solo se la struttura corrisponde alla descrizione
            if (s.corrisponde(tipologia, caratteristiche)) {
                risultato.add(s);
            }
        }
        return risultato;
    }

    // --- PASSO 6 UC2: VERIFICA DISPONIBILITÀ FASCIA ORARIA ---
    public Prenotazione selezionaRisorsa(String idStruttura, LocalDate data, LocalTime oraInizio, LocalTime oraFine, int numeroPostiRichiesti) {
        Struttura s = getStruttura(idStruttura);
        if (s == null) throw new IllegalArgumentException("Struttura non trovata");

        // QUI facciamo il controllo reale sulla fascia oraria specifica
        if (!isRisorsaDisponibile(s, data, oraInizio, oraFine, numeroPostiRichiesti)) {
            System.out.println("ERRORE: Struttura piena o non disponibile in questo orario specifico!");
            return null; // Ritorna null per indicare fallimento al Main
        }

        // Se passa il controllo, crea la prenotazione (Passo 7)
        Prenotazione p = new Prenotazione(s, currentUser, data, oraInizio, oraFine, numeroPostiRichiesti);
        p.calcolaCostoTotale();
        this.prenotazioneCorrente = p;
        return p;
    }

    // --- LOGICA DI CONTROLLO (Strategy: Orario vs Persona) ---
    private boolean isRisorsaDisponibile(Struttura s, LocalDate data, LocalTime inizio, LocalTime fine, int postiRichiesti) {
        int occupatiInQuestoOrario = 0;

        for (Prenotazione p : archivioPrenotazioni) {
            // Filtra solo prenotazioni confermate per questa struttura/data
            if (p.getStruttura().getId().equals(s.getId()) && 
                p.getData().equals(data) && 
                p.getStato().equals("Confermata")) {
                
                // Verifica sovrapposizione temporale esatta
                boolean sovrapposizione = inizio.isBefore(p.getOraFine()) && fine.isAfter(p.getOraInizio());
                
                if (sovrapposizione) {
                    // CASO TENNIS (Esclusivo)
                    if ("ORARIO".equalsIgnoreCase(s.getTipoTariffa())) {
                        return false; // Basta una sovrapposizione per bloccare tutto
                    }
                    // CASO PISCINA (Condiviso)
                    occupatiInQuestoOrario += p.getNumeroPosti();
                }
            }
        }

        // Verifica finale capienza residua
        int postiLiberi = s.getCapienza() - occupatiInQuestoOrario;
        System.out.println("   [DEBUG] Posti Totali: " + s.getCapienza() + " | Occupati in orario: " + occupatiInQuestoOrario + " | Richiesti: " + postiRichiesti);
        
        return postiLiberi >= postiRichiesti;
    }

    public void confermaPrenotazione() {
        if (this.prenotazioneCorrente != null) {
            this.prenotazioneCorrente.setStato("Confermata");
            this.archivioPrenotazioni.add(this.prenotazioneCorrente);
            System.out.println(">>> PRENOTAZIONE REGISTRATA CON SUCCESSO <<<");
            this.prenotazioneCorrente = null;
        }
    }
    
    // Utilities
    public void resetSistemaPerTest() {
        this.archivioPrenotazioni.clear();
        this.prenotazioneCorrente = null;
    }
    
    private Struttura getStruttura(String id) {
        for (Struttura s : catalogoStrutture) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    private void inizializzaDatiTest() {
        elencoSportivi.add(new Sportivo("U1", "Mario", "Rossi", "mario@email.it", "pw"));
        // S1: Tennis (Esclusivo)
        catalogoStrutture.add(new Struttura("S1", "Campo A", "Tennis", Arrays.asList("Terra Rossa"), 1, 20.0, true, "ORARIO"));
        // S2: Piscina (Condiviso)
        catalogoStrutture.add(new Struttura("S2", "Piscina Comunale", "Piscina", Arrays.asList("Doccia"), 20, 8.0, true, "PERSONA"));
    }
}