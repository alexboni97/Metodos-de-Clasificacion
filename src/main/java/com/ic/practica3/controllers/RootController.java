package com.ic.practica3.controllers;

import java.util.*;

import org.springframework.ui.Model;
// ABRE UNA TERMINAL Y EJECUTA EL SIGUIENTE COMANDO: mvn spring-boot:run VE A
// UN NAVEGADOR Y ESCRIBE LA SIGUIENTE DIRECCION: http://localhost:8080/
import org.springframework.web.bind.annotation.*;
import com.ic.practica3.models.*;
import com.ic.practica3.models.KMedias.FuzzyResult;
import com.ic.practica3.models.Lloyd.LloydResult;

import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "*") // Permitir solicitudes desde el frontend
public class RootController {

    private KMedias km;
    private Bayes by;
    private Lloyd ll;

    @GetMapping("/mediaBayes")
    @ResponseBody
    public List<List<Double>> calcularMediasPorBayes() {
        List<List<Double>> solucion = new ArrayList<>();
        solucion.add(by.calcularMedia("Iris-setosa"));
        solucion.add(by.calcularMedia("Iris-versicolor"));
        return solucion;
    }

    @GetMapping("/covarianzaBayes")
    @ResponseBody
    public List<List<List<Double>>> calcularClasesPorBayes() {
        List<List<List<Double>>> solucion = new ArrayList<>();
        solucion.add(by.calcularCovarianza("Iris-setosa"));
        solucion.add(by.calcularCovarianza("Iris-versicolor"));
        return solucion;
    }

    @PostMapping("/clasificarBayes")
    @ResponseBody
    public Map<String, Double> clasificarBayes(@RequestBody double[] muestra) {
        double[] muestra1 = new double[]{5.5,4.2,1.4,0.2};
        return by.clasificarMuestraWeb(muestra);
    }

    @GetMapping("/bayes")
    public String mostrarBayes(Model model) {
        if (by == null)
            by = new Bayes();
        return "bayes";
    }

    @GetMapping("/kmedias")
    public String mostrarKMedias(Model model) {
        if (km == null)
            km = new KMedias();
        return "kmedias";
    }

    @GetMapping("/lloyd")
    public String mostrarLloyd(Model model) {
        if (ll == null)
            ll = new Lloyd();
        return "lloyd";
    }

    @GetMapping("/autores")
    public String mostrarAutores(Model model) {
        return "autores";
    }

    @PostMapping("/clasificar-k-medias")
    @ResponseBody
    public FuzzyResult clasificarAjax(
            @RequestBody Map<String, Object> payload) {

        double x1 = Double.parseDouble(payload.get("x1").toString());
        double x2 = Double.parseDouble(payload.get("x2").toString());
        double x3 = Double.parseDouble(payload.get("x3").toString());
        double x4 = Double.parseDouble(payload.get("x4").toString());

        double[] muestra = new double[] { x1, x2, x3, x4 };

        return km.clasificarNuevaMuestraConGrado(muestra);
    }

    @PostMapping("/clasificar-lloyd")
    @ResponseBody
    public LloydResult clasificarLloyd(
            @RequestBody Map<String, Object> payload) {

        double x1 = Double.parseDouble(payload.get("x1").toString());
        double x2 = Double.parseDouble(payload.get("x2").toString());
        double x3 = Double.parseDouble(payload.get("x3").toString());
        double x4 = Double.parseDouble(payload.get("x4").toString());

        double[] muestra = new double[] { x1, x2, x3, x4 };

        return ll.clasificarMuestra(muestra);
    }
}
