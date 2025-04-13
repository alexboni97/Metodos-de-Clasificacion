package com.ic.practica3.models;

import java.io.*;
import java.util.*;

public class Fichero {

    private static final String FILE_PATH = "src/main/resources/data/Iris2Clases.txt";

    public static Map<String, List<Muestra>> leerDatos() {
        Map<String, List<Muestra>> datos = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] fila = linea.split(",");
                Muestra m = new Muestra(Double.parseDouble(fila[0]), Double.parseDouble(fila[1]),
                        Double.parseDouble(fila[2]), Double.parseDouble(fila[3]), fila[4]);
                if (!datos.containsKey(fila[4])) {
                    datos.put(fila[4], new ArrayList<>());
                }
                datos.get(fila[4]).add(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datos;
    }
}
