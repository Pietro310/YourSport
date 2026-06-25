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

public class GestoreSegnalazioniTest {

    private YourSport sistema;
    private GestoreSegnalazioni gestore;
    private Segnalazione segnalazioneTest;
    private String idSegnalazioneEsistente;

    // =========================================================
    // ARRANGE GLOBALE: Preparazione dell'ambiente prima di ogni test
    // =========================================================
    @BeforeEach
    public void setUp() {
        sistema = YourSport.getInstance();
        sistema.resetSistemaPerTest(); 

        // 1. Per testare il Gestore (UC6), deve esserci almeno una segnalazione nel sistema (UC5).
        // Simuliamo l'azione di uno sportivo per creare il dato di partenza.
        sistema.login("mario@email.it", "pw"); 
        
        // Diamo per scontato che esista la struttura "S1" 
        segnalazioneTest = sistema.inviaSegnalazione("S1", "Riscaldamento spogliatoio guasto");
        idSegnalazioneEsistente = segnalazioneTest.getId();

        // 2. Istanziamo il Gestore che vogliamo testare
        gestore = new GestoreSegnalazioni();
    }

    @AfterEach
    public void tearDown() {
        sistema.resetSistemaPerTest(); // Pulizia finale
    }

    // =========================================================
    // TEST UC6: GESTIONE SEGNALAZIONI
    // =========================================================

    // =========================================================
    // TEST 1: Lettura dell'elenco (Messaggio 1 dell'SSD)
    // =========================================================
    @Test
    public void testMostraSegnalazioni_Successo() {
        System.out.println("Test UC6: Mostra Segnalazioni");

        // --- ACT ---
        List<Segnalazione> lista = gestore.mostraSegnalazioni();

        // --- ASSERT ---
        assertNotNull(lista, "La lista delle segnalazioni non deve mai essere null");
        assertFalse(lista.isEmpty(), "La lista deve contenere almeno la segnalazione creata nel setUp");
        
        // Verifichiamo che la segnalazione di test sia fisicamente dentro la lista
        boolean trovata = false;
        for (Segnalazione s : lista) {
            if (s.getId().equals(idSegnalazioneEsistente)) {
                trovata = true;
                break;
            }
        }
        assertTrue(trovata, "La segnalazione con l'ID atteso deve essere presente nell'elenco");
    }

    // =========================================================
    // TEST 2: Aggiornamento riuscito
    // =========================================================
    @Test
    public void testAggiornaStatoSegnalazione_Successo() {
        System.out.println("Test UC6: Aggiorna Stato (Successo)");

        // --- ARRANGE ---
        String nuovoStato = "In risoluzione";
        
        // Controllo di sicurezza: lo stato di partenza DEVE essere "Aperta"
        assertEquals("Aperta", segnalazioneTest.getStato(), "Lo stato iniziale della segnalazione deve essere 'Aperta'");

        // --- ACT ---
        // Il Gestore esegue l'operazione di sistema
        boolean esito = gestore.aggiornaStatoSegnalazione(idSegnalazioneEsistente, nuovoStato);

        // --- ASSERT ---
        // 1. Verifica che il metodo abbia risposto true (Successo)
        assertTrue(esito, "Il metodo deve restituire TRUE se l'ID esiste ed è stato aggiornato");
        
        // 2. Verifica che l'oggetto sia stato effettivamente modificato (Il messaggio 1.2 dell'SD!)
        assertEquals(nuovoStato, segnalazioneTest.getStato(), "Lo stato della segnalazione in memoria deve essere cambiato a 'In risoluzione'");
    }

    // =========================================================
    // TEST 3: Aggiornamento fallito (Il riquadro [opt] dell'SD)
    // =========================================================
    @Test
    public void testAggiornaStatoSegnalazione_IdInesistente() {
        System.out.println("Test UC6: Aggiorna Stato (Fallimento per ID errato)");

        // --- ARRANGE ---
        String idFalso = "ID_FALSO_INVENTATO_999";
        String nuovoStato = "Risolta";

        // --- ACT ---
        boolean esito = gestore.aggiornaStatoSegnalazione(idFalso, nuovoStato);

        // --- ASSERT ---
        // 1. Verifica che il metodo abbia bloccato l'esecuzione rispondendo false (il riquadro [opt] ha funzionato)
        assertFalse(esito, "Il metodo deve restituire FALSE se viene passato un ID inesistente");
        
        // 2. Verifica di sicurezza: la nostra segnalazione vera non deve essere stata toccata per sbaglio
        assertEquals("Aperta", segnalazioneTest.getStato(), "Lo stato della segnalazione originale NON deve cambiare");
    }
}
