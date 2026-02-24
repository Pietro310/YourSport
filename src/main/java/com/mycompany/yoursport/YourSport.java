/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.yoursport;

/**
 *
 * @author pietroalberio
 */
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
    
    //Associazione 1 a 1 con l'Amministratore 
    private Admin amministratore;
    
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

    //
    // METODI UC2 (Prenotazione Struttura)
    // 

    public List<Struttura> cercaStruttura(String tipologia, List<String> caratteristiche, LocalDate data) {
        System.out.println("--- Ricerca Strutture compatibili per il " + data + " ---");
        List<Struttura> risultato = new ArrayList<>();

        for (Struttura s : catalogoStrutture) {
            if (s.corrisponde(tipologia, caratteristiche)) {
                risultato.add(s);
            }
        }
        return risultato;
    }

    public Prenotazione selezionaRisorsa(String idStruttura, LocalDate data, LocalTime oraInizio, LocalTime oraFine, int numeroPostiRichiesti) {
        Struttura s = getStruttura(idStruttura);
        if (s == null) throw new IllegalArgumentException("Struttura non trovata");

        if (!isRisorsaDisponibile(s, data, oraInizio, oraFine, numeroPostiRichiesti)) {
            System.out.println("ERRORE: Struttura piena o non disponibile in questo orario specifico!");
            return null; 
        }

        Prenotazione p = new Prenotazione(s, currentUser, data, oraInizio, oraFine, numeroPostiRichiesti);
        p.calcolaCostoTotale();
        this.prenotazioneCorrente = p;
        return p;
    }

    private boolean isRisorsaDisponibile(Struttura s, LocalDate data, LocalTime inizio, LocalTime fine, int postiRichiesti) {
        int occupatiInQuestoOrario = 0;

        for (Prenotazione p : archivioPrenotazioni) {
            if (p.getStruttura().getId().equals(s.getId()) && 
                p.getData().equals(data) && 
                p.getStato().equals("Confermata")) {
                
                boolean sovrapposizione = inizio.isBefore(p.getOraFine()) && fine.isAfter(p.getOraInizio());
                
                if (sovrapposizione) {
                    if ("ORARIO".equalsIgnoreCase(s.getTipoTariffa())) {
                        return false; 
                    }
                    occupatiInQuestoOrario += p.getNumeroPosti();
                }
            }
        }

        int postiLiberi = s.getCapienza() - occupatiInQuestoOrario;
        return postiLiberi >= postiRichiesti;
    }

    public void confermaPrenotazione() {
        if (this.prenotazioneCorrente != null) {
            this.prenotazioneCorrente.setStato("Confermata");
            // Ora che la prenotazione è confermata, la aggiungiamo all'archivio generale...
            this.archivioPrenotazioni.add(this.prenotazioneCorrente);
            // ... e la aggiungiamo anche alla lista personale dello Sportivo (coerenza col DCD!)
            if (this.currentUser != null) {
                this.currentUser.addPrenotazione(this.prenotazioneCorrente);
            }
            System.out.println(">>> PRENOTAZIONE REGISTRATA CON SUCCESSO <<<");
            this.prenotazioneCorrente = null;
        }
    }
    
    // 
    // NUOVI METODI UC3 (Gestione Costi)
    // 

    public List<Struttura> mostraCatalogo() {
        return this.catalogoStrutture;
    }

    public String mostraDettagliStruttura(String idStruttura) {
        Struttura s = getStruttura(idStruttura);
        if (s != null) {
            // Pattern Information Expert
            return s.getDettagli();
        }
        return "Errore: Struttura inesistente nel catalogo.";
    }

public void aggiornaTariffa(String idStruttura, String tipoTariffa, double costoBase) {
        Struttura s = getStruttura(idStruttura);
        
        if (s != null) {
            // --- NUOVO CONTROLLO: Validazione Tipo Tariffa ---
            // Se la parola NON è "ORARIO" e NON è "PERSONA", blocca tutto.
            if (!tipoTariffa.equalsIgnoreCase("ORARIO") && !tipoTariffa.equalsIgnoreCase("PERSONA")) {
                System.out.println("ERRORE DI SISTEMA: Tipo tariffa non valido! Inserire solo 'ORARIO' o 'PERSONA'.");
                return; // Esce immediatamente dal metodo senza modificare nulla
            }

            // --- Blocco ALT (Estensione 3a del caso d'uso: controllo costoBase >= 0) ---
            if (costoBase >= 0) {
                // Normalizziamo la stringa mettendola in maiuscolo (es. se digita "orario" diventa "ORARIO")
                s.setTipoTariffa(tipoTariffa.toUpperCase());
                s.setCostoBase(costoBase);
                System.out.println(">>> Sistema: Aggiornamento tariffa confermato per la struttura " + s.getNome() + " <<<");
            } else {
                System.out.println("ERRORE DI SISTEMA: Il costo base inserito non è valido (deve essere >= 0).");
            }
        } else {
            System.out.println("ERRORE DI SISTEMA: Impossibile aggiornare, la struttura non è stata trovata.");
        }
    }

    // 
    // UTILITIES & INIZIALIZZAZIONE
    // 

    public void resetSistemaPerTest() {
        this.archivioPrenotazioni.clear();
        this.prenotazioneCorrente = null;
    }
    
    public Struttura getStruttura(String id) {
        for (Struttura s : catalogoStrutture) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    private void inizializzaDatiTest() {
        // Inizializza l'unico amministratore del sistema
        this.amministratore = new Admin("A1", "Super", "Admin", "admin@yoursport.it", "adminpass");
        
        // Inizializza uno sportivo
        elencoSportivi.add(new Sportivo("U1", "Mario", "Rossi", "mario@email.it", "pw"));
        
        // S1: Tennis (Esclusivo)
        catalogoStrutture.add(new Struttura("S1", "Campo A", "Tennis", Arrays.asList("Terra Rossa"), 1, 20.0, true, "ORARIO"));
        // S2: Piscina (Condiviso)
        catalogoStrutture.add(new Struttura("S2", "Piscina Comunale", "Piscina", Arrays.asList("Doccia"), 20, 8.0, true, "PERSONA"));
    }
}