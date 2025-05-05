package com.ic.practica3.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KMedias {
    private Map<String, List<Muestra>> datos;
    private double[] v1;
    private double[] v2;
    private double[][] centroides;
    private final double TOLERANCIA = 0.01;
    private final double PESO = 2;
    private double[][] centroidesFinales;

    public KMedias() {
        datos = Fichero.leerDatos();
        inicializarCentros();
        calcularKMedias();
    }

    public void inicializarCentros() {
        v1 = new double[] { 4.6, 3.0, 4.0, 0.0 };
        v2 = new double[] { 6.8, 3.4, 4.6, 0.7 };
        centroides = new double[][] { v1, v2 };
    }

    public void calcularKMedias() {
        List<Muestra> muestras = datos.values().stream().flatMap(List::stream).collect(Collectors.toList());
        List<double[]> puntos = muestras.stream().map(Muestra::getVector).collect(Collectors.toList());
        final int MAX_ITER = 100;
        final double m = 2.0;

        int n = puntos.size();
        int dim = puntos.get(0).length;
        int k = 2;

        double[][] U = new double[n][k];
        double[][] prevU = new double[n][k];

        // Calcular U inicial con los centroides dados
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                double dij = distanciaEuclideaCuadrado(puntos.get(i), centroides[j]);
                if (dij == 0) {
                    U[i][j] = 1;
                    for (int l = 0; l < k; l++) {
                        if (l != j)
                            U[i][l] = 0;
                    }
                    continue;
                }
                double sum = 0;
                for (int l = 0; l < k; l++) {
                    double dil = distanciaEuclideaCuadrado(puntos.get(i), centroides[l]);
                    sum += Math.pow(1.0 / dil, 1.0 / (m - 1));
                }
                U[i][j] = Math.pow(1.0 / dij, 1.0 / (m - 1)) / sum;
            }
        }

        for (int iter = 0; iter < MAX_ITER; iter++) {
            for (int i = 0; i < n; i++)
                prevU[i] = Arrays.copyOf(U[i], k);

            // Recalcular centroides
            for (int j = 0; j < k; j++) {
                double[] num = new double[dim];
                double den = 0;
                for (int i = 0; i < n; i++) {
                    double uij_m = Math.pow(U[i][j], m);
                    for (int d = 0; d < dim; d++) {
                        num[d] += uij_m * puntos.get(i)[d];
                    }
                    den += uij_m;
                }
                for (int d = 0; d < dim; d++) {
                    centroides[j][d] = num[d] / den;
                }
            }

            // Recalcular U con nueva matriz de pertenencia
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    double dij = distanciaEuclideaCuadrado(puntos.get(i), centroides[j]);
                    if (dij == 0) {
                        U[i][j] = 1;
                        for (int l = 0; l < k; l++) {
                            if (l != j)
                                U[i][l] = 0;
                        }
                        continue;
                    }
                    double sum = 0;
                    for (int l = 0; l < k; l++) {
                        double dil = distanciaEuclideaCuadrado(puntos.get(i), centroides[l]);
                        sum += Math.pow(1.0 / dil, 1.0 / (m - 1));
                    }
                    U[i][j] = Math.pow(1.0 / dij, 1.0 / (m - 1)) / sum;
                }
            }

            // Verificar convergencia
            double maxDiff = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    maxDiff = Math.max(maxDiff, Math.abs(U[i][j] - prevU[i][j]));
                }
            }
            if (maxDiff < TOLERANCIA)
                break;
        }
        // Guardar centroides finales para clasificar nuevas muestras
        centroidesFinales = centroides;
    }

    private double distanciaEuclideaCuadrado(double[] a, double[] b) {
        double suma = 0;
        for (int i = 0; i < a.length; i++) {
            suma += Math.pow(a[i] - b[i], 2);
        }
        return suma;
    }

    public static class FuzzyResult {
        public int id;
        public double[] valores;
        public double[] grados;

        public FuzzyResult(int id, double[] valores, double[] grados) {
            this.id = id;
            this.valores = valores;
            this.grados = grados;
        }
    }

    public FuzzyResult clasificarNuevaMuestraConGrado(double[] muestra) {
        if (centroidesFinales == null) {
            throw new IllegalStateException("Debes ejecutar aplicarFuzzyKMedias antes de clasificar.");
        }

        int k = centroidesFinales.length;
        double[] grados = new double[k];

        for (int j = 0; j < k; j++) {
            double dij = distanciaEuclideaCuadrado(muestra, centroidesFinales[j]);
            if (dij == 0) {
                grados[j] = 1;
                for (int l = 0; l < k; l++) {
                    if (l != j)
                        grados[l] = 0;
                }
                return new FuzzyResult(0, muestra, grados);
            }
            double sum = 0;
            for (int l = 0; l < k; l++) {
                double dil = distanciaEuclideaCuadrado(muestra, centroidesFinales[l]);
                sum += Math.pow(1.0 / dil, 1.0 / (PESO - 1));
            }
            grados[j] = Math.pow(1.0 / dij, 1.0 / (PESO - 1)) / sum;
        }

        return new FuzzyResult(0, muestra, grados);
    }

}
