package com.ic.practica3.models;

import java.util.*;

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
    /*
     * TODO: Terminar esto para las clases de varianzas y tal
     * public List<Double> calcularVarianza(String key) {
     * List<Muestra> clase = datos.get(key);
     * List<Double> varianzas = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0));
     * for (int j = 0; j < clase.size(); j++) {
     * double aux1 = Math.pow(clase.get(j).getValor1() -
     * medias_totales.get(key).get(0), 2) * 1 / clase.size();
     * double aux2 = Math.pow(clase.get(j).getValor2() -
     * medias_totales.get(key).get(1), 2) * 1 / clase.size();
     * double aux3 = Math.pow(clase.get(j).getValor3() -
     * medias_totales.get(key).get(2), 2) * 1 / clase.size();
     * double aux4 = Math.pow(clase.get(j).getValor4() -
     * medias_totales.get(key).get(3), 2) * 1 / clase.size();
     * varianzas.set(0, aux1 + varianzas.get(0));
     * varianzas.set(1, aux2 + varianzas.get(1));
     * varianzas.set(2, aux3 + varianzas.get(2));
     * varianzas.set(3, aux4 + varianzas.get(3));
     * }
     * varianzas_totales.put(key, varianzas);
     * for (int i = 0; i < varianzas.size(); i++) {
     * System.out.println("Varianza " + i + " de " + key + ": " + varianzas.get(i));
     * }
     * return varianzas;
     * }
     */
}
