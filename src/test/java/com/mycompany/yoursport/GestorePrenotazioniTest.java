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

public class GestorePrenotazioniTest {
    
    private YourSport sistema;
    private GestorePrenotazioni gestore;
    
    // Preparazione prima di ogni test
    @BeforeEach
    public void setUp() {
        // Recuperiamo il Singleton globale perché ci serve per caricare le prenotazioni finte
        sistema = YourSport.getInstance();
        sistema.resetSistemaPerTest(); 
        sistema.login("mario@email.it", "pw"); 
        
        // Inizializziamo il Controller specifico che vogliamo testare
        gestore = new GestorePrenotazioni();
    }
    
    @AfterEach
    public void tearDown() {
        sistema.resetSistemaPerTest();
    }

    // ==========================================================
    // TEST UC4: Mostra e Annulla Prenotazioni
    // ==========================================================

    @Test
    public void testMostraMiePrenotazioni() {
        System.out.println("Test UC4: Mostra Mie Prenotazioni");
        
        // 1. Setup: Creiamo una prenotazione valida
        LocalDate domani = LocalDate.now().plusDays(1);
        Prenotazione p = sistema.selezionaRisorsa("S1", domani, LocalTime.of(10, 0), LocalTime.of(11, 0), 1);
        sistema.confermaPrenotazione();
        
        // 2. Esecuzione
        List<Prenotazione> lista = gestore.mostraMiePrenotazioni("U1");
        
        // 3. Verifica
        assertNotNull(lista, "La lista non deve essere null");
        assertFalse(lista.isEmpty(), "La lista deve contenere la prenotazione appena creata");
        assertEquals(p.getId(), lista.get(0).getId(), "L'ID della prenotazione deve combaciare con quello generato");
    }

    @Test
    public void testAnnullaPrenotazione_Successo() {
        System.out.println("Test UC4: Annulla Prenotazione (Data Futura)");
        
        // 1. Setup: Creiamo una prenotazione per una data futura
        LocalDate traDueGiorni = LocalDate.now().plusDays(2);
        Prenotazione p = sistema.selezionaRisorsa("S2", traDueGiorni, LocalTime.of(15, 0), LocalTime.of(16, 0), 2);
        sistema.confermaPrenotazione();
        
        // 2. Esecuzione
        String esito = gestore.annullaPrenotazione(p.getId());
        
        // 3. Verifica
        assertEquals("prenotazione annullata", esito, "Il sistema deve confermare l'annullamento");
        assertEquals("Annullata", p.getStato(), "La post-condizione deve cambiare lo stato in 'Annullata'");
    }

    @Test
    public void testAnnullaPrenotazione_Inesistente() {
        System.out.println("Test UC4: Annulla Prenotazione Inesistente");
        
        // 1. Esecuzione
        String esito = gestore.annullaPrenotazione("PRN-FALSO");
        
        // 2. Verifica
        assertEquals("errore: prenotazione inesistente", esito, "Il sistema deve restituire errore per ID errati");
    }
}