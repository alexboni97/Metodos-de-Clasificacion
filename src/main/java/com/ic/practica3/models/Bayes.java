package com.ic.practica3.models;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bayes {
    private Map<String, List<Muestra>> datos;
    private Map<String, List<Double>> medias_totales;
    private Map<String, List<Double>> varianzas_totales;

    public Bayes() {
        datos = Fichero.leerDatos();
        medias_totales = new HashMap<>();
        varianzas_totales = new HashMap<>();
    }

    public List<Double> calcularMedia(String key) {
        List<Muestra> clase = datos.get(key);
        List<Double> medias = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0));
        for (int j = 0; j < clase.size(); j++) {
            double aux1 = clase.get(j).getValor1() * 1 / clase.size();
            double aux2 = clase.get(j).getValor2() * 1 / clase.size();
            double aux3 = clase.get(j).getValor3() * 1 / clase.size();
            double aux4 = clase.get(j).getValor4() * 1 / clase.size();
            medias.set(0, aux1 + medias.get(0));
            medias.set(1, aux2 + medias.get(1));
            medias.set(2, aux3 + medias.get(2));
            medias.set(3, aux4 + medias.get(3));
        }
        medias_totales.put(key, medias);
        for (int i = 0; i < medias.size(); i++) {
            System.out.println("Media " + i + " de " + key + ": " + medias.get(i));
        }
        return medias;
    }

    public List<List<Double>> calcularCovarianza(String key) {
        List<Muestra> clase = datos.get(key);

        // Lista de matrices que guardaremos para sumarlas entre ellas y devolver la
        // matriz de covarianza solucion
        List<List<ArrayList<Double>>> matriz = Stream
                .generate(() -> Stream.generate(() -> new ArrayList<>(Collections.nCopies(4, 0.0)))
                        .limit(4)
                        .map(list -> (ArrayList<Double>) list) // Conversión explícita
                        .collect(Collectors.toList()))
                .limit(clase.size())
                .collect(Collectors.toList());
        // Matriz de Covarianza
        List<List<Double>> solucion = Stream.generate(() -> new ArrayList<>(Collections.nCopies(4, 0.0)))
                .limit(4)
                .collect(Collectors.toList());

        for (int j = 0; j < clase.size(); j++) {
            double aux1 = clase.get(j).getValor1() - medias_totales.get(key).get(0);
            double aux2 = clase.get(j).getValor2() - medias_totales.get(key).get(1);
            double aux3 = clase.get(j).getValor3() - medias_totales.get(key).get(2);
            double aux4 = clase.get(j).getValor4() - medias_totales.get(key).get(3);

            List<Double> diferencias = Arrays.asList(aux1, aux2, aux3, aux4);
            for (int i = 0; i < 4; i++) {
                for (int k = 0; k < 4; k++) {
                    matriz.get(j).get(i).set(k, diferencias.get(i) * diferencias.get(k));
                }
            }
        }

        // Sumar todas las matrices y dividir por el tamaño de la clase para obtener la
        // covarianza
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < 4; k++) {
                double suma = 0.0;
                for (int j = 0; j < clase.size(); j++) {
                    suma += matriz.get(j).get(i).get(k);
                }
                solucion.get(i).set(k, suma / clase.size());
            }
        }

        return solucion;
    }

}
