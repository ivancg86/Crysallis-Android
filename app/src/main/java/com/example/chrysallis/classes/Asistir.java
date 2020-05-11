package com.example.chrysallis.classes;

import java.io.Serializable;
import java.util.Objects;

public class Asistir implements Serializable {
    private int id_socio;
    private int id_evento;
    private int valoracion;
    private String comentario;
    private int cuantos;
    private String codigo_asistir;

    public Asistir(int id_socio, int id_evento, int cuantos, String codigo_asistir) {
        this.id_socio = id_socio;
        this.id_evento = id_evento;
        this.cuantos = cuantos;
        this.codigo_asistir = codigo_asistir;
    }

    public Asistir(int id_socio, int id_evento) {
        this.id_socio = id_socio;
        this.id_evento = id_evento;
    }


    public int getId_socio() {
        return id_socio;
    }

    public int getId_evento() {
        return id_evento;
    }

    public int getValoracion() {
        return valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public int getCuantos() {
        return cuantos;
    }

    public String getCodigo_asistir() {
        return codigo_asistir;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (this == o) equals = true;
        if (o == null || getClass() != o.getClass()) equals = false;
        Asistir asistir = (Asistir) o;
        if(id_socio == asistir.id_socio && id_evento == asistir.id_evento) equals = true;
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_socio, id_evento);
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
