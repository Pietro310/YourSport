/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yoursport;

/**
 *
 * @author pietroalberio
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalTime;

public class GestoreJSON {

    private static final String FILE_PATH = "database.json";
    
    // VARIABILE FONDAMENTALE PER I TEST
    public static boolean DISABILITA_SALVATAGGIO_PER_TEST = false;
    
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
            @Override
            public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                jsonWriter.value(localDate != null ? localDate.toString() : null);
            }
            @Override
            public LocalDate read(JsonReader jsonReader) throws IOException {
                return LocalDate.parse(jsonReader.nextString());
            }
        })
        .registerTypeAdapter(LocalTime.class, new TypeAdapter<LocalTime>() {
            @Override
            public void write(JsonWriter jsonWriter, LocalTime localTime) throws IOException {
                jsonWriter.value(localTime != null ? localTime.toString() : null);
            }
            @Override
            public LocalTime read(JsonReader jsonReader) throws IOException {
                return LocalTime.parse(jsonReader.nextString());
            }
        })
        .create();

    public static void salvaDati(YourSport sistema) {
        // SE STIAMO ESEGUENDO I TEST, NON TOCCARE IL DISCO RIGIDO!
        if (DISABILITA_SALVATAGGIO_PER_TEST) {
            return; 
        }

        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(sistema, writer);
            System.out.println(">>> Sistema: Dati salvati con successo in " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("ERRORE DI SISTEMA: Impossibile salvare i dati. " + e.getMessage());
        }
    }

    public static YourSport caricaDati() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, YourSport.class);
        } catch (IOException e) {
            System.out.println("Nessun salvataggio precedente trovato. Partenza con dati di default.");
            return null;
        }
    }
}