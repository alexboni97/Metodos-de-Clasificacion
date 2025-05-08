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
            double[][] matriz_iteracion = iteracion("Iris-setosa", 0);
            matriz_iteracion = iteracion("Iris-versicolor", 1);
            if (!seguir(matriz_iteracion)) {
                parar = true;
            }
            matriz = matriz_iteracion;
            index++;
        }
    }

    public double[][] iteracion(String key, int c) {
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
        resta[0] = (muestra.getValor1() - m[dist_euclidea.i][0]) * aprendizaje;
        resta[1] = (muestra.getValor2() - m[dist_euclidea.i][1]) * aprendizaje;
        resta[2] = (muestra.getValor3() - m[dist_euclidea.i][2]) * aprendizaje;
        resta[3] = (muestra.getValor4() - m[dist_euclidea.i][3]) * aprendizaje;

        for (int index = 0; index < resta.length; index++) {
            m[dist_euclidea.i][index] = m[dist_euclidea.i][index] + resta[index];
        }
        return m;
    }

    public sol_euclidea distanciaEuclidea(Muestra muestra, double[][] m) {
        double setosa = Math.pow(muestra.getValor1() - m[0][0], 2) +
                Math.pow(muestra.getValor2() - m[0][1], 2) +
                Math.pow(muestra.getValor3() - m[0][2], 2) +
                Math.pow(muestra.getValor4() - m[0][3], 2);
        double versicolor = Math.pow(muestra.getValor1() - m[1][0], 2) +
                Math.pow(muestra.getValor2() - m[1][1], 2) +
                Math.pow(muestra.getValor3() - m[1][2], 2) +
                Math.pow(muestra.getValor4() - m[1][3], 2);

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
        public String resultado;
        public double[] grado_pertenencia;

        public LloydResult() {
        }

        public LloydResult(String resultado, double[] grados) {
            this.resultado = resultado;
            this.grado_pertenencia = grados;
        }
    }

    public LloydResult clasificarMuestra(double[] muestra) {
        sol_euclidea euc_setosa = distanciaEuclidea2(0, new Muestra(muestra[0], muestra[1], muestra[2], muestra[3]),
                matriz);
        sol_euclidea euc_versicolor = distanciaEuclidea2(1, new Muestra(muestra[0], muestra[1], muestra[2], muestra[3]),
                matriz);
        double total = euc_setosa.valor + euc_versicolor.valor;
        LloydResult ll = new LloydResult();
        ll.grado_pertenencia = new double[2];
        if (euc_setosa.valor < euc_versicolor.valor) {
            ll.resultado = "Iris-setosa";
        } else {
            ll.resultado = "Iris-versicolor";
        }
        ll.grado_pertenencia[0] = euc_versicolor.valor * 100 / total;
        ll.grado_pertenencia[1] = euc_setosa.valor * 100 / total;
        System.out.println(ll.grado_pertenencia[0] + " | " + ll.grado_pertenencia[1]);
        return ll;
    }

    private sol_euclidea distanciaEuclidea2(int i, Muestra muestra, double[][] matriz) {
        double valor = Math.pow(muestra.getValor1() - matriz[i][0], 2) +
                Math.pow(muestra.getValor2() - matriz[i][1], 2) +
                Math.pow(muestra.getValor3() - matriz[i][2], 2) +
                Math.pow(muestra.getValor4() - matriz[i][3], 2);

        sol_euclidea euc = new sol_euclidea(i, valor);
        return euc;
    }
}
