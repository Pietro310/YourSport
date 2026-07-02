# YourSport

YourSport è una piattaforma software sviluppata per la gestione automatizzata di strutture sportive. Il sistema consente agli utenti di prenotare impianti sportivi in autonomia e agli amministratori di gestire tariffe, manutenzione, segnalazioni e statistiche economiche.

Progetto realizzato per il corso di Ingegneria del Software del Corso di Laurea Magistrale in Ingegneria Informatica.

## Autori

- Antonino Caruso
- Davide Consoli
- Pietro Alberio

## Descrizione

YourSport nasce con l'obiettivo di sostituire le tradizionali procedure manuali di gestione degli impianti sportivi, offrendo una piattaforma centralizzata per:

- Prenotazione autonoma delle strutture sportive
- Gestione delle prenotazioni
- Amministrazione delle tariffe
- Segnalazione e gestione dei guasti
- Sospensione e riattivazione delle strutture
- Gestione delle notifiche
- Monitoraggio degli incassi previsti
- Gestione del profilo utente

Il sistema previene conflitti di prenotazione, automatizza la gestione delle disponibilità e supporta la comunicazione tra utenti e amministratori.

---

## Funzionalità Principali

### Utente Sportivo

- Registrazione alla piattaforma
- Ricerca avanzata delle strutture sportive
- Prenotazione di strutture o posti disponibili
- Visualizzazione e gestione delle proprie prenotazioni
- Segnalazione di problemi e malfunzionamenti
- Visualizzazione delle notifiche ricevute
- Gestione del proprio profilo personale

### Amministratore

- Gestione dei costi di prenotazione
- Gestione delle segnalazioni ricevute
- Sospensione e riattivazione delle strutture
- Invio automatico di notifiche in caso di disservizi
- Consultazione delle statistiche sugli incassi previsti

---

## Architettura Software

Il progetto è stato sviluppato seguendo un approccio iterativo e incrementale articolato in cinque iterazioni.

### Pattern Utilizzati

#### GRASP

- Controller
- Creator
- Information Expert
- Pure Fabrication
- High Cohesion

#### GoF

- Singleton

### Principali Componenti

- `YourSport`
  - Facade Controller principale
  - Singleton del sistema

- `Sportivo`
  - Utente registrato che utilizza le strutture

- `Admin`
  - Gestore delle strutture sportive

- `Struttura`
  - Risorsa prenotabile

- `Prenotazione`
  - Gestione delle prenotazioni

- `Segnalazione`
  - Gestione dei problemi segnalati

- `Notifica`
  - Comunicazioni automatiche del sistema

- `GestorePrenotazioni`
  - Controller dedicato alla gestione prenotazioni

- `GestoreSegnalazioni`
  - Controller dedicato alla gestione delle segnalazioni

- `GestoreStrutture`
  - Controller dedicato alla gestione delle strutture

- `GestoreNotifiche`
  - Controller dedicato alle notifiche

---

## Casi d'Uso Implementati

| ID | Caso d'Uso |
|----|------------|
| UC1 | Registrazione alla piattaforma |
| UC2 | Prenotazione autonoma struttura |
| UC3 | Gestione dei costi di prenotazione |
| UC4 | Gestione delle proprie prenotazioni |
| UC5 | Segnalazione problemi struttura |
| UC6 | Gestione segnalazioni |
| UC7 | Sospensione e riattivazione struttura |
| UC8 | Visualizza notifiche |
| UC9 | Visualizza statistiche incassi previsti |
| UC10 | Gestione profilo personale |

---

## Modello di Prenotazione

Il sistema supporta due modalità di utilizzo delle strutture:

### Uso Esclusivo

L'intera struttura viene prenotata da un singolo utente per una specifica fascia oraria.

Esempi:

- Campo da Tennis
- Campo da Calcetto

Tariffazione:

- Tariffa ORARIO

### Uso Condiviso

Più utenti possono prenotare contemporaneamente posti disponibili fino al raggiungimento della capienza massima.

Esempi:

- Piscina
- Palestra

Tariffazione:

- Tariffa PERSONA

---

## Persistenza dei Dati

Il sistema utilizza file JSON per:

- Salvataggio automatico dello stato del sistema
- Ripristino dopo arresti anomali
- Conservazione di utenti, prenotazioni, notifiche e segnalazioni

---

## Requisiti Software

### Ambiente di Esecuzione

- Java JDK 17 o superiore

### Dipendenze

- Libreria JSON per serializzazione/deserializzazione dei dati

---

## Requisiti Non Funzionali

### Usabilità

- Interfaccia semplice e differenziata in base al ruolo dell'utente
- Feedback immediati sugli errori

### Affidabilità

- Prevenzione dell'overbooking
- Validazione degli input
- Ripristino automatico dei dati

### Sicurezza

- Gestione autenticazione utenti
- Protezione delle credenziali
- Rispetto delle normative sulla privacy (GDPR)

---

## Testing

Per ciascun caso d'uso sono stati sviluppati test dedicati per verificare:

- Correttezza delle funzionalità implementate
- Gestione degli errori
- Robustezza del sistema
- Validazione degli input
- Integrità dei dati

I test coprono sia gli scenari principali sia i flussi alternativi e le condizioni di errore.

---

## Struttura del Progetto

```text
src/
│
├── model/
│   ├── Sportivo
│   ├── Admin
│   ├── Struttura
│   ├── Prenotazione
│   ├── Segnalazione
│   └── Notifica
│
├── controller/
│   ├── YourSport
│   ├── GestorePrenotazioni
│   ├── GestoreSegnalazioni
│   ├── GestoreStrutture
│   └── GestoreNotifiche
│
├── persistence/
│   └── JSON Manager
│
└── test/
    └── Test Suite
```

---

## Possibili Evoluzioni Future

- Interfaccia grafica Web
- Applicazione mobile Android/iOS
- Sistema di pagamento online
- Notifiche push
- Dashboard amministrativa avanzata
- Integrazione con sistemi di autenticazione esterni
- Calendario condiviso delle prenotazioni
- Reportistica avanzata e analytics

---

## Licenza

Questo progetto è stato sviluppato a scopo accademico e didattico.

Può essere distribuito e utilizzato come software Open Source esclusivamente per finalità di studio, ricerca e apprendimento.
