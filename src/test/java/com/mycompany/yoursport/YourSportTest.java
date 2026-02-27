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
        sistema.resetSistemaPerTest(); // Metodo che svuota le prenotazioni e abilita il flag Test
        sistema.login("mario@email.it", "pw"); 
    }
    
    // Eseguito DOPO ogni test: Pulizia finale
    @AfterEach
    public void tearDown() {
        sistema.resetSistemaPerTest();
    }

    // ==========================================================
    // NUOVI TEST - ITERAZIONE 3 (UC1: Registrazione Sportivo)
    // ==========================================================

    @Test
    public void testRegistrazioneSportivo_Successo() {
        System.out.println("Test UC1: Registrazione Nuovo Utente (Successo)");

        // 1. Azione: Registriamo un nuovo utente con un'email libera
        String esito = sistema.registrazioneSportivo("Giulia", "Bianchi", "giulia@email.it", "pass123");

        // 2. Verifica del messaggio di ritorno (dal Diagramma di Sequenza)
        assertEquals("utente registrato", esito, "Il sistema deve confermare la registrazione");

        // 3. Verifica strutturale: Proviamo a loggarci per vedere se esiste davvero in memoria!
        boolean loginRiuscito = sistema.login("giulia@email.it", "pass123");
        assertTrue(loginRiuscito, "Deve essere possibile effettuare il login con l'utente appena creato");
    }

    @Test
    public void testRegistrazioneSportivo_EmailGiaInUso() {
        System.out.println("Test UC1: Registrazione bloccata per Email Duplicata");

        // 1. Azione: Tentiamo di usare "mario@email.it" (già caricata dal metodo inizializzaDatiTest)
        String esito = sistema.registrazioneSportivo("Luigi", "Verdi", "mario@email.it", "altraPass");

        // 2. Verifica del blocco ALT (dal Diagramma di Sequenza)
        assertEquals("email già in uso", esito, "Il sistema deve impedire la registrazione se l'email esiste già");
    }


    // ==========================================================
    // ITERAZIONE 1 (UC2: Prenotazione)
    // ==========================================================
    
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


    // ==========================================================
    // ITERAZIONE 2 (UC3: Gestione Costi)
    // ==========================================================
  
    @Test
    public void testMostraCatalogo() {
        // Esecuzione
        List<Struttura> catalogo = sistema.mostraCatalogo();

        // Verifica: Il catalogo non deve essere nullo e deve contenere le strutture di test
        assertNotNull(catalogo, "Il catalogo non dovrebbe essere nullo");
        assertTrue(catalogo.size() >= 2, "Il catalogo dovrebbe contenere almeno le 2 strutture di test");
    }

    @Test
    public void testMostraDettagliStruttura_Esistente() {
        // Esecuzione
        String dettagli = sistema.mostraDettagliStruttura("S1");

        // Verifica: La stringa restituita non deve essere un messaggio d'errore
        assertFalse(dettagli.contains("Errore"), "Non dovrebbe dare errore per una struttura esistente");
        assertTrue(dettagli.contains("Campo A"), "I dettagli dovrebbero contenere il nome della struttura");
        assertTrue(dettagli.contains("20.0"), "I dettagli dovrebbero contenere il costo base attuale");
    }

    @Test
    public void testMostraDettagliStruttura_Inesistente() {
        // Esecuzione
        String dettagli = sistema.mostraDettagliStruttura("S999");

        // Verifica: Deve restituire il messaggio di errore previsto nel codice
        assertTrue(dettagli.contains("Errore: Struttura inesistente"), "Dovrebbe dare errore per un ID inesistente");
    }

    @Test
    public void testAggiornaTariffa_Successo() {
        // Setup: recuperiamo S1 e verifichiamo lo stato iniziale
        Struttura s1 = sistema.getStruttura("S1");
        assertEquals(20.0, s1.getTariffa());
        assertEquals("ORARIO", s1.getTipoTariffa());

        // Esecuzione: l'Admin imposta a PERSONA e cambia il prezzo a 25.0
        sistema.aggiornaTariffa("S1", "PERSONA", 25.0);

        // Verifica: I valori devono essersi aggiornati (Post-condizioni del Contratto Operazione soddisfatte)
        assertEquals(25.0, s1.getTariffa(), "Il costo base dovrebbe essersi aggiornato a 25.0");
        assertEquals("PERSONA", s1.getTipoTariffa(), "Il tipo tariffa dovrebbe essersi aggiornato a PERSONA");
        
        // Teardown manuale per non sporcare i test successivi (visto che è un Singleton)
        sistema.aggiornaTariffa("S1", "ORARIO", 20.0);
    }

    @Test
    public void testAggiornaTariffa_ErroreCostoNegativo_Estensione3a() {
        // Setup
        Struttura s1 = sistema.getStruttura("S1");
        double costoIniziale = s1.getTariffa();

        // Esecuzione: l'Admin tenta di inserire un costo negativo
        sistema.aggiornaTariffa("S1", "ORARIO", -5.0);

        // Verifica: Il costo NON deve essere cambiato (l'aggiornamento è stato bloccato)
        assertEquals(costoIniziale, s1.getTariffa(), "Il costo base NON deve cambiare se si inserisce un valore negativo");
    }

    @Test
    public void testAggiornaTariffa_ErroreTipoInvalido() {
        // Setup
        Struttura s1 = sistema.getStruttura("S1");
        String tipoIniziale = s1.getTipoTariffa();

        // Esecuzione: l'Admin tenta di inserire un tipo di tariffa non contemplato
        sistema.aggiornaTariffa("S1", "TESTVAL", 30.0);

        // Verifica: Il tipo di tariffa NON deve essere cambiato
        assertEquals(tipoIniziale, s1.getTipoTariffa(), "Il tipo tariffa NON deve cambiare se si inserisce un valore non valido");
        // Verifica secondaria: anche il prezzo non deve essere cambiato (Fail Fast)
        assertEquals(20.0, s1.getTariffa(), "Il costo base non deve aggiornarsi se il tipo tariffa era invalido");
    }

}