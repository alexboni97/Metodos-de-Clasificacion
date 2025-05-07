package com.ic.practica3.models;

import java.util.*;

public class Lloyd {

    private static double tolerancia = 10e-10;
    private static int max_iteraciones = 10;
    private static double aprendizaje = 0.1;
    private double[][] matriz = { { 4.6, 3.0, 4.0, 0.0 }, { 6.8, 3.4, 4.6, 0.7 } };

    private Map<String, List<Muestra>> datos;

    private class sol_euclidea {
        int i;
        double valor;

        private sol_euclidea(int i, double v) {
            this.i = i;
            this.valor = v;
        };
    }

    public Lloyd() {
        datos = Fichero.leerDatos();
        boolean parar = false;
        int index = 0;
        while (!parar && index < max_iteraciones) {
            double[][] matriz_iteracion = iteracion("Iris-setosa");
            matriz_iteracion = iteracion("Iris-versicolor");
            if (!seguir(matriz_iteracion)) {
                parar = true;
            }
            matriz = matriz_iteracion;
            index++;
        }
    }

    public double[][] iteracion(String key) {
        List<Muestra> clase = datos.get(key);
        double[][] m = matriz;
        for (int i = 0; i < clase.size(); i++) {
            sol_euclidea distancia_euclidea = distanciaEuclidea(clase.get(i), m);
            m = recalcular_centro(distancia_euclidea, m, clase.get(i));
        }
        return m;
    }

    public double[][] recalcular_centro(sol_euclidea dist_euclidea, double[][] m, Muestra muestra) {
        double[] resta = { 0.0, 0.0, 0.0, 0.0 };
        for (int index = 0; index < resta.length; index++) {
            resta[index] = (muestra.getValor1() - m[dist_euclidea.i][index]) * aprendizaje;
        }
        for (int index = 0; index < resta.length; index++) {
            m[dist_euclidea.i][index] = m[dist_euclidea.i][index] + resta[index];
        }
        return m;
    }

    public sol_euclidea distanciaEuclidea(Muestra muestra, double[][] m) {
        double setosa = Math.sqrt((Math.pow(2, (muestra.getValor1() - m[0][0])) +
                Math.pow(2, (muestra.getValor2() - m[0][1])) +
                Math.pow(2, (muestra.getValor3() - m[0][2])) +
                Math.pow(2, (muestra.getValor4() - m[0][3]))));
        double versicolor = Math.sqrt((Math.pow(2, (muestra.getValor1() - m[1][0])) +
                Math.pow(2, (muestra.getValor2() - m[1][1])) +
                Math.pow(2, (muestra.getValor3() - m[1][2])) +
                Math.pow(2, (muestra.getValor4() - m[1][3]))));
        if (setosa < versicolor) {
            return new sol_euclidea(0, setosa);
        } else {
            return new sol_euclidea(1, versicolor);
        }
    }

    public boolean seguir(double[][] matriz_iteracion) {
        int i = 0;
        while (i < 2) {
            Muestra m = new Muestra(matriz[i][0], matriz[i][1], matriz[i][2], matriz[i][3]);
            if (distanciaEuclidea(m, matriz_iteracion).valor > tolerancia) {
                return true;
            }
            i++;
        }
        return false;
    }

    public static class LloydResult {
        public int id;
        public double[] valores;
        public double[] grados;

        public LloydResult(int id, double[] valores, double[] grados) {
            this.id = id;
            this.valores = valores;
            this.grados = grados;
        }
    }

    public LloydResult clasificarMuestra(double[] muestra) {
        sol_euclidea euc_setosa = distanciaEuclidea(new Muestra(muestra[0], muestra[1], muestra[2], muestra[3]),
                matriz);
        sol_euclidea euc_versicolor = distanciaEuclidea(new Muestra(muestra[0], muestra[1], muestra[2], muestra[3]),
                matriz);
        return null;
    }
}
