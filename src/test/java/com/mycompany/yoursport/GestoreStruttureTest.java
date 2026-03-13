/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author pietroalberio
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GestoreStruttureTest {

    private YourSport sistema;
    private GestoreStrutture gestore;
    private Prenotazione prenotazioneFutura;

    // =========================================================
    // ARRANGE GLOBALE: Preparazione dell'ambiente prima di ogni test
    // =========================================================
    @BeforeEach
    public void setUp() {
        sistema = YourSport.getInstance();
        sistema.resetSistemaPerTest(); 
        
        // Istanziamo il Gestore che vogliamo testare
        gestore = new GestoreStrutture();

        // --- PREPARAZIONE DATI PER IL TEST ---
        // 1. Logghiamo lo sportivo di test (Mario Rossi, ID: U1)
        sistema.login("mario@email.it", "pw");

        // 2. Creiamo una data futura per la prenotazione (domani)
        LocalDate domani = LocalDate.now().plusDays(1);
        LocalTime inizio = LocalTime.of(10, 0);
        LocalTime fine = LocalTime.of(11, 0);

        // 3. Facciamo prenotare il "Campo A" (ID: S1) allo sportivo
        prenotazioneFutura = sistema.selezionaRisorsa("S1", domani, inizio, fine, 1);
        sistema.confermaPrenotazione();
        
        // Logout per pulizia
        sistema.logout();
    }

    @AfterEach
    public void tearDown() {
        sistema.resetSistemaPerTest(); // Pulizia finale
    }

    // =========================================================
    // TEST 1: Sospensione Riuscita (Scenario Principale UC7)
    // =========================================================
    @Test
    public void testSospendiStruttura_Successo() {
        System.out.println("Test UC7: Sospensione Struttura (Cammino Base)");

        // --- ACT ---
        boolean esito = gestore.sospendiStruttura("S1");

        // --- ASSERT ---
        // 1. Il metodo deve restituire true
        assertTrue(esito, "Il metodo deve restituire TRUE per una struttura esistente");

        // 2. La struttura deve essere fisicamente disattivata
        Struttura s1 = sistema.getStruttura("S1");
        assertFalse(s1.isOperativo(), "Lo stato della struttura deve essere cambiato a FALSE");

        // 3. Il loop ha funzionato? La prenotazione futura deve essere annullata
        assertEquals("Annullata", prenotazioneFutura.getStato(), "La prenotazione futura deve essere stata forzatamente annullata");

        // 4. La notifica è stata creata? (Il messaggio 1.6 e 1.7 dell'SD)
        List<Notifica> notifiche = sistema.getArchivioNotifiche();
        assertFalse(notifiche.isEmpty(), "Deve essere stata generata almeno una notifica");
        assertEquals("U1", notifiche.get(0).getDestinatario().getId(), "La notifica deve essere destinata all'utente U1 che aveva prenotato");
    }

    // =========================================================
    // TEST 2: Sospensione Fallita (Blocco OPT dell'SD)
    // =========================================================
    @Test
    public void testSospendiStruttura_IdInesistente() {
        System.out.println("Test UC7: Sospensione Struttura (Fallimento per ID errato)");

        // --- ACT ---
        boolean esito = gestore.sospendiStruttura("ID_FALSO_999");

        // --- ASSERT ---
        assertFalse(esito, "Il metodo deve restituire FALSE se viene passato un ID inesistente");
        
        // Verifica che "S1" sia ancora accesa (nessun danno collaterale)
        assertTrue(sistema.getStruttura("S1").isOperativo(), "Le strutture esistenti non devono essere modificate");
    }

    // =========================================================
    // TEST 3: Riattivazione Riuscita (Scenario Alternativo UC7)
    // =========================================================
    @Test
    public void testRiattivaStruttura_Successo() {
        System.out.println("Test UC7: Riattivazione Struttura");

        // --- ARRANGE ---
        // Forziamo lo spegnimento della struttura "S2" per testarne la riaccensione
        Struttura s2 = sistema.getStruttura("S2");
        s2.setOperativo(false);

        // --- ACT ---
        boolean esito = gestore.riattivaStruttura("S2");

        // --- ASSERT ---
        assertTrue(esito, "La riattivazione deve restituire TRUE");
        assertTrue(s2.isOperativo(), "L'interruttore della struttura deve essere tornato a TRUE");
    }

    // =========================================================
    // TEST 4: Riattivazione Fallita (Blocco OPT del nuovo SD)
    // =========================================================
    @Test
    public void testRiattivaStruttura_IdInesistente() {
        System.out.println("Test UC7: Riattivazione Struttura (Fallimento per ID errato)");

        // --- ACT ---
        boolean esito = gestore.riattivaStruttura("STRUTTURA_GHOST");

        // --- ASSERT ---
        assertFalse(esito, "La riattivazione deve restituire FALSE se l'ID non esiste");
    }
}