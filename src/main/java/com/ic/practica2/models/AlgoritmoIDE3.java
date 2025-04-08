package com.ic.practica2.models;

import java.io.*;
import java.security.KeyStore.Entry;
import java.util.*;

import lombok.val;

public class AlgoritmoIDE3 {
    public class Nodo {
        String atributo;
        Map<String, Nodo> hijos = new HashMap<>();
        boolean solucion;// solo hojas

        public Nodo(String atributo) {
            this.atributo = atributo;
        }

        public String getAtributo() {
            return atributo;
        }

        public Map<String, Nodo> getHijos() {
            return hijos;
        }
    }

    public class id3_datos {
        double pos, neg;
        int positivos, negativos, a;
        double r;

        id3_datos(double pos, double neg, int positivos, int negativos, int a, double r) {
            this.pos = pos;
            this.neg = neg;
            this.positivos = positivos;
            this.negativos = negativos;
            this.a = a;
            this.r = r;
        }

        id3_datos() {
            this(0, 0, 0, 0, 0, 0);
        }
    }

    private Nodo raiz;

    public Nodo getNodoRaiz() {
        return this.raiz;
    }

    private void mostrarNodo(Nodo nodo) {
        System.out.println("NODO: " + nodo.atributo);
        for (Map.Entry<String, Nodo> n : nodo.hijos.entrySet()) {
            System.out.println("Rama: " + n.getKey());
            mostrarNodo(n.getValue());
        }
    }

    public AlgoritmoIDE3(String c, String d) throws IOException {
        List<String> categorias = cargarCategorias(c);
        List<List<String>> datos = cargarDatos(d);
        this.raiz = new Nodo(null);
        construirArbol(categorias, datos, raiz);
        mostrarNodo(raiz);
    }

    private void construirArbol(List<String> categorias, List<List<String>> datos, Nodo n) {
        if (datos.isEmpty() || categorias.isEmpty()) {
            System.out.println("No hay datos o categorias");
            return;
        }

        List<String> mejorAtributo = obtenerMejorAtributo(categorias, datos);
        if (mejorAtributo.isEmpty()) {
            System.out.println("No hay mejor atributo");
            return;
        }

        n.atributo = mejorAtributo.get(0);

        for (int i = 1; i < mejorAtributo.size(); i++) {
            String valorAtributo = mejorAtributo.get(i);
            Nodo hijo = new Nodo(null);
            n.hijos.put(valorAtributo, hijo);

            List<List<String>> nuevosDatos = calcularNuevosDatos(categorias, datos, valorAtributo, n.atributo);
            if (calcularSiSolucion(nuevosDatos)) {
                hijo.solucion = true;
                hijo.atributo = nuevosDatos.get(0).get(nuevosDatos.get(0).size() - 1);
            } else {
                List<String> nuevaCategoria = new ArrayList<>(categorias);
                nuevaCategoria.remove(n.atributo); // Eliminar el atributo
                construirArbol(nuevaCategoria, nuevosDatos, hijo);
            }
        }
    }

    private List<List<String>> calcularNuevosDatos(List<String> categorias, List<List<String>> datos, String atributo,
            String atributoPadre) {
        List<List<String>> nuevosDatos = new ArrayList<>();
        int index = categorias.indexOf(atributoPadre);

        for (List<String> fila : datos) {
            if (index < fila.size() && fila.get(index).equals(atributo)) {
                List<String> nuevaFila = new ArrayList<>(fila);
                nuevaFila.remove(index);
                nuevosDatos.add(nuevaFila);
            }
        }
        return nuevosDatos;
    }

    private boolean calcularSiSolucion(List<List<String>> datos) {
        if (datos.isEmpty())
            return false;

        String opt = datos.get(0).get(datos.get(0).size() - 1);
        for (List<String> fila : datos) {
            if (!fila.get(fila.size() - 1).equals(opt)) {
                return false;
            }
        }
        return true;
    }

    private List<String> obtenerMejorAtributo(List<String> categorias, List<List<String>> datos) {
        String mejorAtributo = null;
        double menorMerito = Double.MAX_VALUE;
        double[] gananciaAct = { 0.0 };
        Map<String, Map<String, id3_datos>> frecuencias = new HashMap<>();
        List<String> cat = new ArrayList<>();

        for (int i = 0; i < categorias.size() - 1; i++) {
            String categoria = categorias.get(i);
            frecuencias.putIfAbsent(categoria, new HashMap<>());

            for (List<String> fila : datos) {
                String valor = fila.get(i);
                String decision = fila.get(categorias.size() - 1);

                frecuencias.get(categoria).putIfAbsent(valor, new id3_datos());
                id3_datos info = frecuencias.get(categoria).get(valor);

                if (decision.equals("si")) {
                    info.positivos++;
                } else {
                    info.negativos++;
                }
                info.a++;
            }

            // Calcular méritos
            gananciaAct[0] = 0.0;
            for (id3_datos info : frecuencias.get(categoria).values()) {
                info.pos = info.a > 0 ? (double) info.positivos / info.a : 0;
                info.neg = info.a > 0 ? (double) info.negativos / info.a : 0;
                info.r = datos.size() > 0 ? (double) info.a / datos.size() : 0;

                double merito = 0;
                if (info.pos > 0) {
                    merito -= info.pos * Math.log(info.pos) / Math.log(2);
                }
                if (info.neg > 0) {
                    merito -= info.neg * Math.log(info.neg) / Math.log(2);
                }
                merito *= info.r;

                if (!Double.isNaN(merito)) {
                    gananciaAct[0] += merito;
                }
            }

            if (gananciaAct[0] < menorMerito) {
                menorMerito = gananciaAct[0];
                mejorAtributo = categoria;
            }
        }

        if (mejorAtributo != null) {
            cat.add(mejorAtributo);
            cat.addAll(frecuencias.get(mejorAtributo).keySet());
        }

        return cat;
    }

    private List<String> cargarCategorias(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return Arrays.asList(br.readLine().split(","));
        }
    }

    private List<List<String>> cargarDatos(String filePath) throws IOException {
        List<List<String>> registros = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(Arrays.asList(linea.split(",")));
            }
        }
        return registros;
    }

    public String calcular(List<String> entrada) {
        return rec_calcular(entrada, raiz);
    }

    public String rec_calcular(List<String> entrada, Nodo n) {
        boolean encontrado = false;
        int i = 0;
        while (!encontrado && i < entrada.size()) {
            if (n.getHijos().containsKey(entrada.get(i)))
                encontrado = true;
            else {
                i++;
            }
        }
        if (!encontrado) {
            return "No hay solucion";
        } else {
            if (n.getHijos().get(entrada.get(i)).solucion) {
                return n.getHijos().get(entrada.get(i)).getAtributo();
            } else {
                return rec_calcular(entrada, n.getHijos().get(entrada.get(i)));
            }
        }
    }
}