package com.ic.practica2.models;
import java.io.*;
import java.util.*;


public class Fichero {

    private static final String FILE_PATH = "src/main/resources/data/Juego.txt";

    public static List<String[]> leerDatos() {
        List<String[]> matriz = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] fila = linea.split(",");
                matriz.add(fila);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matriz;
    }

    public void guardarDato(String dato) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(dato);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
