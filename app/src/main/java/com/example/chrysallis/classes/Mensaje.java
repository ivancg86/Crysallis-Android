package com.example.chrysallis.classes;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Mensaje implements Serializable,Comparable<Mensaje>{
    private int id;
    private int id_socio;
    private int id_evento;
    private String mensaje;
    private String fecha;
    private Socio socios;

    public Mensaje(int id_socio, int id_evento, String mensaje, String fecha) {
        this.id_socio = id_socio;
        this.id_evento = id_evento;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public int getId_socio() {
        return id_socio;
    }

    public int getId_evento() {
        return id_evento;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public Socio getSocios() {
        return socios;
    }

    @Override
    public int compareTo(Mensaje o) {
        return this.getFecha().compareTo(o.getFecha());
    }
}
