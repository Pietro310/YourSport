/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.yoursport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author david
 */
public class YourSportTest {
    
    private YourSport sistema;
    
    // Eseguito PRIMA di ogni test: Pulisce tutto e prepara il terreno
    @BeforeEach
    public void setUp() {
        sistema = YourSport.getInstance();
        sistema.resetSistemaPerTest(); // Metodo che svuota le prenotazioni
        sistema.login("U1"); 
    }
    
    // Eseguito DOPO ogni test: Pulizia finale
    @AfterEach
    public void tearDown() {
        sistema.resetSistemaPerTest();
    }

    // --- TEST 1: LA RICERCA (Filtro Catalogo) ---
    @Test
    public void testCercaStruttura() {
        System.out.println("Test: Filtro Ricerca");
        
        // Cerchiamo "Tennis"
        List<String> caratteristiche = new ArrayList<>(); // Nessuna caratteristica specifica
        List<Struttura> risultati = sistema.cercaStruttura("Tennis", caratteristiche, LocalDate.now());
        
        // Deve trovare S1 (Tennis) ma NON S2 (Piscina)
        assertEquals(1, risultati.size(), "Dovrebbe trovare 1 sola struttura");
        assertEquals("Tennis", risultati.get(0).getTipologia());
    }

    // --- TEST 2: PRENOTAZIONE TENNIS (Logica Esclusiva) ---
    @Test
    public void testPrenotazioneTennis_Conflitto() {
        System.out.println("Test: Conflitto Tennis (Orario Esclusivo)");
        
        LocalDate data = LocalDate.now().plusDays(1);
        LocalTime inizio = LocalTime.of(10, 0);
        LocalTime fine = LocalTime.of(11, 0);
        
        // 1. Mario prenota il campo -> SUCCESSO
        Prenotazione p1 = sistema.selezionaRisorsa("S1", data, inizio, fine, 1); // S1 è Tennis
        assertNotNull(p1, "La prima prenotazione deve riuscire");
        sistema.confermaPrenotazione();
        
        // 2. Luigi prova a prenotare STESSA ORA -> FALLIMENTO
        // Anche se chiede 1 posto e la capienza è 1, il campo è esclusivo ("ORARIO")
        Prenotazione p2 = sistema.selezionaRisorsa("S1", data, inizio, fine, 1);
        
        assertNull(p2, "La seconda prenotazione deve fallire perché il campo è occupato");
    }

    // --- TEST 3: PRENOTAZIONE PISCINA (Logica Cumulativa) ---
    @Test
    public void testPrenotazionePiscina_Capienza() {
        System.out.println("Test: Capienza Piscina (Tariffa a Persona)");
        
        LocalDate data = LocalDate.now().plusDays(2);
        LocalTime inizio = LocalTime.of(15, 0);
        LocalTime fine = LocalTime.of(16, 0);
        String idPiscina = "S2"; // Piscina ha capienza 20
        
        // 1. Corso di Nuoto prenota 15 posti -> SUCCESSO
        Prenotazione p1 = sistema.selezionaRisorsa(idPiscina, data, inizio, fine, 15);
        assertNotNull(p1);
        sistema.confermaPrenotazione();
        
        // 2. Famiglia prenota 4 posti (Totale 19/20) -> SUCCESSO
        Prenotazione p2 = sistema.selezionaRisorsa(idPiscina, data, inizio, fine, 4);
        assertNotNull(p2);
        sistema.confermaPrenotazione();
        
        // 3. Coppia prova a prenotare 2 posti (Richiesti 2, Disponibile 1) -> FALLIMENTO
        Prenotazione p3 = sistema.selezionaRisorsa(idPiscina, data, inizio, fine, 2);
        assertNull(p3, "Dovrebbe fallire: 19 occupati + 2 richiesti > 20 capienza");
    }
    
    // --- TEST 4: PRENOTAZIONE PISCINA (Orari Diversi) ---
    @Test
    public void testPiscina_OrariDiversi() {
        System.out.println("Test: Piscina piena la mattina ma libera il pomeriggio");
        
        LocalDate data = LocalDate.now().plusDays(3);
        String idPiscina = "S2";

        // 1. Mattina: Riempiamo la piscina (20 posti) dalle 10 alle 11
        sistema.selezionaRisorsa(idPiscina, data, LocalTime.of(10, 0), LocalTime.of(11, 0), 20);
        sistema.confermaPrenotazione();
        
        // 2. Pomeriggio: Proviamo a prenotare dalle 15 alle 16 -> SUCCESSO
        // Il sistema deve capire che alle 15 la piscina è vuota
        Prenotazione pPome = sistema.selezionaRisorsa(idPiscina, data, LocalTime.of(15, 0), LocalTime.of(16, 0), 5);
        
        assertNotNull(pPome, "Dovrebbe permettere prenotazione in orario diverso anche se mattina era piena");
    }
    
// --- TEST 5: FILTRO CARATTERISTICHE ---
    @Test
   public void testRicercaPerCaratteristica() {
        System.out.println("Test: Ricerca con filtro Caratteristiche (es. Doccia)");
        
        List<String> caratteristicheRichieste = new ArrayList<>();
        caratteristicheRichieste.add("Doccia"); // La Piscina ha la doccia, il Tennis no
        
        System.out.println("   > Sto cercando strutture con: " + caratteristicheRichieste);
        
        // Cerco qualsiasi cosa abbia la doccia (Tipologia null o vuota per dire "tutti")
        List<Struttura> risultati = sistema.cercaStruttura("", caratteristicheRichieste, LocalDate.now());
        
        System.out.println("   > Risultati trovati: " + risultati.size());
        
        boolean trovatoPiscina = false;
        boolean trovatoTennis = false;
        
        for (Struttura s : risultati) {
            System.out.println("     - Trovato: " + s.getNome() + " (" + s.getTipologia() + ")");
            if (s.getNome().contains("Piscina")) trovatoPiscina = true;
            if (s.getNome().contains("Tennis")) trovatoTennis = true;
        }
        
        assertTrue(trovatoPiscina, "Dovrebbe trovare la piscina che ha la doccia");
        assertFalse(trovatoTennis, "Non dovrebbe trovare il tennis che non ha la doccia");
    }

    // --- TEST 6: RICERCA A VUOTO ---
    @Test
public void testRicercaNessunRisultato() {
        System.out.println("---------------------------------------------------");
        System.out.println("TEST 6: Ricerca Sport Inesistente (es. 'Golf')");
        
        List<String> caratteristiche = new ArrayList<>();
        
        // 1. Azione: Cerchiamo uno sport che non esiste
        System.out.println("   > Sto chiedendo al sistema: 'Dammi campi da Golf'...");
        List<Struttura> risultati = sistema.cercaStruttura("Golf", caratteristiche, LocalDate.now());
        
        // 2. Verifica Visiva (Debug)
        if (risultati == null) {
            System.out.println("   > [ERRORE GRAVE] Il sistema ha restituito NULL (non deve succedere)!");
        } else {
            System.out.println("   > [OK] Il sistema ha restituito una lista (non è null).");
            System.out.println("   > [INFO] Numero elementi trovati: " + risultati.size());
            
            if (risultati.isEmpty()) {
                System.out.println("   > [SUCCESS] Perfetto! La lista è vuota come previsto.");
            } else {
                System.out.println("   > [ERRORE] Attenzione! Ha trovato qualcosa che non doveva esserci.");
            }
        }
        
        // 3. Verifiche Formali (JUnit Assertions)
        assertNotNull(risultati, "La lista non deve essere null (deve essere vuota ma esistente)");
        assertTrue(risultati.isEmpty(), "La lista dovrebbe essere vuota per sport inesistenti");
        System.out.println("---------------------------------------------------");
    }
}