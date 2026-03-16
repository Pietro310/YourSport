/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author david
 */
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GestoreNotificheTest {

    private YourSport sistema;
    private GestoreNotifiche gestore;

    // =========================================================
    // ARRANGE GLOBALE: Preparazione prima di ogni test
    // =========================================================
    @BeforeEach
    public void setUp() {
        sistema = YourSport.getInstance();
        sistema.resetSistemaPerTest(); // Carica l'utente base "mario@email.it" (ID: U1)
        gestore = new GestoreNotifiche();

        // 1. Recupero Mario (U1)
        Sportivo mario = sistema.getSportivo("U1");

        // 2. Creo un secondo utente (Luigi) per testare il filtro
        sistema.registrazioneSportivo("Luigi", "Verdi", "luigi@email.it", "pw");
        Sportivo luigi = sistema.getSportivo("U2"); // Il sistema gli assegnerà U2

        // 3. Inserisco finte notifiche nel sistema
        // Due per Mario
        sistema.aggiungiNotifica(new Notifica("Messaggio 1 per Mario", mario));
        sistema.aggiungiNotifica(new Notifica("Messaggio 2 per Mario", mario));
        // una per Luigi
        sistema.aggiungiNotifica(new Notifica("Messaggio segreto per Luigi", luigi));
    }

    @AfterEach
    public void tearDown() {
        sistema.logout();
        sistema.resetSistemaPerTest(); // Pulisco tutto alla fine
    }

    // =========================================================
    // TEST 1: L'utente ha notifiche (Filtro funzionante)
    // =========================================================
    @Test
    public void testMostraNotifiche_ConNotifiche() {
        System.out.println("Test UC8: Mostra Notifiche (Successo e Filtro)");

        // --- ARRANGE ---
        sistema.login("mario@email.it", "pw"); // Loggo Mario

        // --- ACT ---
        List<Notifica> risultato = gestore.mostraNotifiche();

        // --- ASSERT ---
        assertNotNull(risultato, "La lista non deve essere null");
        assertEquals(2, risultato.size(), "Mario deve vedere ESATTAMENTE 2 notifiche (ignorando quella di Luigi)");
        assertEquals("Messaggio 1 per Mario", risultato.get(0).getMessaggio(), "Il testo della prima notifica deve coincidere");
    }

    // =========================================================
    // TEST 2: L'utente non ha notifiche
    // =========================================================
    @Test
    public void testMostraNotifiche_SenzaNotifiche() {
        System.out.println("Test UC8: Mostra Notifiche (Bacheca Vuota)");

        // --- ARRANGE ---
        // Creo un terzo utente (Anna) a cui non mando nessuna notifica
        sistema.registrazioneSportivo("Anna", "Neri", "anna@email.it", "pw");
        sistema.login("anna@email.it", "pw"); // Loggo Anna

        // --- ACT ---
        List<Notifica> risultato = gestore.mostraNotifiche();

        // --- ASSERT ---
        assertNotNull(risultato, "La lista non deve essere null, ma semplicemente vuota");
        assertTrue(risultato.isEmpty(), "La bacheca di Anna deve risultare vuota (size = 0)");
    }

    // =========================================================
    // TEST 3: Nessun utente loggato (Sicurezza)
    // =========================================================
    @Test
    public void testMostraNotifiche_UtenteNonLoggato() {
        System.out.println("Test UC8: Mostra Notifiche (Non Autorizzato)");

        // --- ARRANGE ---
        sistema.logout(); // Mi assicuro che nessuno sia loggato

        // --- ACT ---
        List<Notifica> risultato = gestore.mostraNotifiche();

        // --- ASSERT ---
        assertNull(risultato, "Se l'utente non è loggato, il gestore deve bloccare la richiesta e restituire null");
    }
}
