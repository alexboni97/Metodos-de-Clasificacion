package com.ic.practica3.controllers;
// package com.ic.practica2.models.Fichero;

import java.io.IOException;

import java.util.*;

import org.springframework.ui.Model;
// ABRE UNA TERMINAL Y EJECUTA EL SIGUIENTE COMANDO: mvn spring-boot:run VE A
// UN NAVEGADOR Y ESCRIBE LA SIGUIENTE DIRECCION: http://localhost:8080/
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ic.practica3.models.*;

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

    @GetMapping("/mediaBayes")
    @ResponseBody
    public List<List<Double>> calcularClasesPorBayes() {
        List<List<Double>> solucion = new ArrayList<>();
        solucion.add(by.calcularVarianza("Iris-setosa"));
        solucion.add(by.calcularVarianza("Iris-versicolor"));
        return solucion;
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

}
