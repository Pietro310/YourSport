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

    private static YourSport instance; //singleton
    private List<Struttura> catalogoStrutture;
    private List<Prenotazione> archivioPrenotazioni;
    private List<Sportivo> elencoSportivi;
    private List<Segnalazione> archivioSegnalazioni;
    private List<Notifica> archivioNotifiche; // <-- AGGIUNTO PER UC7
    private Admin amministratore;
    private Prenotazione prenotazioneCorrente;
    private Sportivo currentUser;

    private YourSport() {
        this.catalogoStrutture = new ArrayList<>();
        this.archivioPrenotazioni = new ArrayList<>();
        this.elencoSportivi = new ArrayList<>();
        this.archivioSegnalazioni = new ArrayList<>();
        this.archivioNotifiche = new ArrayList<>(); // <-- INIZIALIZZATO PER UC7
        inizializzaDatiTest();
    }

    public static YourSport getInstance() {
        if (instance == null) instance = new YourSport();
        return instance;
    }
    
    public static void setInstance(YourSport loadedInstance) {
        instance = loadedInstance;
    }

    public void ricollegaDatiTrasienti() {
        for (Sportivo s : elencoSportivi) {
            s.getElencoPrenotazioni().clear(); 
        }
        for (Prenotazione p : archivioPrenotazioni) {
            if (p.getSportivo() != null) {
                Sportivo proprietario = getSportivo(p.getSportivo().getId());
                if (proprietario != null) {
                    proprietario.addPrenotazione(p);
                }
            }
        }
        for (Segnalazione seg : this.archivioSegnalazioni) {
    
            // Ricollega l'autore (Sportivo)
            if (seg.getAutore() != null) {
                Sportivo veroAutore = this.getSportivo(seg.getAutore().getId());
                if (veroAutore != null) {
                    seg.setAutore(veroAutore);
                }
            }
            if (seg.getStrutturaCoinvolta() != null) {
                Struttura veraStruttura = this.getStruttura(seg.getStrutturaCoinvolta().getId());
                if (veraStruttura != null) {
                    seg.setStrutturaCoinvolta(veraStruttura);
                }
            }
        }
    }

    // ==========================================
    // SISTEMA DI AUTENTICAZIONE
    // ==========================================
    public boolean login(String email, String password) {
        for (Sportivo s : elencoSportivi) {
            if (s.getEmail().equalsIgnoreCase(email) && s.getPassword().equals(password)) {
                this.currentUser = s;
                return true; 
            }
        }
        return false; 
    }
    
    // --- NUOVO: Autenticazione specifica per l'Admin ---
    public boolean loginAdmin(String email, String password) {
        if (this.amministratore != null && 
            this.amministratore.getEmail().equalsIgnoreCase(email) && 
            this.amministratore.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
    
    public void logout() {
        this.currentUser = null;
    }

    // ==========================================
    // UC1: REGISTRAZIONE
    // ==========================================
    public String registrazioneSportivo(String nome, String cognome, String email, String password) {
        boolean esiste = verificaEmail(email);
        if (esiste) return "email già in uso";
        
        String nuovoId = "U" + (elencoSportivi.size() + 1);
        Sportivo nuovoSportivo = new Sportivo(nuovoId, nome, cognome, email, password);
        elencoSportivi.add(nuovoSportivo);
        
        GestoreJSON.salvaDati(this);
        return "utente registrato";
    }

    private boolean verificaEmail(String email) {
        for (Sportivo s : elencoSportivi) {
            if (s.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    // ==========================================
    // UC2: PRENOTAZIONE
    // ==========================================
    public List<Struttura> cercaStruttura(String tipologia, List<String> caratteristiche, LocalDate data) {
        System.out.println("--- Ricerca Strutture compatibili per il " + data + " ---");
        List<Struttura> risultato = new ArrayList<>();
        for (Struttura s : catalogoStrutture) {
            // ---> MODIFICA UC7: Aggiunto controllo s.isOperativo() <---
            if (s.isOperativo() && s.corrisponde(tipologia, caratteristiche)) {
                risultato.add(s);
            }
        }
        return risultato;
    }

    public Prenotazione selezionaRisorsa(String idStruttura, LocalDate data, LocalTime oraInizio, LocalTime oraFine, int numeroPostiRichiesti) {
        LocalDate oggi = LocalDate.now();
        LocalTime oraAttuale = LocalTime.now();

        if (data.isBefore(oggi)) {
            System.out.println("ERRORE DI SISTEMA: Non puoi prenotare per una data passata!");
            return null;
        }
        
        if (data.isEqual(oggi) && oraInizio.isBefore(oraAttuale)) {
            System.out.println("ERRORE DI SISTEMA: L'orario di inizio specificato è già passato!");
            return null;
        }
        
        if (!oraFine.isAfter(oraInizio)) {
            System.out.println("ERRORE DI SISTEMA: L'orario di fine deve essere successivo all'orario di inizio!");
            return null;
        }

        Struttura s = getStruttura(idStruttura);
        if (s == null) throw new IllegalArgumentException("Struttura non trovata");

        // ---> MODIFICA UC7: Blocco immediato se la struttura non è operativa <---
        if (!s.isOperativo()) {
            System.out.println("ERRORE: Struttura attualmente fuori servizio per manutenzione.");
            return null;
        }

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
                    if ("ORARIO".equalsIgnoreCase(s.getTipoTariffa())) return false; 
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
            this.archivioPrenotazioni.add(this.prenotazioneCorrente);
            if (this.currentUser != null) {
                this.currentUser.addPrenotazione(this.prenotazioneCorrente);
            }
            System.out.println(">>> PRENOTAZIONE REGISTRATA CON SUCCESSO <<<");
            this.prenotazioneCorrente = null;
            
            GestoreJSON.salvaDati(this);
        }
    }
    
    // ==========================================
    // UC3: GESTIONE COSTI
    // ==========================================
    public List<Struttura> mostraCatalogo() { return this.catalogoStrutture; }

    public String mostraDettagliStruttura(String idStruttura) {
        Struttura s = getStruttura(idStruttura);
        if (s != null) return s.getDettagli();
        return "Errore: Struttura inesistente nel catalogo.";
    }

    public void aggiornaTariffa(String idStruttura, String tipoTariffa, double costoBase) {
        Struttura s = getStruttura(idStruttura);
        if (s != null) {
            if (!tipoTariffa.equalsIgnoreCase("ORARIO") && !tipoTariffa.equalsIgnoreCase("PERSONA")) {
                System.out.println("ERRORE DI SISTEMA: Tipo tariffa non valido!");
                return;
            }
            if (costoBase >= 0) {
                s.setTipoTariffa(tipoTariffa.toUpperCase());
                s.setCostoBase(costoBase);
                System.out.println(">>> Sistema: Aggiornamento tariffa confermato <<<");
                GestoreJSON.salvaDati(this);
            } else {
                System.out.println("ERRORE DI SISTEMA: Costo base non valido.");
            }
        } else {
            System.out.println("ERRORE DI SISTEMA: Struttura non trovata.");
        }
    }

      // --------------------------------------------------------
      // UC5: INVIA SEGNALAZIONE
      // --------------------------------------------------------
    public Segnalazione inviaSegnalazione(String idStruttura, String descrizione) {
        
        if (this.archivioSegnalazioni == null) {
        this.archivioSegnalazioni = new ArrayList<>();
        }
        // Messaggio 1.1: Trova la struttura (metodo privato di supporto)
        Struttura str = this.getStruttura(idStruttura);
        
        // Messaggio 1.2: Recupera chi è loggato in questo momento (metodo privato di supporto)
        Sportivo currentUser = this.getCurrentUser();
        
        // (Opzionale: piccolo controllo di sicurezza per evitare che Java vada in crash)
        if (str == null || currentUser == null) {
            throw new IllegalArgumentException("Errore: struttura non trovata o utente non loggato");
        }

        // Messaggio 1.3: Creazione (<<create>>) dell'oggetto Segnalazione
        Segnalazione seg = new Segnalazione(descrizione, currentUser, str);

        // Messaggio 1.4: Salvataggio in memoria (nella lista del DCD)
        this.archivioSegnalazioni.add(seg);

        // Messaggio 1.5: Ritorna l'oggetto appena creato
        return seg;
    }
    
    
      // --------------------------------------------------------
      // UC6: GESTIONE SEGNALAZIONI
      // --------------------------------------------------------
    
    // Restituisce l'intera lista al Gestore
    public List<Segnalazione> getArchivioSegnalazioni() {
        if (this.archivioSegnalazioni == null) {
            this.archivioSegnalazioni = new java.util.ArrayList<>();
        }
        return this.archivioSegnalazioni;
    }

    // Messaggio 1.1 dell'SD: Cerca una specifica segnalazione per ID
    public Segnalazione getSegnalazione(String idSegnalazione) {
        if (this.archivioSegnalazioni != null) {
            for (Segnalazione s : archivioSegnalazioni) {
                if (s.getId().equalsIgnoreCase(idSegnalazione)) {
                    return s;
                }
            }
        }
        return null; // Ritorna null se non la trova (gestito dal riquadro opt dell'SD!)
    }

    // ==========================================
    // UC7: NOTIFICHE E DISSERVIZI (AGGIUNTE PER GESTORESTRUTTURE)
    // ==========================================
    
    // Salva la notifica nel sistema
    public void aggiungiNotifica(Notifica n) {
        this.archivioNotifiche.add(n);
    }
    
    
    // Trova le prenotazioni future di una struttura specifica
    public List<Prenotazione> getPrenotazioniFuture(String idStruttura) {
        List<Prenotazione> future = new ArrayList<>();
        LocalDate oggi = LocalDate.now();
        
        for (Prenotazione p : this.archivioPrenotazioni) {
            if (p.getStruttura().getId().equalsIgnoreCase(idStruttura) && 
               !p.getStato().equalsIgnoreCase("Annullata") &&
               (p.getData().isEqual(oggi) || p.getData().isAfter(oggi))) {
                
                future.add(p);
            }
        }
        return future;
    }
    
    // ==========================================
    // UC8: VISUALIZZA NOTIFICHE
    // ==========================================
    public List<Notifica> getNotificheUtente(Sportivo utente) {
        List<Notifica> listaNotifiche = new java.util.ArrayList<>();
        
        // Frammento [loop] dell'SD
        for (Notifica n : this.archivioNotifiche) {
            
            // Frammento [opt] dell'SD: controllo se il destinatario è l'utente loggato
            // (è la stessa logica di == utente)
            if (n.getDestinatario().getId().equals(utente.getId())) {
                
                // Messaggio 1.2.1 (Il Self-Message)
                listaNotifiche.add(n);
            }
        }
        return listaNotifiche;
    }
    
    // ==========================================
    // UC9: STATISTICHE INCASSI PREVISTI
    // ==========================================
    
    // Messaggio 1 dell'SD
    public double generaReportIncassi(LocalDate dataInizio, LocalDate dataFine) {
        double totaleIncassi = 0.0; // Variabile locale per sommare i soldi
        
        // Frammento [loop] dell'SD
        for (Prenotazione p : this.archivioPrenotazioni) {
            
            // Frammento [opt] dell'SD: Controllo Date e Stato "Confermata"
            // isBefore e isAfter sono l'equivalente elegante di >= e <= in Java per le date
            boolean nelPeriodo = !p.getData().isBefore(dataInizio) && !p.getData().isAfter(dataFine);
            boolean isConfermata = p.getStato().equalsIgnoreCase("Confermata");
            
            if (nelPeriodo && isConfermata) {
                // Messaggio 1.1
                double c = p.getCostoTotale(); 
                
                // Messaggio 1.2 (Self-Message per sommare)
                totaleIncassi = aggiornaTotale(totaleIncassi, c);
            }
        }
        
        return totaleIncassi;
    }

    // Metodo privato per il Self-Message 1.2
    private double aggiornaTotale(double parziale, double costoAggiuntivo) {
        return parziale + costoAggiuntivo;
    }

    // ==========================================
    // getter e setter
    // ==========================================
    public Struttura getStruttura(String id) {
        for (Struttura s : catalogoStrutture) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }
    
    public Sportivo getSportivo(String idSportivo) {
        for (Sportivo s : elencoSportivi) {
            if (s.getId().equalsIgnoreCase(idSportivo)) return s;
        }
        return null;
    }
    
    public Prenotazione getPrenotazione(String idPrenotazione) {
        for (Prenotazione p : archivioPrenotazioni) {
            if (p.getId().equalsIgnoreCase(idPrenotazione)) return p;
        }
        return null;
    }

    public Sportivo getCurrentUser() {
        return currentUser;
    }
    
    // Serve per il benvenuto nell'interfaccia
    public Admin getAmministratore() {
        return amministratore;
    }
    
    public List<Notifica> getArchivioNotifiche() {
        return this.archivioNotifiche;
    }

    private void inizializzaDatiTest() {
        this.amministratore = new Admin("A1", "Super", "Admin", "admin@yoursport.it", "adminpass");
        elencoSportivi.add(new Sportivo("U1", "Mario", "Rossi", "mario@email.it", "pw"));
        catalogoStrutture.add(new Struttura("S1", "Campo A", "Tennis", Arrays.asList("Terra Rossa"), 1, 20.0, true, "ORARIO"));
        catalogoStrutture.add(new Struttura("S2", "Piscina Comunale", "Piscina", Arrays.asList("Doccia"), 20, 8.0, true, "PERSONA"));
    }

    public void resetSistemaPerTest() {
        GestoreJSON.DISABILITA_SALVATAGGIO_PER_TEST = true; 
        this.archivioPrenotazioni.clear();
        this.prenotazioneCorrente = null;
        this.catalogoStrutture.clear();
        this.elencoSportivi.clear();
        this.archivioSegnalazioni.clear();
        this.archivioNotifiche.clear(); // <-- AGGIUNTO PER UC7
        inizializzaDatiTest();
    }
}