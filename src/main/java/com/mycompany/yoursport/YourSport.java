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

    // --- 1. PATTERN SINGLETON (Da Diagramma delle Classi) ---
    private static YourSport instance;

    // --- 2. ASSOCIAZIONI (Liste e Riferimenti) ---
    private List<Struttura> catalogoStrutture;       // - catalogoStrutture
    private List<Prenotazione> archivioPrenotazioni; // - archivioPrenotazioni
    private List<Sportivo> elencoSportivi;           // - elencoSportivi

    private Prenotazione prenotazioneCorrente;       // - prenotazioneCorrente [0..1]
    private Sportivo currentUser;                    // - sportivoCorrente [0..1]

    // --- 3. COSTRUTTORE PRIVATO ---
    private YourSport() {
        this.catalogoStrutture = new ArrayList<>();
        this.archivioPrenotazioni = new ArrayList<>();
        this.elencoSportivi = new ArrayList<>();

        // Inizializziamo dati finti per il testing
        inizializzaDatiTest();
    }

    // Metodo statico per ottenere l'istanza
    public static YourSport getInstance() {
        if (instance == null) {
            instance = new YourSport();
        }
        return instance;
    }

    // --- 4. GESTIONE UTENTE (Helper) ---
    public void login(String idSportivo) {
        for (Sportivo s : elencoSportivi) {
            if (s.getId().equalsIgnoreCase(idSportivo)) {
                this.currentUser = s;
                System.out.println("Login effettuato: " + s.getNome());
                return;
            }
        }
        System.out.println("Utente non trovato! (Prova con 'U1')");
    }

    // --- 5. UC2: cercaStruttura (Implementazione fedele al Diagramma) ---
    public List<Struttura> cercaStruttura(String tipologia, List<String> caratteristiche, LocalDate data) {
        System.out.println("--- Ricerca in corso per: " + tipologia + " ---");
        List<Struttura> risultato = new ArrayList<>();

        // Loop (Passo 1.2 del Diagramma cercaStruttura)
        for (Struttura s : catalogoStrutture) {
            
            // Passo 1.2: Verifica corrispondenza
            if (s.corrisponde(tipologia, caratteristiche)) {
                
                // Passo 1.3: n = calcolaPostiOccupati(s, data)
                int n = calcolaPostiOccupati(s, data);

                // Passo 1.4: [n < capienza] -> Aggiungi ai risultati
                if (n < s.getCapienza()) {
                    risultato.add(s);
                }
            }
        }
        return risultato;
    }

    // Helper per cercaStruttura (Passo 1.3)
    private int calcolaPostiOccupati(Struttura s, LocalDate data) {
        int count = 0;
        for (Prenotazione p : archivioPrenotazioni) {
            // Conta le prenotazioni CONFERMATE per quella struttura in quella data
            if (p.getStruttura().getId().equals(s.getId()) && 
                p.getData().equals(data) && 
                p.getStato().equals("Confermata")) {
                
                // Logica semplificata: contiamo le prenotazioni come "slot occupati"
                count++;
            }
        }
        return count;
    }

    // --- 6. UC2: selezionaRisorsa (Con controllo orario specifico) ---
    public Prenotazione selezionaRisorsa(String idStruttura, LocalDate data, LocalTime oraInizio, LocalTime oraFine, int numeroPosti) {
        // 1.1 Recupero struttura
        Struttura s = getStruttura(idStruttura);
        if (s == null) {
            throw new IllegalArgumentException("Struttura non trovata con ID: " + idStruttura);
        }

        // --- CONTROLLO DISPONIBILITÀ (Estensione UC2 4a) ---
        // Questo impedisce la doppia prenotazione nello stesso orario
        if (!isRisorsaDisponibile(s, data, oraInizio, oraFine)) {
            System.out.println("ERRORE: La struttura è già occupata in questo orario!");
            return null; 
        }

        // 1.2 Creator: YourSport crea l'istanza (Pattern Creator)
        Prenotazione p = new Prenotazione(s, currentUser, data, oraInizio, oraFine, numeroPosti);
        
        // 1.2.2 Calcolo costo (delegato alla prenotazione - Information Expert)
        p.calcolaCostoTotale();
        
        // Salviamo nella sessione corrente
        this.prenotazioneCorrente = p;
        
        System.out.println("Risorsa selezionata. Costo preventivato: " + p);
        return p;
    }

    // Helper per verificare sovrapposizioni di orario esatte
    private boolean isRisorsaDisponibile(Struttura s, LocalDate data, LocalTime inizio, LocalTime fine) {
        for (Prenotazione p : archivioPrenotazioni) {
            if (p.getStruttura().getId().equals(s.getId()) && 
                p.getData().equals(data) && 
                p.getStato().equals("Confermata")) {
                
                // Verifica sovrapposizione temporale:
                // (InizioRichiesto < FinePrenotata) AND (FineRichiesta > InizioPrenotato)
                if (inizio.isBefore(p.getOraFine()) && fine.isAfter(p.getOraInizio())) {
                    return false; // Sovrapposizione trovata!
                }
            }
        }
        return true; // Nessuna sovrapposizione
    }

    // --- 7. UC2: confermaPrenotazione (Pattern Information Expert) ---
    public void confermaPrenotazione() {
        if (this.prenotazioneCorrente == null) {
            System.out.println("Nessuna prenotazione da confermare.");
            return;
        }

        // 1.1 Cambio stato (sulla Prenotazione)
        this.prenotazioneCorrente.setStato("Confermata");

        // 1.2 Aggiunta al registro (Information Expert: YourSport gestisce la lista)
        this.archivioPrenotazioni.add(this.prenotazioneCorrente);

        System.out.println("Prenotazione CONFERMATA e archiviata con successo.");
        
        // Reset sessione
        this.prenotazioneCorrente = null;
    }

    // --- 8. Metodi di Supporto ---

    private Struttura getStruttura(String id) {
        for (Struttura s : catalogoStrutture) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    private void inizializzaDatiTest() {
        // Utente Test
        elencoSportivi.add(new Sportivo("U1", "Mario", "Rossi", "mario@email.it", "password"));

        // Struttura 1: Calcetto
        List<String> carattS1 = new ArrayList<>(Arrays.asList("Doccia", "Spogliatoio", "Luci"));
        catalogoStrutture.add(new Struttura("S1", "Campo A", "Calcetto", carattS1, 10, 50.0, true, "ORARIO"));

        // Struttura 2: Tennis
        List<String> carattS2 = new ArrayList<>(Arrays.asList("Terra Rossa", "Doccia"));
        catalogoStrutture.add(new Struttura("S2", "Campo B", "Tennis", carattS2, 2, 20.0, true, "ORARIO"));
    }
}