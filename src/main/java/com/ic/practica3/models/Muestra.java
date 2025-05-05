package com.ic.practica3.models;

//Clase auxiliar usada para representar cada elemneto de Iris2Clases.txt

public class Muestra {
    private Double valor1, valor2, valor3, valor4;
    private String nombre;

    public Muestra(Double valor1, Double valor2, Double valor3, Double valor4, String nombre) {
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.valor3 = valor3;
        this.valor4 = valor4;
        this.nombre = nombre;
    }

    public Double getValor1() {
        return valor1;
    }

    public void setValor1(Double valor1) {
        this.valor1 = valor1;
    }

    public Double getValor2() {
        return valor2;
    }

    public void setValor2(Double valor2) {
        this.valor2 = valor2;
    }

    public Double getValor3() {
        return valor3;
    }

    public void setValor3(Double valor3) {
        this.valor3 = valor3;
    }

    public Double getValor4() {
        return valor4;
    }

    public void setValor4(Double valor4) {
        this.valor4 = valor4;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double[] getVector(){
        return new double[]{valor1,valor2,valor3,valor4};
    }
}
