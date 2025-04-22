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

    // Método principal para clasificar una muestra
    public Map<String, Double> clasificarMuestra(double[] muestra) {
        Map<String, Double> probabilidades = new HashMap<>();
        double sumaTotal = 0.0;

        // Calcular probabilidad para cada clase
        for (String clase : datos.keySet()) {
            // Verificar que tengamos las medias y covarianzas calculadas
            if (!medias_totales.containsKey(clase)) {
                calcularMedia(clase);
            }

            // Obtener la media para la clase actual
            List<Double> mediaLista = medias_totales.get(clase);
            double[] media = new double[mediaLista.size()];
            for (int i = 0; i < mediaLista.size(); i++) {
                media[i] = mediaLista.get(i);
            }

            // Obtener la matriz de covarianza para la clase actual
            List<List<Double>> covLista = calcularCovarianza(clase);
            double[][] covarianza = new double[covLista.size()][covLista.get(0).size()];
            for (int i = 0; i < covLista.size(); i++) {
                for (int j = 0; j < covLista.get(i).size(); j++) {
                    covarianza[i][j] = covLista.get(i).get(j);
                }
            }

            // Calcular la probabilidad usando la función de densidad gaussiana
            double probabilidad = calcularProbabilidadBayes(muestra, media, covarianza);

            // Almacenar la probabilidad para esta clase
            probabilidades.put(clase, probabilidad);
            sumaTotal += probabilidad;
        }

        // Normalizar las probabilidades para que sumen 1
        for (String clase : probabilidades.keySet()) {
            probabilidades.put(clase, probabilidades.get(clase) / sumaTotal);
        }

        return probabilidades;
    }

    // Función para calcular la densidad de probabilidad gaussiana multivariante
    private double calcularProbabilidadBayes(double[] muestra, double[] media, double[][] covarianza) {
        // Dimensión del espacio de características (4 para las flores iris)
        int k = muestra.length;

        // 1. Calcular (x - μ), la diferencia entre la muestra y la media
        double[] diferencia = new double[k];
        for (int i = 0; i < k; i++) {
            diferencia[i] = muestra[i] - media[i];
        }

        // 2. Calcular la inversa de la matriz de covarianza
        double[][] covInversa = calcularInversa(covarianza);

        // 3. Calcular (x - μ)^T * Σ^(-1) * (x - μ)
        // Primero calculamos Σ^(-1) * (x - μ)
        double[] temp = new double[k];
        for (int i = 0; i < k; i++) {
            temp[i] = 0;
            for (int j = 0; j < k; j++) {
                temp[i] += covInversa[i][j] * diferencia[j];
            }
        }

        // Luego calculamos (x - μ)^T * (Σ^(-1) * (x - μ))
        double mahalanobis = 0;
        for (int i = 0; i < k; i++) {
            mahalanobis += diferencia[i] * temp[i];
        }

        // 4. Calcular el determinante de la matriz de covarianza
        double determinante = calcularDeterminante(covarianza);

        // 5. Calcular la constante de normalización (1/sqrt((2π)^k * |Σ|))
        double constante = 1.0 / (Math.sqrt(Math.pow(2 * Math.PI, k) * determinante));

        // 6. Calcular la exponencial exp(-0.5 * mahalanobis)
        double exponencial = Math.exp(-0.5 * mahalanobis);

        // 7. Resultado final: constante * exponencial
        return constante * exponencial;
    }

    // Método para calcular la matriz inversa de una matriz
    private double[][] calcularInversa(double[][] matriz) {
        // Para matrices 4x4 es más práctico usar adjunta/determinante
        int n = matriz.length;
        double[][] inversa = new double[n][n];
        double det = calcularDeterminante(matriz);

        if (Math.abs(det) < 1e-10) {
            // Si el determinante es casi cero, la matriz no es invertible
            // En este caso, añadimos una pequeña cantidad a la diagonal para regularizar
            for (int i = 0; i < n; i++) {
                matriz[i][i] += 1e-5;
            }
            det = calcularDeterminante(matriz);
        }

        // Calcular la matriz adjunta
        double[][] adjunta = calcularAdjunta(matriz);

        // Dividir cada elemento de la adjunta por el determinante
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inversa[i][j] = adjunta[i][j] / det;
            }
        }

        return inversa;
    }

    // Método para calcular la matriz adjunta de una matriz
    private double[][] calcularAdjunta(double[][] matriz) {
        int n = matriz.length;
        double[][] adjunta = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Calcular el cofactor (i,j)
                double[][] menor = new double[n - 1][n - 1];
                int row = 0, col = 0;

                // Construir la matriz menor
                for (int k = 0; k < n; k++) {
                    if (k == i)
                        continue;
                    col = 0;
                    for (int l = 0; l < n; l++) {
                        if (l == j)
                            continue;
                        menor[row][col] = matriz[k][l];
                        col++;
                    }
                    row++;
                }

                // Calcular determinante del menor y aplicar signo adecuado
                double signo = ((i + j) % 2 == 0) ? 1 : -1;
                adjunta[j][i] = signo * calcularDeterminante(menor); // Nota: j,i para la transpuesta
            }
        }

        return adjunta;
    }

    // Método para calcular el determinante de una matriz
    private double calcularDeterminante(double[][] matriz) {
        int n = matriz.length;

        // Caso base: matriz 1x1
        if (n == 1)
            return matriz[0][0];

        // Caso base: matriz 2x2
        if (n == 2) {
            return matriz[0][0] * matriz[1][1] - matriz[0][1] * matriz[1][0];
        }

        // Desarrollo por cofactores para matrices mayores
        double det = 0;
        for (int j = 0; j < n; j++) {
            double[][] menor = new double[n - 1][n - 1];

            // Construir la matriz menor
            for (int i = 1; i < n; i++) {
                int col = 0;
                for (int k = 0; k < n; k++) {
                    if (k == j)
                        continue;
                    menor[i - 1][col] = matriz[i][k];
                    col++;
                }
            }

            // Agregar el término al determinante con el signo adecuado
            double signo = (j % 2 == 0) ? 1 : -1;
            det += signo * matriz[0][j] * calcularDeterminante(menor);
        }

        return det;
    }

    // Método para el endpoint web que retorna un resultado formateado para la UI
    public Map<String, Double> clasificarMuestraWeb(double[] muestra) {
        Map<String, Double> probabilidades = clasificarMuestra(muestra);

        // Si solo nos interesan las clases Iris-setosa e Iris-versicolor
        Map<String, Double> resultado = new HashMap<>();

        // Extraer las probabilidades específicas (ajustar según las clases disponibles)
        double probSetosa = probabilidades.getOrDefault("Iris-setosa", 1e-10);
        double probVersicolor = probabilidades.getOrDefault("Iris-versicolor", 1e-10);

        // Normalizar estas dos probabilidades para que sumen 1
        double suma = probSetosa + probVersicolor;
        if (suma > 0) {
            probSetosa /= suma;
            probVersicolor /= suma;
        }

        resultado.put("setosa", probSetosa);
        resultado.put("versicolor", probVersicolor);

        return resultado;
    }
}
