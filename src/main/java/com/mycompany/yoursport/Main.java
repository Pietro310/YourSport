/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author anton
 */


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        
        YourSport sistemaCaricato = GestoreJSON.caricaDati();
        YourSport sistema;
        
        if (sistemaCaricato != null) {
            System.out.println(">>> Dati caricati con successo dal database locale!");
            sistema = sistemaCaricato;
            sistema.ricollegaDatiTrasienti(); 
            YourSport.setInstance(sistema);
        } else {
            System.out.println(">>> Nessun database trovato. Inizializzazione con dati di default.");
            sistema = YourSport.getInstance();
        }

        Scanner scanner = new Scanner(System.in);
        boolean esci = false;

        while (!esci) {
            System.out.println("\n==========================================");
            System.out.println("        BENVENUTO IN YOURSPORT            ");
            System.out.println("==========================================");
            System.out.println("1. Registrati al sistema (Test UC1)");
            System.out.println("2. Accedi come Sportivo  (Test UC2, UC4 e UC5)");
            System.out.println("3. Accedi come Admin     (Test UC3, UC6 e UC7)");
            System.out.println("4. Esci dal sistema");
            System.out.print("Scegli un'opzione: ");
            
            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    gestisciRegistrazione(sistema, scanner);
                    break;
                case "2":
                    gestisciSportivo(sistema, scanner);
                    break;
                case "3":
                    gestisciAdmin(sistema, scanner);
                    break;
                case "4":
                    esci = true;
                    GestoreJSON.salvaDati(sistema);
                    System.out.println("Uscita dal sistema. A presto!");
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        }
        scanner.close();
    }

    private static void gestisciRegistrazione(YourSport sistema, Scanner scanner) {
        System.out.println("\n==========================================");
        System.out.println("      REGISTRAZIONE NUOVO SPORTIVO        ");
        System.out.println("==========================================");

        System.out.print("Inserisci il tuo Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Inserisci il tuo Cognome: ");
        String cognome = scanner.nextLine().trim();
        System.out.print("Inserisci la tua Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Scegli una Password: ");
        String password = scanner.nextLine().trim();

        System.out.println("\nElaborazione richiesta in corso...");
        String esito = sistema.registrazioneSportivo(nome, cognome, email, password);

        System.out.println("Esito Registrazione: " + esito.toUpperCase());
    }

    private static void gestisciSportivo(YourSport sistema, Scanner scanner) {
        System.out.println("\n--- AUTENTICAZIONE SPORTIVO ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (!sistema.login(email, password)) {
            System.out.println("ERRORE: Email o password errati! Accesso negato.");
            return;
        }

        Sportivo utenteLoggato = sistema.getCurrentUser();
        System.out.println("\nLogin effettuato. Benvenuto " + utenteLoggato.getNome() + "!");
        
        boolean continua = true;
        while (continua) {
            System.out.println("\n==========================================");
            System.out.println("                AREA SPORTIVO             ");
            System.out.println("==========================================");
            System.out.println("1. Nuova Prenotazione (UC2)");
            System.out.println("2. Le Mie Prenotazioni / Annulla (UC4)");
            System.out.println("3. Segnala un guasto/problema (UC5)");
            System.out.println("4. Esci / Logout");
            System.out.print("Scegli un'opzione: ");
            
            String opz = scanner.nextLine().trim();
            
            if (opz.equals("1")) {
                eseguiFlussoPrenotazioneUC2(sistema, scanner);
                
            } else if (opz.equals("2")) {
                GestorePrenotazioni gestoreUC4 = new GestorePrenotazioni();
                List<Prenotazione> miePrenotazioni = gestoreUC4.mostraMiePrenotazioni(utenteLoggato.getId());
                
                if (miePrenotazioni == null || miePrenotazioni.isEmpty()) {
                    System.out.println("\nNon hai ancora effettuato nessuna prenotazione.");
                } else {
                    System.out.println("\n--- ELENCO DELLE TUE PRENOTAZIONI ---");
                    for (Prenotazione p : miePrenotazioni) {
                        System.out.println("ID: [" + p.getId() + "] | " + p.getStruttura().getNome() + 
                                           " | Data: " + p.getData() + " | Stato: " + p.getStato());
                    }
                    
                    System.out.print("\nInserisci l'ID della prenotazione da ANNULLARE (oppure premi Invio per tornare indietro): ");
                    String idPrenotazioneDaAnnullare = scanner.nextLine().trim();
                    
                    if (!idPrenotazioneDaAnnullare.isEmpty()) {
                        System.out.println("Elaborazione annullamento in corso...");
                        String esito = gestoreUC4.annullaPrenotazione(idPrenotazioneDaAnnullare);
                        System.out.println(">>> ESITO: " + esito.toUpperCase() + " <<<");
                    }
                }
                
            } else if (opz.equals("3")) {
                System.out.println("\n--- INVIA SEGNALAZIONE GUASTO ---");
                
                System.out.print("Inserisci l'ID della struttura guasta (es. S1): ");
                String idStrutturaGuasta = scanner.nextLine().trim();
                
                System.out.print("Descrivi il problema (es. Rete bucata, doccia fredda): ");
                String descrizioneGuasto = scanner.nextLine().trim();
                
                try {
                    Segnalazione nuovaSeg = sistema.inviaSegnalazione(idStrutturaGuasta, descrizioneGuasto);
                    
                    System.out.println("\n Segnalazione inviata con successo!");
                    System.out.println("ID Ticket generato: [" + nuovaSeg.getId() + "]");
                } catch (IllegalArgumentException e) {
                    System.out.println("\n Errore durante l'invio: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("\n Si è verificato un errore imprevisto: " + e.getMessage());
                }
                
            } else if (opz.equals("4")) {
                continua = false;
                sistema.logout();
                System.out.println("Logout effettuato.");
            } else {
                System.out.println("Opzione non valida.");
            }
        }
    }

    private static void eseguiFlussoPrenotazioneUC2(YourSport sistema, Scanner scanner) {
        try {
            System.out.print("\n1. Tipologia (es. Piscina, Tennis - Invio per tutto): ");
            String tipologia = scanner.nextLine().trim();

            System.out.print("2. Caratteristiche (es. Doccia - Invio per nessuna): ");
            String carInput = scanner.nextLine().trim();
            List<String> caratteristiche = new ArrayList<>();
            if (!carInput.isEmpty()) {
                String[] carArray = carInput.split(",");
                for (String c : carArray) caratteristiche.add(c.trim());
            }

            System.out.print("3. Data (YYYY-MM-DD): ");
            LocalDate dataScelta = LocalDate.parse(scanner.nextLine());

            List<Struttura> risultati = sistema.cercaStruttura(tipologia, caratteristiche, dataScelta);
            
            System.out.println("\n--- RISULTATI RICERCA ---");
            for (Struttura s : risultati) {
                System.out.println(" -> ID: [" + s.getId() + "] " + s.getNome() + 
                                   " (" + s.getTipologia() + ") | Capienza Totale: " + s.getCapienza());
            }

            if (risultati.isEmpty()) {
                System.out.println("Nessuna struttura trovata con questi filtri o tutte fuori servizio.");
            } else {
                System.out.print("4. ID Struttura da prenotare: ");
                String id = scanner.nextLine();
                System.out.println("\n--- SPECIFICA ORARIO ---");
                System.out.print("5. Ora Inizio (HH:mm): ");
                LocalTime inizio = LocalTime.parse(scanner.nextLine());
                System.out.print("6. Ora Fine (HH:mm): ");
                LocalTime fine = LocalTime.parse(scanner.nextLine());
                System.out.print("7. Numero persone/posti: ");
                int posti = Integer.parseInt(scanner.nextLine());

                Prenotazione p = sistema.selezionaRisorsa(id, dataScelta, inizio, fine, posti);

                if (p != null) {
                    System.out.println("\n--- RIEPILOGO PRENOTAZIONE ---");
                    System.out.println("ID: " + p.getId() + " | Struttura: " + p.getStruttura().getNome() + " | Costo: " + p.getCostoTotale() + "€");
                    System.out.print("8. Vuoi CONFERMARE? (si/no): ");
                    if (scanner.nextLine().equalsIgnoreCase("si")) {
                        sistema.confermaPrenotazione();
                    } else {
                        System.out.println("Annullato.");
                    }
                } else {
                    System.out.println("!!! IMPOSSIBILE PROCEDERE: Risorsa non disponibile in quell'orario !!!");
                }
            }
        } catch (DateTimeParseException e) {
            System.out.println("ERRORE: Formato data o ora non valido!");
        } catch (Exception e) {
            System.out.println("ERRORE: " + e.getMessage());
        }
    }

    private static void gestisciAdmin(YourSport sistema, Scanner scanner) {
        // --- AUTENTICAZIONE ADMIN ---
        System.out.println("\n--- AUTENTICAZIONE ADMIN ---");
        System.out.print("Email Admin: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (!sistema.loginAdmin(email, password)) {
            System.out.println("ERRORE: Credenziali errate! Accesso negato.");
            return;
        }

        Admin amministratore = sistema.getAmministratore();
        System.out.println("\nLogin effettuato. Benvenuto " + amministratore.getNome() + " (ADMIN)!");

        boolean continuaAdmin = true;
        
        while (continuaAdmin) {
            System.out.println("\n==========================================");
            System.out.println("                PANNELLO ADMIN            ");
            System.out.println("==========================================");
            System.out.println("1. Gestione Tariffe Strutture (UC3)");
            System.out.println("2. Gestione Segnalazioni Guasti (UC6)"); 
            System.out.println("3. Sospendi Struttura per Disservizio (UC7)"); // <-- NUOVA OPZIONE
            System.out.println("4. Esci / Logout");
            System.out.print("Scegli un'opzione: ");
            
            String opz = scanner.nextLine().trim();
            
            if (opz.equals("1")) {
                // ==========================================
                // VECCHIO CODICE UC3: GESTIONE TARIFFE
                // ==========================================
                boolean continuaTariffe = true;
                while (continuaTariffe) {
                    System.out.println("\n--- CATALOGO STRUTTURE ---");
                    List<Struttura> catalogo = sistema.mostraCatalogo();
                    for (Struttura s : catalogo) {
                        System.out.println(" - ID: [" + s.getId() + "] " + s.getNome() + " (" + s.getTipologia() + ")");
                    }

                    System.out.print("\nInserisci l'ID della struttura da modificare (o 'esci' per tornare indietro): ");
                    String idTarget = scanner.nextLine().trim();
                    if (idTarget.equalsIgnoreCase("esci")) break;

                    String dettagli = sistema.mostraDettagliStruttura(idTarget);
                    System.out.println("\n--- Dettagli Attuali ---");
                    System.out.println(dettagli);

                    if (!dettagli.contains("Errore")) {
                        System.out.print("\nInserisci il nuovo Tipo di Tariffa (es. ORARIO o PERSONA): ");
                        String nuovoTipo = scanner.nextLine().trim();
                        System.out.print("Inserisci il nuovo Costo Base: ");
                        double nuovoCosto = -1;
                        try {
                            nuovoCosto = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        } catch (NumberFormatException e) {
                            System.out.println("Formato numero non valido.");
                        }

                        System.out.println("\nElaborazione richiesta...");
                        sistema.aggiornaTariffa(idTarget, nuovoTipo, nuovoCosto);
                    }

                    System.out.print("\nVuoi modificare un'altra tariffa? (si/no): ");
                    if (!scanner.nextLine().equalsIgnoreCase("si")) continuaTariffe = false;
                }

            } else if (opz.equals("2")) {
                // ==========================================
                // NUOVO CODICE UC6: GESTIONE SEGNALAZIONI
                // ==========================================
                GestoreSegnalazioni gestoreUC6 = new GestoreSegnalazioni();
                
                System.out.println("\n--- ELENCO SEGNALAZIONI GUASTI ---");
                List<Segnalazione> listaGuasti = gestoreUC6.mostraSegnalazioni();
                
                if (listaGuasti == null || listaGuasti.isEmpty()) {
                    System.out.println("Ottime notizie: Non ci sono guasti segnalati nel sistema!");
                } else {
                    for (Segnalazione seg : listaGuasti) {
                        String nomeStruttura = (seg.getStrutturaCoinvolta() != null) ? seg.getStrutturaCoinvolta().getNome() : "Sconosciuta";
                        System.out.println("ID: [" + seg.getId() + "] | Struttura: " + nomeStruttura + 
                                           " | Stato: [" + seg.getStato().toUpperCase() + "] | Problema: " + seg.getDescrizione());
                    }
                    
                    System.out.print("\nInserisci l'ID della segnalazione da aggiornare (o premi Invio per tornare indietro): ");
                    String idDaAggiornare = scanner.nextLine().trim();
                    
                    if (!idDaAggiornare.isEmpty()) {
                        System.out.print("Inserisci il NUOVO STATO (es. In lavorazione, Risolta, Rifiutata): ");
                        String nuovoStato = scanner.nextLine().trim();
                        
                        System.out.println("Elaborazione aggiornamento in corso...");
                        boolean successo = gestoreUC6.aggiornaStatoSegnalazione(idDaAggiornare, nuovoStato);
                        
                        if (successo) {
                            System.out.println("Stato aggiornato con successo a: " + nuovoStato);
                        } else {
                            System.out.println("Errore: Segnalazione non trovata. Controlla l'ID.");
                        }
                    }
                }
                
            } else if (opz.equals("3")) {
                // ==========================================
                // NUOVO CODICE UC7: GESTIONE STATO STRUTTURA
                // ==========================================
                System.out.println("\n--- GESTIONE STATO STRUTTURE (Sospendi/Riattiva) ---");
                List<Struttura> catalogo = sistema.mostraCatalogo();
                
                // Mostro le strutture con il loro stato attuale
                for (Struttura s : catalogo) {
                    String statoStruttura = s.isOperativo() ? "OPERATIVA" : "FUORI SERVIZIO";
                    System.out.println(" - ID: [" + s.getId() + "] " + s.getNome() + " (" + statoStruttura + ")");
                }

                System.out.print("\nInserisci l'ID della struttura da modificare (o premi Invio per annullare): ");
                String idTarget = scanner.nextLine().trim();

                if (!idTarget.isEmpty()) {
                    Struttura sTarget = sistema.getStruttura(idTarget);
                    
                    if (sTarget != null) {
                        GestoreStrutture gestoreUC7 = new GestoreStrutture();
                        
                        // INTERRUTTORE INTELLIGENTE
                        if (sTarget.isOperativo()) {
                            // Se è operativa, la sospendiamo
                            System.out.println("Elaborazione sospensione e annullamento prenotazioni in corso...");
                            gestoreUC7.sospendiStruttura(idTarget);
                            System.out.println(">>> SUCCESSO: La struttura è stata chiusa. Gli sportivi sono stati notificati. <<<");
                        } else {
                            // Se è fuori servizio, la riattiviamo
                            System.out.println("Elaborazione riattivazione in corso...");
                            gestoreUC7.riattivaStruttura(idTarget);
                            System.out.println(">>> SUCCESSO: La struttura è di nuovo OPERATIVA e pronta per essere prenotata! <<<");
                        }
                    } else {
                        System.out.println(">>> ERRORE: Struttura non trovata. Controlla l'ID inserito. <<<");
                    }
                    
                  }
                } else if (opz.equals("4")) {
                continuaAdmin = false;
                sistema.logout(); 
                System.out.println("Logout Admin effettuato. Ritorno al menu principale.");
            } else {
                System.out.println("Opzione non valida.");
            }
        }
    }
}
