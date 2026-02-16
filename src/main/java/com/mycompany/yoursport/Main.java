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
        // 1. Inizializzazione Sistema
        YourSport sistema = YourSport.getInstance();
        Scanner scanner = new Scanner(System.in);

        // 2. Login
        System.out.println("=== BENVENUTO IN YOURSPORT ===");
        System.out.print("Login (premi Invio per U1 default): ");
        String user = scanner.nextLine();
        if(user.isEmpty()) user = "U1";
        sistema.login(user); 

        boolean continua = true;
        while (continua) {
            try {
                System.out.println("\n----------------------------------");
                System.out.println("=== NUOVA PRENOTAZIONE ===");
                
               // 1. Input Tipologia (Reso opzionale)
                 System.out.print("Cosa vuoi giocare? (Calcetto, Tennis... o premi Invio per tutto): ");
                 String tipologia = scanner.nextLine().trim(); // .trim() toglie spazi vuoti

                 // 2. Input Caratteristiche (NUOVO)
                 System.out.print("Caratteristiche richieste (es. Doccia,Luci... o premi Invio per nessuna): ");
                 String carInput = scanner.nextLine().trim();

                 List<String> caratteristiche = new ArrayList<>();
                 if (!carInput.isEmpty()) {
                // Trasforma la stringa "Doccia, Luci" in una Lista ["Doccia", "Luci"]
                String[] carArray = carInput.split(",");
                for (String c : carArray) {
                caratteristiche.add(c.trim()); // Aggiunge pulendo gli spazi
    }
}
                
                // Richiesta DATA (Modifica richiesta)
                System.out.print("Inserisci la data (formato YYYY-MM-DD, es. 2026-02-20): ");
                String dataStr = scanner.nextLine();
                LocalDate dataScelta = LocalDate.parse(dataStr); // Converte la stringa in Data

                // --- RICERCA (UC2) ---
                
                List<Struttura> risultati = sistema.cercaStruttura(tipologia, caratteristiche, dataScelta);
                
                System.out.println("\nStrutture disponibili per il " + dataScelta + ":");
                for (Struttura s : risultati) {
                    System.out.println(" -> ID: " + s.getId() + " | " + s.getNome() + 
                                       " | Max Posti: " + s.getCapienza() + 
                                       " | Tariffa: " + s.getTariffa() + " euro");
                }

                if (risultati.isEmpty()) {
                    System.out.println("Nessuna struttura trovata per i criteri inseriti.");
                } else {
                    // --- SELEZIONE RISORSA ---
                    System.out.println("\n--- COMPILA LA PRENOTAZIONE ---");
                    System.out.print("Inserisci ID Struttura da prenotare: ");
                    String id = scanner.nextLine();
                    
                    System.out.print("Ora Inizio (HH:mm, es. 18:00): ");
                    LocalTime inizio = LocalTime.parse(scanner.nextLine());
                    
                    System.out.print("Ora Fine (HH:mm, es. 19:30): ");
                    LocalTime fine = LocalTime.parse(scanner.nextLine());
                    
                    System.out.print("Numero persone/posti: ");
                    int posti = Integer.parseInt(scanner.nextLine());

                    // Chiamata al sistema usando la data scelta
                    Prenotazione p = sistema.selezionaRisorsa(id, dataScelta, inizio, fine, posti);

                    if (p != null) {
                        System.out.println("\nRIEPILOGO COSTO: " + p); // Chiama il toString di Prenotazione
                        
                        System.out.print("Vuoi CONFERMARE la prenotazione? (si/no): ");
                        if (scanner.nextLine().equalsIgnoreCase("si")) {
                            sistema.confermaPrenotazione();
                        } else {
                            System.out.println("Prenotazione annullata.");
                        }
                    }
                }

            } catch (DateTimeParseException e) {
                System.out.println("ERRORE FORMATO DATA/ORA! Usa il formato corretto (YYYY-MM-DD o HH:mm).");
            } catch (Exception e) {
                System.out.println("ERRORE GENERICO: " + e.getMessage());
            }

            System.out.print("\nVuoi effettuare un'altra ricerca? (si/no): ");
            if (!scanner.nextLine().equalsIgnoreCase("si")) {
                continua = false;
            }
        }
        System.out.println("Arrivederci!");
    }
}