/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author anton
 */


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author anton
 */

/*
 * Main.java - Test Driver per UC2
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        YourSport sistema = YourSport.getInstance();
        Scanner scanner = new Scanner(System.in);

        boolean esci = false;

        while (!esci) {
            System.out.println("\n==========================================");
            System.out.println("       BENVENUTO IN YOURSPORT             ");
            System.out.println("==========================================");
            System.out.println("1. Accedi come Sportivo (Test UC2 - Prenotazione)");
            System.out.println("2. Accedi come Admin    (Test UC3 - Gestione Costi)");
            System.out.println("3. Esci dal sistema");
            System.out.print("Scegli un'opzione: ");
            
            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    gestisciSportivo(sistema, scanner);
                    break;
                case "2":
                    gestisciAdmin(sistema, scanner);
                    break;
                case "3":
                    esci = true;
                    System.out.println("Uscita dal sistema...");
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        }
        scanner.close();
    }

    // ==========================================================
    // FLUSSO UC2: PRENOTAZIONE STRUTTURA (Codice Originale)
    // ==========================================================
    private static void gestisciSportivo(YourSport sistema, Scanner scanner) {
        // Pre-condizione: Login fittizio
        sistema.login("U1"); 
        
        boolean continua = true;
        while (continua) {
            try {
                System.out.println("\n==========================================");
                System.out.println("      NUOVO FLUSSO DI PRENOTAZIONE (UC2)  ");
                System.out.println("==========================================");
                
                // --- PASSO 1: INSERISCI TIPOLOGIA ---
                System.out.print("1. Tipologia (es. Piscina, Tennis - Invio per tutto): ");
                String tipologia = scanner.nextLine().trim();

                // --- PASSO 2: INSERISCI CARATTERISTICHE ---
                System.out.print("2. Caratteristiche (es. Doccia - Invio per nessuna): ");
                String carInput = scanner.nextLine().trim();
                List<String> caratteristiche = new ArrayList<>();
                if (!carInput.isEmpty()) {
                    String[] carArray = carInput.split(",");
                    for (String c : carArray) caratteristiche.add(c.trim());
                }

                // --- PASSO 3: INSERISCI DATA ---
                System.out.print("3. Data (YYYY-MM-DD): ");
                String dataStr = scanner.nextLine();
                LocalDate dataScelta = LocalDate.parse(dataStr);

                // --- PASSO 4: SISTEMA MOSTRA DISPONIBILITÀ ---
                List<Struttura> risultati = sistema.cercaStruttura(tipologia, caratteristiche, dataScelta);
                
                System.out.println("\n--- RISULTATI RICERCA ---");
                for (Struttura s : risultati) {
                    System.out.println(" -> ID: [" + s.getId() + "] " + s.getNome() + 
                                       " (" + s.getTipologia() + ") | Capienza Totale: " + s.getCapienza());
                }

                if (risultati.isEmpty()) {
                    System.out.println("Nessuna struttura trovata con questi filtri.");
                } else {
                    // --- PASSO 5: INSERISCI FASCIA ORARIA ---
                    System.out.println("\n--- SPECIFICA ORARIO ---");
                    
                    System.out.print("4. ID Struttura da prenotare: ");
                    String id = scanner.nextLine();
                    
                    System.out.print("5. Ora Inizio (HH:mm): ");
                    LocalTime inizio = LocalTime.parse(scanner.nextLine());
                    
                    System.out.print("6. Ora Fine (HH:mm): ");
                    LocalTime fine = LocalTime.parse(scanner.nextLine());
                    
                    // --- PASSO 7: SELEZIONA POSTI ---
                    System.out.print("7. Numero persone/posti: ");
                    int posti = Integer.parseInt(scanner.nextLine());

                    // --- PASSO 6 (Backend): VERIFICA DISPONIBILITÀ REALE ---
                    Prenotazione p = sistema.selezionaRisorsa(id, dataScelta, inizio, fine, posti);

                    if (p != null) {
                        System.out.println("\n--- RIEPILOGO PRENOTAZIONE ---");
                        System.out.println(p); 
                        
                        // --- PASSO 8: CONFERMA ---
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

            System.out.print("\nVuoi fare un'altra prenotazione? (si/no): ");
            if (!scanner.nextLine().equalsIgnoreCase("si")) continua = false;
        }
    }

    // ==========================================================
    // FLUSSO UC3: GESTIONE COSTI (Nuovo Codice per l'Admin)
    // ==========================================================
    private static void gestisciAdmin(YourSport sistema, Scanner scanner) {
        System.out.println("\n==========================================");
        System.out.println("     PANNELLO ADMIN - GESTIONE TARIFFE    ");
        System.out.println("==========================================");
        
        boolean continuaAdmin = true;
        
        // Loop del Sequence Diagram
        while (continuaAdmin) {
            // Operazione 1: mostraCatalogo
            System.out.println("\n--- CATALOGO STRUTTURE ---");
            List<Struttura> catalogo = sistema.mostraCatalogo();
            for (Struttura s : catalogo) {
                System.out.println(" - ID: [" + s.getId() + "] " + s.getNome() + " (" + s.getTipologia() + ")");
            }

            System.out.print("\nInserisci l'ID della struttura da modificare (o 'esci' per tornare al menu): ");
            String idTarget = scanner.nextLine().trim();
            
            if (idTarget.equalsIgnoreCase("esci")) {
                break;
            }

            // Operazione 2: mostraDettagliStruttura
            String dettagli = sistema.mostraDettagliStruttura(idTarget);
            System.out.println("\n--- Dettagli Attuali ---");
            System.out.println(dettagli);

            // Se la struttura esiste, procedo con l'aggiornamento
            if (!dettagli.contains("Errore")) {
                System.out.print("\nInserisci il nuovo Tipo di Tariffa (es. ORARIO o PERSONA): ");
                String nuovoTipo = scanner.nextLine().trim();

                System.out.print("Inserisci il nuovo Costo Base (es. per provare l'errore inserisci -5): ");
                double nuovoCosto = -1;
                try {
                    // Gestisce sia il punto che la virgola per i decimali inseriti da tastiera
                    nuovoCosto = Double.parseDouble(scanner.nextLine().replace(",", "."));
                } catch (NumberFormatException e) {
                    System.out.println("Formato numero non valido. Il sistema tenterà con un valore negativo per testare i controlli.");
                }

                // Operazione 3: aggiornaTariffa (Qui dentro avviene il controllo costoBase >= 0)
                System.out.println("\nElaborazione richiesta...");
                sistema.aggiornaTariffa(idTarget, nuovoTipo, nuovoCosto);
            }

            System.out.print("\nVuoi modificare un'altra tariffa? (si/no): ");
            if (!scanner.nextLine().equalsIgnoreCase("si")) {
                continuaAdmin = false;
            }
        }
    }
}