package com.ic.practica2.controllers;
// package com.ic.practica2.models.Fichero;

import com.ic.practica2.controllers.RootController.NodoPrueba;
import com.ic.practica2.models.AlgoritmoIDE3;
import com.ic.practica2.models.Fichero;
import com.ic.practica2.models.AlgoritmoIDE3.Nodo;

import java.io.IOException;

import java.util.*;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
// ABRE UNA TERMINAL Y EJECUTA EL SIGUIENTE COMANDO: mvn spring-boot:run VE A
// UN NAVEGADOR Y ESCRIBE LA SIGUIENTE DIRECCION: http://localhost:8080/

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "*") // Permitir solicitudes desde el frontend
public class RootController {

    private AlgoritmoIDE3 id3;
    private NodoPrueba nodoRaiz;

    public class NodoPrueba {
        private String atributo;
        private String seleccion;
        private List<NodoPrueba> hijos = new ArrayList<>();

        public NodoPrueba(String atributo, String seleccion) {
            this.atributo = atributo;
            this.seleccion = seleccion;
        }

        public String getAtributo() {
            return atributo;
        }

        public String getSeleccion() {
            return seleccion;
        }

        public List<NodoPrueba> getHijos() {
            return hijos;
        }

        public void agregarHijo(NodoPrueba hijo) {
            hijos.add(hijo);
        }
    }

    public RootController() throws IOException {
        this.id3 = new AlgoritmoIDE3("src/main/resources/data/AtributosJuego.txt",
                "src/main/resources/data/Juego.txt");
        inicializaNodoPrueba();
    }

    private void parsearNodo(Nodo nodo, NodoPrueba nodoPrueba) {
        for (Map.Entry<String, Nodo> n : nodo.getHijos().entrySet()) {
            n.getKey();
            n.getValue().getAtributo();
            NodoPrueba nodoAux = new NodoPrueba(n.getValue().getAtributo(), n.getKey());
            nodoPrueba.hijos.add(nodoAux);
            parsearNodo(n.getValue(), nodoAux);
        }
    }

    private void inicializaNodoPrueba() {
        Nodo raiz = this.id3.getNodoRaiz();
        nodoRaiz = new NodoPrueba(raiz.getAtributo(), null);
        parsearNodo(raiz, nodoRaiz);
        System.out.println(raiz.toString());
    }

    private void convertirAGrafo(NodoPrueba nodo, NodoPrueba padre, List<Map<String, Object>> nodos,
            List<Map<String, Object>> aristas) {
        Map<String, Object> nodoMapa = new HashMap<>();
        nodoMapa.put("id", nodo.hashCode());
        nodoMapa.put("label", nodo.getAtributo());
        nodos.add(nodoMapa);

        if (padre != null) {
            Map<String, Object> arista = new HashMap<>();
            arista.put("from", padre.hashCode());
            arista.put("to", nodo.hashCode());
            arista.put("label", nodo.getSeleccion());
            aristas.add(arista);
        }

        for (NodoPrueba hijo : nodo.getHijos()) {
            convertirAGrafo(hijo, nodo, nodos, aristas);
        }
    }

    @GetMapping("/")
    public String mostrarRoot(Model model) {
        List<Map<String, Object>> nodos = new ArrayList<>();
        List<Map<String, Object>> aristas = new ArrayList<>();
        convertirAGrafo(nodoRaiz, null, nodos, aristas);

        model.addAttribute("nodos", nodos);
        model.addAttribute("aristas", aristas);
        model.addAttribute("nodo", nodoRaiz);
        return "index";
    }

    @GetMapping("/simular")
    public String simularRoot(Model model) {
        return "simular";
    }

    @GetMapping("/autores")
    public String mostrarAutores(Model model) {
        return "autores";
    }

    @GetMapping("/ejemplos")
    public String mostrarEjemplos(Model model) {
        List<String[]> datos = Fichero.leerDatos();
        model.addAttribute("datos", datos);
        return "ejemplos";
    }

    @PostMapping("/solucion")
    public String mostrarArbol(@RequestParam String tiempoExterior,
            @RequestParam String temperatura,
            @RequestParam String humedad,
            @RequestParam String viento,
            Model model,
            RedirectAttributes redirectAttributes) {
        System.out.println(tiempoExterior);
        List<String> entrada = new ArrayList<>();
        if (tiempoExterior != null)
            entrada.add(tiempoExterior);
        if (temperatura != null)
            entrada.add(temperatura);
        if (humedad != null)
            entrada.add(humedad);
        if (viento != null)
            entrada.add(viento);

        String resultado = this.id3.calcular(entrada);
        redirectAttributes.addFlashAttribute("resultado", resultado);
        return "redirect:/simular";
    }

    @PostMapping("/ejecutar-uno")
    public String ejecutarUno(
            @RequestParam int index,
            @RequestParam String tiempoExterior,
            @RequestParam String temperatura,
            @RequestParam String humedad,
            @RequestParam String viento,
            RedirectAttributes redirectAttributes) {
        List<String> entrada = new ArrayList<>();
        if (tiempoExterior != null)
            entrada.add(tiempoExterior);
        if (temperatura != null)
            entrada.add(temperatura);
        if (humedad != null)
            entrada.add(humedad);
        if (viento != null)
            entrada.add(viento);

        String sol = this.id3.calcular(entrada);
        redirectAttributes.addFlashAttribute("resultado", sol);
        redirectAttributes.addFlashAttribute("indexResult", index);

        // model.addAttribute("solucion", sol);
        return "redirect:/ejemplos";
    }

    @GetMapping("/ejecutar-todos")
    public String calcularTodos(RedirectAttributes redirectAttributes) {
        List<String> resultados = new ArrayList<>();
        List<String[]> f = Fichero.leerDatos();

        for (int i = 0; i < 14; i++) {
            List<String> aux = Arrays.asList(f.get(i));
            resultados.add(id3.calcular(aux));
        }
        redirectAttributes.addFlashAttribute("resultados", resultados);
        return "redirect:/ejemplos";
    }

}
