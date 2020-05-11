package com.example.chrysallis.classes;

import java.io.Serializable;
import java.util.HashSet;

public class Socio implements Serializable {
    private int id;
    private String dni;
    private String telefono;
    private String nombre;
    private String imagenUsuario;
    private String apellidos;
    private boolean activo;
    private String mail;
    private String password;
    private boolean administrador;
    private String idiomaDefecto;
    private boolean estatal;
    private int id_comunidad;
    private HashSet<Asistir> asistir;

    public void setId_comunidad(int id_comunidad) {
        this.id_comunidad = id_comunidad;
    }

    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public String getIdiomaDefecto() {
        return idiomaDefecto;
    }

    public boolean isEstatal() {
        return estatal;
    }

    public int getId_comunidad() {
        return id_comunidad;
    }

    public HashSet<Asistir> getAsistir() {
        return asistir;
    }

    public String getImagenUsuario() {
        return imagenUsuario;
    }

    public void setImagenUsuario(String imagenUsuario) {
        this.imagenUsuario = imagenUsuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIdiomaDefecto(String idiomaDefecto) {
        this.idiomaDefecto = idiomaDefecto;
    }
}
