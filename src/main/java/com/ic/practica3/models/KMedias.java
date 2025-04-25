package com.ic.practica3.models;

import java.util.List;
import java.util.Map;

public class KMedias {
    private Map<String, List<Muestra>> datos;
    

    public KMedias(){
        datos = Fichero.leerDatos();
    }
}
