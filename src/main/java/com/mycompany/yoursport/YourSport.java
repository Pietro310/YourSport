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
            if (s.corrisponde(tipologia, caratteristiche)) {
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

    // ==========================================
    // UTILITIES & INIZIALIZZAZIONE
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
        inizializzaDatiTest();
    }
}